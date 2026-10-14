package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.upgrades.UpgradeLevel;
import com.bgsoftware.superiorskyblock.api.upgrades.cost.UpgradeCost;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class UpgradeButton extends SuperiorButton {
    private final UpgradeLevel upgradeLevel;
    private final Upgrade upgrade;
    private final MenuItemStack errorItemStack;

    public UpgradeButton(SuperiorSkyblockPlugin plugin, UpgradeLevel upgradeLevel, Upgrade upgrade, MenuItemStack errorItemStack) {
        super(plugin);
        this.upgradeLevel = upgradeLevel;
        this.upgrade = upgrade;
        this.errorItemStack = errorItemStack;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        Island island = getCache(player).getIsland();
        UpgradeLevel upgradeLevel = island.getUpgradeLevel(this.upgrade);
        return super.checkPermission(player, inventory, placeholders) && this.upgradeLevel.getLevel() == upgradeLevel.getLevel();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        plugin.getCommands().dispatchSubCommand(player, "rankup", upgrade.getName());
        menuManager.openInventory(player, "upgrades");
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {
        UpgradeCost levelCost = this.upgradeLevel.getCost();
        return levelCost.hasEnoughBalance(getSuperiorPlayer(player)) ? super.getCustomItemStack(player, useCache, placeholders) : this.errorItemStack.build(player, false, placeholders);
    }
}
