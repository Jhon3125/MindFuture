package com.example.mindfuture.controller;

import com.example.mindfuture.services.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping("/chat")
    @ResponseBody
    public String handleChat(@RequestParam("message") String message) {
        try {
            return chatGPTService.getResponse(message);
        } catch (Exception e) {
            return "Ocurrió un error al comunicarse con la IA.";
        }
    }
}