# AI Companion Builder NPC — V1 Implementation Roadmap

A **single Minecraft NPC** that can:

- talk through your AI backend
- understand commands
- follow / stay / protect / go home
- remember the player
- build **3 predefined structures**: small house, wheat farm, wall

---

## 0) Final V1 Scope

### Conversation

- NPC can chat using your AI backend
- NPC knows player name, nearby mobs, time, biome, current task

### Commands

- `follow me`
- `stay here`
- `come here`
- `protect me`
- `go home`
- `build a small house here`
- `build a wheat farm here`
- `build a wall here`

### Memory

- last 10–20 chat messages
- trust score with player
- last 10 important events

### Building

- only **3 predefined templates**
- no freeform AI-generated builds yet
- creative-style building first (no survival resource gathering)

---

## 1) High-Level Architecture

### A. Minecraft Mod Side

- NPC entity
- pathfinding
- combat/follow behavior
- scanning world state
- sending AI requests
- executing returned actions
- block placement during builds
- saving NPC state

### B. AI Backend

- building prompt/context
- calling LLM
- returning **strict JSON**
- extracting intents (`FOLLOW`, `BUILD_HOUSE`, etc.)

### C. Memory Layer

- conversation history
- relationship/trust
- important events
- current active task

### D. Builder System

- selecting a template
- validating build site
- generating placement tasks
- making NPC place blocks step-by-step

---

## 2) Package Structure

```
com.aman.ainpc
├── entity
│   ├── AiNpcEntity.java
│   ├── AiNpcGoals.java
│   └── NpcAnimations.java
│
├── brain
│   ├── NpcBrain.java
│   ├── NpcState.java
│   ├── NpcTaskType.java
│   └── NpcActionExecutor.java
│
├── ai
│   ├── AiClient.java
│   ├── AiRequestBuilder.java
│   ├── AiResponseParser.java
│   └── AiResponseValidator.java
│
├── ai/model
│   ├── AiNpcRequest.java
│   ├── AiNpcResponse.java
│   ├── AiAction.java
│   └── AiIntent.java
│
├── memory
│   ├── NpcMemory.java
│   ├── PlayerRelationship.java
│   ├── EventMemory.java
│   ├── ConversationMemory.java
│   └── NpcMemoryStore.java
│
├── world
│   ├── NpcWorldScanner.java
│   ├── NpcWorldContext.java
│   └── NearbyEntityInfo.java
│
├── command
│   ├── NpcConversationManager.java
│   ├── NpcCommandRouter.java
│   └── NpcInteractionHandler.java
│
├── builder
│   ├── BuildIntent.java
│   ├── StructureTemplate.java
│   ├── TemplateLoader.java
│   ├── BuildPlanner.java
│   ├── BuildTask.java
│   ├── PlacementTask.java
│   ├── BuildExecutor.java
│   ├── SiteValidator.java
│   └── BuildProgress.java
│
├── persistence
│   ├── NpcSaveData.java
│   └── NpcSaveManager.java
│
└── util
    ├── BlockUtils.java
    ├── PositionUtils.java
    └── JsonUtils.java
```

---

## 3) Build Order — 11 Phases

---

### PHASE 1 — NPC Foundation

**Goal:** A working NPC entity with basic state and interaction.

1. Create `AiNpcEntity` — entity class, spawn via command, custom name, attributes
2. Create `NpcState` — modes (`IDLE`, `FOLLOWING`, `GUARDING`, `GOING_HOME`, `BUILDING`, `TALKING`), home position, target player UUID, build task id
3. Add deterministic movement goals (no AI yet):
   - walk
   - follow player
   - stop and stay
   - go to home position
   - attack hostile mob

**Deliverable:** Spawn NPC, manually toggle follow/stay/go-home/attack — no AI yet.

---

### PHASE 2 — AI Backend Contract

**Goal:** Define request/response schema between mod and backend.

#### Request Schema

```json
{
  "npc_profile": {
    "name": "Kael",
    "role": "Companion Builder",
    "personality": "Loyal, practical, slightly sarcastic"
  },
  "npc_state": {
    "mode": "IDLE",
    "health": 20,
    "position": {"x": 100, "y": 64, "z": 200},
    "home_position": {"x": 95, "y": 64, "z": 195},
    "current_task": null
  },
  "player_state": {
    "name": "Steve",
    "health": 18,
    "position": {"x": 102, "y": 64, "z": 201},
    "distance_to_npc": 2.3,
    "held_item": "minecraft:iron_sword"
  },
  "world_state": {
    "time_of_day": "NIGHT",
    "biome": "plains",
    "weather": "clear",
    "nearby_mobs": ["zombie", "skeleton"]
  },
  "memory": {
    "trust": 50,
    "recent_events": ["Player asked NPC to follow"],
    "recent_conversation": [
      {"speaker": "player", "text": "Come with me"},
      {"speaker": "npc", "text": "Alright, I'll follow."}
    ]
  },
  "player_message": "Build a small house here"
}
```

#### Response Schema

```json
{
  "speech": "Alright. I'll build a small house here.",
  "intent": "BUILD_STRUCTURE",
  "emotion": "FOCUSED",
  "actions": [
    {
      "type": "START_BUILD",
      "parameters": {
        "structureId": "small_house",
        "locationMode": "NEAR_PLAYER"
      }
    }
  ],
  "memory_updates": [
    {"type": "ADD_EVENT", "value": "Player asked NPC to build a small house"}
  ],
  "relationship_changes": {
    "trust_delta": 0
  }
}
```

#### DTO Classes

- `AiNpcRequest` — npcProfile, npcState, playerState, worldState, memory, playerMessage
- `AiNpcResponse` — speech, intent, emotion, actions, memoryUpdates, relationshipChanges
- `AiAction` — type, parameters
- `AiIntent` — enum: `TALK`, `FOLLOW_PLAYER`, `STAY_HERE`, `PROTECT_PLAYER`, `GO_HOME`, `BUILD_STRUCTURE`, `UNKNOWN`

**Deliverable:** DTOs, backend endpoint spec, parser + validator, mocked end-to-end response.

---

### PHASE 3 — AI Conversation Loop

**Goal:** Right-click NPC → type message → backend → structured response → show NPC speech.

1. Build conversation interaction flow
2. Create `NpcWorldScanner` — nearby mobs, biome, time, weather, player held item, distance
3. Create `NpcConversationManager` — builds request, calls AI client, receives response, passes actions to executor, stores memory

**Deliverable:** Talk to NPC, get contextual reply + parsed intent + speech in game. No building yet.

---

### PHASE 4 — Companion Commands

**Goal:** AI's returned intents actually do something.

1. Create `NpcActionExecutor` — handles:
   - `FOLLOW_PLAYER`
   - `STAY_HERE`
   - `GO_HOME`
   - `PROTECT_PLAYER`
   - `STOP_CURRENT_TASK`

2. State transitions in `NpcBrain`:
   - `FOLLOW_PLAYER` → mode = `FOLLOWING`, activate follow goal
   - `STAY_HERE` → mode = `IDLE`, clear movement target
   - `GO_HOME` → mode = `GOING_HOME`, pathfind to home
   - `PROTECT_PLAYER` → mode = `GUARDING`, target hostile mobs near player

**Deliverable:** "follow me", "stay here", "go home", "protect me" all work correctly.

---

### PHASE 5 — Memory + Relationship

**Goal:** NPC is persistent and remembers.

1. `ConversationMemory` — last N messages (Deque)
2. `PlayerRelationship` — UUID, trust, friendship
3. `EventMemory` — timestamp, event text, importance
4. `NpcMemory` — Map<UUID, PlayerRelationship>, Deque<EventMemory>, ConversationMemory, currentGoalSummary
5. Update memory after every AI interaction
6. Persist to disk / entity NBT — trust, recent events, home position, current task

**Deliverable:** NPC remembers recent chat, trust score, important events, home, active task across reloads.

---

### PHASE 6 — Builder System Foundation

**Goal:** Template-based building with 3 predefined structures.

#### Structure 1 — `small_house`
- 7x7 or 9x9 footprint
- Oak planks + cobblestone
- Door, windows, roof, torch

#### Structure 2 — `wheat_farm`
- 9x9 farmland
- Water center
- Fence optional

#### Structure 3 — `wall_segment`
- Straight 10–20 block wall or small fenced perimeter

#### Structure Template Format (JSON)

```json
{
  "id": "small_house",
  "size": {"x": 7, "y": 5, "z": 7},
  "blocks": [
    {"x": 0, "y": 0, "z": 0, "block": "minecraft:cobblestone"},
    {"x": 1, "y": 0, "z": 0, "block": "minecraft:cobblestone"},
    {"x": 0, "y": 1, "z": 0, "block": "minecraft:oak_planks"}
  ]
}
```

#### TemplateLoader
- Load templates from `/data/modid/structures/*.json`

**Deliverable:** Structure templates load correctly and can be inspected.

---

### PHASE 7 — Site Validation

**Goal:** Find a valid place to build before starting.

1. `SiteValidator` — checks:
   - Inside loaded chunks
   - Enough empty space
   - Acceptable terrain (max height diff <= 1–2 blocks)
   - No water/lava/large obstruction
2. Location strategy — "build here" → use near player or current NPC position; if invalid, search nearby radius

**Deliverable:** Mod answers "yes, can build here" or "not enough flat space".

---

### PHASE 8 — Build Planning

**Goal:** Convert template into a build task queue.

- `BuildPhase` — `PREPARING_SITE`, `PLACING_FOUNDATION`, `PLACING_STRUCTURE`, `FINISHING`, `COMPLETED`, `FAILED`
- `PlacementTask` — worldPos, targetState, priority
- `BuildPlanner` — ordered placement list (lower Y first → walls → roof)

**Deliverable:** Create build task from template, see full placement queue.

---

### PHASE 9 — Build Execution

**Goal:** NPC places blocks in the world.

1. `BuildExecutor` — owns current BuildTask, takes next PlacementTask, pathfinds NPC close, places block, repeats
2. Build state in NpcBrain — when `START_BUILD`, set mode = `BUILDING`, create BuildTask, give to executor
3. Build interruption — pause if attacked / told to stop

Pseudo flow:
- No active task → return
- Peek next placement
- Too far → pathfind closer
- Wrong block already there → skip
- Block in way and replaceable → clear
- Place target block → pop → repeat

**Deliverable:** NPC builds a predefined structure block by block.

---

### PHASE 10 — Connect AI Build Commands

**Goal:** "Build a house here" triggers the builder.

1. Backend returns `BUILD_STRUCTURE` intent with `structureId` parameter
2. `NpcActionExecutor` handles `START_BUILD`:
   - Read structureId
   - Load template
   - Find valid site
   - Create BuildTask
   - Set NPC to BUILDING
   - NPC says confirmation

**Deliverable:** "Build a small house here", "Make a farm here", "Build a wall here" all work.

---

### PHASE 11 — Polish NPC Experience

**Goal:** Feels like a real companion.

- Build progress dialogue — "I've started the house", "The farm is finished"
- Better world-aware replies — mention nearby danger, night/day, current build state
- Build status memory — last structure built, current progress, last failure reason

**Deliverable:** NPC talks about what it's doing, remembers builds, pauses/resumes naturally.

---

## 4) Weekly Schedule

| Week | Focus | Deliverable |
|------|-------|-------------|
| 1 | NPC entity + base actions | Controllable NPC exists in game (follow/stay/go-home/protect) |
| 2 | AI backend integration | Chat with NPC, receive structured JSON response |
| 3 | Action execution + memory | NPC talks + remembers + obeys simple commands |
| 4 | Structure templates + site validation | Load templates, find valid build sites |
| 5 | Build planner + executor | NPC builds one structure block by block |
| 6 | Polish + remaining builds | All 3 structures, interruption, progress dialogue |

---

## 5) First Milestone

> Player says "follow me" → backend returns JSON → NPC follows.

Not building. Not memory. Not templates. Just prove the full loop:
**player message → backend → structured response → in-game action**

Once that works, everything else is layering.

---

## 6) Key Simplification for V1

**Creative-style building.** NPC places blocks without gathering materials. No survival inventory management, missing item checks, or gathering AI.

Survival logistics (material requirements, gathering, crafting) is V2 scope.
