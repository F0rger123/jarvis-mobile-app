package com.jarvis.ai.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.jarvis.ai.R;
import com.jarvis.ai.api.AIClient;
import com.jarvis.ai.data.Message;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private EditText inputField;
    private TextView responseView;
    private AIClient aiClient;
    private List<Message> conversationHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inputField = findViewById(R.id.inputField);
        responseView = findViewById(R.id.responseView);
        
        // Initialize AI client with API key from environment/config
        String apiKey = getApiKey();
        aiClient = new AIClient(apiKey);
        
        // Set welcome message
        responseView.setText("🤖 Jarvis at your service!\n\nType a message and I'll respond. Your API key is configured.\n\nTry: \"Hello, how are you?\"");
    }
    
    private String getApiKey() {
        // Try environment variable first, then check SharedPreferences
        String envKey = System.getenv("OPENAI_API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        
        // Check shared preferences for stored key
        android.content.SharedPreferences prefs = getSharedPreferences("jarvis_prefs", MODE_PRIVATE);
        return prefs.getString("api_key", "");
    }
    
    public void sendMessage(View view) {
        String userInput = inputField.getText().toString().trim();
        if (userInput.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if API key is configured
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Please configure your API key in settings", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Add user message to history
        conversationHistory.add(new Message("user", userInput));
        
        // Show loading
        responseView.setText("🤖 Thinking...\n");
        inputField.setText("");
        
        // Send to AI
        aiClient.sendMessage(userInput, conversationHistory, new AIClient.AICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    conversationHistory.add(new Message("assistant", response));
                    responseView.setText("🤖 Jarvis:\n\n" + response);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    responseView.setText("❌ Error: " + error + "\n\nCheck your API key and try again.");
                });
            }
        });
    }
    
    public void clearChat(View view) {
        conversationHistory.clear();
        responseView.setText("🤖 Chat cleared. Start fresh!");
    }
    
    public void openSettings(View view) {
        // Show a simple dialog to set API key
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Configure API Key");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        EditText apiKeyInput = dialogView.findViewById(R.id.apiKeyInput);
        
        // Pre-fill current key
        String currentKey = getApiKey();
        if (!currentKey.isEmpty()) {
            apiKeyInput.setText(currentKey);
        }
        
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newKey = apiKeyInput.getText().toString().trim();
            if (!newKey.isEmpty()) {
                getSharedPreferences("jarvis_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("api_key", newKey)
                    .apply();
                Toast.makeText(this, "API key saved!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}