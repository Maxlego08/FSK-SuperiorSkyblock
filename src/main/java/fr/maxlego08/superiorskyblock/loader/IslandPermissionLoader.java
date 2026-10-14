package fr.maxlego08.superiorskyblock.loader;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.superiorskyblock.buttons.IslandPermissionButton;
import fr.maxlego08.superiorskyblock.utils.Permission;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IslandPermissionLoader extends SuperiorButtonLoader {

    public IslandPermissionLoader(SuperiorSkyblockPlugin plugin) {
        super(plugin, "PERMISSIONS");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        String noRolePermission = configuration.getString(path + "no-role-permission", "");
        String exactRolePermission = configuration.getString(path + "exact-role-permission", "");
        String higherRolePermission = configuration.getString(path + "higher-role-permission", "");

        List<Permission> permissions = new ArrayList<>();
        Optional.ofNullable(configuration.getConfigurationSection(path + "permissions")).ifPresent(permissionSection -> {
            for (String permission : permissionSection.getKeys(false)) {
                Optional.ofNullable(permissionSection.getConfigurationSection(permission)).ifPresent(section -> {
                    if (section.getBoolean("display-menu", true)) {
                        Permission loaded = loadPermission(permission, configuration, path + "permissions." + permission + ".", this.menuManager.getInventoryManager());
                        if (loaded != null) permissions.add(loaded);
                    }
                });
            }
        });

        return new IslandPermissionButton(plugin, noRolePermission, exactRolePermission, higherRolePermission, permissions);
    }

    private Permission loadPermission(String permission, YamlConfiguration configuration, String path, InventoryManager inventoryManager) {

        // Certains privileges sont enregistres depuis entity-categories.yml / interactables.yml
        IslandPrivilege islandPrivilege;
        try {
            islandPrivilege = IslandPrivilege.getByName(permission);
        } catch (NullPointerException error) {
            Log.warnFromFile("permissions.yml", "Invalid island privilege ", permission, ", skipping...");
            return null;
        }

        File file = new File(plugin.getDataFolder(), "inventories/permissions.yml");
        MenuItemStack itemStackEnabled = inventoryManager.loadItemStack(configuration, path + "permission-enabled.", file);
        MenuItemStack itemStackDisabled = inventoryManager.loadItemStack(configuration, path + "permission-disabled.", file);
        MenuItemStack itemStackPermission = inventoryManager.loadItemStack(configuration, path + "role-permission.", file);

        return new Permission(islandPrivilege, itemStackEnabled, itemStackDisabled, itemStackPermission);
    }
}
