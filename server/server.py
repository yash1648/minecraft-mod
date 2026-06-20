import os
from pathlib import Path
import requests
from dotenv import load_dotenv
from flask import Flask, request, jsonify

# Load .env from the same folder as this script
load_dotenv(Path(__file__).parent / ".env")

app = Flask(__name__)

# Server config
HOST = os.environ.get("HOST", "127.0.0.1")
PORT = int(os.environ.get("PORT", 5000))

# NVIDIA NIMs API configuration — all overridable via .env
NVIDIA_API_KEY = os.environ.get("NVIDIA_API_KEY")
NVIDIA_BASE_URL = os.environ.get("NVIDIA_BASE_URL", "https://integrate.api.nvidia.com/v1/chat/completions")
NVIDIA_MODEL = os.environ.get("NVIDIA_MODEL", "meta/llama-3.1-8b-instruct")

# AI behavior
SYSTEM_PROMPT = os.environ.get("SYSTEM_PROMPT", "You are a friendly villager NPC in Minecraft. Always respond in 1 short sentence like a villager would. Talk about trading, building, village life, or adventures. Use simple words. Never write paragraphs or lists.")
AI_TEMPERATURE = float(os.environ.get("AI_TEMPERATURE", 0.5))
AI_MAX_TOKENS = int(os.environ.get("AI_MAX_TOKENS", 60))
AI_TIMEOUT = int(os.environ.get("AI_TIMEOUT", 30))

@app.route("/chat", methods=["POST"])
def chat():
    msg = request.json["message"]

    if not NVIDIA_API_KEY:
        return jsonify({"reply": "ERROR: NVIDIA_API_KEY environment variable not set"}), 500

    headers = {
        "Authorization": f"Bearer {NVIDIA_API_KEY}",
        "Content-Type": "application/json"
    }


    payload = {
        "model": NVIDIA_MODEL,
        "messages": [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": msg}
        ],
        "temperature": AI_TEMPERATURE,
        "max_tokens": AI_MAX_TOKENS,
        "stream": False
    }

    try:
        r = requests.post(NVIDIA_BASE_URL, headers=headers, json=payload, timeout=AI_TIMEOUT)
        r.raise_for_status()
        reply = r.json()["choices"][0]["message"]["content"]
    except requests.exceptions.Timeout:
        reply = "AI NPC is thinking too long... try again."
    except requests.exceptions.RequestException as e:
        reply = f"AI ERROR: {e}"
    except (KeyError, IndexError) as e:
        reply = f"AI ERROR: unexpected response format - {e}"

    return jsonify({"reply": reply})

if __name__ == "__main__":
    app.run(host=HOST, port=PORT)