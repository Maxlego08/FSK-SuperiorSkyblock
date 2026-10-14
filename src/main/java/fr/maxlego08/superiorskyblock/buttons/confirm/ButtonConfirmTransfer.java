package fr.maxlego08.superiorskyblock.buttons.confirm;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.PlayerCache;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ButtonConfirmTransfer extends SuperiorButton {

    public ButtonConfirmTransfer(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot,
            Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);

        PlayerCache cache = getCache(player);
        // Only the player's own island, the cache may hold an island from another menu
        Island island = superiorPlayer.getIsland();
        SuperiorPlayer newOwner = cache.getTargetPlayer();
        if (island == null || newOwner == null) return;
        if (cache.getIsland() != null && !cache.getIsland().equals(island)) return;

        // Re-check like CmdTransfer, the menu can be opened without the command
        if (!IslandUtils.checkTransferRestrictions(superiorPlayer, island, newOwner)) return;

        // Same behavior as SSB's confirm-transfer menu: event, transfer and broadcast are handled by IslandUtils
        IslandUtils.handleTransferIsland(superiorPlayer, island, newOwner);
    }
}
