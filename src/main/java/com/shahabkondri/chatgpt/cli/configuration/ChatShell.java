package com.shahabkondri.chatgpt.cli.configuration;

import com.shahabkondri.chatgpt.cli.command.ChatGptCommand;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Primary;
import org.springframework.shell.Input;
import org.springframework.shell.ResultHandlerService;
import org.springframework.shell.Shell;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.context.ShellContext;
import org.springframework.shell.exit.ExitCodeMappings;
import org.springframework.stereotype.Component;

/**
 * {@link ChatShell} is a custom implementation of the Spring {@link Shell} that allows
 * users to interact with the ChatGPT API in a more user-friendly way. It extends the base
 * {@link Shell} class and overrides the evaluate method to handle user input differently.
 * When the user input starts with a colon ":", it treats the input as a command by
 * removing the colon and evaluating the remaining text as a command. Otherwise, it sends
 * the input directly to the ChatGPT API through the ChatGptCommand class and displays the
 * AI-generated response in the terminal.
 *
 * @author Shahab Kondri
 */
@Component
@Primary
public class ChatShell extends Shell {

	private final ChatGptCommand chatGptCommand;

	/**
	 * Constructs a new {@link ChatShell} instance.
	 * @param chatGptCommand The ChatGptCommand instance for interacting with the ChatGPT
	 * API.
	 * @param resultHandlerService The ResultHandlerService instance for handling command
	 * results.
	 * @param commandRegistry The CommandCatalog instance for managing available commands.
	 * @param terminal The Terminal instance for managing the command-line interface.
	 * @param shellContext The ShellContext instance for managing the shell state.
	 * @param exitCodeMappings The ExitCodeMappings instance for mapping exit codes.
	 */
	public ChatShell(ChatGptCommand chatGptCommand, ResultHandlerService resultHandlerService,
			CommandCatalog commandRegistry, Terminal terminal, ShellContext shellContext,
			ExitCodeMappings exitCodeMappings) {
		super(resultHandlerService, commandRegistry, terminal, shellContext, exitCodeMappings);
		this.chatGptCommand = chatGptCommand;
	}

	/**
	 * Evaluates the user input and either sends it directly to the ChatGPT API or treats
	 * it as a command if it starts with a colon ":".
	 * @param input The user input to evaluate.
	 * @return The result of the evaluation.
	 */
	@Override
	public Object evaluate(Input input) {
		if (noInput(input)) {
			return NO_INPUT;
		}

		String text = String.join(" ", input.words());
		if (text.startsWith(":")) {
			input = () -> text.substring(1);
			return super.evaluate(input);
		}
		else {
			chatGptCommand.chat(input.words().toArray(String[]::new));
			return null;
		}
	}

	/**
	 * Checks if the given input is empty, contains only whitespace or starts with a
	 * comment.
	 * @param input The input to check.
	 * @return True if the input is empty, contains only whitespace or starts with a
	 * comment, false otherwise.
	 */
	private boolean noInput(Input input) {
		return input.words().isEmpty() || (input.words().size() == 1 && input.words().get(0).trim().isEmpty())
				|| (input.words().iterator().next().matches("\\s*//.*"));
	}

}
