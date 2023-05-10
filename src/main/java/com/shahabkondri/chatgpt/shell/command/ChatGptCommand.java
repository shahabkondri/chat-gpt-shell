package com.shahabkondri.chatgpt.shell.command;

import com.shahabkondri.chatgpt.api.client.ChatGptClient;
import com.shahabkondri.chatgpt.api.model.ChatGptRequest;
import com.shahabkondri.chatgpt.api.model.MessageRole;
import com.shahabkondri.chatgpt.shell.configuration.ChatGptProperties;
import com.shahabkondri.chatgpt.shell.shell.Spinner;
import com.shahabkondri.chatgpt.shell.shell.TerminalPrinter;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link ShellComponent} that facilitates interaction with the OpenAI GPT-3 model
 * through the terminal. This component provides commands for sending user messages to the
 * API, processing AI-generated responses as a stream, and managing conversation history.
 * It simplifies the process of obtaining and displaying AI-generated responses in
 * real-time. This class uses a {@link ChatGptClient} to communicate with the OpenAI API,
 * a {@link TerminalPrinter} to display output in the terminal, and a
 * {@link ChatGptProperties} to manage ChatGPT configuration. A {@link Spinner} is used to
 * indicate loading states.
 *
 * @author Shahab Kondri
 */
@ShellComponent
public class ChatGptCommand {

	private final ChatGptClient chatGptClient;

	private final ConversationCommand conversationCommand;

	private final TerminalPrinter terminalPrinter;

	private final ChatGptProperties chatGptProperties;

	private final Spinner spinner;

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\n\n");

	private static final Duration CHAT_TIMEOUT = Duration.ofSeconds(30);

	/**
	 * Constructs a new ChatGptCommand.
	 * @param chatGptClient The ChatGPT client for interacting with the API.
	 * @param conversationCommand The ConversationCommand object used for managing
	 * conversations.
	 * @param terminalPrinter The terminal printer for printing messages.
	 * @param chatGptProperties The properties for the ChatGPT API.
	 * @param spinner The spinner for showing loading state.
	 */
	public ChatGptCommand(ChatGptClient chatGptClient, ConversationCommand conversationCommand,
			TerminalPrinter terminalPrinter, ChatGptProperties chatGptProperties, Spinner spinner) {
		this.chatGptClient = chatGptClient;
		this.conversationCommand = conversationCommand;
		this.terminalPrinter = terminalPrinter;
		this.chatGptProperties = chatGptProperties;
		this.spinner = spinner;
	}

	/**
	 * Interacts with the ChatGPT API by sending a user message and processing the
	 * AI-generated response as a stream.
	 * <p>
	 * example: <pre>
	 * :> Hello ChatGPT, can you help me with my question?
	 * </pre>
	 * @param prompt The user input to send to the ChatGPT API.
	 */
	@ShellMethod(key = { "chat" }, value = "Interacts with the ChatGPT API by sending a"
			+ " user message and processing the AI-generated response as a stream")
	public void chat(@ShellOption(arity = Integer.MAX_VALUE) String... prompt) {
		spinner.startSpinner();
		String message = String.join(" ", prompt);
		conversationCommand.addMessage(message, MessageRole.USER);
		ChatGptRequest request = new ChatGptRequest(chatGptProperties.model(), conversationCommand.getMessages());

		AtomicBoolean isFirstResultPrinted = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
		CountDownLatch latch = new CountDownLatch(1);

		chatGptClient.completions(request).filter(response -> response.choices().get(0).delta().content() != null)
				.doOnNext(__ -> spinner.stopSpinner())
				.map(response -> normalizeOutput(response.choices().get(0).delta().content(), isFirstResultPrinted))
				.doOnNext(builder::append).publishOn(Schedulers.parallel()).timeout(CHAT_TIMEOUT).doFinally(signal -> {
					if (conversationCommand.getConversationStored().compareAndSet(false, true)) {
						conversationCommand.generateConversationTitle(message);
					}
					terminalPrinter.newLine();
					conversationCommand.addMessage(builder.toString(), MessageRole.ASSISTANT);
					conversationCommand.updateCurrentConversation(message, MessageRole.USER);
					conversationCommand.updateCurrentConversation(builder.toString(), MessageRole.ASSISTANT);
					latch.countDown();
				}).onErrorResume(throwable -> {
					spinner.stopSpinner();
					handleApiException(throwable);
					return Mono.empty();
				}).subscribe(terminalPrinter::print);
		try {
			latch.await();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Sets or updates a system message that helps define the behavior of the ChatGPT
	 * assistant. The system message can be changed at any time during the conversation
	 * session, affecting all subsequent interactions with the ChatGPT API. To use this
	 * command in the terminal, type ':system', followed by the message. For example:
	 * <pre>
	 * :> :system You are an AI trained to assist with programming questions.
	 * </pre> The system message is added or updated at the beginning of the messages list
	 * to ensure its influence on the assistant's behavior.
	 * @param prompt The user input to set or update as the system message.
	 */
	@ShellMethod(key = "system", value = "The system message helps set the behavior of the assistant.")
	public void systemMessage(@ShellOption(arity = Integer.MAX_VALUE) String... prompt) {
		String message = String.join(" ", prompt);
		conversationCommand.getMessages().stream().filter(m -> m.role() == MessageRole.SYSTEM).findFirst()
				.ifPresent(conversationCommand.getMessages()::remove);
		ChatGptRequest.Message systemMessage = new ChatGptRequest.Message(MessageRole.SYSTEM, message);
		conversationCommand.getMessages().addFirst(systemMessage);

		conversationCommand.updateSystemMessage(systemMessage);
	}

	/**
	 * Normalizes the output generated by the ChatGPT API, removing unnecessary new lines.
	 * @param output The output generated by the ChatGPT API.
	 * @param isFirstResultPrinted An atomic boolean flag to check if this is the first
	 * result printed.
	 * @return The normalized output string.
	 */
	private static String normalizeOutput(String output, AtomicBoolean isFirstResultPrinted) {
		Matcher matcher = NEW_LINE_PATTERN.matcher(output);
		if (matcher.matches()) {
			if (!isFirstResultPrinted.getAndSet(true)) {
				return matcher.replaceAll("");
			}
			return matcher.replaceAll("\n");
		}
		return output;
	}

	private void handleApiException(Throwable throwable) {
		if (throwable instanceof WebClientResponseException.TooManyRequests) {
			terminalPrinter.println(
					"There might be an issue with your API key." + " Please check your API key and try again.");
		}
		else {
			terminalPrinter.println("Oops, something went wrong. Try reloading the conversation.");
		}
	}

}
