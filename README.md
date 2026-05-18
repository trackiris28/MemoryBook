<div align="center">

# 📖 MemoryBook

### A Minecraft SMP history plugin that turns your server into a living story.

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.x-brightgreen?style=for-the-badge)
![Server](https://img.shields.io/badge/Server-Paper%20%2F%20Spigot-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge)
![DiscordSRV](https://img.shields.io/badge/DiscordSRV-Optional-5865F2?style=for-the-badge)

**MemoryBook** automatically records important SMP milestones such as first Nether entry, first End entry, boss kills, season events, custom admin memories, and community history.

Players can view the story of your server through commands, GUI menus, and an in-game written book.

</div>

---

## ✨ Overview

MemoryBook is designed for SMP servers that want to preserve memorable moments instead of letting them disappear.

Every season has its own history. Every player can leave a mark. Every major moment can become part of the server timeline.

Example moments MemoryBook can save:

- The first player to join the SMP season
- The first player to enter the Nether
- The first player to enter The End
- The first Ender Dragon kill
- The first Wither kill
- Custom admin-created server events
- Season milestones
- Player-related memories
- Community achievements

Example broadcast:

```text
━━━━━━━━━━━━━━━━━━━━
📖 MEMORY BOOK UPDATED
🔥 The Nether gate has opened!
Kaito was the first player to enter the Nether.
━━━━━━━━━━━━━━━━━━━━
```

---

## 📚 Table of Contents

- [Features](#-features)
- [Commands](#-commands)
- [Permissions](#-permissions)
- [Configuration](#-configuration)
- [DiscordSRV Integration](#-optional-discordsrv-integration)
- [Storage](#-storage)
- [Memory Types](#-memory-types)
- [Rarity System](#-rarity-system)
- [Installation](#-installation)
- [Building from Source](#-building-from-source)
- [Recommended Usage](#-recommended-usage)
- [Planned Features](#-planned-features)
- [License](#-license)

---

## 🚀 Features

### 🧠 Automatic Server Memories

MemoryBook can automatically record important SMP milestones.

Currently supported automatic events include:

- First join
- First Nether entry
- First End entry
- First Ender Dragon kill
- First Wither kill

When a new memory is created, the plugin can:

- Save the memory to `memories.yml`
- Broadcast the event in chat
- Give Memory Points to involved players
- Add the event to the server timeline
- Optionally send the event to Discord through DiscordSRV

---

### 🕰 Timeline System

Players can use `/timeline` to view the history of the current SMP season.

```text
===== SMP Timeline =====

Day 1  | DucMinh joined the server for the first time.
Day 5  | Kaito was the first player to enter the Nether.
Day 12 | The Ender Dragon was defeated.
```

Supported commands:

```text
/timeline
/timeline <page>
```

---

### 🗂 History GUI

Players can use `/history` to open an in-game GUI menu.

The GUI can display memories by category, such as:

- All memories
- First achievements
- Boss history
- Player milestones
- Admin events
- Season memories

Command:

```text
/history
```

---

### 📖 In-Game Memory Book

Players can receive a written book containing the server timeline.

Command:

```text
/book
```

Example book content:

```text
MemoryBook - SMP Season 1

Day 1:
DucMinh joined the server for the first time.

Day 5:
Kaito was the first player to enter the Nether.

Day 12:
The Ender Dragon was defeated.
```

---

### 🛠 Admin Custom Memories

Admins can manually add memories to the timeline.

This is useful for events that cannot be detected automatically, such as:

- A town being founded
- A war ending
- A server festival
- A community build being completed
- A special event hosted by staff
- A major spawn update

Command:

```text
/memory add <type> <title> | <description>
```

Example:

```text
/memory add EVENT Spawn Completed | The community finished building the main spawn area.
```

---

### 👤 Player Profiles

MemoryBook can show memories related to a specific player.

Command:

```text
/memory profile <player>
```

Example:

```text
/memory profile Kaito
```

This can display player-related historical moments such as:

- First Nether entry
- Boss kills
- Custom admin memories
- Season milestones

---

### ⭐ Memory Points

Players can receive Memory Points when they are part of important server memories.

Commands:

```text
/memory points
/memory points <player>
```

Memory Points are intended for cosmetic or prestige systems.

Recommended uses:

- Cosmetic titles
- Profile frames
- Particle effects
- Timeline name colors
- Hall of Fame ranking
- Supporter display styles

Memory Points should not be used for pay-to-win rewards.

---

### 🌍 Season System

MemoryBook supports SMP seasons.

Admins can start a new season:

```text
/memory season start <name>
```

Example:

```text
/memory season start SMP Season 2
```

Each memory stores the season name, allowing your server to separate history between SMP seasons.

---

## 💬 Commands

### Player Commands

| Command | Description |
|---|---|
| `/timeline` | View the server memory timeline |
| `/timeline <page>` | View a specific timeline page |
| `/history` | Open the MemoryBook GUI |
| `/book` | Receive the in-game written Memory Book |
| `/memory profile <player>` | View memories related to a player |
| `/memory points` | View your Memory Points |
| `/memory points <player>` | View another player's Memory Points |

### Admin Commands

| Command | Description |
|---|---|
| `/memory add <type> <title> \| <description>` | Add a custom memory |
| `/memory list` | List stored memories |
| `/memory remove <id>` | Remove a memory by ID |
| `/memory reload` | Reload plugin configuration |
| `/memory season start <name>` | Start a new SMP season |

---

## 🔐 Permissions

| Permission | Description | Default |
|---|---|---|
| `memorybook.use` | Allows players to use basic MemoryBook commands | `true` |
| `memorybook.admin` | Allows access to admin commands | `op` |

Recommended permission setup:

```yaml
memorybook.use:
  default: true

memorybook.admin:
  default: op
```

---

## ⚙️ Configuration

Example `config.yml`:

```yaml
settings:
  current-season: "SMP Season 1"
  broadcast-new-memory: true
  give-memory-points: true
  save-coordinates: true
  enable-book-item: true
  enable-gui: true

memory-points:
  first-join: 5
  first-nether: 10
  first-end: 15
  first-dragon-kill: 30
  first-wither-kill: 25
  custom-event: 5

discord:
  enabled: false
  use-discordsrv: true
  channel: "global"
  send-memory-created: true
  send-season-start: true
  send-season-end: true
  format:
    memory-created: "**📖 Memory Book Updated**\n{icon} **{title}**\n{description}\n👤 Player: `{players}`\n🗓 Season: `{season}`"
```

---

## 🔗 Optional DiscordSRV Integration

MemoryBook can optionally integrate with DiscordSRV.

If DiscordSRV is installed and enabled in the configuration, MemoryBook can send memory announcements to a Discord channel.

If DiscordSRV is not installed, MemoryBook will continue working normally.

This makes Discord integration fully optional.

Example Discord message:

```text
📖 Memory Book Updated
🔥 The Nether gate has opened!

Kaito was the first player to enter the Nether.

Season: SMP Season 1
```

Recommended Discord configuration:

```yaml
discord:
  enabled: true
  use-discordsrv: true
  channel: "global"
  send-memory-created: true
```

To enable Discord messages:

1. Install DiscordSRV on your server.
2. Configure your DiscordSRV channels.
3. Set `discord.enabled` to `true` in `config.yml`.
4. Set the correct DiscordSRV game channel name.
5. Restart the server or reload the plugin.

---

## 💾 Storage

MemoryBook stores server history in YAML files.

Default storage file:

```text
plugins/MemoryBook/memories.yml
```

Example memory entry:

```yaml
memories:
  '1':
    type: FIRST_NETHER
    title: "The Nether gate has opened"
    description: "Kaito was the first player to enter the Nether."
    players:
      - Kaito
    world: world_nether
    x: 124
    y: 67
    z: -230
    server_day: 5
    real_time: "2026-06-02 21:30"
    season: "SMP Season 1"
    rarity: "RARE"
```

---

## 🧩 Memory Types

MemoryBook can support memory types such as:

```text
FIRST_JOIN
FIRST_NETHER
FIRST_END
FIRST_DRAGON_KILL
FIRST_WITHER_KILL
CUSTOM
EVENT
BUILD
TOWN
BOSS
SEASON
```

These types can be used to categorize memories in the timeline, GUI, book, and Discord messages.

---

## 💎 Rarity System

Memories can have rarity levels:

```text
COMMON
RARE
EPIC
LEGENDARY
MYTHIC
```

Recommended examples:

| Memory | Rarity |
|---|---|
| First Join | Common |
| First Nether Entry | Rare |
| First End Entry | Epic |
| First Dragon Kill | Legendary |
| Season Finale | Mythic |

---

## 📦 Installation

1. Download or build the plugin `.jar`.
2. Place the `.jar` file into your server's `plugins` folder.
3. Start or restart the server.
4. Edit the generated configuration files if needed.
5. Use `/memory reload` after making configuration changes.

---

## 🧱 Building from Source

MemoryBook uses Gradle.

Build command:

```bash
./gradlew build
```

The compiled plugin will be located in:

```text
build/libs/
```

---

## ✅ Requirements

- Minecraft server: Paper or Spigot
- Recommended version: Minecraft 1.20.x
- Java: 17 or newer
- Optional: DiscordSRV for Discord integration

---

## 🏆 Recommended Usage

MemoryBook is best used on SMP servers that want:

- Long-term player engagement
- Seasonal history
- Community storytelling
- Player recognition
- Hall of Fame systems
- Cosmetic reward systems
- Discord announcements
- Non-pay-to-win donation rewards

MemoryBook can help turn your server into a story that players remember.

---

## 💰 Donation and Monetization Ideas

MemoryBook works well with cosmetic donation systems.

Recommended donation rewards:

- Custom title colors
- Cosmetic profile frames
- Particle effects
- Special MemoryBook icons
- Supporter tags
- Cosmetic book skins
- Hall of Fame display styles
- Discord announcement style cosmetics

Not recommended:

- Selling fake achievements
- Selling extra votes
- Selling gameplay power
- Selling admin history editing rights
- Selling unfair progression advantages

Keep donation rewards cosmetic and fair for all players.

---

## 🎮 Example Gameplay Flow

1. A new SMP season starts.
2. Players join the server.
3. MemoryBook records the first join.
4. A player enters the Nether.
5. MemoryBook broadcasts the milestone.
6. The memory is saved to the timeline.
7. Players use `/timeline` to view server history.
8. Players use `/book` to receive an in-game history book.
9. Admins add custom community events.
10. At the end of the season, the server has a complete historical record.

---

## 🛣 Planned Features

Future versions may include:

- Build voting system
- Weekly best build memory
- Towny or Lands integration
- PlaceholderAPI support
- LuckPerms title integration
- SQLite storage
- Web dashboard
- BlueMap or Dynmap markers
- Hall of Fame holograms
- More automatic achievement tracking
- Cosmetic memory frames
- Custom GUI themes

---

## 📄 License

Choose a license depending on how you want others to use the plugin.

Recommended open-source licenses:

- MIT License
- Apache License 2.0
- GPL-3.0 License

---

## ❤️ Credits

Created for SMP servers that want to preserve player memories, community milestones, and seasonal history.

MemoryBook turns your server into a living archive.

