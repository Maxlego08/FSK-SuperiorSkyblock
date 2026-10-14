package fr.maxlego08.superiorskyblock.buttons.warps;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.warps.IslandWarp;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.wrappers.WorldPosition;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.LazyWorldLocation;
import com.bgsoftware.superiorskyblock.core.events.args.PluginEventArgs;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEvent;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.warp.SignWarp;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WarpManageLocationButton extends SuperiorButton {
    public WarpManageLocationButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
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

        Location playerLocation = player.getLocation();

        if (!islandWarp.getIsland().isInsideRange(playerLocation)) {
            Message.SET_WARP_OUTSIDE.send(player);
            return;
        }

        PluginEvent<PluginEventArgs.IslandChangeWarpLocation> changeWarpLocationEvent = PluginEventsFactory.callIslandChangeWarpLocationEvent(islandWarp.getIsland(), player, islandWarp, playerLocation);

        if (changeWarpLocationEvent.isCancelled()) return;


        Message.WARP_LOCATION_UPDATE.send(player);

        // Position + WorldInfo : getLocation() leve une exception si le monde n'est pas charge
        WorldPosition warpPosition = islandWarp.getPosition();
        WorldInfo warpWorld = this.plugin.getGrid().getIslandsWorldInfo(islandWarp.getIsland(), islandWarp.getPositionDimension());
        Location newLocation = changeWarpLocationEvent.getArgs().location;

        if (!isSameLocation(newLocation, warpWorld, warpPosition)) {
            ChunksProvider.loadChunk(ChunkPosition.of(warpWorld, warpPosition), ChunkLoadReason.WARP_SIGN_BREAK, chunk -> {
                SignWarp.trySignWarpBreak(islandWarp, player);
            });
        }

        islandWarp.setLocation(newLocation);

        GameSoundImpl.playSound(player, Menus.MENU_WARP_MANAGE.getSuccessUpdateSound());
    }

    private static boolean isSameLocation(Location location, WorldInfo worldInfo, WorldPosition worldPosition) {
        if (location.getX() != worldPosition.getX() || location.getY() != worldPosition.getY() ||
                location.getZ() != worldPosition.getZ() || location.getYaw() != worldPosition.getYaw() ||
                location.getPitch() != worldPosition.getPitch())
            return false;
        return Objects.equals(LazyWorldLocation.getWorldName(location), worldInfo.getName());
    }
}
