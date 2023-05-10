package com.shahabkondri.chatgpt.shell.command;

/**
 * Enum representing different system prompts. Each enum constant provides a system
 * message for a specific purpose. Currently, it only contains a single type of prompt for
 * generating conversation titles.
 *
 * @author Shahab Kondri
 */
public enum SystemMessagePrompt {

	/**
	 * The enum constant used for generating a conversation title. The system message asks
	 * the model to generate a concise, contextually relevant title based on the initial
	 * conversation message.
	 */
	GENERATE_CONVERSATION_TITLE {
		@Override
		public String getSystemMessage(String firstPrompt) {
			return "Given the initial message of this conversation: \"" + firstPrompt + "\", "
					+ "please generate a concise (2-5 words max), and contextually relevant title. "
					+ "The title should summarize the main topic or theme of the conversation. "
					+ "Please provide the title directly, without preceding it with any label."
					+ "Avoid using single or double quotes in the title.";
		}
	};

	/**
	 * Abstract method that should be implemented by each enum constant to provide a
	 * specific system message.
	 * @param firstPrompt The initial message of the conversation.
	 * @return The system message.
	 */
	public abstract String getSystemMessage(String firstPrompt);

}
