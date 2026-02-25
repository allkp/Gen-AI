package com.example.gen_ai_demo.controllers;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    // Normal API
    @GetMapping("/api/chat")
    @SuppressWarnings("unchecked")
    public String chat(
            @RequestParam(defaultValue = "Tell me a joke") String message,
            HttpSession session) {

        List<Message> history =
                (List<Message>) session.getAttribute("conversationHistory");

        if (history == null) {
            history = new ArrayList<>();
        }

        history.add(new UserMessage(message));

        String response = chatClient
                .prompt(new Prompt(history))
                .call()
                .content();

        history.add(new AssistantMessage(response));
        session.setAttribute("conversationHistory", history);

        return response;
    }

    // Streaming API
//    @GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @SuppressWarnings("unchecked")
//    public Flux<String> streamChat(
//            @RequestParam String message,
//            HttpSession session) {
//
//        List<Message> history =
//                (List<Message>) session.getAttribute("conversationHistory");
//
//        if (history == null) {
//            history = new ArrayList<>();
//            history.add(new SystemMessage(
//                    """
//                            You are a senior software engineer.
//
//                            STRICT CODE FORMATTING RULES:
//
//                            1. Always output properly formatted code.
//                            2. Always preserve indentation.
//                            3. Always use real newline characters.
//                            4. Never compress code into a single line.
//                            5. Always wrap code inside triple backticks.
//                            6. Always put a newline after ```language.
//                            7. Always indent blocks correctly.
//                            8. This applies to ALL languages (Java, Python, JS, C++, Go, etc.)
//
//                            Example format:
//
//                            ```python
//                            def test():
//                                print("hello")
//                            ```
//                    """
//            ));
//        }
//
//        history.add(new UserMessage(message));
//
//        StringBuilder fullResponse = new StringBuilder();
//        List<Message> finalHistory = history;
//
//        return chatClient
//                .prompt(new Prompt(history))
//                .stream()
////                .chatResponse()
//                .content()
////                .map(resp -> {
////                    String content = resp.getResult().getOutput().getText();
////                    fullResponse.append(content);
////                    return content;
////                })
//                .map(token -> {
//                    fullResponse.append(token);
//                    return token;   // DO NOT MODIFY
//                })
//                .doOnComplete(() -> {
//                    finalHistory.add(new AssistantMessage(fullResponse.toString()));
//                    session.setAttribute("conversationHistory", finalHistory);
//                });
//    }

@GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@SuppressWarnings("unchecked")
public Flux<String> streamChat(
        @RequestParam String message,
        HttpSession session) {

    List<Message> history =
            (List<Message>) session.getAttribute("conversationHistory");

    if (history == null) {
        history = new ArrayList<>();
        history.add(new SystemMessage("""
                 You are a code assistant. Follow these rules STRICTLY, no exceptions:
                
               RULE 1: Every code block MUST start exactly like this (with a real newline after the language):
           ```java
               public class Example {

               RULE 2: NEVER write ```java followed immediately by code on the same line.
               RULE 3: Use real newlines between every statement and every line of code.
               RULE 4: Indent with 4 spaces. Never compress code on to one line.
               RULE 5: Every { opens a new indented block on a new line.
           ```
        """));
    }

    history.add(new UserMessage(message));

    StringBuilder fullResponse = new StringBuilder();
    List<Message> finalHistory = history;

    return chatClient
            .prompt(new Prompt(history))
            .stream()
            .chatResponse()   // 🔥 IMPORTANT
            .map(resp -> {
                String content = resp.getResult().getOutput().getText();
                fullResponse.append(content);
                return content;
            })
            .doOnComplete(() -> {
                finalHistory.add(new AssistantMessage(fullResponse.toString()));
                session.setAttribute("conversationHistory", finalHistory);
            });
}

    private boolean needsSpace(StringBuilder sb, String token) {
        if (sb.isEmpty()) return false;

        char lastChar = sb.charAt(sb.length() - 1);

        // don't add space before punctuation
        if (token.matches("[.,!?:;]")) return false;

        return Character.isLetterOrDigit(lastChar)
                || ".!?".indexOf(lastChar) >= 0;
    }

    private boolean shouldAddSpace(StringBuilder sb, String token) {
        if (sb.isEmpty()) return false;

        char lastChar = sb.charAt(sb.length() - 1);

        // If last char is whitespace or newline → no space needed
        if (Character.isWhitespace(lastChar)) return false;

        // If token starts with punctuation → no space
        if (token.matches("^[.,!?:;)}\\]].*")) return false;

        // If token is newline
        if (token.startsWith("\n")) return false;

        return true;
    }
}