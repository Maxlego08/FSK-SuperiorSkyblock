package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.missions.MissionCategoryButton;
import org.bukkit.configuration.file.YamlConfiguration;

public class MissionCategoryLoader extends SuperiorButtonLoader {

    public MissionCategoryLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "MISSION_CATEGORY");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        String categoryName = configuration.getString(path + "category");
        if (categoryName == null || categoryName.isEmpty()) {
            this.plugin.getLogger().severe("Mission category is missing for " + path);
            return null;
        }

        // La catégorie est résolue à l'affichage : le module missions peut être chargé/rechargé après les inventaires
        boolean hideWithoutAccess = configuration.getBoolean(path + "hide-without-access", false);

        return new MissionCategoryButton(this.plugin, categoryName, hideWithoutAccess);
    }
}
