package com.shahabkondri.chatgpt.cli.configuration;

import com.shahabkondri.chatgpt.api.model.TextCompletionModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @author Shahab Kondri
 */
@ConfigurationProperties(prefix = "chat.gpt")
public class ChatGptProperties {

	private final TextCompletionModel model;

	public ChatGptProperties(@DefaultValue("GPT_4_32_K") TextCompletionModel model) {
		this.model = model;
	}

	public TextCompletionModel getModel() {
		return model;
	}

}
