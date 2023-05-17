package me.yochran.yocore;

import me.yochran.yocore.commands.*;
import me.yochran.yocore.commands.bungee.*;
import me.yochran.yocore.commands.economy.BalanceCommand;
import me.yochran.yocore.commands.economy.BountyCommand;
import me.yochran.yocore.commands.economy.PayCommand;
import me.yochran.yocore.commands.economy.staff.EconomyCommand;
import me.yochran.yocore.commands.economy.staff.UnbountyCommand;
import me.yochran.yocore.commands.punishments.*;
import me.yochran.yocore.commands.staff.*;
import me.yochran.yocore.commands.stats.StatsCommand;
import me.yochran.yocore.commands.stats.staff.ResetStatsCommand;
import me.yochran.yocore.data.*;
import me.yochran.yocore.grants.Grant;
import me.yochran.yocore.grants.GrantType;
import me.yochran.yocore.listeners.*;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.permissions.Permissions;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.runnables.*;
import me.yochran.yocore.scoreboard.ScoreboardSetter;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.tags.Tag;
import me.yochran.yocore.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class yoCore extends JavaPlugin {

    private static yoCore instance;
    public static yoCore getInstance() { return instance; }

    public PlayerData playerData;
    public PunishmentData punishmentData;
    public GrantData grantData;
    public StatsData statsData;
    public EconomyData economyData;
    public PermissionsData permissionsData;
    public WorldData worldData;

    private PermissionManagement permissionManagement;
    private final PluginManager manager = getServer().getPluginManager();

    public boolean chat_muted;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran is loading...");

        instance = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                chat_muted = false;
                permissionManagement = new PermissionManagement();

                saveDefaultConfig();
                registerData();
                registerCommands();
                registerListeners();
                runRunnables();
                registerPunishments();
                refreshPunishments();
                registerGrants();
                registerRanks();
                registerTags();

                permissionManagement.initialize();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (player_permissions.containsKey(players.getUniqueId())) Permissions.setup(yoPlayer.getYoPlayer(players));
                    else Permissions.refresh(yoPlayer.getYoPlayer(players));
                }

                registerServers();
                registerLastLocations();

                Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran has successfully loaded.");
            }
        }.runTaskLater(this, 20 * 5);

    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (last_location.get(player.getUniqueId()) == null) {
                Map<Server, Location> location = new HashMap<>();
                location.put(Server.getServer(player), player.getLocation());
                last_location.put(player.getUniqueId(), location);
            }

            last_location.get(player.getUniqueId()).put(Server.getServer(player), player.getLocation());
        }

        for (Map.Entry<UUID, Map<Server, Location>> entry : last_location.entrySet()) {
            for (Map.Entry<Server, Location> data : entry.getValue().entrySet()) {
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".Server", data.getKey().getName());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".World", data.getValue().getWorld().getName());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".X", data.getValue().getX());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".Y", data.getValue().getY());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".Z", data.getValue().getZ());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".Yaw", data.getValue().getYaw());
                playerData.config.set(entry.getKey().toString() + ".LastLocation." + data.getKey().getName() + ".Pitch", data.getValue().getPitch());
            }
        }

        playerData.saveData();
    }

    public List<UUID> vanished_players = new ArrayList<>();
    public List<UUID> vanish_logged = new ArrayList<>();
    public List<UUID> staff_alerts = new ArrayList<>();
    public List<UUID> frozen_players = new ArrayList<>();
    public List<UUID> frozen_cooldown = new ArrayList<>();
    public List<UUID> modmode_players = new ArrayList<>();
    public List<UUID> buildmode_players = new ArrayList<>();
    public List<UUID> message_toggled = new ArrayList<>();
    public List<UUID> message_sounds_toggled = new ArrayList<>();
    public List<UUID> chat_toggled = new ArrayList<>();
    public List<UUID> tsb = new ArrayList<>();
    public List<UUID> grant_custom_reason = new ArrayList<>();
    public List<UUID> bchat_toggle = new ArrayList<>();
    public List<UUID> schat_toggle = new ArrayList<>();
    public List<UUID> achat_toggle = new ArrayList<>();
    public List<UUID> mchat_toggle = new ArrayList<>();

    public Map<UUID, ItemStack[]> inventory_contents = new HashMap<>();
    public Map<UUID, ItemStack[]> armor_contents = new HashMap<>();
    public Map<UUID, List<Double>> frozen_coordinates = new HashMap<>();
    public Map<UUID, UUID> reply = new HashMap<>();
    public Map<UUID, String> chat_color = new HashMap<>();
    public Map<UUID, Boolean> muted_players = new HashMap();
    public Map<UUID, Boolean> banned_players = new HashMap<>();
    public Map<UUID, String> blacklisted_players = new HashMap<>();
    public Map<UUID, UUID> selected_history = new HashMap<>();
    public Map<UUID, UUID> selected_grant_history = new HashMap<>();
    public Map<UUID, UUID> grant_player = new HashMap<>();
    public Map<UUID, String> grant_grant = new HashMap<>();
    public Map<UUID, GrantType> grant_type = new HashMap<>();
    public Map<UUID, String> grant_duration = new HashMap<>();
    public Map<UUID, String> grant_reason = new HashMap<>();
    public Map<UUID, Rank> rank_disguise = new HashMap<>();
    public Map<UUID, String> nickname = new HashMap<>();
    public Map<UUID, Tag> tag = new HashMap<>();
    public Map<UUID, PermissionAttachment> player_permissions = new HashMap<>();
    public Map<UUID, String> powertool_command = new HashMap<>();
    public Map<UUID, Material> powertool_material = new HashMap<>();
    public Map<UUID, UUID> tpa = new HashMap<>();
    public Map<UUID, Location> tpa_coords = new HashMap<>();
    public Map<UUID, Integer> tpa_timer = new HashMap<>();
    public Map<UUID, Map<Server, Location>> last_location = new HashMap<>();

    private void registerListeners() {
        manager.registerEvents(new PlayerLogListener(), this);
        manager.registerEvents(new PlayerChatListener(), this);
        manager.registerEvents(new GUIClickListener(), this);
        manager.registerEvents(new GUIExitListener(), this);
        manager.registerEvents(new VanishCheckListeners(), this);
        manager.registerEvents(new ModmodeListeners(), this);
        manager.registerEvents(new FreezeListener(), this);
        manager.registerEvents(new BuildModeListener(), this);
        manager.registerEvents(new ListCommand(), this);
        manager.registerEvents(new PlayerDeathListener(), this);
        manager.registerEvents(new ScoreboardSetter(), this);
        manager.registerEvents(new WorldChangeListener(), this);
        manager.registerEvents(new GrantCustomReasonListener(), this);
        manager.registerEvents(new PlayerInteractListener(), this);
        manager.registerEvents(new TPAListener(), this);
    }

    private void runRunnables() {
        new MuteUpdater().runTaskTimer(this, 10, 20);
        new BanUpdater().runTaskTimer(this, 10, 20);
        new GrantUpdater().runTaskTimer(this, 10, 20);
        new VanishUpdater().runTaskTimer(this, 10, 10);
        new TPAUpdater().runTaskTimer(this, 10, 20);
        if (getConfig().getBoolean("Nametags.Enabled")) new NametagUpdater().runTaskTimer(this, 0, 5);
        if (getConfig().getBoolean("Scoreboard.Enabled")) new ScoreboardUpdater().runTaskTimer(this, 0, 5);
        if (getConfig().getBoolean("Servers.WorldSeparation")) new WorldSeparator().runTaskTimer(this, 0, 5);
    }

    public void registerData() {
        playerData = new PlayerData();
        playerData.setupData();
        playerData.saveData();
        playerData.reloadData();

        punishmentData = new PunishmentData();
        punishmentData.setupData();
        punishmentData.saveData();
        punishmentData.reloadData();

        grantData = new GrantData();
        grantData.setupData();
        grantData.saveData();
        grantData.reloadData();

        statsData = new StatsData();
        statsData.setupData();
        statsData.saveData();
        statsData.reloadData();

        economyData = new EconomyData();
        economyData.setupData();
        economyData.saveData();
        economyData.reloadData();

        permissionsData = new PermissionsData();
        permissionsData.setupData();
        permissionsData.saveData();
        permissionsData.reloadData();

        worldData = new WorldData();
        worldData.setupData();
        worldData.saveData();
        worldData.reloadData();

        if (!worldData.config.contains("Servers")) {
            List<String> worlds = new ArrayList<>();
            for (World world : Bukkit.getWorlds())
                worlds.add(world.getName());

            Location spawn = new Location(Bukkit.getWorld("world"), 0.5, 75, 0.5, (float) 0.0, (float) 0.0);

            Server server = new Server("server", worlds, spawn);
            server.create();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerData.saveData();
                punishmentData.saveData();
                grantData.saveData();
                economyData.saveData();
                statsData.saveData();
                permissionsData.saveData();
                worldData.saveData();
            }
        }.runTaskLater(this, 10);
    }

    private void registerServers() {
        for (String server : worldData.config.getConfigurationSection("Servers").getKeys(false)) {
            String world = worldData.config.getString("Servers." + server + ".Spawn.World");
            double X = worldData.config.getDouble("Servers." + server + ".Spawn.X");
            double Y = worldData.config.getDouble("Servers." + server + ".Spawn.Y");
            double Z = worldData.config.getDouble("Servers." + server + ".Spawn.Z");
            double Yaw = worldData.config.getDouble("Servers." + server + ".Spawn.Yaw");
            double Pitch = worldData.config.getDouble("Servers." + server + ".Spawn.Pitch");

            Location spawn = new Location(Bukkit.getWorld(world), X, Y, Z, (float) Yaw, (float) Pitch);

            Server newServer = new Server(worldData.config.getString("Servers." + server + ".Name"), worldData.config.getStringList("Servers." + server + ".Worlds"), spawn);

            Server.getServers().put(newServer.getName(), newServer);
        }
    }

    private void registerRanks() {
        for (Permission permission : Permissions.getAllServerPermissions()) {
            if (permission.getName().contains("yocore.grant."))
                getServer().getPluginManager().removePermission(permission);
        }

        for (String rank : getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            Permission permission = new Permission("yocore.grant." + getConfig().getString("Ranks." + rank + ".ID").toLowerCase());
            permission.setDescription("Permission");

            if (!Permissions.getAllServerPermissions().contains(permission))
                manager.addPermission(permission);

            String ID = getConfig().getString("Ranks." + rank + ".ID");
            String prefix = getConfig().getString("Ranks." + rank + ".Prefix");
            String color = getConfig().getString("Ranks." + rank + ".Color");
            String display = getConfig().getString("Ranks." + rank + ".Display");
            String tabIndex = getConfig().getString("Ranks." + rank + ".TabIndex");
            ItemStack grantItem = Utils.getMaterialFromConfig(getConfig().getString("Ranks." + rank + ".GrantItem"));
            boolean isDefault = getConfig().getBoolean("Ranks." + rank + ".Default");

            Rank.getRanks().put(ID, new Rank(ID, prefix, color, display, tabIndex, grantItem, isDefault));
        }
    }

    private void registerTags() {
        for (Permission permission : getServer().getPluginManager().getPermissions()) {
            if (permission.getName().contains("yocore.tags."))
                getServer().getPluginManager().removePermission(permission);
        }

        for (String tag : getConfig().getConfigurationSection("Tags").getKeys(false)) {
            Permission permission = new Permission("yocore.tags." + getConfig().getString("Tags." + tag + ".ID").toLowerCase());
            permission.setDescription("Permission");

            if (!Permissions.getAllServerPermissions().contains(permission))
                manager.addPermission(permission);

            String ID = getConfig().getString("Tags." + tag + ".ID");
            String prefix = getConfig().getString("Tags." + tag + ".Prefix");
            String display = getConfig().getString("Tags." + tag + ".Display");

            Tag.getTags().put(ID, new Tag(ID, prefix, display));
        }
    }

    private void registerPunishments() {
        for (String player : punishmentData.config.getKeys(false)) {
            for (PunishmentType type : PunishmentType.values()) {
                if (punishmentData.config.contains(player + "." + PunishmentType.convertToString(type))) {
                    for (String entry : punishmentData.config.getConfigurationSection(player + "." + PunishmentType.convertToString(type)).getKeys(false)) {
                        yoPlayer target = new yoPlayer(UUID.fromString(player));
                        String executor = punishmentData.config.getString(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Executor");
                        Object duration = punishmentData.config.get(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Duration");
                        boolean silent = punishmentData.config.getBoolean(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Silent");
                        String reason = punishmentData.config.getString(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Reason");

                        Punishment punishment = new Punishment(type, target, executor, duration, silent, reason);
                        punishment.setStatus(punishmentData.config.getString(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Status"));
                        punishment.setDate(punishmentData.config.getLong(player + "." + PunishmentType.convertToString(type) + "." + entry + ".Date"));

                        Punishment.getPunishments().put(punishmentData.config.getInt(player + "." + PunishmentType.convertToString(type) + "." + entry + ".ID"), punishment);
                    }
                }
            }
        }
    }

    private void refreshPunishments() {
        if (punishmentData.config.contains("MutedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false))
                muted_players.put(UUID.fromString(player), punishmentData.config.getBoolean("MutedPlayers." + player + ".Temporary"));
        }

        if (punishmentData.config.contains("BannedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false))
                banned_players.put(UUID.fromString(player), punishmentData.config.getBoolean("BannedPlayers." + player + ".Temporary"));
        }

        if (punishmentData.config.contains("BlacklistedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("BlacklistedPlayers").getKeys(false))
                blacklisted_players.put(UUID.fromString(player), punishmentData.config.getString("BlacklistedPlayers." + player + ".Reason"));
        }
    }

    private void registerGrants() {
        for (String player : grantData.config.getKeys(false)) {
            if (grantData.config.contains(player + ".Grants")) {
                for (String grants : grantData.config.getConfigurationSection(player + ".Grants").getKeys(false)) {
                    GrantType type = GrantType.valueOf(grantData.config.getString(player + ".Grants." + grants + ".Type"));
                    String granted = grantData.config.getString(player + ".Grants." + grants + ".Grant");
                    yoPlayer target = new yoPlayer(UUID.fromString(player));
                    OfflinePlayer executor = Bukkit.getOfflinePlayer(UUID.fromString(grantData.config.getString(player + ".Grants." + grants + ".Executor")));
                    Object duration = grantData.config.get(player + ".Grants." + grants + ".Duration");
                    String reason = grantData.config.getString(player + ".Grants." + grants + ".Reason");

                    Grant grant = new Grant(type, granted, target, executor, duration, reason, grantData.config.getString(player + ".Grants." + grants + ".PreviousRank"));
                    grant.setStatus(grantData.config.getString(player + ".Grants." + grants + ".Status"));
                    grant.setDate(grantData.config.getLong(player + ".Grants." + grants + ".Date"));

                    Grant.getGrants().put(grantData.config.getInt(player + ".Grants." + grants + ".ID"), grant);
                }
            }
        }
    }

    private void registerLastLocations() {
        for (String player : playerData.config.getKeys(false)) {
            if (playerData.config.contains(player + ".LastLocation")) {
                last_location.put(UUID.fromString(player), new HashMap<>());

                for (String locations : playerData.config.getConfigurationSection(player + ".LastLocation").getKeys(false)) {
                    String world = playerData.config.getString(player + ".LastLocation." + locations + ".World");
                    double X = playerData.config.getDouble(player + ".LastLocation." + locations + ".X");
                    double Y = playerData.config.getDouble(player + ".LastLocation." + locations + ".Y");
                    double Z = playerData.config.getDouble(player + ".LastLocation." + locations + ".Z");
                    double Yaw = playerData.config.getDouble(player + ".LastLocation." + locations + ".Yaw");
                    double Pitch = playerData.config.getDouble(player + ".LastLocation." + locations + ".Pitch");

                    Location location = new Location(Bukkit.getWorld(world), X, Y, Z, (float) Yaw, (float) Pitch);
                    Server server = Server.getServer(playerData.config.getString(player + ".LastLocation." + locations + ".Server"));

                    last_location.get(UUID.fromString(player)).put(server, location);
                }
            }
            playerData.config.set(player + ".LastLocation", null);
        }
        playerData.saveData();
    }

    private void registerCommands() {
        getCommand("Setrank").setExecutor(new SetrankCommand());
        getCommand("Warn").setExecutor(new WarnCommand());
        getCommand("Kick").setExecutor(new KickCommand());
        getCommand("Mute").setExecutor(new MuteCommand());
        getCommand("Unmute").setExecutor(new UnmuteCommand());
        getCommand("Tempmute").setExecutor(new TempmuteCommand());
        getCommand("Ban").setExecutor(new BanCommand());
        getCommand("Unban").setExecutor(new UnbanCommand());
        getCommand("Tempban").setExecutor(new TempbanCommand());
        getCommand("Blacklist").setExecutor(new BlacklistCommand());
        getCommand("Unblacklist").setExecutor(new UnblacklistCommand());
        getCommand("History").setExecutor(new HistoryCommand());
        getCommand("ClearHistory").setExecutor(new ClearHistoryCommand());
        getCommand("Grant").setExecutor(new GrantCommand());
        getCommand("Grants").setExecutor(new GrantsCommand());
        getCommand("Ungrant").setExecutor(new UngrantCommand());
        getCommand("ClearGrantHistory").setExecutor(new ClearGrantHistoryCommand());
        getCommand("StaffChat").setExecutor(new StaffChatCommand());
        getCommand("AdminChat").setExecutor(new AdminChatCommand());
        getCommand("ManagementChat").setExecutor(new ManagementChatCommand());
        getCommand("Vanish").setExecutor(new VanishCommand());
        getCommand("ToggleStaffAlerts").setExecutor(new ToggleStaffAlertsCommand());
        getCommand("Gamemode").setExecutor(new GamemodeCommands());
        getCommand("Gmc").setExecutor(new GamemodeCommands());
        getCommand("Gms").setExecutor(new GamemodeCommands());
        getCommand("Gmsp").setExecutor(new GamemodeCommands());
        getCommand("Gma").setExecutor(new GamemodeCommands());
        getCommand("Heal").setExecutor(new HealCommand());
        getCommand("Feed").setExecutor(new FeedCommand());
        getCommand("Clear").setExecutor(new ClearCommand());
        getCommand("ClearChat").setExecutor(new ClearChatCommand());
        getCommand("MuteChat").setExecutor(new MuteChatCommand());
        getCommand("Fly").setExecutor(new FlyCommand());
        getCommand("Teleport").setExecutor(new TeleportCommands());
        getCommand("TeleportHere").setExecutor(new TeleportCommands());
        getCommand("TeleportAll").setExecutor(new TeleportCommands());
        getCommand("Modmode").setExecutor(new ModmodeCommand());
        getCommand("Freeze").setExecutor(new FreezeCommand());
        getCommand("Report").setExecutor(new ReportCommand());
        getCommand("BuildMode").setExecutor(new BuildModeCommand());
        getCommand("ToggleMessages").setExecutor(new ToggleMessagesCommand());
        getCommand("Alts").setExecutor(new AltsCommand());
        getCommand("OnlinePlayers").setExecutor(new ListCommand());
        getCommand("Invsee").setExecutor(new InvseeCommand());
        getCommand("Rank").setExecutor(new RankCommand());
        getCommand("ChatColor").setExecutor(new ChatColorCommand());
        getCommand("Broadcast").setExecutor(new BroadcastCommand());
        getCommand("Settings").setExecutor(new SettingsCommand());
        getCommand("Speed").setExecutor(new SpeedCommand());
        getCommand("Sudo").setExecutor(new SudoCommand());
        getCommand("Balance").setExecutor(new BalanceCommand());
        getCommand("Bounty").setExecutor(new BountyCommand());
        getCommand("Unbounty").setExecutor(new UnbountyCommand());
        getCommand("Pay").setExecutor(new PayCommand());
        getCommand("Economy").setExecutor(new EconomyCommand());
        getCommand("Stats").setExecutor(new StatsCommand());
        getCommand("ResetStats").setExecutor(new ResetStatsCommand());
        getCommand("ToggleScoreboard").setExecutor(new ToggleScoreboardCommand());
        getCommand("EnderChest").setExecutor(new EnderChestCommand());
        getCommand("ReWarn").setExecutor(new ReWarnCommand());
        getCommand("ReMute").setExecutor(new ReMuteCommand());
        getCommand("ReTempMute").setExecutor(new ReTempmuteCommand());
        getCommand("ReBan").setExecutor(new ReBanCommand());
        getCommand("ReTempBan").setExecutor(new ReTempbanCommand());
        getCommand("ReBlacklist").setExecutor(new ReBlacklistCommand());
        getCommand("ServerManager").setExecutor(new ServerManagerCommand());
        getCommand("Ping").setExecutor(new PingCommand());
        getCommand("Reports").setExecutor(new ReportsCommand());
        getCommand("ClearReports").setExecutor(new ClearReportsCommand());
        getCommand("Seen").setExecutor(new SeenCommand());
        getCommand("RankDisguise").setExecutor(new RankDisguiseCommand());
        getCommand("Nickname").setExecutor(new NickCommand());
        getCommand("RealName").setExecutor(new RealNameCommand());
        getCommand("Tags").setExecutor(new TagsCommand());
        getCommand("Tag").setExecutor(new TagCommand());
        getCommand("User").setExecutor(new UserCommand());
        getCommand("Powertool").setExecutor(new PowertoolCommand());
        getCommand("Find").setExecutor(new FindCommand());
        getCommand("Server").setExecutor(new ServerCommand());
        getCommand("Send").setExecutor(new SendCommand());
        getCommand("Glist").setExecutor(new GListCommand());
        getCommand("Hub").setExecutor(new HubCommand());
        getCommand("Spawn").setExecutor(new SpawnCommand());
        getCommand("Skull").setExecutor(new SkullCommand());
        getCommand("ItemName").setExecutor(new ItemNameCommand());
        getCommand("TeleportA").setExecutor(new TeleportCommands());
        getCommand("TeleportAccept").setExecutor(new TeleportCommands());
        getCommand("TeleportDeny").setExecutor(new TeleportCommands());
        getCommand("TeleportCancel").setExecutor(new TeleportCommands());
        getCommand("BuildChat").setExecutor(new BuilderChatCommand());
        getCommand("Message").setExecutor(new MessageCommand());
        getCommand("Reply").setExecutor(new ReplyCommand());
        getCommand("CoreReload").setExecutor(new ReloadCommand());
    }
}
