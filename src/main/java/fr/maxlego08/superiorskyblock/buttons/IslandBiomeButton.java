package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.world.Dimension;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.args.PluginEventArgs;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEvent;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.PlayerCache;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class IslandBiomeButton extends SuperiorButton {

    private final Biome biome;
    private final boolean shouldCurrentBiomeGlow;
    private final List<Dimension> dimensions;

    public IslandBiomeButton(SuperiorSkyblockPlugin plugin, Biome biome, boolean shouldCurrentBiomeGlow, List<Dimension> dimensions) {
        super(plugin);
        this.biome = biome;
        this.shouldCurrentBiomeGlow = shouldCurrentBiomeGlow;
        this.dimensions = dimensions;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        // Without configured dimensions, the biome is only available in the default dimension (same as SSB)
        Dimension dimension = getDimension(player);
        boolean isAvailable = this.dimensions.isEmpty() ? dimension == plugin.getSettings().getWorlds().getDefaultWorldDimension() : this.dimensions.contains(dimension);
        return isAvailable && super.checkPermission(player, inventory, placeholders);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        placeholders.register("dimension", Formatters.CAPITALIZED_FORMATTER.format(getDimension(player).getName()));
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        ItemStack itemStack = super.getCustomItemStack(player, useCache, placeholders);

        Island island = getIsland(player);

        if (island == null || island.getBiome(getDimension(player)) != this.biome)
            return itemStack;

        if (shouldCurrentBiomeGlow) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            plugin.getNMSAlgorithms().makeItemGlow(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot,
            Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        SuperiorPlayer inventoryViewer = getSuperiorPlayer(player);
        Island island = getIsland(player);
        Dimension dimension = getDimension(player);

        if (island == null)
            return;

        PluginEvent<PluginEventArgs.IslandBiomeChange> biomeChangeEvent = PluginEventsFactory
                .callIslandBiomeChangeEvent(island, inventoryViewer, dimension, this.biome);

        if (biomeChangeEvent.isCancelled())
            return;

        Biome biome = biomeChangeEvent.getArgs().biome;

        island.setBiome(dimension, biome);
        Message.CHANGED_BIOME.send(inventoryViewer, Formatters.CAPITALIZED_FORMATTER.format(biome.getKey().getKey()),
                Formatters.CAPITALIZED_FORMATTER.format(dimension.getName()));

        player.closeInventory();
    }

    private Island getIsland(Player player) {
        PlayerCache playerCache = getCache(player);
        return playerCache.getIsland() != null ? playerCache.getIsland() : getSuperiorPlayer(player).getIsland();
    }

    private Dimension getDimension(Player player) {
        Dimension dimension = getCache(player).getDimension();
        return dimension != null ? dimension : plugin.getSettings().getWorlds().getDefaultWorldDimension();
    }
}
