package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TargetShowButton extends SuperiorButton {
    public TargetShowButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        PlayerCache playerCache = getCache(player);
        SuperiorPlayer targetPlayer = playerCache.getTargetPlayer();
        Placeholders buttonPlaceholders = placeholders.child();
        buttonPlaceholders.register("player-name", targetPlayer.getName());

        ItemStack itemStack = this.getItemStack().build(player, false, buttonPlaceholders);
        return ItemSkulls.getPlayerHead(itemStack, targetPlayer.getTextureValue());
    }
}
