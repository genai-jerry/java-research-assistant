package com.genaipeople.researcher.aiclient;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.genaipeople.openai.ThreadRunStep;
import com.genaipeople.openai.assistant.message.ThreadMessageContent;
import com.genaipeople.openai.assistant.message.ThreadMessageObject;
import com.genaipeople.openai.assistant.message.ThreadTextMessageContent;
import com.genaipeople.openai.assistant.response.AssistantObject;
import com.genaipeople.openai.assistant.thread.run.MessageCreationStep;
import com.genaipeople.openai.assistant.thread.run.RunObject;
import com.genaipeople.openai.assistant.thread.run.RunStepObject;
import com.genaipeople.researcher.services.assistant.AssistantService;
import com.genaipeople.researcher.services.thread.SummaryThread;
import com.genaipeople.researcher.util.Config;
import com.genaipeople.researcher.util.PromptLoader;

public class AIClient {
    private static SummaryThread summaryThread;
    public static void main(String[] args) {
        String input = System.console().readLine(getInput());
        while (!input.equals("5")) {
            if (input.equals("1")) {
                handleNewAssistantCreation();
            } else if (input.equals("2")) {
                handleDeleteAssistant();
            } else if (input.equals("3")) {
                runThread();
            } else if (input.equals("4")) {
                handleRun();
            }
            input = System.console().readLine(getInput());
        }
    }

    private static String getInput() {
        return "To create enter 1, " +
            "To delete enter 2, " +
            "To run enter 3, " +
            "To handle run steps enter 4, " +
            "To quit enter 5: ";
    }

    private static void handleDeleteAssistant(){
        try {
            System.out.println("Provide the assistant id to delete: ");
            String assistantId = System.console().readLine();
            AssistantService.getInstance().deleteAssistant(assistantId);
            System.out.println("Assistant deleted");
            return;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void handleNewAssistantCreation(){
        try {
            String assistantName = System.console().readLine("Enter assistant name: ");
            AssistantObject assistant = createAssistant(assistantName, 
                Config.getInstance().getValue("openai.assistantid"));
            System.out.println("Assistant created with id: " + assistant.getId());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void runThread() {
        try {
            String textToSummarize = System.console().readLine("Enter text to summarize: ");
            
            String assistantId = Config.getInstance().getValue("openai.assistantid");
            System.out.println("Executing for Assistant id: " + assistantId);
            if (assistantId == null) {
                System.out.println("No assistant id found, please create an assistant first");
                return;
            }
            AssistantObject assistant = AssistantService.getInstance().loadOrCreateAssistant(assistantId, 
                null, null);
            if (assistant == null) {
                System.out.println("No assistant found, please create an assistant first");
                return;
            }
            executeThread(assistant, textToSummarize);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    private static AssistantObject createAssistant(String assistantName, String assistantId) 
        throws InterruptedException, ExecutionException {
        AssistantObject assistant = AssistantService.getInstance().loadOrCreateAssistant(assistantId, assistantName, 
            PromptLoader.loadPrompt("summary.prompt"));
        if (assistant == null) {
            System.out.println("No assistant found, please create an assistant first");
            return null;
        }
        Config.getInstance().setValue("openai.assistantid", assistant.getId());
        return assistant;
    }

    private static void createThread(String linkUrl) throws InterruptedException, ExecutionException {
        summaryThread = new SummaryThread();
        summaryThread.initialize(linkUrl);
    }

    private static void executeThread(AssistantObject assistant, String linkUrl) 
        throws InterruptedException, ExecutionException {
        createThread(linkUrl);
        System.out.println("Thread created with id: " + summaryThread.getId());
        summaryThread.executeThread(assistant, linkUrl);
    }
    private static void handleRun()  {
        try {
            System.out.println("Getting run object");
            RunObject runObject = summaryThread.getRunObject();
            System.out.println("Run object: " + runObject.getStatus());
            boolean isCompleted = false;
            while (!isCompleted) {
                switch (runObject.getStatus()) {
                    case "queued":
                        System.out.println("Queued");
                        Thread.sleep(1000);
                        break;
                    case "in_progress":
                        System.out.println("In progress");
                        Thread.sleep(1000);
                        break;
                    case "failed":
                        System.out.println("Failed");
                        throw new RuntimeException("Run failed");
                    case "completed":
                        System.out.println("Completed");
                        handleRunStep(runObject.getThreadId(), runObject.getId());
                        isCompleted = true;
                        break;
                    default:
                        System.out.println("Unknown status: " + runObject.getStatus());
                        break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void handleRunStep(String threadId, String runId) throws InterruptedException, ExecutionException {
        ThreadRunStep step = new ThreadRunStep(Config.getInstance().getValue("openai.api.key"), 
            threadId, runId);
        List<RunStepObject> list = step.list().get();
        handleRunSteps(list);
    }
    private static void handleRunSteps(List<RunStepObject> list) throws InterruptedException, ExecutionException {
        System.out.println("Handling run steps");
        for (RunStepObject step : list) {
            if (step.getStatus().equals("completed")) {
                if (step.getStepDetails().getType().equals("message_creation")) {
                    MessageCreationStep messageCreationStep = (MessageCreationStep) step.getStepDetails();
                    System.out.println("Messaget Id is " + messageCreationStep.getMessageId());
                    ThreadMessageObject messageObject = summaryThread.getMessage(messageCreationStep.getMessageId());
                    for (ThreadMessageContent content : messageObject.getContent()) {
                        if (content.getType().equals("text")) {
                            System.out.println("Message is " + 
                                ((ThreadTextMessageContent)content).getText());
                        }
                    }
                }
            }
        }
    }
}

