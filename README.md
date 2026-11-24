# MasterManCore

**Complete all-in-one core plugin for MasterManMC (Minecraft 1.12.2)**

MasterManCore is designed to be the single essential plugin your server needs. It includes a full economy system, Auction House, Crates, Custom Enchants, Custom Weapons, Homes, Warps, TPA, Kits, and essential player commands.

## Features

### Economy
- Starting balance configurable
- `/balance` (`/bal`, `/money`)
- `/pay <player> <amount>`
- Admin: `/eco give|take|set|reset <player> [amount]`
- Persistent YAML storage

### Auction House
- `/ah` – Open the Auction House GUI
- `/ah sell <price>` – List the item in your hand
- Tax system, listing limits, expiration
- Click to buy

### Crates & Keys
- Configurable crates (`crates.yml`)
- Vote, Rare, Legendary crates by default
- `/crate list` / `/crate open <name>`
- `/key <player> <crate> [amount]` (admin)

### Custom Enchants
- Life Steal, Thunder, Poison, Speed, Explosive, Auto Sell, Haste
- `/ce list`
- `/ce apply <enchant> <level>` (admin, hold item)

### Custom Weapons
- Excalibur, Shadow Blade, Dragonslayer, Frost Bow, Staff of Healing
- `/giveweapon <player> <weapon>`

### Teleportation & Homes
- `/sethome [name]`, `/home [name]`, `/delhome <name>`, `/homes`
- `/setwarp <name>`, `/warp <name>`, `/delwarp <name>`, `/warps`
- `/tpa <player>`, `/tpaccept`, `/tpdeny`
- `/spawn`, `/setspawn`

### Essentials-style Commands
- `/msg` / `/reply`
- `/heal`, `/feed`, `/fly`, `/gamemode` (`/gm`), `/invsee`, `/clear`
- `/kit <starter|tools|food>`, `/kits`

### Other
- Custom join/quit messages
- Configurable prefix and messages
- `/masterman` or `/mm` for info + `/masterman reload`

## Installation

1. Download the latest `MasterManCore.jar` from [Releases](https://github.com/kwolf99393/MasterManCore/releases)
2. Place it in your server's `plugins` folder
3. Restart the server (or use `/reload` carefully)
4. Edit `plugins/MasterManCore/config.yml` and `crates.yml` as needed
5. Give permissions (most admin commands default to OP)

**Requires:** Spigot / Paper / CraftBukkit **1.12.2**

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `mastermancore.*` | Everything | OP |
| `mastermancore.admin` | Admin commands (warps, spawn, reload) | OP |
| `mastermancore.eco.admin` | Economy admin | OP |
| `mastermancore.crate.admin` | Give keys, manage crates | OP |
| `mastermancore.weapon.admin` | Give custom weapons | OP |
| `mastermancore.heal` / `feed` / `fly` / `gamemode` / `invsee` / `clear` | Respective commands | OP |
| `mastermancore.ah.sell` | Sell in AH | true |

## Building from Source

```bash
git clone https://github.com/kwolf99393/MasterManCore.git
cd MasterManCore
mvn clean package
```

The jar will be in `target/MasterManCore.jar`.

## Configuration

- `config.yml` – Main settings, messages, economy, AH, homes
- `crates.yml` – Define crates and rewards
- Data files (`economy.yml`, `auctions.yml`, `homes.yml`, `warps.yml`, `crate-data.yml`) are created automatically

## Notes

This is a solid, functional core aimed at 1.12.2 servers. It covers the major systems most servers need so you can run with fewer plugins. For very large or highly customized networks you may still want specialized plugins on top, but MasterManCore is built to be the foundation.

## Support / Contributing

Open issues or pull requests on this repository.

---

**MasterManMC** – Built with ❤️ for the community
