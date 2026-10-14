package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.menu.parser.MenuParserUtils;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.missions.MissionsButton;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MissionsLoader extends SuperiorButtonLoader {

    public MissionsLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "MISSIONS");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        // Options du bouton, avec repli sur les clés à la racine du fichier (format SSB)
        boolean sortByCompletion = configuration.getBoolean(path + "sort-by-completion", configuration.getBoolean("sort-by-completion", false));
        boolean removeCompleted = configuration.getBoolean(path + "remove-completed", configuration.getBoolean("remove-completed", false));

        GameSound completedSound = loadSound(configuration, path + "sounds.completed", Sound.BLOCK_ANVIL_LAND);
        GameSound notCompletedSound = loadSound(configuration, path + "sounds.not-completed", Sound.BLOCK_ANVIL_LAND);
        GameSound canCompleteSound = loadSound(configuration, path + "sounds.can-complete", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        GameSound lockedSound = loadSound(configuration, path + "sounds.locked", Sound.BLOCK_ANVIL_LAND);

        return new MissionsButton(this.plugin, sortByCompletion, removeCompleted, completedSound, notCompletedSound, canCompleteSound, lockedSound);
    }

    /**
     * Charge un son au format SSB (type, volume, pitch). Sans section, le son par défaut de SSB est utilisé.
     * Un type invalide (ex: NONE) désactive le son.
     */
    private GameSound loadSound(YamlConfiguration configuration, String path, Sound defaultSound) {
        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section == null) return new GameSoundImpl(defaultSound, 0.2f, 0.2f);
        return MenuParserUtils.getSound(section);
    }
}
