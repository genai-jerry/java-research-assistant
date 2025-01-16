package com.genaipeople.researcher.util;

import java.io.IOException;
import java.io.InputStream;

public class PromptLoader {

    public static String loadPrompt(String promptFileName) {
        try (InputStream inputStream = PromptLoader.class.getResourceAsStream("/prompts/" + promptFileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Prompt file not found: " + promptFileName);
            }
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt file: " + promptFileName, e);
        }
    }
    
}
