package com.example.chat_app.controller;

import com.example.chat_app.model.Message;
import com.example.chat_app.service.MessageService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class ChatController {
    private final MessageService service;

    public ChatController(MessageService service) {
        this.service = service;
    }

    @GetMapping("/chat")
    public String chatPage(Model model) {
        model.addAttribute("messages", service.getLastMessages(20));
        return "chat";
    }

    @PostMapping("/chat")
    public String sendMessage(@RequestParam String sender,
                              @RequestParam String receiver,
                              @RequestParam String message) {
        service.saveMessage(new Message(sender, receiver, message));
        return "redirect:/chat";
    }

    @GetMapping("/chat-data")
    @ResponseBody
    public List<Message> getMessages() {
        List<Message> messages = service.getLastMessages(20);
        return messages;
    }
}
