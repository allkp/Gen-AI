package com.allkp.SpringAIDemo.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama")
@CrossOrigin("*")
public class OllamaAI_Controller {
	
//	@Autowired
	private ChatClient chatClient;
	
	public OllamaAI_Controller(OllamaChatModel chatModel) {
		this.chatClient = ChatClient.create(chatModel);
	}
	
	@GetMapping("/{message}")
	public ResponseEntity<String> getAnswer(@PathVariable String message){
		ChatResponse chatResponse = chatClient
				.prompt(message)
				.call()
				.chatResponse();
		String response = chatResponse.getResult().getOutput().getText();
		System.out.println(chatResponse.getMetadata().getModel());
		
//		String response = chatClient.prompt(message).call().content();
		
		return ResponseEntity.ok(response);
	}

}
