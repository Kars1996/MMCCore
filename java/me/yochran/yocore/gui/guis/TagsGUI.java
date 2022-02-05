package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.*;
import me.yochran.yocore.tags.Tag;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TagsGUI extends CustomGUI implements PagedGUI {

    private final yoCore plugin;

    public TagsGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void setupPagedGUI(Map<Integer, Button> buttons, int page) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            int[] info = Utils.getHistorySlotData(entry.getKey());
            if (page == info[0])
                gui.setButton(info[1] + 9, entry.getValue());
        }
    }

    public void setup(int page) {
        int loop = -1;

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        for (Map.Entry<String, Tag> tag : Tag.getTags().entrySet()) {
            loop++;
            ItemBuilder itemBuilder = new ItemBuilder(
                    Utils.getMaterialFromConfig(plugin.getConfig().getString("TagsCommand.TagItem")),
                    1,
                    tag.getValue().getDisplay(),
                    ItemBuilder.formatLore(new String[] {
                            "&3&m-----------------------",
                            "&bTag: &3" + tag.getValue().getID(),
                            "&bPrefix: &3" + tag.getValue().getPrefix(),
                            "&bDisplay: &3" + tag.getValue().getDisplay(),
                            "&3&m-----------------------"
                    })
            );

            if (player.hasPermission(tag.getValue().getPermission()))
                itemBuilder.getLore().add(Utils.translate("&aClick to select this tag."));
            else itemBuilder.getLore().add(Utils.translate("&cYou cannot use this tag."));

            Button button = new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        if (itemBuilder.getLore().contains(Utils.translate("&aClick to select this tag."))) {
                            plugin.tag.put(gui.getPlayer().getUniqueId(), tag.getValue());

                            gui.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.FormatOn")
                                    .replace("%tag%", tag.getValue().getDisplay())));
                        }
                    },
                    itemBuilder.getName(),
                    itemBuilder.getLore()
            );

            buttons.put(loop, button);
        }

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) pages.add((entry.getKey() / 9) + 1);

        Toolbar toolbar = new Toolbar(getGui(), "Tags", page, new ArrayList<>(pages), () -> new BukkitRunnable() {
            @Override
            public void run() {
                TagsGUI tagsGUI = new TagsGUI(player,18, "&aChat tags.");
                tagsGUI.setup(Toolbar.getNewPage().get());
                GUI.open(tagsGUI.getGui());
            }
        }.runTaskLater(plugin, 1));

        toolbar.create(null, null, false);
        setupPagedGUI(buttons, page);
    }
}
