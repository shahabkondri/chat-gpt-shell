package com.shahabkondri.chatgpt.shell.command;

import com.shahabkondri.chatgpt.api.client.ChatGptClient;
import com.shahabkondri.chatgpt.api.model.ChatGptRequest;
import com.shahabkondri.chatgpt.api.model.MessageRole;
import com.shahabkondri.chatgpt.api.model.TextCompletionModel;
import com.shahabkondri.chatgpt.shell.configuration.ChatGptProperties;
import com.shahabkondri.chatgpt.shell.session.Conversation;
import com.shahabkondri.chatgpt.shell.session.ConversationRepository;
import com.shahabkondri.chatgpt.shell.session.MessageEntity;
import com.shahabkondri.chatgpt.shell.session.MessageRepository;
import com.shahabkondri.chatgpt.shell.shell.TerminalPrinter;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link ShellComponent} that manages user conversations with the ChatGPT API.
 * Conversations are stored in a database, enabling retrieval and continuation of past
 * interactions. This component provides commands to start a new conversation, load an
 * existing conversation from the database, and display current and all stored
 * conversations. It also generates titles for new conversations using the ChatGPT API.
 *
 * @author Shahab Kondri
 */
@ShellComponent
public class ConversationCommand {

	private final ChatGptClient chatGptClient;

	private final ConversationRepository conversationRepository;

	private final MessageRepository messageRepository;

	private final ChatGptProperties chatGptProperties;

	private final TerminalPrinter terminalPrinter;

	private final LinkedList<ChatGptRequest.Message> messages = new LinkedList<>();

	private Conversation conversation = new Conversation();

	private final AtomicBoolean conversationStored = new AtomicBoolean(false);

	/**
	 * Constructs a new ConversationCommand. It initializes a new conversation and loads a
	 * system message if one is specified in the ChatGPT properties.
	 * @param chatGptClient The client for interacting with the ChatGPT API.
	 * @param conversationRepository The repository for storing and retrieving
	 * conversations.
	 * @param messageRepository The repository for storing and retrieving messages.
	 * @param chatGptProperties The properties for the ChatGPT API.
	 * @param terminalPrinter The terminal printer for printing messages.
	 */
	public ConversationCommand(ChatGptClient chatGptClient, ConversationRepository conversationRepository,
			MessageRepository messageRepository, ChatGptProperties chatGptProperties, TerminalPrinter terminalPrinter) {
		this.chatGptClient = chatGptClient;
		this.conversationRepository = conversationRepository;
		this.messageRepository = messageRepository;
		this.chatGptProperties = chatGptProperties;
		this.terminalPrinter = terminalPrinter;

		loadSystemMessage();
	}

	/**
	 * Generates a title for the current conversation using the ChatGPT API. The title is
	 * generated based on the user's first prompt in the conversation.
	 * @param firstPrompt The user's first prompt in the conversation.
	 */
	public void generateConversationTitle(String firstPrompt) {
		ChatGptRequest.Message message = new ChatGptRequest.Message(MessageRole.SYSTEM,
				SystemMessagePrompt.GENERATE_CONVERSATION_TITLE.getSystemMessage(firstPrompt));
		ChatGptRequest request = new ChatGptRequest(TextCompletionModel.GPT_3_5_TURBO, List.of(message));
		StringBuilder builder = new StringBuilder();
		CountDownLatch latch = new CountDownLatch(1);

		chatGptClient.completions(request).filter(response -> response.choices().get(0).delta().content() != null)
				.doOnNext(response -> builder.append(response.choices().get(0).delta().content()))
				.timeout(Duration.ofSeconds(5)).doFinally(signal -> {
					updateCurrentConversation(builder.toString());
					latch.countDown();
				}).onErrorResume(throwable -> Mono.empty()).subscribe();
		try {
			latch.await();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Displays the ID and title of the current conversation.
	 */
	@ShellMethod(key = "conversation", value = "Displays the current conversation ID.")
	public void conversation() {
		if (!conversationStored.get()) {
			terminalPrinter.println("No active conversation found.");
		}
		else {
			terminalPrinter
					.println("Conversation ID: " + conversation.getId() + " | Title: " + conversation.getTitle());
		}
	}

	/**
	 * Displays the IDs and titles of all stored conversations. The conversations are
	 * retrieved from the database.
	 */
	@ShellMethod(key = "conversation --all", value = "Displays all stored conversations.")
	public void conversations() {
		List<Conversation> conversations = conversationRepository.findAll();
		if (conversations.isEmpty()) {
			terminalPrinter.println("No conversations have been stored.");
		}
		else {
			for (Conversation conversation : conversations) {
				terminalPrinter
						.println("Conversation Id: " + conversation.getId() + " | Title: " + conversation.getTitle());
			}
		}
	}

	/**
	 * Loads a conversation by its ID from the database. If a conversation with the given
	 * ID is found, it loads the conversation and informs the user. If no conversation
	 * with the given ID is found, it informs the user that no conversation was found.
	 * @param conversationId The ID of the conversation to load.
	 */
	@ShellMethod(key = "conversation --load", value = "Load a conversation by its ID.")
	public void loadConversation(@ShellOption long conversationId) {
		conversationRepository.findById(conversationId).ifPresentOrElse(c -> {
			conversation = c;
			List<ChatGptRequest.Message> chatGptMessages = MessageEntity.toChatGptMessages(c.getMessages());
			messages.clear();
			messages.addAll(chatGptMessages);
			terminalPrinter.println(
					"Conversation loaded. ID: " + conversation.getId() + " | Title: " + conversation.getTitle());
		}, () -> terminalPrinter.println("No conversation found with ID: " + conversationId));
	}

	/**
	 * Starts a new conversation. If a conversation is already active, it clears the
	 * current conversation and starts a new one. The new conversation will be stored in
	 * the database. If no conversation is currently active, it informs the user to start
	 * a conversation first.
	 */
	@ShellMethod(key = "conversation --new", value = "Starts a new conversation.")
	public void newConversation() {
		if (conversationStored.get()) {
			resetConversation();
			conversationStored.compareAndSet(true, false);
			terminalPrinter.println("New conversation started");
		}
		else {
			terminalPrinter.println("Please start a conversation first.");
		}
	}

	/**
	 * Deletes a conversation given its ID. If the conversation to be deleted is the
	 * current one, it resets the conversation state and prints a message indicating the
	 * deletion.
	 * @param conversationId The ID of the conversation to be deleted.
	 */
	@ShellMethod(key = "conversation --delete", value = "Deletes a conversation by its ID.")
	public void deleteConversation(@ShellOption long conversationId) {
		boolean exists = conversationRepository.existsById(conversationId);
		if (exists) {
			conversationRepository.deleteById(conversationId);
			if (conversationId == conversation.getId()) {
				resetConversation();
				conversationStored.compareAndSet(true, false);
			}
			terminalPrinter.println("Conversation deleted. ID: " + conversationId);
		}
		else {
			terminalPrinter.println("No conversation found with ID: " + conversationId);
		}
	}

	/**
	 * Deletes all stored conversations, resets the current conversation, and prints a
	 * message indicating that all conversations have been deleted.
	 */
	@ShellMethod(key = "conversation --delete-all", value = "Deletes a conversation by its ID.")
	public void deleteAllConversation() {
		conversationRepository.deleteAll();
		resetConversation();
		conversationStored.set(false);
		terminalPrinter.println("All conversation are deleted.");
	}

	/**
	 * Resets the current conversation by creating a new empty conversation, clearing the
	 * messages, and loading the system message.
	 */
	private void resetConversation() {
		conversation = new Conversation();
		messages.clear();
		loadSystemMessage();
	}

	/**
	 * Loads the system message from the ChatGPT properties, if it exists. The system
	 * message is added to the beginning of the messages list and associated with the
	 * current conversation. This method is typically used when initializing a new
	 * conversation, to set the behavior of the assistant.
	 */
	private void loadSystemMessage() {
		if (StringUtils.hasLength(this.chatGptProperties.systemMessage())) {
			messages.addFirst(new ChatGptRequest.Message(MessageRole.SYSTEM, chatGptProperties.systemMessage()));
			List<MessageEntity> messageEntities = MessageEntity.toMessageEntities(messages, conversation);
			conversation.setMessages(messageEntities);
		}
	}

	/**
	 * Adds a new message to the list of messages in the current conversation.
	 * @param content The content of the message.
	 * @param role The role associated with the message. This can be either 'USER',
	 * 'ASSISTANT', or 'SYSTEM'.
	 */
	public void addMessage(String content, MessageRole role) {
		messages.add(new ChatGptRequest.Message(role, content));
	}

	/**
	 * Searches for the system message associated with the given conversation in the
	 * database.
	 * @param conversation The {@link Conversation} object for which the system message is
	 * to be found.
	 * @return An {@link Optional} containing the {@link MessageEntity} representing the
	 * system message if found, otherwise an empty Optional.
	 */
	private Optional<MessageEntity> findSystemMessage(Conversation conversation) {
		return messageRepository.findByConversationAndRole(conversation, MessageRole.SYSTEM);
	}

	/**
	 * Updates the system message of the current conversation if it exists. If a system
	 * message is present in the conversation, its content is updated with the content of
	 * the input message. The updated system message is then saved into the database.
	 * @param systemMessage A {@link ChatGptRequest.Message} object containing the new
	 * system message.
	 */
	public void updateSystemMessage(ChatGptRequest.Message systemMessage) {
		if (conversationStored.get()) {
			findSystemMessage(conversation).ifPresent(messageEntity -> {
				messageEntity.setContent(systemMessage.content());
				updateCurrentConversation(messageEntity);
			});
		}
	}

	/**
	 * Updates a message in the current conversation by saving it to the database. This
	 * method should be used when a message's content or role has been modified.
	 * @param messageEntity The {@link MessageEntity} object to be updated.
	 */
	public void updateCurrentConversation(MessageEntity messageEntity) {
		messageRepository.save(messageEntity);
	}

	/**
	 * Creates a new message for the current conversation with the specified content and
	 * role and saves it to the database. This method should be used when adding new user
	 * or assistant messages to the conversation.
	 * @param content The content of the new message.
	 * @param role The role of the new message, either USER or ASSISTANT.
	 */
	public void updateCurrentConversation(String content, MessageRole role) {
		updateCurrentConversation(
				MessageEntity.toMessageEntity(new ChatGptRequest.Message(role, content), conversation));
	}

	/**
	 * Updates the title of the current conversation and saves the changes to the
	 * database. This method should be used when the title of the conversation needs to be
	 * modified.
	 * @param title The new title of the conversation.
	 */
	public void updateCurrentConversation(String title) {
		conversation.setTitle(title);
		conversationRepository.save(conversation);
	}

	/**
	 * Returns the list of messages associated with the Conversation.
	 * @return A list of MessageEntity objects.
	 */
	public LinkedList<ChatGptRequest.Message> getMessages() {
		return messages;
	}

	/**
	 * Retrieves the current state of conversation storage.
	 * @return An {@link AtomicBoolean} that represents whether the conversation is stored
	 * or not. Returns 'true' if the conversation has been stored, 'false' otherwise.
	 */
	public AtomicBoolean getConversationStored() {
		return conversationStored;
	}

}
