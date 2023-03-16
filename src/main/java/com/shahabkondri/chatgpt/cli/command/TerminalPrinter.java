package com.shahabkondri.chatgpt.cli.command;

import org.jline.terminal.Terminal;
import org.springframework.stereotype.Component;

/**
 * @author Shahab Kondri
 */
@Component
public class TerminalPrinter {

	private final Terminal terminal;

	public TerminalPrinter(Terminal terminal) {
		this.terminal = terminal;
	}

	public void print(String message) {
		terminal.writer().printf(message);
		terminal.flush();
	}

	public void newLine() {
		print(System.lineSeparator());
	}

}