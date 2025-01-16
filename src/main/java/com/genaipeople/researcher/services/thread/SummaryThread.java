package com.genaipeople.researcher.services.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.genaipeople.openai.AssistantThreadMessage;
import com.genaipeople.openai.ExecutionThread;
import com.genaipeople.openai.Role;
import com.genaipeople.openai.ThreadRun;
import com.genaipeople.openai.assistant.message.ThreadMessageObject;
import com.genaipeople.openai.assistant.response.AssistantObject;
import com.genaipeople.openai.assistant.thread.ThreadObject;
import com.genaipeople.openai.assistant.thread.ThreadRequest;
import com.genaipeople.openai.assistant.thread.run.RunCreateRequest;
import com.genaipeople.openai.assistant.thread.run.RunObject;
import com.genaipeople.openai.assistant.thread.run.RunRequest;
import com.genaipeople.openai.assistant.thread.run.ToolChoice;
import com.genaipeople.openai.assistant.thread.run.ToolChoiceType;
import com.genaipeople.openai.message.Message;
import com.genaipeople.openai.tool.CodeInterpreter;
import com.genaipeople.openai.tool.Tool;
import com.genaipeople.researcher.services.feedback.FeedbackManager;
import com.genaipeople.researcher.services.message.MessageService;
import com.genaipeople.researcher.util.Config;
import com.genaipeople.researcher.util.PromptLoader;

public class SummaryThread extends Thread implements AssistantThread {
    private static String urlPrompt = "url.prompt";
    private ExecutionThread threadCreator;
    private ThreadObject threadObject;
    private ThreadRun threadRun;
    private RunObject runObject;

    public SummaryThread(){
        loadPrompts();
    }

    private void loadPrompts(){PromptLoader.loadPrompt("summary.prompt");
        urlPrompt = PromptLoader.loadPrompt("url.prompt");
    }

    public void initialize(String linkUrl) throws InterruptedException, ExecutionException{
        threadCreator = new ExecutionThread(Config.getInstance().getValue("openai.api.key"));
        threadObject = threadCreator.create(createExecutionThreadRequest(linkUrl)).get();
    }

    private ThreadRequest createExecutionThreadRequest(String linkUrl) {
        ThreadRequest threadRequest = new ThreadRequest();
        List<Message> messages = new ArrayList<>();
        messages.add(MessageService.createTextMessage(urlPrompt, Role.user));
        messages.add(MessageService.createTextMessage(linkUrl, Role.user));
        threadRequest.setMessages(messages);
        return threadRequest;
    }

    public void handleFeedback(StringBuffer threadOutput) throws IOException {
        FeedbackManager.seekFeedback(threadOutput, this);   
    }

    public void executeThread(AssistantObject assistant,  String linkUrl) throws InterruptedException, ExecutionException{
        threadRun = new ThreadRun(Config.getInstance().getValue("openai.api.key"), 
            threadObject.getId());
        runObject = threadRun.create(createRunRequest(assistant)).get();
        RunObject runResponse = threadRun.run(createRunRequest(assistant.getId())).get();
        System.out.println(runResponse.getStatus());
    }
    
    private RunCreateRequest createRunRequest(AssistantObject assistant) {
        RunCreateRequest runRequest = new RunCreateRequest();
        runRequest.setAssistantId(assistant.getId());
        runRequest.setTools(Arrays.asList(createTool("code_interpreter")));
        ToolChoice toolChoice = new ToolChoice();
        toolChoice.setType(ToolChoiceType.auto);
        toolChoice.setTool(null);
        runRequest.setToolChoice(toolChoice);
        return runRequest;
    }

    private RunRequest createRunRequest(String assistantId) {
        RunRequest runRequest = new RunRequest(assistantId);
        runRequest.setThread(createRunThread());
        return runRequest;
    }

    private ThreadRequest createRunThread() {
        ThreadRequest threadRequest = new ThreadRequest();
        return threadRequest;
    }

     private Tool createTool(String type) {
        switch (type) {
            case "code_interpreter":
                return new CodeInterpreter();
            default:
                throw new IllegalArgumentException("Invalid tool type: " + type);
        }
    }

    public RunObject getRunObject() throws InterruptedException, ExecutionException {
        ThreadRun threadRun = new ThreadRun(Config.getInstance().getValue("openai.api.key"), 
            threadObject.getId());
        runObject = threadRun.retrieve(runObject.getId()).get();
        return runObject;
        
    }

    public ThreadMessageObject getMessage(String messageId) throws InterruptedException, ExecutionException {
        AssistantThreadMessage message = new AssistantThreadMessage(Config.getInstance().getValue("openai.api.key"));
        ThreadMessageObject messageObject = message.retrieve(threadObject.getId(), messageId).get();
        return messageObject;
    }
}
