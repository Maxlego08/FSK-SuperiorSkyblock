package fr.maxlego08.superiorskyblock.buttons.missions;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.modules.PluginModule;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.buttons.SuperiorButton;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Bouton qui ouvre le menu d'une catégorie de missions (équivalent de OpenMissionCategoryButton dans SSB).
 */
public class MissionCategoryButton extends SuperiorButton {

    private final String categoryName;
    private final boolean hideWithoutAccess;
    // Cache du nom résolu depuis la clé de config (null = pas encore résolu)
    private String resolvedName;
    private boolean warned = false;

    public MissionCategoryButton(SuperiorSkyblockPlugin plugin, String categoryName, boolean hideWithoutAccess) {
        super(plugin);
        this.categoryName = categoryName;
        this.hideWithoutAccess = hideWithoutAccess;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        MissionCategory missionCategory = getMissionCategory();
        // Catégorie inexistante (module missions désactivé ou nom invalide) : on affiche le bouton else
        if (missionCategory == null) return false;
        if (this.hideWithoutAccess && !hasAccess(getSuperiorPlayer(player), missionCategory)) return false;
        return super.checkPermission(player, inventory, placeholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        MissionCategory missionCategory = getMissionCategory();
        if (missionCategory == null) return;

        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        // Les catégories de missions d'île nécessitent une île, comme dans SSB
        if (!hasAccess(superiorPlayer, missionCategory)) {
            Message.INVALID_ISLAND.send(superiorPlayer);
            return;
        }

        this.plugin.getMenus().openMissionsCategory(superiorPlayer, null, missionCategory);
    }

    private boolean hasAccess(SuperiorPlayer superiorPlayer, MissionCategory missionCategory) {
        return superiorPlayer.hasIsland() || this.plugin.getMissions().isPlayerMissionCategory(missionCategory);
    }

    /**
     * La catégorie est récupérée à chaque fois car le module missions recrée ses objets lors d'un reload.
     * SSB indexe les catégories par leur 'name' (insensible à la casse) : on accepte aussi la clé de config
     * (categories.<clé> dans modules/missions/config.yml) en la convertissant en 'name'.
     */
    private MissionCategory getMissionCategory() {
        MissionCategory missionCategory = this.plugin.getMissions().getMissionCategory(this.categoryName);
        if (missionCategory != null) return missionCategory;

        if (this.resolvedName != null) {
            missionCategory = this.plugin.getMissions().getMissionCategory(this.resolvedName);
            if (missionCategory != null) return missionCategory;
        }

        // Catégorie déjà signalée comme introuvable : on évite de relire le fichier à chaque affichage
        if (this.warned) return null;

        // Le cache est invalide (reload, config modifiée) : on relit la config du module
        this.resolvedName = resolveNameFromKey();
        if (this.resolvedName != null) {
            missionCategory = this.plugin.getMissions().getMissionCategory(this.resolvedName);
            if (missionCategory != null) return missionCategory;
        }

        this.warned = true;
        this.plugin.getLogger().warning("Mission category '" + this.categoryName + "' not found (SUPERIORSKYBLOCK_MISSION_CATEGORY). Available categories: "
                + this.plugin.getMissions().getMissionCategories().stream().map(MissionCategory::getName).collect(Collectors.joining(", ")));
        return null;
    }

    private String resolveNameFromKey() {
        try {
            PluginModule module = this.plugin.getModules().getModule("missions");
            if (module == null) return null;

            File file = new File(module.getModuleFolder(), "config.yml");
            if (!file.exists()) return null;

            ConfigurationSection categories = YamlConfiguration.loadConfiguration(file).getConfigurationSection("categories");
            if (categories == null) return null;

            for (String key : categories.getKeys(false)) {
                if (key.equalsIgnoreCase(this.categoryName)) {
                    return categories.getString(key + ".name", key);
                }
            }
        } catch (Exception exception) {
            this.plugin.getLogger().warning("Unable to read the missions module config: " + exception.getMessage());
        }
        return null;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
