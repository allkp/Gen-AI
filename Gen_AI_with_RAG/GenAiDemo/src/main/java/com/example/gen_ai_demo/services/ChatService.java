package com.example.gen_ai_demo.services;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String ask(String context, String question) {

        return chatClient.prompt()
                .user("""
                        Answer using ONLY the context below.
                        
                        Context:
                        %s
                        
                        Question:
                        %s
                        """.formatted(context, question))
                .call()
                .content();

    }

}
