package com.shahabkondri.chatgpt.shell.session;

import com.shahabkondri.chatgpt.api.model.ChatGptRequest;
import com.shahabkondri.chatgpt.api.model.MessageRole;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Message entity
 *
 * @author Shahab Kondri
 */
@Entity
public class MessageEntity {

	/**
	 * The unique identifier for the Conversation. It is automatically generated.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Represents the role of the message in the conversation (e.g., system, user,
	 * assistant, etc.). This field is annotated with {@code @Enumerated(EnumType.STRING)}
	 * to store the enum values as strings in the database.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MessageRole role;

	/**
	 * Represents the content of the message.
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * Represents the conversation to which the message belongs. This field is annotated
	 * with {@code @ManyToOne} to denote the many-to-one relationship between messages and
	 * a conversation.
	 */
	@ManyToOne(optional = false)
	private Conversation conversation;

	/**
	 * Default constructor
	 */
	public MessageEntity() {
	}

	/**
	 * Constructs a new {@link MessageEntity} with the specified role, content, and
	 * conversation.
	 * @param role The role of the message (e.g., system, user, assistant, etc.).
	 * @param content The content of the message.
	 * @param conversation The conversation to which the message belongs.
	 */
	public MessageEntity(MessageRole role, String content, Conversation conversation) {
		this.role = role;
		this.content = content;
		this.conversation = conversation;
	}

	/**
	 * Returns the ID of this message.
	 * @return The ID of this message.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of this message.
	 * @param id The ID to be set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the role of this message.
	 * @return The role of this message.
	 */
	public MessageRole getRole() {
		return role;
	}

	/**
	 * Sets the role of this message.
	 * @param role The role to be set.
	 */
	public void setRole(MessageRole role) {
		this.role = role;
	}

	/**
	 * Returns the content of this message.
	 * @return The content of this message.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content of this message.
	 * @param content The content to be set.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the conversation to which this message belongs.
	 * @return The conversation to which this message belongs.
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * Sets the conversation of this message.
	 * @param conversation The conversation to be set.
	 */
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	/**
	 * Converts a {@link ChatGptRequest.Message} and a {@link Conversation} to a
	 * {@link MessageEntity}.
	 * @param message The ChatGptRequest.Message to be converted.
	 * @param conversation The Conversation to be associated with the message.
	 * @return A MessageEntity based on the provided ChatGptRequest.Message and
	 * Conversation.
	 */
	public static MessageEntity toMessageEntity(ChatGptRequest.Message message, Conversation conversation) {
		return new MessageEntity(message.role(), message.content(), conversation);
	}

	/**
	 * Converts a collection of {@link ChatGptRequest.Message} instances and a
	 * {@link Conversation} to a list of {@link MessageEntity} instances.
	 * @param messages The collection of ChatGptRequest.Message instances to be converted.
	 * @param conversation The Conversation to be associated with the messages.
	 * @return A list of MessageEntity instances based on the provided collection of
	 * ChatGptRequest.Message instances and Conversation.
	 */
	public static List<MessageEntity> toMessageEntities(Collection<ChatGptRequest.Message> messages,
			Conversation conversation) {
		return messages.stream().map(message -> toMessageEntity(message, conversation)).collect(Collectors.toList());
	}

	/**
	 * Converts a {@link MessageEntity} to a {@link ChatGptRequest.Message}.
	 * @param messageEntity The MessageEntity to be converted.
	 * @return A ChatGptRequest.Message based on the provided MessageEntity.
	 */
	public static ChatGptRequest.Message toChatGptMessage(MessageEntity messageEntity) {
		return new ChatGptRequest.Message(messageEntity.getRole(), messageEntity.getContent());
	}

	/**
	 * Converts a list of {@link MessageEntity} instances to a list of
	 * {@link ChatGptRequest.Message} instances.
	 * @param messageEntities The list of MessageEntity instances to be converted.
	 * @return A list of ChatGptRequest.Message instances based on the provided list of
	 * MessageEntity instances.
	 */
	public static List<ChatGptRequest.Message> toChatGptMessages(List<MessageEntity> messageEntities) {
		return messageEntities.stream().map(MessageEntity::toChatGptMessage).collect(Collectors.toList());
	}

}
