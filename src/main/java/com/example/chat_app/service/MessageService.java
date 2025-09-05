package com.example.chat_app.service;

import com.example.chat_app.model.Message;
import com.example.chat_app.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public void saveMessage(Message msg) {
        repo.save(msg);
    }

    public List<Message> getLastMessages(int limit) {
        List<Message> all = repo.findAll();
        return all.stream()
                  .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                  .skip(Math.max(0, all.size() - limit))
                  .toList();
    }
}
