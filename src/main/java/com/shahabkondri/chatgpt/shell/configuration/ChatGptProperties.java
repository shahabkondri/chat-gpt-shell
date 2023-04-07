package com.shahabkondri.chatgpt.shell.configuration;

import com.shahabkondri.chatgpt.api.model.TextCompletionModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * {@link ConfigurationProperties properties} that holds properties related to the ChatGPT
 * API, such as the text completion model to be used. These properties can be set using
 * the 'chat.gpt' prefix in the configuration file.
 *
 * @author Shahab Kondri
 */
@ConfigurationProperties(prefix = "chat.gpt")
public record ChatGptProperties(@DefaultValue("GPT_3_5_TURBO") TextCompletionModel model) {
}
