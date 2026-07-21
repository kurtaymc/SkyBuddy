# <p align="center">SkyBuddy</p>
<p align="center"><b>The Ultimate Island Companion</b></p>
<p align="center"><i>Elevate your Skyblock server with a highly optimized, interactive, and customizable island assistant!</i></p>

**SkyBuddy** is an advanced island assistant plugin that spawns an interactive NPC (Bee or Trader Llama) at the center of a player's island. It is designed to give your players a unique, immersive way to manage their islands by combining aesthetics with utility.

## ✨ Features

* **Seamless Integration:** Works flawlessly out of the box with **BentoBox** and **SuperiorSkyblock2**.
* **Fully Customizable:** If you have custom island schematics on your server, you can perfectly integrate the assistant into your custom islands by configuring the center offset settings in the config file.
* **Interactive GUI:** Players can **Left-Click** their buddy to open a visually pleasing customization menu. Change your Llama's carpet color, modify your Bee's nectar/anger states, and toggle between Adult and Baby sizes instantly!
* **Action Commands:** **Right-Clicking** the buddy triggers a customizable command (e.g., opening your server's main island panel without typing).
* **Dynamic Holograms:** Fully configurable multi-line floating text above the buddy.
* **Highly Optimized:** Built with performance in mind. Uses *PersistentDataContainers* instead of heavy database queries, includes smart orphaned-hologram cleanup, and utilizes secure GUI anti-drag protections.
* **Vanilla Safe:** Buddies are completely invulnerable. They will not take damage, fly away, despawn, or attack players.
* **Multi-Language Support:** Comes with English (en) and Turkish (tr) configuration files by default.

<p align="center">
  <img src="https://i.imgur.com/nhNNdGv.gif" width="45%"/>
  <img src="https://i.imgur.com/6zzalZ1.gif" width="45%"/>
</p>
<p align="center">
  <img src="https://i.imgur.com/MRsWd5b.gif" width="45%"/>
  <img src="https://i.imgur.com/HjZbwZx.gif" width="45%"/>
</p>

## 🛠️ Commands & Permissions

* `/skybuddy spawn` (or respawn) - Spawns or updates the buddy at your island center.  
  * *Permission:* `skybuddy.use` (Default: true)
* `/skybuddy reload` - Reloads the configuration, cache, and language files.  
  * *Permission:* `skybuddy.admin` (Default: OP)
* `/skybuddy help` - Displays the help menu.

## 📁 Files

* [config.yml](https://github.com/kurtaymc/SkyBuddy/blob/main/src/main/resources/config.yml) - Main plugin configuration file
* [lang-en.yml](https://github.com/kurtaymc/SkyBuddy/blob/main/src/main/resources/lang/lang-en.yml) - English language and message file
* [lang-tr.yml](https://github.com/kurtaymc/SkyBuddy/blob/main/src/main/resources/lang/lang-tr.yml) - Turkish language and message file

## 💡 Support, Bugs & Suggestions

Found a bug, an issue, or do you have a great suggestion to make **SkyBuddy** even better? Feel free to report bugs or share your feedback and ideas with us!

<p align="center"><b>Discord:</b> kurtaymc</p>
