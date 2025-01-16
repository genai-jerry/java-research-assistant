package com.genaipeople.researcher.services.thread;

import java.io.IOException;

public interface AssistantThread {
   public void handleFeedback(StringBuffer threadOutput) throws IOException;
}
