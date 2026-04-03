package com.jarvis.ai.api;

import com.jarvis.ai.data.Message;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AIClient {
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    
    private String apiKey;
    private String model;
    private final OkHttpClient client;
    
    public AIClient(String apiKey) {
        this.apiKey = apiKey;
        this.model = DEFAULT_MODEL;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public void sendMessage(String userMessage, List<Message> history, AICallback callback) {
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onError("API key not configured");
            return;
        }
        
        // Build messages array including history
        JSONArray messages = new JSONArray();
        
        // System prompt
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are Jarvis, a helpful, witty AI assistant. Be concise, friendly, and helpful. Keep responses under 200 words unless more detail is needed.");
        messages.put(systemMsg);
        
        // Add conversation history (last 10 messages to stay within limits)
        int startIndex = Math.max(0, history.size() - 10);
        for (int i = startIndex; i < history.size(); i++) {
            Message msg = history.get(i);
            JSONObject msgObj = new JSONObject();
            msgObj.put("role", msg.getRole());
            msgObj.put("content", msg.getContent());
            messages.put(msgObj);
        }
        
        // Add current user message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.put(userMsg);
        
        // Build request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);
        
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                
                if (!response.isSuccessful()) {
                    callback.onError("API error: " + response.code() + " - " + responseBody);
                    return;
                }
                
                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray choices = json.getJSONArray("choices");
                    if (choices.length() > 0) {
                        String aiResponse = choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        callback.onSuccess(aiResponse);
                    } else {
                        callback.onError("No response from AI");
                    }
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public interface AICallback {
        void onSuccess(String response);
        void onError(String error);
    }
}