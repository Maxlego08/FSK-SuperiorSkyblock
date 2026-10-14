package fr.maxlego08.superiorskyblock.buttons.confirm;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
import com.bgsoftware.superiorskyblock.island.privilege.IslandPrivileges;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Collections;

public class ButtonConfirmDisband extends SuperiorButton {

    public ButtonConfirmDisband(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        // On agit uniquement sur l'île du joueur, le cache peut contenir une île d'un autre menu
        Island island = superiorPlayer.getIsland();
        if (island == null) return;

        Island cachedIsland = getCache(player).getIsland();
        if (cachedIsland != null && !cachedIsland.equals(island)) return;

        // Mêmes vérifications que CmdDisband, le menu peut être ouvert sans passer par la commande
        if (!island.hasPermission(superiorPlayer, IslandPrivileges.DISBAND_ISLAND)) {
            Message.NO_DISBAND_PERMISSION.send(superiorPlayer);
            return;
        }

        if (!superiorPlayer.hasDisbands() && plugin.getSettings().getDisbandCount() >= 0) {
            Message.NO_MORE_DISBANDS.send(superiorPlayer);
            return;
        }

        if (PluginEventsFactory.callIslandDisbandEvent(island, superiorPlayer)) {
            IslandUtils.sendMessage(island, Message.DISBAND_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());

            Message.DISBANDED_ISLAND.send(superiorPlayer);

            if (BuiltinModules.BANK.getConfiguration().hasDisbandRefund()) {
                BigDecimal disbandRefund = BuiltinModules.BANK.getConfiguration().getDisbandRefund();
                Message.DISBAND_ISLAND_BALANCE_REFUND.send(island.getOwner(), Formatters.NUMBER_FORMATTER.format(island.getIslandBank().getBalance().multiply(disbandRefund)));
            }

            // Uniquement si la limite de disband est active
            if (plugin.getSettings().getDisbandCount() >= 0) {
                superiorPlayer.setDisbands(superiorPlayer.getDisbands() - 1);
            }

            island.disbandIsland();
        }
    }
}
