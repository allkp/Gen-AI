package com.allkp.SpringAIDemo.controllers;

//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
@CrossOrigin("*")
public class OpenAI_Controller {
	
//	@Autowired
//	private OpenAiChatModel chatModel;
	
	@GetMapping("/{message}")
	public ResponseEntity<String> getAnswer(@PathVariable String message){
//		String response = chatModel.call(message);
//		return ResponseEntity.ok(response);

		return ResponseEntity.ok("Hello Open-AI...!!!");
	}

}
