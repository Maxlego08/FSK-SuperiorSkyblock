package fr.maxlego08.superiorskyblock;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.hooks.MenusProvider;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.island.SortingType;
import com.bgsoftware.superiorskyblock.api.island.warps.IslandWarp;
import com.bgsoftware.superiorskyblock.api.island.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.menu.ISuperiorMenu;
import com.bgsoftware.superiorskyblock.api.menu.MenuIslandCreationConfig;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.Dimension;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.superiorskyblock.buttons.IslandCreationButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ZMenusProvider implements MenusProvider {

    private final SuperiorSkyblockPlugin plugin;
    private final Map<Schematic, MenuIslandCreationConfig> ISLAND_CREATION_CONFIG_CACHE = new IdentityHashMap<>();

    private final ZMenuManager zMenuManager;
    private final MenusProvider originalMenusProvider;

    public ZMenusProvider(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
        this.zMenuManager = new ZMenuManager(plugin);
        this.originalMenusProvider = plugin.getProviders().getMenusProvider();
    }

    public ZMenuManager getMenuManager() {
        return zMenuManager;
    }

    @Override
    public void initializeMenus() {
        this.originalMenusProvider.initializeMenus();
        this.zMenuManager.registerButtons();
        loadInventories();
    }

    /**
     * Recharge les inventaires zMenu et vide le cache des configurations de création d'île,
     * sinon les anciens IslandCreationButton resteraient utilisés après un reload.
     */
    public void loadInventories() {
        this.ISLAND_CREATION_CONFIG_CACHE.clear();
        this.zMenuManager.loadInventories();
    }

    @Override
    public void openBankLogs(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "bank-logs", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshBankLogs(Island island) {
        refreshInventories(MenuType.BANK_LOGS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openBiomes(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland, Dimension dimension) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(dimension, "dimension parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "biomes", cache -> {
            cache.setIsland(targetIsland);
            cache.setDimension(dimension);
        });
    }

    @Override
    public void openBorderColor(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "border-color");
    }

    @Override
    public void openConfirmBan(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland,
            SuperiorPlayer bannedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(bannedPlayer, "bannedPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "confirm-ban", cache -> {
            cache.setIsland(targetIsland);
            cache.setTargetPlayer(bannedPlayer);
        });
    }

    @Override
    public void openConfirmDisband(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "confirm-disband", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void openConfirmKick(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland,
            SuperiorPlayer kickedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(kickedPlayer, "kickedPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "confirm-kick", cache -> {
            cache.setIsland(targetIsland);
            cache.setTargetPlayer(kickedPlayer);
        });
    }

    @Override
    public void openConfirmLeave(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "confirm-leave");
    }

    @Override
    public void openConfirmTransfer(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer newOwner) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(newOwner, "newOwner parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "confirm-transfer", cache -> {
            cache.setTargetPlayer(newOwner);
            cache.setIsland(targetIsland);
        });
    }

    @Override
    public void openControlPanel(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "control-panel", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void openCoops(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "coops", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshCoops(Island island) {
        refreshInventories(MenuType.COOPS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openCounts(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "counts", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshCounts(Island island) {
        refreshInventories(MenuType.COUNTS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openGlobalWarps(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "global-warps");
    }

    @Override
    public void refreshGlobalWarps() {
        refreshInventories(MenuType.GLOBAL_WARPS);
    }

    @Override
    public void openIslandBank(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "island-bank", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshIslandBank(Island island) {
        refreshInventories(MenuType.ISLAND_BANK, cache -> cache.getIsland() == island);
    }

    @Override
    public void openIslandBannedPlayers(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "banned-players", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshIslandBannedPlayers(Island island) {
        refreshInventories(MenuType.BANNED_PLAYERS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openIslandChest(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "island-chests", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshIslandChest(Island island) {
        refreshInventories(MenuType.ISLAND_CHESTS, cache -> cache.getIsland() == island);
    }

    @Override
    public MenuIslandCreationConfig getIslandCreationConfig(Schematic schematic) {
        Inventory creationMenu = this.zMenuManager.getInventory(MenuType.ISLAND_CREATION);
        // Inventaire non chargé (désactivé ou en cours de reload) : on ne met pas la config par défaut en cache
        if (creationMenu == null) return new ZMenuConfig.IslandCreation(schematic, null);

        return ISLAND_CREATION_CONFIG_CACHE.computeIfAbsent(schematic, unused -> {
            for (Button button : creationMenu.getButtons()) {
                if (button instanceof IslandCreationButton && schematic.equals(((IslandCreationButton) button).getSchematic())) {
                    return new ZMenuConfig.IslandCreation((IslandCreationButton) button);
                }
            }

            return new ZMenuConfig.IslandCreation(schematic, null);
        });
    }

    @Override
    public void openIslandCreation(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, String islandName) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandName, "islandName parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "island-creation", cache -> cache.setIslandName(islandName));
    }

    @Override
    public void openIslandRate(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "island-rate", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void openIslandRatings(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "island-ratings", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshIslandRatings(Island island) {
        refreshInventories(MenuType.ISLAND_RATINGS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openMemberManage(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "member-manage",
                playerCache -> playerCache.setTargetPlayer(islandMember));
    }

    @Override
    public void destroyMemberManage(SuperiorPlayer islandMember) {
        destroyInventories(MenuType.MEMBER_MANAGE, cache -> cache.getTargetPlayer() == islandMember);
    }

    @Override
    public void openMemberRole(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "member-role",
                playerCache -> playerCache.setTargetPlayer(islandMember));
    }

    @Override
    public void destroyMemberRole(SuperiorPlayer islandMember) {
        destroyInventories(MenuType.MEMBER_ROLE, cache -> cache.getTargetPlayer() == islandMember);
    }

    @Override
    public void openMembers(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "members", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshMembers(Island island) {
        refreshInventories(MenuType.MEMBERS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openMissions(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "missions");
    }

    @Override
    public void openMissionsCategory(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu,
            MissionCategory missionCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "missions-category", cache -> cache.setMissionCategory(missionCategory));
    }

    @Override
    public void refreshMissionsCategory(MissionCategory missionCategory) {
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        // SSB appelle cette méthode depuis un thread async lors de la complétion d'une mission
        BukkitExecutor.ensureMain(() -> refreshInventories(MenuType.MISSIONS_CATEGORY, cache -> cache.getMissionCategory() != null
                && cache.getMissionCategory().getName().equalsIgnoreCase(missionCategory.getName())));
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland,
            SuperiorPlayer permissiblePlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(permissiblePlayer, "permissiblePlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "permissions");
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland,
            PlayerRole permissibleRole) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(permissibleRole, "permissibleRole parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "permissions");
    }

    @Override
    public void refreshPermissions(Island island) {
        refreshInventories(MenuType.PERMISSIONS, cache -> cache.getIsland() == island);
    }

    @Override
    public void refreshPermissions(Island island, SuperiorPlayer permissiblePlayer) {
        refreshInventories(MenuType.COUNTS,
                cache -> cache.getIsland() == island && cache.getTargetPlayer() == permissiblePlayer);
    }

    @Override
    public void refreshPermissions(Island island, PlayerRole permissibleRole) {
        // TODO
    }

    @Override
    public void updatePermission(IslandPrivilege islandPrivilege) {
        // The default implementation does not care if the island privilege is valid for
        // showing the island
        // privileges in the menu. If the island privilege is not valid at the time of
        // opening the menu, it
        // will show it as it was disabled. This is the responsibility of the server
        // owners to properly
        // configure the menu.
    }

    @Override
    public void openPlayerLanguage(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "player-language");
    }

    @Override
    public void openSettings(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "settings");
    }

    @Override
    public void refreshSettings(Island island) {
        refreshInventories(MenuType.SETTINGS, cache -> cache.getIsland() == island);
    }

    @Override
    public void updateSettings(IslandFlag islandFlag) {
        // The default implementation does not care if the island flag is valid for
        // showing the island flags
        // in the menu. If the island flag is not valid at the time of opening the menu,
        // it will show it as
        // it was disabled. This is the responsibility of the server owners to properly
        // configure the menu.
    }

    @Override
    public void openTopIslands(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, SortingType sortingType) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "top-islands", cache -> cache.setSortingType(sortingType));
    }

    @Override
    public void refreshTopIslands(SortingType sortingType) {
        refreshInventories(MenuType.TOP_ISLANDS, cache -> cache.getSortingType() == sortingType);
    }

    @Override
    public void openUniqueVisitors(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "unique-visitors", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshUniqueVisitors(Island island) {
        refreshInventories(MenuType.UNIQUE_VISITORS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openUpgrades(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "upgrades", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshUpgrades(Island island) {
        refreshInventories(MenuType.UPGRADES, cache -> cache.getIsland() == island);
    }

    @Override
    public void openValues(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "values");
    }

    @Override
    public void refreshValues(Island island) {
        refreshInventories(MenuType.VALUES, cache -> cache.getIsland() == island);
    }

    @Override
    public void openVisitors(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "visitors", cache -> cache.setIsland(targetIsland));
    }

    @Override
    public void refreshVisitors(Island island) {
        refreshInventories(MenuType.VISITORS, cache -> cache.getIsland() == island);
    }

    @Override
    public void openWarpCategories(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");

        if (this.plugin.getSettings().isWarpCategories() && targetIsland.getWarpCategories().size() > 1) {
            this.zMenuManager.openInventory(targetPlayer, "warp-categories", cache -> cache.setIsland(targetIsland));
        } else {
            WarpCategory warpCategory = targetIsland.getWarpCategories().values().stream().findFirst().orElseGet(() -> targetIsland.createWarpCategory("Default Category"));
            openWarps(targetPlayer, previousMenu, warpCategory);
        }
    }

    @Override
    public void refreshWarpCategories(Island island) {
        refreshInventories(MenuType.WARP_CATEGORIES, cache -> cache.getIsland() == island);
    }

    @Override
    public void destroyWarpCategories(Island island) {
        destroyInventories(MenuType.WARP_CATEGORIES, cache -> cache.getIsland() == island);
    }

    @Override
    public void openWarpCategoryIconEdit(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu,
            WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "warp-category-icon-edit", cache -> cache.setWarpCategory(targetCategory));
    }

    @Override
    public void openWarpCategoryManage(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu,
            WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "warp-category-manage", cache -> cache.setWarpCategory(targetCategory));
    }

    @Override
    public void refreshWarpCategoryManage(WarpCategory warpCategory) {
        refreshInventories(MenuType.WARP_CATEGORY_MANAGE, cache -> cache.getWarpCategory() == warpCategory);
    }

    @Override
    public void openWarpIconEdit(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, IslandWarp targetWarp) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetWarp, "targetWarp parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "warp-icon-edit", cache -> cache.setIslandWarp(targetWarp));
    }

    @Override
    public void openWarpManage(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, IslandWarp targetWarp) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetWarp, "targetWarp parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "warp-manage", cache -> cache.setIslandWarp(targetWarp));
    }

    @Override
    public void refreshWarpManage(IslandWarp islandWarp) {
        refreshInventories(MenuType.WARP_MANAGE, cache -> cache.getIslandWarp() == islandWarp);
    }

    @Override
    public void openWarps(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        this.zMenuManager.openInventory(targetPlayer, "warps", cache -> cache.setWarpCategory(targetCategory));
    }

    @Override
    public void refreshWarps(WarpCategory warpCategory) {
        refreshInventories(MenuType.WARPS, cache -> cache.getWarpCategory() == warpCategory);
    }

    @Override
    public void destroyWarps(WarpCategory warpCategory) {
        destroyInventories(MenuType.WARPS, cache -> cache.getWarpCategory() == warpCategory);
    }

    private void refreshInventories(MenuType menuType) {
        iterateOpenedInventories(menuType, (inventory, player) -> this.zMenuManager.getInventoryManager().updateInventory(player));
    }

    private void refreshInventories(MenuType menuType, Predicate<PlayerCache> predicate) {
        iterateOpenedInventories(menuType, (inventory, player) -> {
            PlayerCache playerCache = this.zMenuManager.getCacheOrNull(player);
            if (playerCache != null && predicate.apply(playerCache))
                this.zMenuManager.getInventoryManager().updateInventory(player);
        });
    }

    private void destroyInventories(MenuType menuType, Predicate<PlayerCache> predicate) {
        iterateOpenedInventories(menuType, (inventory, player) -> {
            PlayerCache playerCache = this.zMenuManager.getCacheOrNull(player);
            if (playerCache != null && predicate.apply(playerCache)) player.closeInventory();
        });
    }

    private void iterateOpenedInventories(MenuType menuType, BiConsumer<Inventory, Player> consumer) {
        Inventory inventory = this.zMenuManager.getInventory(menuType);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.zMenuManager.getInventoryManager().getCurrentPlayerInventory(player).ifPresent(openedInventory -> {
                if (inventory == openedInventory) {
                    consumer.accept(inventory, player);
                }
            });
        }
    }

}
