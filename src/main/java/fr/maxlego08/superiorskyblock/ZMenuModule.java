package fr.maxlego08.superiorskyblock;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.modules.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public final class ZMenuModule extends PluginModule {

    private ZMenusProvider menusProvider;

    public ZMenuModule() {
        super("zMenu", "Maxlego08");
    }

    @Override
    public void onEnable(SuperiorSkyblock superiorSkyblock) {
        SuperiorSkyblockPlugin plugin = (SuperiorSkyblockPlugin) Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2");
        SuperiorSkyblockAPI.getProviders().setMenusProvider(this.menusProvider = new ZMenusProvider(plugin));
    }

    @Override
    public void onReload(SuperiorSkyblock superiorSkyblock) {
        menusProvider.loadInventories();
    }

    @Override
    public void onDisable(SuperiorSkyblock superiorSkyblock) {
    }

    @Override
    public Listener[] getModuleListeners(SuperiorSkyblock superiorSkyblock) {
        return new Listener[]{this.menusProvider.getMenuManager()};
    }

    @Override
    public SuperiorCommand[] getSuperiorCommands(SuperiorSkyblock superiorSkyblock) {
        return new SuperiorCommand[0];
    }

    @Override
    public SuperiorCommand[] getSuperiorAdminCommands(SuperiorSkyblock superiorSkyblock) {
        return new SuperiorCommand[0];
    }
}
