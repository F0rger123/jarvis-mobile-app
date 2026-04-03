# Jarvis AI Assistant 🦞🤖

Android app that connects to OpenAI (or compatible) API for AI conversations.

## Features

- **AI Chat** - Send messages to ChatGPT and get responses
- **Conversation History** - Remembers recent messages
- **Configurable API Key** - Set your key in the app or via environment variable
- **Clean UI** - Simple dark theme interface

## Requirements

- Android 7.0 (API 24) or higher
- OpenAI API key (get at [platform.openai.com](https://platform.openai.com))

## Download APK

### Latest Build
Download from GitHub Actions artifacts:
1. Go to https://github.com/F0rger123/jarvis-mobile-app/actions
2. Click the latest workflow run
3. Download `app-debug` artifact

### Build from Source
```bash
# Clone
git clone https://github.com/F0rger123/jarvis-mobile-app.git
cd jarvis-mobile-app

# Build
chmod +x gradlew
./gradlew assembleDebug

# APK at: app/build/outputs/apk/debug/app-debug.apk
```

## How to Install

1. **Download APK** to your Android device
2. Open the APK file (may need to enable "Install from unknown sources" in Settings)
3. Tap Install

## How to Use

### First Time Setup
1. Open the app
2. Tap the ⚙️ gear icon in the top right
3. Enter your OpenAI API key (starts with `sk-`)
4. Tap Save

### Using the App
1. Type a message in the text box
2. Tap the ➤ button to send
3. Wait for Jarvis's response

### Get Free API Credits
- New OpenAI accounts get $5-18 free credits
- Go to https://platform.openai.com/settings/billing/overview

## Troubleshooting

### "API key not configured"
- Tap ⚙️ and enter your key
- Keys start with `sk-`

### "Network error"
- Check your internet connection
- Ensure API key is valid

### "API error: 401"
- Your API key is invalid or expired
- Get a new key at platform.openai.com

### "API error: 429"
- You've hit rate limits
- Wait a minute and try again

### "Parse error"
- API response was unexpected
- Check your key and try again

## Environment Variables

The app checks for `OPENAI_API_KEY` from:
1. Environment variable (for CI/CD builds)
2. SharedPreferences (set via in-app settings)

## Tech Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **HTTP Client**: OkHttp 4.12.0
- **JSON**: org.json

## GitHub Actions

Builds automatically on push to main:
- Runs `./gradlew assembleDebug`
- Uploads APK as artifact

## License

MIT