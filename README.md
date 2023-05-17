# MMCCore
Will be working on this properly soon

### Languages:
  - Java [(Paper API)](https://papermc.io)

### Dependencies:
  - None

### Info:
This is exactly what the description says it is.

***Find this plugin on [spigot](https://www.spigotmc.org/resources/1-8-1-17-1-%E2%9C%85-yocore-ranks-punishments-essentials-and-more.94564/)!***

# General Features:
  - `Punishments`
  - `Ranks`
  - `Permissions`
  - `Staff Utilities`
  - `Essential commands`
  - `Scoreboard`
  - `Economy`
  - `PvP Stats`
  - `World Separation Bungeecord` *(from vBungee in vCores)*

# Servers:
With an improved version of **vBungee servers**, yoCore has a world separation protocol that allows you to have multiple "servers" in one single server, with chat separation, tab separation (including tab completing in chat), etc.

### Bungee Commands:
  - `/hub`
  - `/glist`
  - `/find`
  - `/send`
  - `/server`

# Commands (Alphabetical Order):
  - `/adminchat`
  - `/alts`
  - `/balance`
  - `/ban`
  - `/blacklist`
  - `/bounty`
  - `/broadcast`
  - `/buildchat`
  - `/buildmode`
  - `/chatcolor`
  - `/clear`
  - `/clearchat`
  - `/clearhistory`
  - `/cleargranthistory`
  - `/clearreports`
  - `/economy`
  - `/enderchest`
  - `/feed`
  - `/find`
  - `/fly`
  - `/freeze`
  - `/gamemode`
  - `/glist`
  - `/gma`
  - `/gmc`
  - `/gms`
  - `/gmsp`
  - `/grant`
  - `/grants`
  - `/heal`
  - `/history`
  - `/hub`
  - `/invsee`
  - `/itemname`
  - `/kick`
  - `/managementchat`
  - `/message`
  - `/modmode`
  - `/mute`
  - `/mutechat`
  - `/nickname`
  - `/onlineplayers`
  - `/pay`
  - `/ping`
  - `/powertool`
  - `/rank`
  - `/rankdisguise`
  - `/realname`
  - `/reban`
  - `/reblacklist`
  - `/remute`
  - `/reload`
  - `/reply`
  - `/report`
  - `/reports`
  - `/resetstats`
  - `/retempban`
  - `/retempmute`
  - `/rewarn`
  - `/seen`
  - `/send`
  - `/server`
  - `/servermanager`
  - `/setrank`
  - `/settings`
  - `/skull`
  - `/spawn`
  - `/speed`
  - `/staffchat`
  - `/stats`
  - `/sudo`
  - `/tag`
  - `/tags`
  - `/teleport`
  - `/tempban`
  - `/tempmute`
  - `/togglemessages`
  - `/togglescoreboard`
  - `/togglestaffalerts`
  - `/unban`
  - `/unblacklist`
  - `/unbounty`
  - `/ungrant`
  - `/unmute`
  - `/user`
  - `/vanish`
  - `/warn`

# Chat Prefixes:
Using `#, @, $` or `!` before a message *(with a space)* puts you in a staff chat channel.

### Chats:
  - `$ <message>` (Builder Chat)
  - `# <message>` (Staff Chat)
  - `@ <message>` (Admin Chat)
  - `! <message>` (Management Chat)

# Scoreboard:
yoCore has a scoreboard, which can be per-world or global. You can use placeholders from this plugin, however, you cannot use placeholders from any other plugin **yet.** (PlaceholderAPI support will be added in the future)

# Nametags & Tab List
yoCore has custom nametags and a custom tab list for the ranks as well as custom nametags for vanished, modmoded, and frozen players. All 3 of these are customizable in the [config.yml](https://github.com/Yochran/yoCore/blob/main/resources/config.yml).

# Permissions:
Permissions look like this:
```yocore.<command/listener>```

### For Example:
  - `yocore.ban` (/ban)
  - `yocore.granthistory` (/cg)
  - `yocore.chats.staff` (/sc, #)

***All Permissions are listed in the [plugin.yml](https://github.com/Yochran/yoCore/blob/main/resources/plugin.yml).***
