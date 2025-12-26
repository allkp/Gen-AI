package com.allkp.SpringAIDemo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anthropic")
@CrossOrigin("*")
public class AnthropicAI_Controller {
	
	
	
	@GetMapping("/{message}")
	public ResponseEntity<String> getAnswer(@PathVariable String message){
//		String response = llm.call(message);
		return ResponseEntity.ok("Hello, Anthropic-AI..!!!");
	}

}
