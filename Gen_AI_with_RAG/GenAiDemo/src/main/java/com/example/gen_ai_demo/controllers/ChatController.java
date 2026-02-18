package com.example.gen_ai_demo.controllers;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping("/api/chat")
    @SuppressWarnings("unchecked")
    public String chat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message, HttpSession session) {
        // Retrieve conversation history from session, or create a new one if none exists
        List<Message> conversationHistory = (List<Message>) session.getAttribute("conversationHistory");
        if (conversationHistory == null) {
            conversationHistory = new ArrayList<>();
        }

        // Add user's message to the history
        conversationHistory.add(new UserMessage(message));

        // Call the AI model and get the response content directly
        String aiMessageContent = chatClient.prompt(new Prompt(conversationHistory)).call().content();

        // Add the AI's response to the history
        conversationHistory.add(new AssistantMessage(aiMessageContent));
        session.setAttribute("conversationHistory", conversationHistory);

        // Return the AI's latest response
        return aiMessageContent;
    }
}
