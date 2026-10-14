package fr.maxlego08.superiorskyblock.buttons.members;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.superiorskyblock.buttons.SuperiorPaginateButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public class IslandMembersButton extends SuperiorPaginateButton {

    public IslandMembersButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        Island island = getIsland(player);
        if (island != null) {
            placeholders.register("members", String.valueOf(island.getIslandMembers(true).size()));
            placeholders.register("max-members", String.valueOf(island.getTeamLimit()));
        }
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        Island island = getIsland(player);
        if (island == null) return;

        List<SuperiorPlayer> members = new ArrayList<>(island.getIslandMembers(true));
        members.sort(Comparator.comparingInt((ToIntFunction<SuperiorPlayer>) value -> value.getPlayerRole().getWeight()).reversed());

        Pagination<SuperiorPlayer> pagination = new Pagination<>();
        members = pagination.paginate(members, this.slots.size(), inventory.getPage());

        for (int i = 0; i != Math.min(members.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            SuperiorPlayer currentPlayer = members.get(i);

            Placeholders placeholders = new Placeholders();
            placeholders.register("name", currentPlayer.getName());
            placeholders.register("role", currentPlayer.getPlayerRole().getDisplayName());

            ItemStack itemStack = getItemStack().build(player, false, placeholders);
            itemStack = ItemSkulls.getPlayerHead(itemStack, currentPlayer.getTextureValue());

            inventory.addItem(slot, itemStack).setClick(event -> menuManager.openInventory(superiorPlayer, "member-manage", playerCache -> playerCache.setTargetPlayer(currentPlayer)));
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        Island island = getIsland(player);
        return island == null ? 0 : island.getIslandMembers(true).size();
    }

    // Utilise l'ile stockee dans le cache (openMembers), sinon celle du joueur
    private Island getIsland(Player player) {
        Island island = getCache(player).getIsland();
        return island != null ? island : getSuperiorPlayer(player).getIsland();
    }
}
