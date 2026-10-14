package fr.maxlego08.superiorskyblock.buttons.border;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BorderColorToggleButton extends SuperiorButton {

    private final MenuItemStack menuItemStackEnabled;
    private final MenuItemStack menuItemStackDisabled;

    public BorderColorToggleButton(SuperiorSkyblockPlugin plugin, MenuItemStack menuItemStackEnabled, MenuItemStack menuItemStackDisabled) {
        super(plugin);
        this.menuItemStackEnabled = menuItemStackEnabled;
        this.menuItemStackDisabled = menuItemStackDisabled;
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        SuperiorPlayer inventoryViewer = getSuperiorPlayer(player);
        return (inventoryViewer.hasWorldBorderEnabled() ? this.menuItemStackEnabled : this.menuItemStackDisabled).build(player, false, placeholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        plugin.getCommands().dispatchSubCommand(player, "toggle", "border");
    }
}
