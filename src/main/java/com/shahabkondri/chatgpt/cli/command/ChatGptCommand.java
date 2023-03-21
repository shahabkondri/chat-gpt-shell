package com.shahabkondri.chatgpt.cli.command;

import com.shahabkondri.chatgpt.api.client.ChatGptClient;
import com.shahabkondri.chatgpt.api.model.ChatGptRequest;
import com.shahabkondri.chatgpt.api.model.MessageRole;
import com.shahabkondri.chatgpt.cli.configuration.ChatGptProperties;
import com.shahabkondri.chatgpt.cli.shell.Spinner;
import com.shahabkondri.chatgpt.cli.shell.TerminalPrinter;
import org.springframework.core.codec.DecodingException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shahab Kondri
 */
@ShellComponent
public class ChatGptCommand {

	private final ChatGptClient chatGptClient;

	private final TerminalPrinter terminalPrinter;

	private final ChatGptProperties chatGptProperties;

	private final Spinner spinner;

	private final List<ChatGptRequest.Message> messages = new CopyOnWriteArrayList<>();

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\n\n");

	public ChatGptCommand(ChatGptClient chatGptClient, TerminalPrinter terminalPrinter,
			ChatGptProperties chatGptProperties, Spinner spinner) {
		this.chatGptClient = chatGptClient;
		this.terminalPrinter = terminalPrinter;
		this.chatGptProperties = chatGptProperties;
		this.spinner = spinner;
	}

	@ShellMethod(key = { "chat", "c" }, value = "Interacts with the ChatGPT API by sending a"
			+ " user message and processing the AI-generated response as a stream")
	public void chat(@ShellOption(arity = Integer.MAX_VALUE) String... prompt) {
		spinner.startSpinner(100);
		String message = String.join(" ", prompt);
		messages.add(new ChatGptRequest.Message(MessageRole.USER, message));
		ChatGptRequest request = new ChatGptRequest(chatGptProperties.getModel(), messages);

		AtomicBoolean isFirstResultPrinted = new AtomicBoolean(false);
		StringBuilder builder = new StringBuilder();
		CountDownLatch latch = new CountDownLatch(1);

		chatGptClient.completions(request).filter(response -> response.choices().get(0).delta().content() != null)
				.doOnNext(__ -> spinner.stopSpinner())
				.map(response -> normalizeOutput(response.choices().get(0).delta().content(), isFirstResultPrinted))
				.doOnNext(builder::append).onErrorContinue((error, o) -> {
					if (error instanceof DecodingException && o.toString().contains("[DONE]")) {
						terminalPrinter.newLine();
					}
				}).publishOn(Schedulers.parallel()).timeout(Duration.ofMillis(10000)).doFinally(signal -> {
					ChatGptRequest.Message assistantMessage = new ChatGptRequest.Message(MessageRole.ASSISTANT,
							builder.toString());
					messages.add(assistantMessage);
					latch.countDown();

					// Stop the spinner if the timeout occurred
					if (signal == SignalType.ON_ERROR && latch.getCount() == 0) {
						spinner.stopSpinner();
						terminalPrinter.print("The chat session timed out. Please try again.");
						terminalPrinter.newLine();
					}
				}).subscribe(terminalPrinter::print);
		try {
			latch.await();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

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

	@ShellMethod(key = "chat --clear", value = "Clear chat history.")
	public void clearChat() {
		messages.clear();
		terminalPrinter.print("Chat history is cleared.");
		terminalPrinter.newLine();
	}

}
