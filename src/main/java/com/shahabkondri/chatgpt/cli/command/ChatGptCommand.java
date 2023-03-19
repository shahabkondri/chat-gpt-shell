package com.shahabkondri.chatgpt.cli.command;

import com.shahabkondri.chatgpt.api.client.ChatGptClient;
import com.shahabkondri.chatgpt.api.model.ChatGptRequest;
import com.shahabkondri.chatgpt.api.model.MessageRole;
import com.shahabkondri.chatgpt.cli.configuration.ChatGptProperties;
import org.springframework.core.codec.DecodingException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shahab Kondri
 */
@ShellComponent
public class ChatGptCommand {

	private final ChatGptClient chatGptClient;

	private final TerminalPrinter terminalPrinter;

	private final List<ChatGptRequest.Message> messages = new CopyOnWriteArrayList<>();

	private final ChatGptProperties chatGptProperties;

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\n\n");

	public ChatGptCommand(ChatGptClient chatGptClient, TerminalPrinter terminalPrinter,
						  ChatGptProperties chatGptProperties) {
		this.chatGptClient = chatGptClient;
		this.terminalPrinter = terminalPrinter;
		this.chatGptProperties = chatGptProperties;
	}

	@ShellMethod(key = {"chat", "c"}, value = "sends a message to a ChatGPT API client and returns the response.")
	public void chat(@ShellOption(arity = Integer.MAX_VALUE) String... prompt) {
		String message = String.join(" ", prompt);
		messages.add(new ChatGptRequest.Message(MessageRole.USER, message));
		ChatGptRequest request = new ChatGptRequest(chatGptProperties.getModel(), messages);

		StringBuilder builder = new StringBuilder();
		chatGptClient.completions(request).filter(response -> response.choices().get(0).delta().content() != null)
				.map(response -> normalizeOutput(response.choices().get(0).delta().content()))
				.doOnNext(response -> {
					terminalPrinter.print(response);
					builder.append(response);
				}).onErrorContinue((error, o) -> {
					if (error instanceof DecodingException && o.toString().contains("[DONE]")) {
						terminalPrinter.newLine();
					}
				}).doFinally(signal -> {
					ChatGptRequest.Message assistantMessage = new ChatGptRequest.Message(MessageRole.ASSISTANT, builder.toString());
					messages.add(assistantMessage);
				}).collectList().block();

	}

	private static String normalizeOutput(String input) {
		Matcher matcher = NEW_LINE_PATTERN.matcher(input);
		if (matcher.matches()) {
			return matcher.replaceAll("\n");
		}
		return input;
	}

	@ShellMethod(key = "chat --clear", value = "Clear chat history.")
	public void clearChat() {
		messages.clear();
		terminalPrinter.print("Chat history is cleared.");
		terminalPrinter.newLine();
	}

}
