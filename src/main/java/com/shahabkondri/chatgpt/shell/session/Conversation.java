package com.shahabkondri.chatgpt.shell.session;

import jakarta.persistence.*;

import java.util.List;

/**
 * Represents a {@link Conversation} entity.
 *
 * @author Shahab Kondri
 */
@Entity
public class Conversation {

	/**
	 * The unique identifier for the Conversation. It is automatically generated.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * The title of the Conversation. This field is mandatory.
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String title;

	/**
	 * A list of messages associated with the {@link Conversation}. Fetch type is
	 * {@code EAGER}, meaning they are retrieved from the database as soon as the
	 * {@link Conversation} is loaded.
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "conversation")
	private List<MessageEntity> messages;

	/**
	 * Returns the ID of the Conversation.
	 * @return The unique identifier of the Conversation.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of the Conversation.
	 * @param id The unique identifier to be set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the title of the Conversation.
	 * @return The title of the Conversation.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the Conversation.
	 * @param title The title to be set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the list of messages associated with the Conversation.
	 * @return A list of MessageEntity objects.
	 */
	public List<MessageEntity> getMessages() {
		return messages;
	}

	/**
	 * Sets the list of messages associated with the Conversation.
	 * @param messages A list of MessageEntity objects to be associated with the
	 * Conversation.
	 */
	public void setMessages(List<MessageEntity> messages) {
		this.messages = messages;
	}

}
