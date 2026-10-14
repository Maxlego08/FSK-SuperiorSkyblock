package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.IslandSettingsButton;
import fr.maxlego08.superiorskyblock.utils.Setting;
import fr.maxlego08.superiorskyblock.utils.SettingOtherButton;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IslandSettingsLoader extends SuperiorButtonLoader {

    public IslandSettingsLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "SETTINGS");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        List<Setting> settings = new ArrayList<>();
        Optional.ofNullable(configuration.getConfigurationSection(path + "settings")).ifPresent(settingsSection -> {
            for (String islandFlagName : settingsSection.getKeys(false)) {
                Optional.ofNullable(settingsSection.getConfigurationSection(islandFlagName)).ifPresent(islandFlagSection -> {
                    if (islandFlagSection.getBoolean("display-menu", true)) {
                        settings.add(loadIslandFlagInfo(islandFlagName, settings.size(), configuration, path + "settings." + islandFlagName + ".", menuManager.getInventoryManager()));
                    }
                });
            }
        });

        return new IslandSettingsButton(plugin, settings);
    }

    private Setting loadIslandFlagInfo(String islandFlagName, int position, YamlConfiguration configuration, String path, InventoryManager inventoryManager) {

        File file = new File(plugin.getDataFolder(), "inventories/settings.yml");

        MenuItemStack itemStackEnabled = inventoryManager.loadItemStack(configuration, path + "settings-enabled.", file);
        MenuItemStack itemStackDisabled = inventoryManager.loadItemStack(configuration, path + "settings-disabled.", file);

        List<SettingOtherButton> settingOtherButtons = new ArrayList<>();
        ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "other-items");
        if (configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                int slot = configuration.getInt(path + "other-items." + key + ".slot");
                MenuItemStack menuItemStack = inventoryManager.loadItemStack(configuration, path + "other-items." + key + ".", file);
                settingOtherButtons.add(new SettingOtherButton(menuItemStack, slot));
            }
        }

        return new Setting(islandFlagName, itemStackEnabled, itemStackDisabled, position, settingOtherButtons);
    }
}
