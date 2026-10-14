package fr.maxlego08.superiorskyblock.buttons.warps;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.warps.WarpCategory;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WarpCategoryManageIconButton extends SuperiorButton {

    public WarpCategoryManageIconButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);

        WarpCategory category = getCache(player).getWarpCategory();
        if (category != null) {
            placeholders.register("name", category.getName() == null ? "" : category.getName());
        }
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        WarpCategory category = getCache(player).getWarpCategory();

        Placeholders buttonPlaceholders = placeholders.child();
        if (category != null) {
            buttonPlaceholders.register("material", category.getIcon(getSuperiorPlayer(player)) == null ? "STONE"
                    : category.getIcon(getSuperiorPlayer(player)).getType().name());
        } else {
            buttonPlaceholders.register("material", "STONE");
        }

        return getItemStack().build(player, false, buttonPlaceholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot,
            Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        WarpCategory category = getCache(player).getWarpCategory();
        if (category == null)
            return;

        this.menuManager.openInventory(player, "warp-category-icon-edit", cache -> {
            cache.setWarpCategory(category);
        });
    }
}
