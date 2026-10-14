package fr.maxlego08.superiorskyblock.buttons.warps;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.warps.IslandWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WarpManagePrivacyButton extends SuperiorButton {

    private final String publicMessage;
    private final String privateMessage;

    public WarpManagePrivacyButton(SuperiorSkyblockPlugin plugin, String publicMessage, String privateMessage) {
        super(plugin);
        this.publicMessage = publicMessage;
        this.privateMessage = privateMessage;
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);

        IslandWarp islandWarp = getCache(player).getIslandWarp();
        placeholders.register("name", islandWarp.getName() == null ? "" : islandWarp.getName());
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        IslandWarp islandWarp = getCache(player).getIslandWarp();

        boolean openToPublic = islandWarp.hasPrivateFlag();

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(player);

        if (openToPublic ? !PluginEventsFactory.callIslandOpenWarpEvent(islandWarp.getIsland(), superiorPlayer, islandWarp) :
                !PluginEventsFactory.callIslandCloseWarpEvent(islandWarp.getIsland(), superiorPlayer, islandWarp)) {
            return;
        }

        islandWarp.setPrivateFlag(!openToPublic);

        (openToPublic ? Message.WARP_PUBLIC_UPDATE : Message.WARP_PRIVATE_UPDATE).send(superiorPlayer);
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {

        var buttonPlaceholders = placeholders.child();
        var isPrivate = getCache(player).getIslandWarp().hasPrivateFlag();
        buttonPlaceholders.register("current-privacy", isPrivate ? this.privateMessage : this.publicMessage);
        buttonPlaceholders.register("next-privacy", isPrivate ? this.publicMessage : this.privateMessage);

        return getItemStack().build(player, false, buttonPlaceholders);
    }
}
