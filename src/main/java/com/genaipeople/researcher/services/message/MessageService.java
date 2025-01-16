package com.genaipeople.researcher.services.message;

import com.genaipeople.openai.Role;
import com.genaipeople.openai.message.Message;

public class MessageService {
    public static Message createTextMessage(String content, Role role) {
        return new Message(content, role);
    }

    public static Message createImageMessage(String content, Role role, String imageUrl) {
        return new Message(content, imageUrl, role);
    }
}

