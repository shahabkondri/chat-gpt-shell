package com.shahabkondri.chatgpt.shell.shell;

import org.jline.terminal.Terminal;
import org.springframework.stereotype.Component;

/**
 * Provides a convenient way to print messages to the terminal. This class is responsible
 * for managing terminal output.
 *
 * @author Shahab Kondri
 */
@Component
public class TerminalPrinter {

	private final Terminal terminal;

	/**
	 * Constructs a new TerminalPrinter instance with the specified terminal.
	 * @param terminal The terminal used for printing messages.
	 */
	public TerminalPrinter(Terminal terminal) {
		this.terminal = terminal;
	}

	/**
	 * Prints the specified message to the terminal.
	 * @param message The message to print to the terminal.
	 */
	public void print(String message) {
		terminal.writer().printf(message);
		terminal.flush();
	}

	/**
	 * Prints a newline to the terminal. This is useful for separating messages and
	 * ensuring correct formatting when displaying output.
	 */
	public void newLine() {
		terminal.writer().println();
	}

}