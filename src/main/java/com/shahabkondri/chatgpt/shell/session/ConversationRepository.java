package com.shahabkondri.chatgpt.shell.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for performing CRUD operations on {@link Conversation} instances.
 *
 * @author Shahab Kondri
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
