# AI Backend Server

Flask server that bridges Minecraft mod chat with the NVIDIA NIMs API.

## Setup

```bash
cd server
cp .env.example .env
pip install -r requirements.txt
```

## Configuration

Edit `.env` with your settings:

```env
# Required
NVIDIA_API_KEY=nvapi-your-key-here

# Optional (defaults shown)
NVIDIA_MODEL=meta/llama-3.1-8b-instruct
HOST=127.0.0.1
PORT=5000
```

## Run

```bash
python server.py
```

## API

### `POST /chat`

**Request:**
```json
{"message": "hello there"}
```

**Response:**
```json
{"reply": "Greetings, traveler!"}
```

## Test

```bash
curl -s http://localhost:5000/chat -H "Content-Type: application/json" -d '{"message":"hello"}'
```
