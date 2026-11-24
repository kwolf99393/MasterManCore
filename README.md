# MasterManCore

**Complete all-in-one core plugin for MasterManMC (Minecraft 1.12.2)**

MasterManCore is designed to be the single essential plugin your server needs.

## Features (v1.1.0)

### Economy
- `/balance`, `/pay`, `/eco`

### Auction House
- `/ah`, `/ah sell <price>`

### Crates & Keys
- `/crate`, `/key`

### Custom Enchants & Weapons
- `/ce`, `/giveweapon`

### Homes / Warps / TPA / Kits / Essentials commands

### NEW in 1.1.0 — Moderation & Ranks
- **Bans**: `/ban <player> [duration] [reason]`, `/tempban`, `/unban`, `/banlist`
- **Mutes**: `/mute <player> [duration] [reason]`, `/unmute`
- **Ranks**: `/rank list`, `/rank set <player> <rank>`, `/rank info`
- **Prefixes**: Automatic chat prefixes based on rank
- Configurable ranks with inheritance & permissions (`ranks.yml`)

## Installation

1. Download the latest JAR from Releases
2. Place in `plugins/` folder
3. Restart server
4. Edit `config.yml` and `ranks.yml`

**Requires Spigot/Paper 1.12.2**

## Rank Commands
```
/rank list
/rank set <player> <rank>
/rank info [player]
/setrank <player> <rank>
```

## Ban / Mute Examples
```
/ban Steve 7d Griefing
/tempban Alex 12h Spamming
/mute Bob 30m Toxicity
/unban Steve
/unmute Bob
```

Durations: `1d`, `12h`, `30m`, `perm`

---
**MasterManMC**
