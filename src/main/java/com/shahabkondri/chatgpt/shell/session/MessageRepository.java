package com.shahabkondri.chatgpt.shell.session;

import com.shahabkondri.chatgpt.api.model.MessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for performing CRUD operations on {@link MessageEntity} instances.
 *
 * @author Shahab Kondri
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

	/**
	 * Finds a {@link MessageEntity} by its associated {@link Conversation} and
	 * {@link MessageRole}.
	 * @param conversation The Conversation to which the message belongs.
	 * @param messageRole The role of the message (e.g., system, user, assistant, etc.).
	 * @return An Optional containing the MessageEntity if found, or an empty Optional if
	 * not found.
	 */
	Optional<MessageEntity> findByConversationAndRole(Conversation conversation, MessageRole messageRole);

}
