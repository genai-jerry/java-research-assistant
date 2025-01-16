package com.genaipeople.researcher.services.assistant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.genaipeople.openai.Assistant;
import com.genaipeople.openai.assistant.AssistantRequest;
import com.genaipeople.openai.assistant.response.AssistantObject;
import com.genaipeople.openai.tool.CodeInterpreter;
import com.genaipeople.researcher.util.Config;

public class AssistantService {
    private static AssistantService assistantService;
    private Map<String, AssistantObject> assistants = new HashMap<>();

    private AssistantService() {
    }

    public static AssistantService getInstance() {
        if (assistantService == null) {
            assistantService = new AssistantService();
        }
        return assistantService;
    }

    public AssistantObject loadOrCreateAssistant(String assistantId, 
        String assistantName, String instructions) throws InterruptedException, ExecutionException {
        Assistant assistantCreator = new Assistant(Config.getInstance().getValue("openai.api.key"));
        AssistantObject assistant = null;
        try {
            assistant = assistantCreator.retrieve(assistantId).get();
        } catch (Exception e) {
            System.out.println("Assistant not found, creating new assistant");
        }
        if (assistant == null) {
            if (assistantName == null) {
                return null;
            }
            assistant = assistantCreator.create(createAssistantRequest(assistantName, instructions)).get();
            assistants.put(assistant.getId(), assistant);   
        }
        return assistant;
    }

    private AssistantRequest createAssistantRequest(String assistantName, String instructions) {
        AssistantRequest assistantRequest = new AssistantRequest();
        assistantRequest.setModel(Config.getInstance().getValue("openai.model"));
        assistantRequest.setName(assistantName);
        assistantRequest.setTools(List.of(new CodeInterpreter()));
        assistantRequest.setInstructions(instructions);
        return assistantRequest;
    }

    public AssistantObject deleteAssistant(String assistantId) throws InterruptedException, ExecutionException {
        Assistant assistantCreator = new Assistant(Config.getInstance().getValue("openai.api.key"));
        AssistantObject assistant = assistantCreator.delete(assistantId).get();
        assistants.remove(assistantId);
        return assistant;
    }

    public List<AssistantObject> listAssistants() throws InterruptedException, ExecutionException {
        Assistant assistantCreator = new Assistant(Config.getInstance().getValue("openai.api.key"));
        return assistantCreator.list(null, null, null, null).get();
    }
}
