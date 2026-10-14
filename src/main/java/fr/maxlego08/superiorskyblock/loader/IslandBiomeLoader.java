package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.world.Dimension;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.IslandBiomeButton;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IslandBiomeLoader extends SuperiorButtonLoader {

    public IslandBiomeLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "BIOMES");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        boolean shouldCurrentBiomeGlow = configuration.getBoolean("current-biome-glow", false);
        Biome biome = Biome.PLAINS;

        String biomeName = configuration.getString(path + "biome", "plains").toLowerCase(Locale.ENGLISH);
        try {
            biome = Registry.BIOME.get(NamespacedKey.minecraft(biomeName));
            if (biome == null) {
                Log.warnFromFile("biomes.yml", "Biome '", biomeName, "' is not valid, skipping...");
                biome = Biome.PLAINS;
            }
        } catch (IllegalArgumentException error) {
            Log.warnFromFile("biomes.yml", "Biome '", biomeName, "' is not valid, skipping...");
        }

        // Dimensions where the biome is available, empty means only the default dimension
        List<Dimension> dimensions = new ArrayList<>();
        if (configuration.isString(path + "dimension")) {
            Dimension dimension = getDimensionSafe(configuration.getString(path + "dimension"));
            if (dimension != null) dimensions.add(dimension);
        } else if (configuration.isList(path + "dimensions")) {
            for (String dimensionName : configuration.getStringList(path + "dimensions")) {
                Dimension dimension = getDimensionSafe(dimensionName);
                if (dimension != null) dimensions.add(dimension);
            }
        }

        return new IslandBiomeButton(plugin, biome, shouldCurrentBiomeGlow, dimensions);
    }

    private Dimension getDimensionSafe(String dimensionName) {
        try {
            return Dimension.getByName(dimensionName);
        } catch (Exception error) {
            Log.warnFromFile("biomes.yml", "Dimension '", dimensionName, "' is not valid, skipping...");
            return null;
        }
    }
}
