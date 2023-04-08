# ChatGPT Shell
ChatGPT Shell is a command-line interface for interacting with the ChatGPT API, built with Java, Spring Boot, and Spring Shell. It is a simple and easy-to-use tool that allows users to send messages and receive AI-generated responses.

## Features
- Customizable terminal prompt
- Spring Shell-based application for an enhanced command-line experience

## Prerequisites

- Java 17
- Maven (Optional)

## Configuration
Before running the application, make sure to set the required configuration values:

- `openai.api-key`: Set this to your OpenAI API key.
- `chat.gpt.model`: Set this to the desired GPT model (e.g., `gpt_3_5_turbo`).
- `chat.gpt.system-message`: Set this to the initial system message for the assistant (optional).

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
java -jar target/chat-gpt-shell-1.0.3.jar
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

Enter your message and press Enter to send it to the ChatGPT API. The AI-generated response will be displayed in the terminal. To access other commands, start your input with a colon (':') character, for example :help to display the available commands or :exit to quit the application.

To set or update a system message that helps define the behavior of the ChatGPT assistant, use the `:system` command followed by the message. For example:

```bash
:> :system You are an AI trained to assist with programming questions.
```

The system message is added or updated at the beginning of the messages list to ensure its influence on the assistant's behavior.

```bash
:> tell me a joke.
```

## License
This project is licensed under the MIT License. See the [LICENCE](LICENCE.md) file for details.

## Contributing
Feel free to submit issues and enhancement requests on the GitHub issue tracker.