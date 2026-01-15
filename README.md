# 🎯 Apex

**Rare Item Announcements for Minecraft Servers**

Apex is a lightweight Paper/Spigot plugin that announces when players acquire rare and valuable items. Each tracked item receives a unique serial number, making every drop special and collectible.

---

## ✨ Features

- 🔔 **Announcements** - Get notified when rare items are collected
- 🔢 **Serial Numbers** - Each item gets a unique, persistent serial number
- 🎨 **Visual Effects** - Titles, action bars, and particles for epic moments
- 🔊 **Sound Effects** - Customizable audio feedback per item
- ⚙️ **Per-Player Settings** - Players can toggle notifications via GUI
- 🐉 **Ownership Tracking** - Dragon Egg tracks ownership changes

---

## 📦 Tracked Items

| Item | Rarity | Announcement |
|------|--------|--------------|
| 🥚 Dragon Egg | Very Rare (0.5%) | Title + Broadcast |
| ⚙️ Heavy Core | Very Rare (0.5%) | Title |
| 💨 Wind Burst III | Very Rare (0.5%) | Title |
| 🔱 Trident | Rare (2.0%) | Action Bar |
| 💙 Heart of the Sea | Rare (2.0%) | Action Bar |

---

## 📥 Installation

1. Download the latest `Apex.jar` from [Releases](https://github.com/YOUR_USERNAME/Apex/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Done! ✅

---

## 🎮 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/apex settings` | Opens the settings GUI | `apex.command` |

---

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `apex.command` | Access to /apex command | `true` |

---

## ⚙️ Settings Menu

Players can customize their experience through the in-game GUI:

- **Master Toggle** - Enable/disable all announcements
- **Per-Item Toggle** - Show/hide specific item announcements
- **Sound Toggle** - Enable/disable sounds per item

---

## 📋 Compatibility

| Minecraft Version | Status |
|-------------------|--------|
| 1.21 - 1.21.5 | ✅ Full Support |
| 1.20.x | ⚠️ Partial (no Heavy Core, Wind Burst) |
| < 1.20 | ❌ Not Supported |

**Requires:** Paper 1.21+ or compatible fork

---

## 📁 Data Storage

Apex stores data in the `plugins/Apex/` folder:

- `settings.yml` - Player preferences
- `counters.yml` - Serial number counters

---

## 🛠️ Building from Source

```bash
git clone https://github.com/YOUR_USERNAME/Apex.git
cd Apex
mvn clean package
```

The compiled JAR will be in the `target/` folder.

---

## 📄 License

This project is open source.

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

**Made with ❤️ for the Minecraft community**
