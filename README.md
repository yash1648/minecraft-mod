# AINPC — AI-Powered NPC Mod for Minecraft

An AI-driven villager NPC mod for **Minecraft 1.21.11 / Forge 61.1.0**. Talk to NPCs via chat — they respond using an AI backend powered by NVIDIA NIMs.

## Features

- **Custom NPCs** — spawn AI-powered villagers that walk around and look at you
- **Chat with NPCs** — right-click to start a conversation, then type in chat
- **AI Backend** — uses NVIDIA NIMs API (or any OpenAI-compatible endpoint)
- **Fully Configurable** — all settings via config file or `.env`
- **Emotes** — NPCs display heart/happy/enchant particles based on response tone

## Prerequisites

| Requirement | Version |
|---|---|
| Minecraft | 1.21.11 |
| Forge | 61.1.0 |
| Java | 21+ |
| Python | 3.10+ (for AI server) |

## Quick Start

### 1. Build the Mod

```bash
./gradlew build
cp build/libs/ainpc-1.0.0.jar ~/.minecraft/mods/
```

### 2. Set Up the AI Server

```bash
cd server
cp .env.example .env
# Edit .env — paste your NVIDIA_API_KEY
pip install -r requirements.txt
python server.py
```

### 3. Launch Minecraft

Select the `forge-61.1.0` profile and play.

## In-Game Usage

| Action | How |
|---|---|
| Spawn NPC | `/spawnnpc` (OP level 2+ by default) |
| Talk to NPC | Right-click the NPC → type in chat |
| NPC behavior | Walks around randomly, looks at nearby players |

## Configuration

### Mod Config (`config/ainpc.toml`)

Created automatically after first launch. Editable while Minecraft is closed.

| Option | Default | Description |
|---|---|---|
| `aiServerUrl` | `http://127.0.0.1:5000/chat` | AI backend API URL |
| `npcName` | `AI NPC` | Name displayed above NPCs |
| `npcOpLevel` | `2` | Required OP level for `/spawnnpc` |

### Server Config (`server/.env`)

| Variable | Default | Description |
|---|---|---|
| `NVIDIA_API_KEY` | — | NVIDIA NIMs API key (**required**) |
| `NVIDIA_BASE_URL` | `https://integrate.api.nvidia.com/v1/chat/completions` | AI API endpoint |
| `NVIDIA_MODEL` | `meta/llama-3.1-8b-instruct` | AI model |
| `SYSTEM_PROMPT` | villager NPC prompt | AI personality |
| `AI_TEMPERATURE` | `0.5` | Response creativity |
| `AI_MAX_TOKENS` | `60` | Max response length |
| `HOST` / `PORT` | `127.0.0.1:5000` | Server bind address |

## Project Structure

```
minecraft-mod/
├── src/main/java/com/aman/ainpc/
│   ├── AINPC.java              # Mod entry point
│   ├── AINPCEntity.java        # NPC entity class
│   ├── ChatListener.java       # Chat → AI bridge
│   ├── Config.java             # Forge config spec
│   ├── NPCCommand.java         # /spawnnpc command
│   └── NPCInteractionHandler.java  # Right-click handler
├── server/
│   ├── server.py               # AI backend (Flask)
│   ├── .env.example            # Environment template
│   └── requirements.txt
├── build.gradle                # ForgeGradle 7 build
├── settings.gradle
└── gradle.properties
```

## Building from Source

```bash
./gradlew build
```

Output: `build/libs/ainpc-1.0.0.jar`
