package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BannedPlayersButton extends SuperiorPaginateButton {

    public BannedPlayersButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        return getPaginationSize(player) != 0;
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        placeholders.register("banned", String.valueOf(getCache(player).getIsland().getBannedPlayers().size()));
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        Pagination<SuperiorPlayer> pagination = new Pagination<>();
        List<SuperiorPlayer> bannedPlayers = pagination.paginate(getCache(player).getIsland().getBannedPlayers(), this.slots.size(), inventory.getPage());

        for (int i = 0; i != Math.min(bannedPlayers.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            SuperiorPlayer bannedPlayer = bannedPlayers.get(i);
            Placeholders placeholders = new Placeholders();
            placeholders.register("player", bannedPlayer.getName());
            placeholders.register("role", bannedPlayer.getPlayerRole().toString());

            inventory.addItem(slot, ItemSkulls.getPlayerHead(getItemStack().build(player, false, placeholders), bannedPlayer.getTextureValue())).setClick(event -> {
                plugin.getCommands().dispatchSubCommand(player, "unban", bannedPlayer.getName());
                menuManager.openInventory(player, "banned-players");
            });
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return getCache(player).getIsland().getBannedPlayers().size();
    }
}
