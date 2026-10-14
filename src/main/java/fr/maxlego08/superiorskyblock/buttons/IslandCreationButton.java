package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.Dimension;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockOffset;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.superiorskyblock.PlayerCache;
import fr.maxlego08.superiorskyblock.ZMenuConfig;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;
import java.util.Locale;

public class IslandCreationButton extends SuperiorButton {
    private final Schematic schematic;
    private final Biome biome;
    private final BigDecimal bonusWorth;
    private final BigDecimal bonusLevel;
    private final boolean isOffset;
    private final BlockOffset spawnOffset;

    public IslandCreationButton(SuperiorSkyblockPlugin plugin, Schematic schematic, Biome biome, BigDecimal bonusWorth, BigDecimal bonusLevel, boolean isOffset, BlockOffset spawnOffset) {
        super(plugin);
        this.schematic = schematic;
        this.biome = biome;
        this.bonusWorth = bonusWorth;
        this.bonusLevel = bonusLevel;
        this.isOffset = isOffset;
        this.spawnOffset = spawnOffset;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {

        super.onClick(player, event, inventory, slot, placeholders);
        PlayerCache playerCache = getCache(player);
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);

        if (event.getClick().isRightClick()) {
            Location previewLocation = plugin.getSettings().getIslandPreviews().getLocations().get(schematic.getName().toLowerCase(Locale.ENGLISH));
            if (previewLocation != null) {
                plugin.getGrid().startIslandPreview(superiorPlayer, schematic.getName(), playerCache.getIslandName());
                return;
            }
        }

        Message.ISLAND_CREATE_PROCCESS_REQUEST.send(superiorPlayer);

        SettingsManager.Worlds worlds = this.plugin.getSettings().getWorlds();
        Dimension dimension = worlds.getDefaultWorldDimension();
        boolean offset = this.isOffset || worlds.getDimensionConfig(dimension).isSchematicOffset();

        Biome biome = this.biome != null ? this.biome : ZMenuConfig.getDefaultBiome();
        this.plugin.getGrid().createIsland(superiorPlayer, schematic.getName(), bonusWorth, bonusLevel, biome, playerCache.getIslandName(), offset, spawnOffset);
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public boolean isOffset() {
        return isOffset;
    }

    public BigDecimal getBonusWorth() {
        return bonusWorth;
    }

    public BigDecimal getBonusLevel() {
        return bonusLevel;
    }

    public BlockOffset getSpawnOffset() {
        return spawnOffset;
    }

    public Biome getBiome() {
        return biome;
    }
}
