package com.genaipeople.researcher.services.feedback;

import java.util.StringTokenizer;

import com.genaipeople.researcher.services.thread.AssistantThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FeedbackManager {

    private static BufferedReader reader;

    public FeedbackManager() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void seekFeedback(StringBuffer threadOutput, AssistantThread assistantThread) throws IOException {
       // Print buffer contents line by line
       synchronized(reader) {
            StringTokenizer tokenizer = new StringTokenizer(threadOutput.toString(), "\n");
            while (tokenizer.hasMoreTokens()) {
                String line = tokenizer.nextToken();
                System.out.println(line);
            }

            // Clear the buffer after printing
            threadOutput.setLength(0);

            // Get user input
            System.out.print("\nEnter your response: ");
            String userInput = reader.readLine();
            StringBuffer userInputBuffer = new StringBuffer(userInput);
            assistantThread.handleFeedback(userInputBuffer);
        }
    }
}

