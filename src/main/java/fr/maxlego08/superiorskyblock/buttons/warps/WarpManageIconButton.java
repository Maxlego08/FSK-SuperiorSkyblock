package fr.maxlego08.superiorskyblock.buttons.warps;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.warps.IslandWarp;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WarpManageIconButton extends SuperiorButton {
    public WarpManageIconButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);

        IslandWarp islandWarp = getCache(player).getIslandWarp();
        placeholders.register("name", islandWarp.getName() == null ? "" : islandWarp.getName());
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        IslandWarp islandWarp = getCache(player).getIslandWarp();

        Placeholders buttonPlaceholders = placeholders.child();
        buttonPlaceholders.register("material", islandWarp.getRawIcon() == null ? "STONE" : islandWarp.getRawIcon().getType().name());

        return getItemStack().build(player, false, buttonPlaceholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        IslandWarp islandWarp = getCache(player).getIslandWarp();
        plugin.getMenus().openWarpIconEdit(getSuperiorPlayer(player), null, islandWarp);
    }
}
