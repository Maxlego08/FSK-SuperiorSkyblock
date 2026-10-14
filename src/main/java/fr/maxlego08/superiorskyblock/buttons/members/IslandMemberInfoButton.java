package fr.maxlego08.superiorskyblock.buttons.members;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.PlayerCache;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class IslandMemberInfoButton extends SuperiorButton {
    public IslandMemberInfoButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        PlayerCache playerCache = getCache(player);
        placeholders.register("name", playerCache.getTargetPlayer().getName());
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        PlayerCache playerCache = getCache(player);

        SuperiorPlayer currentPlayer = playerCache.getTargetPlayer();
        Placeholders buttonPlaceholders = placeholders.child();
        buttonPlaceholders.register("name", currentPlayer.getName());
        buttonPlaceholders.register("role", currentPlayer.getPlayerRole().getDisplayName());

        ItemStack itemStack = getItemStack().build(player, false, buttonPlaceholders);
        return ItemSkulls.getPlayerHead(itemStack, currentPlayer.getTextureValue());
    }
}
