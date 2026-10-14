package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.superiorskyblock.utils.Setting;
import fr.maxlego08.superiorskyblock.utils.SettingOtherButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.Consumer;

public class IslandSettingsButton extends SuperiorPaginateButton {

    private final List<Setting> settings;

    public IslandSettingsButton(SuperiorSkyblockPlugin plugin, List<Setting> settings) {
        super(plugin);
        this.settings = settings;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        Pagination<Setting> pagination = new Pagination<>();
        List<Setting> settings = pagination.paginate(this.settings, this.slots.size(), inventory.getPage());
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        Island island = superiorPlayer.getIsland();

        for (int i = 0; i != Math.min(settings.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            Setting setting = settings.get(i);

            IslandFlag islandFlag;
            try {
                islandFlag = IslandFlag.getByName(setting.getName());
            } catch (NullPointerException error) {
                continue; // Flag non enregistre (entity-categories.yml modifie), on l'ignore
            }
            MenuItemStack menuItemStack = island.hasSettingsEnabled(islandFlag) ? setting.getItemStackEnabled() : setting.getItemStackDisabled();

            Consumer<InventoryClickEvent> consumer = event -> {

                if (island.hasSettingsEnabled(islandFlag)) {
                    if (!PluginEventsFactory.callIslandDisableFlagEvent(island, superiorPlayer, islandFlag)) return;
                    island.disableSettings(islandFlag);
                } else {
                    if (!PluginEventsFactory.callIslandEnableFlagEvent(island, superiorPlayer, islandFlag)) return;
                    island.enableSettings(islandFlag);
                }

                Message.UPDATED_SETTINGS.send(superiorPlayer, Formatters.CAPITALIZED_FORMATTER.format(islandFlag.getName()));

                onRender(player, inventory); // Refresh
            };

            String name = menuItemStack.getDisplayName();
            List<String> lore = menuItemStack.getLore();

            inventory.addItem(slot, menuItemStack.build(player, false)).setClick(consumer);
            for (SettingOtherButton settingOtherButton : setting.getSettingOtherButtons()) {

                MenuItemStack localMenuItemStack = settingOtherButton.getItemStack();
                localMenuItemStack.setDisplayName(name);
                localMenuItemStack.setLore(lore);

                inventory.addItem(slot + settingOtherButton.getSlot(), localMenuItemStack.build(player, false)).setClick(consumer);
            }
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return settings.size();
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
