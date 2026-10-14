package fr.maxlego08.superiorskyblock;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandKickEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandQuitEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandUncoopPlayerEvent;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.maxlego08.superiorskyblock.buttons.BannedPlayersButton;
import fr.maxlego08.superiorskyblock.buttons.CoopsButton;
import fr.maxlego08.superiorskyblock.buttons.CountsButton;
import fr.maxlego08.superiorskyblock.buttons.IslandChestButton;
import fr.maxlego08.superiorskyblock.buttons.RatingsButton;
import fr.maxlego08.superiorskyblock.buttons.TargetShowButton;
import fr.maxlego08.superiorskyblock.buttons.UniqueVisitorsButton;
import fr.maxlego08.superiorskyblock.buttons.VisitorsButton;
import fr.maxlego08.superiorskyblock.buttons.bank.BankLogsButton;
import fr.maxlego08.superiorskyblock.buttons.confirm.ButtonConfirmBan;
import fr.maxlego08.superiorskyblock.buttons.confirm.ButtonConfirmDisband;
import fr.maxlego08.superiorskyblock.buttons.confirm.ButtonConfirmKick;
import fr.maxlego08.superiorskyblock.buttons.confirm.ButtonConfirmLeave;
import fr.maxlego08.superiorskyblock.buttons.confirm.ButtonConfirmTransfer;
import fr.maxlego08.superiorskyblock.buttons.members.IslandMemberBanButton;
import fr.maxlego08.superiorskyblock.buttons.members.IslandMemberInfoButton;
import fr.maxlego08.superiorskyblock.buttons.members.IslandMemberKickButton;
import fr.maxlego08.superiorskyblock.buttons.members.IslandMembersButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoriesButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryIconConfirmButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryIconLoreButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryIconNameButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryIconTypeButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryManageIconButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpCategoryManageRenameButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpIconConfirmButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpIconDisplayButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpIconLoreButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpIconNameButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpIconTypeButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpManageIconButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpManageLocationButton;
import fr.maxlego08.superiorskyblock.buttons.warps.WarpManageRenameButton;
import fr.maxlego08.superiorskyblock.loader.BankActionLoader;
import fr.maxlego08.superiorskyblock.loader.BankLogsSortLoader;
import fr.maxlego08.superiorskyblock.loader.BlockValueLoader;
import fr.maxlego08.superiorskyblock.loader.BorderColorLoader;
import fr.maxlego08.superiorskyblock.loader.BorderToggleLoader;
import fr.maxlego08.superiorskyblock.loader.GlobalWarpsLoader;
import fr.maxlego08.superiorskyblock.loader.IslandBiomeLoader;
import fr.maxlego08.superiorskyblock.loader.IslandCreationLoader;
import fr.maxlego08.superiorskyblock.loader.IslandMemberRoleLoader;
import fr.maxlego08.superiorskyblock.loader.IslandPermissionLoader;
import fr.maxlego08.superiorskyblock.loader.IslandSettingsLoader;
import fr.maxlego08.superiorskyblock.loader.IslandTopLoader;
import fr.maxlego08.superiorskyblock.loader.IslandTopSortLoader;
import fr.maxlego08.superiorskyblock.loader.MissionCategoryLoader;
import fr.maxlego08.superiorskyblock.loader.MissionsLoader;
import fr.maxlego08.superiorskyblock.loader.PlayerLanguageLoader;
import fr.maxlego08.superiorskyblock.loader.RateLoader;
import fr.maxlego08.superiorskyblock.loader.UpgradeLoader;
import fr.maxlego08.superiorskyblock.loader.WarpPrivacyLoader;
import fr.maxlego08.superiorskyblock.loader.WarpsLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ZMenuManager implements Listener {

    private final SuperiorSkyblockPlugin plugin;
    private final InventoryManager inventoryManager;
    private final ButtonManager buttonManager;
    private final Map<Player, PlayerCache> caches = new HashMap<>();
    private final EnumMap<MenuType, Inventory> inventories = new EnumMap<>(MenuType.class);

    public ZMenuManager(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
        this.inventoryManager = getProvider(InventoryManager.class);
        this.buttonManager = getProvider(ButtonManager.class);
    }

    private <T> T getProvider(Class<T> classz) {
        RegisteredServiceProvider<T> provider = Bukkit.getServer().getServicesManager().getRegistration(classz);
        return provider == null ? null : provider.getProvider() != null ? provider.getProvider() : null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.caches.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(IslandKickEvent event) {
        clearCache(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(IslandUncoopPlayerEvent event) {
        clearCache(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(IslandQuitEvent event) {
        clearCache(event.getPlayer());
    }

    @EventHandler
    public void onIslandDelete(IslandDisbandEvent event) {
        for (SuperiorPlayer islandMember : event.getIsland().getIslandMembers(true)) {
            clearCache(islandMember);
        }
    }

    /**
     * Clears the cache for a given {@link SuperiorPlayer}. If the player is online,
     * their inventory is closed before removing them from the cache.
     *
     * @param superiorPlayer the player for whom the cache is to be cleared
     */
    private void clearCache(SuperiorPlayer superiorPlayer) {
        if (superiorPlayer.isOnline()) {
            Player player = superiorPlayer.asPlayer();
            this.inventoryManager.getCurrentPlayerInventory(player).ifPresent(i -> player.closeInventory());
            this.caches.remove(player);
        }
    }

    /**
     * Retrieves a {@link PlayerCache} for the given {@link Player}. If a cache does
     * not already exist for the given
     * player, a new one is created.
     *
     * @param player the player to retrieve the cache for
     * @return the cache for the given player
     */
    public PlayerCache getCache(Player player) {
        return this.caches.computeIfAbsent(player, PlayerCache::new);
    }

    /**
     * Retrieves a {@link PlayerCache} for the given {@link Player}. If none exists,
     * {@code null} is returned.
     *
     * @param player the player to retrieve the cache for
     * @return the cache for the given player, or {@code null} if none exists
     */
    public PlayerCache getCacheOrNull(Player player) {
        return this.caches.get(player);
    }

    /**
     * Registers all the buttons from the plugin.
     * This method is called in the {@link SuperiorSkyblockPlugin#onLoad()} method.
     */
    public void registerButtons() {
        this.buttonManager.unregisters(this.plugin);

        this.buttonManager.register(new IslandCreationLoader(this.plugin));
        this.buttonManager.register(new IslandSettingsLoader(this.plugin));
        this.buttonManager.register(new IslandBiomeLoader(this.plugin));
        this.buttonManager.register(new IslandMemberRoleLoader(this.plugin));
        this.buttonManager.register(new IslandPermissionLoader(this.plugin));
        this.buttonManager.register(new IslandTopLoader(this.plugin));
        this.buttonManager.register(new IslandTopSortLoader(this.plugin));
        this.buttonManager.register(new BorderColorLoader(this.plugin));
        this.buttonManager.register(new BorderToggleLoader(this.plugin));
        this.buttonManager.register(new PlayerLanguageLoader(this.plugin));
        this.buttonManager.register(new BlockValueLoader(this.plugin));
        this.buttonManager.register(new BankLogsSortLoader(this.plugin));
        this.buttonManager.register(new GlobalWarpsLoader(this.plugin));
        this.buttonManager.register(new BankActionLoader(this.plugin));
        this.buttonManager.register(new RateLoader(this.plugin));
        this.buttonManager.register(new UpgradeLoader(this.plugin));
        this.buttonManager.register(new WarpsLoader(this.plugin));
        this.buttonManager.register(new WarpPrivacyLoader(this.plugin));
        this.buttonManager.register(new MissionCategoryLoader(this.plugin));
        this.buttonManager.register(new MissionsLoader(this.plugin));

        this.buttonManager.register(new NoneLoader(this.plugin, IslandMembersButton.class, "SUPERIORSKYBLOCK_MEMBERS"));
        this.buttonManager.register(new NoneLoader(this.plugin, IslandMemberInfoButton.class, "SUPERIORSKYBLOCK_MEMBER_INFO"));
        this.buttonManager.register(new NoneLoader(this.plugin, IslandMemberBanButton.class, "SUPERIORSKYBLOCK_MEMBER_BAN"));
        this.buttonManager.register(new NoneLoader(this.plugin, IslandMemberKickButton.class, "SUPERIORSKYBLOCK_MEMBER_KICK"));
        this.buttonManager.register(new NoneLoader(this.plugin, ButtonConfirmBan.class, "SUPERIORSKYBLOCK_CONFIRM_BAN"));
        this.buttonManager.register(new NoneLoader(this.plugin, ButtonConfirmDisband.class, "SUPERIORSKYBLOCK_CONFIRM_DISBAND"));
        this.buttonManager.register(new NoneLoader(this.plugin, ButtonConfirmKick.class, "SUPERIORSKYBLOCK_CONFIRM_KICK"));
        this.buttonManager.register(new NoneLoader(this.plugin, ButtonConfirmLeave.class, "SUPERIORSKYBLOCK_CONFIRM_LEAVE"));
        this.buttonManager.register(new NoneLoader(this.plugin, ButtonConfirmTransfer.class, "SUPERIORSKYBLOCK_CONFIRM_TRANSFER"));
        this.buttonManager.register(new NoneLoader(this.plugin, TargetShowButton.class, "SUPERIORSKYBLOCK_TARGET_SHOW"));
        this.buttonManager.register(new NoneLoader(this.plugin, BankLogsButton.class, "SUPERIORSKYBLOCK_BANK_LOGS"));
        this.buttonManager.register(new NoneLoader(this.plugin, BannedPlayersButton.class, "SUPERIORSKYBLOCK_BANNED_PLAYERS"));
        this.buttonManager.register(new NoneLoader(this.plugin, CoopsButton.class, "SUPERIORSKYBLOCK_COOPS"));
        this.buttonManager.register(new NoneLoader(this.plugin, CountsButton.class, "SUPERIORSKYBLOCK_COUNTS"));
        this.buttonManager.register(new NoneLoader(this.plugin, RatingsButton.class, "SUPERIORSKYBLOCK_RATINGS"));
        this.buttonManager.register(new NoneLoader(this.plugin, IslandChestButton.class, "SUPERIORSKYBLOCK_CHESTS"));
        this.buttonManager.register(new NoneLoader(this.plugin, UniqueVisitorsButton.class, "SUPERIORSKYBLOCK_UNIQUE_VISITORS"));
        this.buttonManager.register(new NoneLoader(this.plugin, VisitorsButton.class, "SUPERIORSKYBLOCK_VISITORS"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpManageLocationButton.class, "SUPERIORSKYBLOCK_WARP_MANAGE_LOCATION"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpManageRenameButton.class, "SUPERIORSKYBLOCK_WARP_MANAGE_RENAME"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpManageIconButton.class, "SUPERIORSKYBLOCK_WARP_MANAGE_ICON"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpIconConfirmButton.class, "SUPERIORSKYBLOCK_WARP_ICON_CONFIRM"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpIconNameButton.class, "SUPERIORSKYBLOCK_WARP_ICON_NAME"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpIconLoreButton.class, "SUPERIORSKYBLOCK_WARP_ICON_LORE"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpIconTypeButton.class, "SUPERIORSKYBLOCK_WARP_ICON_TYPE"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpIconDisplayButton.class, "SUPERIORSKYBLOCK_WARP_ICON_DISPLAY"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoriesButton.class, "SUPERIORSKYBLOCK_WARP_CATEGORIES"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryIconConfirmButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_ICON_CONFIRM"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryIconLoreButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_ICON_LORE"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryIconNameButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_ICON_NAME"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryIconTypeButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_ICON_TYPE"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryManageIconButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_MANAGE_ICON"));
        this.buttonManager.register(new NoneLoader(this.plugin, WarpCategoryManageRenameButton.class,"SUPERIORSKYBLOCK_WARP_CATEGORY_MANAGE_RENAME"));
    }

    /**
     * Loads inventory configurations from files located in the "inventories"
     * folder.
     * If the folder or any expected inventory file does not exist, they are
     * created.
     * Then, each inventory file is processed to load its corresponding inventory
     * data into the inventory manager. Any invalid or erroneous inventory files are
     * logged with stack traces for debugging.
     */
    public void loadInventories() {

        File folder = new File(plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Save inventories files
        List<String> inventories = Arrays.asList("island-creation", "settings", "biomes", "members", "member-manage", "member-role", //
                "permissions", "control-panel", "top-islands", "border-color", "confirm-ban", "confirm-disband", "confirm-kick", "confirm-transfer", //
                "confirm-leave", "warps", "player-language", "values", "bank-logs", "banned-players", "coops", "counts", "visitors", //
                "upgrades", "warp-manage", "warp-icon-edit", "global-warps", "island-bank", "island-ratings", "island-rate", //
                "island-chests", "unique-visitors", "warp-categories", "warp-category-manage", "warp-category-icon-edit", //
                "missions", "missions-category");

        inventories.forEach(inventoryName -> {
            if (!new File(this.plugin.getDataFolder(), "inventories/" + inventoryName + ".yml").exists()) {
                this.saveResource("inventories/" + inventoryName + ".yml", new File(this.plugin.getDataFolder(), "inventories/" + inventoryName + ".yml"));
            }
        });

        this.inventoryManager.deleteInventories(this.plugin);
        this.inventories.clear();

        this.files(folder, file -> {
            try {
                Inventory inventory = this.inventoryManager.loadInventory(this.plugin, file);
                if (inventory == null) return; // Inventaire désactivé (enable: false) ou en attente d'une condition de chargement
                MenuType menuType = MenuType
                        .valueOf(inventory.getFileName().toUpperCase(Locale.ENGLISH).replace("-", "_"));
                ZMenuManager.this.inventories.put(menuType, inventory);
            } catch (InventoryException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void saveResource(String resourcePath, File outputFile) {
        try (InputStream in = ZMenuModule.class.getClassLoader().getResourceAsStream(resourcePath)) {
            System.out.println("IN " + in);
            if (in == null) {
                throw new IOException("Ressource introuvable : " + resourcePath);
            }
            var r = Files.copy(in, outputFile.toPath());
            System.out.println(" Result ! " + r);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Recursively iterates through all files in the given folder, skipping the
     * folder itself, and applies the given consumer to each file that ends with
     * ".yml".
     *
     * @param folder   the folder to iterate through
     * @param consumer the consumer to apply to each file
     */
    private void files(File folder, Consumer<File> consumer) {
        try (Stream<Path> s = Files.walk(Paths.get(folder.getPath()))) {
            s.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(consumer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Retrieves the PlayerCache associated with the given player and applies the
     * provided consumer to it.
     *
     * @param player   the player whose cache is to be retrieved
     * @param consumer the consumer to be applied to the player's cache
     */
    public void cache(Player player, Consumer<PlayerCache> consumer) {
        consumer.accept(getCache(player));
    }

    /**
     * Opens the inventory with the given name for the given player and applies
     * the given consumer to the player's cache before opening the inventory.
     *
     * @param superiorPlayer the player for which to open the inventory
     * @param inventoryName  the name of the inventory to open
     * @param consumer       the consumer to apply to the player's cache
     */
    public void openInventory(SuperiorPlayer superiorPlayer, String inventoryName, Consumer<PlayerCache> consumer) {
        this.openInventory(superiorPlayer.asPlayer(), inventoryName, consumer);
    }

    /**
     * Opens the inventory with the given name for the given player and applies
     * the given consumer to the player's cache before opening the inventory.
     *
     * @param player        the player for which to open the inventory
     * @param inventoryName the name of the inventory to open
     * @param consumer      the consumer to apply to the player's cache
     */
    public void openInventory(Player player, String inventoryName, Consumer<PlayerCache> consumer) {
        this.cache(player, consumer);
        this.openInventory(player, inventoryName);
    }

    /**
     * Opens the inventory with the given name for the given player.
     *
     * @param superiorPlayer the player for which to open the inventory
     * @param inventoryName  the name of the inventory to open
     */
    public void openInventory(SuperiorPlayer superiorPlayer, String inventoryName) {
        this.openInventory(superiorPlayer.asPlayer(), inventoryName);
    }

    /**
     * Opens the inventory with the given name for the given player. If the given
     * inventory does not exist, the player is notified.
     *
     * @param player        the player for which to open the inventory
     * @param inventoryName the name of the inventory to open
     */
    public void openInventory(Player player, String inventoryName) {
        List<Inventory> inventories = new ArrayList<>();
        this.inventoryManager.getCurrentPlayerInventory(player).ifPresent(inventories::add);
        Optional<Inventory> optional = this.inventoryManager.getInventory(plugin, inventoryName);
        if (optional.isPresent()) {
            this.inventoryManager.openInventory(player, optional.get(), 1, inventories);
        } else {
            player.sendMessage(ChatColor.RED + "Impossible to find the inventory " + inventoryName + " !");
        }
    }

    /**
     * Returns the inventory manager for this plugin.
     *
     * @return the inventory manager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * Returns the inventory associated with the given menu type.
     *
     * @param menuType the menu type for which to retrieve the inventory
     * @return the inventory associated with the given menu type, or {@code null} if
     *         no such inventory exists
     */
    public Inventory getInventory(MenuType menuType) {
        return this.inventories.get(menuType);
    }
}
