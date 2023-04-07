package com.shahabkondri.chatgpt.shell.configuration;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * A {@link PromptProvider} that customizes the terminal prompt. This class defines the
 * appearance of the prompt in the terminal.
 *
 * @author Shahab Kondri
 */
@Component
public class ChatPromptProvider implements PromptProvider {

	/**
	 * Returns an {@link AttributedString} representing the customized prompt, which is
	 * displayed in the terminal.
	 * @return The AttributedString representing the prompt with a yellow foreground.
	 */
	@Override
	public AttributedString getPrompt() {
		return new AttributedString(":> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}

}
