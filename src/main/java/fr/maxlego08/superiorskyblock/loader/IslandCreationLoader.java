package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockOffset;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.IslandCreationButton;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

public class IslandCreationLoader extends SuperiorButtonLoader {

    public IslandCreationLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "ISLAND_CREATION");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        ConfigurationSection itemSection = configuration.getConfigurationSection(path);
        if (itemSection == null) {
            Log.warnFromFile("island-creation.yml", "Missing configuration section for item ", path);
            return null;
        }

        // Load schematic
        Schematic schematic = plugin.getSchematics().getSchematic(itemSection.getString("schematic"));
        if (schematic == null) {
            Log.warnFromFile("island-creation.yml", "Invalid schematic for item ", path);
            return null;
        }

        // Load biome (optionnel : sans valeur, le biome de la dimension par defaut est utilise)
        String biomeName = itemSection.getString("biome");
        Biome biome = biomeName == null ? null : getBiome(biomeName, path);

        // Load bonus values
        BigDecimal bonusWorth = getBigDecimal(itemSection.get("bonus", itemSection.get("bonus-worth", 0D)));
        BigDecimal bonusLevel = getBigDecimal(itemSection.get("bonus-level", 0D));

        // Load other configurations
        boolean isOffset = itemSection.getBoolean("offset", false);
        BlockOffset spawnOffset = Optional.ofNullable(itemSection.getString("spawn-offset"))
                .map(Serializers.OFFSET_SPACED_SERIALIZER::deserialize).orElse(null);

        return new IslandCreationButton(plugin, schematic, biome, bonusWorth, bonusLevel, isOffset, spawnOffset);
    }

    // Method to get Biome with error handling
    private Biome getBiome(String biomeName, String path) {
        Biome biome = plugin.getNMSAlgorithms().getBiome(biomeName);
        if (biome != null) {
            return biome;
        }
        Log.warnFromFile("island-creation.yml", "Invalid biome name for item ", path, ": ", biomeName);
        return null; // Biome de la dimension par defaut si invalide
    }

    // Method to safely convert an object to BigDecimal
    private BigDecimal getBigDecimal(Object value) {
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                Log.warn("Invalid number format: " + value);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
}
