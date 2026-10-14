package fr.maxlego08.superiorskyblock.buttons.confirm;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
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
        Island island = getCache(player).getIsland();
        if (island == null) island = superiorPlayer.getIsland();
        if (island == null) return;

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
