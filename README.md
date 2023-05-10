# ChatGPT Shell
ChatGPT Shell is a command-line interface for interacting with the ChatGPT API, built with Java, Spring Boot, Spring Shell, and H2 Database. It allows users to send messages, receive AI-generated responses, and manage chat conversations.

## Features
- Customizable terminal prompt
- Spring Shell-based application for an enhanced command-line experience
- Store and manage chat conversations in a local H2 Database

## Prerequisites

- Java 17
- Maven (Optional)

## Configuration
Before running the application, make sure to set the required configuration values:

- `openai.api-key`: Set this to your OpenAI API key.
- `chat.gpt.model`: Set this to the desired GPT model (e.g., `gpt_3_5_turbo`).
- `chat.gpt.system-message`: Set this to the initial system message for the assistant (optional).
- `spring.datasource.url`: Set this to your H2 database URL (default is `jdbc:h2:file:${user.home}/.chatgptshell/data/chatgptdb`) (optional).
- `spring.datasource.username`: Set this to your H2 database username (default is `chatgptshell`) (optional).
- `spring.datasource.password`: Set this to your H2 database password (default is `password`) (optional).

You can set these values in the `application.properties` file or as environment variables. When setting the system message using an environment variable, make sure to enclose the message in double quotes, like this:

```bash
export CHAT_GPT_SYSTEM_MESSAGE="You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."
```


## How to Build and Run
Clone the repository:

```bash
git clone https://github.com/shahabkondri/chat-gpt-shell.git
cd chat-gpt-shell
```

To build the project, run:

```bash
./mvnw clean package
```

To run the application:

```bash
java -jar target/chat-gpt-shell-1.0.4.jar
```

## Native Build
Native builds offer faster startup times, lower memory footprint, and easier distribution. To create a native build, install GraalVM and use it as your JDK. Then, run the following command:

```bash
./mvnw clean -Pnative native:compile
```

This will produce a standalone executable optimized for your platform.
Once compiled, you can run the native executable:

```bash
./target/chat-gpt-shell
```

## Usage
After starting the application, you will see a terminal prompt:

```bash
:> 
```

Enter your message and press Enter to send it to the ChatGPT API. The AI-generated response will be displayed in the terminal.

```bash
:> tell me a joke.
```
To access other commands, start your input with a colon (':') character, for example `:help` to display the available commands or `:exit` to quit the application.

To set or update a system message that helps define the behavior of the ChatGPT assistant, use the `:system` command followed by the message. For example:

```bash
:> :system You are an AI trained to assist with programming questions.
```

The system message is added or updated at the beginning of the messages list to ensure its influence on the assistant's behavior.

To manage conversations, use the `:conversation` command with the following options:

- Without any options, it displays the current active conversation ID and title. 
- With the `--all` option, it displays all stored conversations.
- With the `--load` option followed by a `conversation ID`, it loads a previous conversation.
- With the `--new` option, it starts a new conversation. 
- With the `--delete` option followed by a `conversation ID`, it deletes a previous conversation.
- With the `--delete-all` option, it deletes all stored conversation.

For example:

```bash
:> :conversation
:> :conversation --all
:> :conversation --load 1234
:> :conversation --new
:> :conversation --delete 1234
:> :conversation --delete-all
```

## License
This project is licensed under the MIT License. See the [LICENCE](LICENCE.md) file for details.

## Contributing
Feel free to submit issues and enhancement requests on the GitHub issue tracker.