package fr.maxlego08.superiorskyblock.buttons.warps;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.SortingType;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.top.SortingTypes;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.superiorskyblock.buttons.SuperiorPaginateButton;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class GlobalWarpsButton extends SuperiorPaginateButton {

    private final boolean visitorWarps;

    public GlobalWarpsButton(SuperiorSkyblockPlugin plugin, boolean visitorWarps) {
        super(plugin);
        this.visitorWarps = visitorWarps;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        return getPaginationSize(player) != 0;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        Pagination<Island> pagination = new Pagination<>();
        List<Island> islands = pagination.paginate(requestObjects(player), this.slots.size(), inventory.getPage());

        SuperiorPlayer viewer = getSuperiorPlayer(player);
        Locale locale = viewer.getUserLocale();

        for (int i = 0; i != Math.min(islands.size(), this.slots.size()); i++) {

            int slot = slots.get(i);
            Island island = islands.get(i);

            String description = !island.getDescription().isEmpty() ? island.getDescription() : (!Message.ISLAND_DESCRIPTION_NONE.isEmpty(locale) ? Message.ISLAND_DESCRIPTION_NONE.getMessage(locale) : "");

            Placeholders placeholders = new Placeholders();
            placeholders.register("player", island.getOwner().getName());
            placeholders.register("description", description);
            placeholders.register("warps", String.valueOf(island.getIslandWarps().size()));
            placeholders.register("island-name", island.getName().isEmpty() ? island.getOwner().getName() : island.getName());
            placeholders.register("island-level", Formatters.NUMBER_FORMATTER.format(island.getIslandLevel()));
            placeholders.register("island-level-formatted", Formatters.FANCY_NUMBER_FORMATTER.format(island.getIslandLevel(), locale));
            placeholders.register("island-worth", Formatters.NUMBER_FORMATTER.format(island.getWorth()));
            placeholders.register("island-worth-formatted", Formatters.FANCY_NUMBER_FORMATTER.format(island.getWorth(), locale));
            placeholders.register("island-total-rating-formatted", Formatters.NUMBER_FORMATTER.format(island.getTotalRating()));
            placeholders.register("island-total-rating", Formatters.RATING_FORMATTER.format(island.getTotalRating(), locale));
            placeholders.register("island-rating-amount", String.valueOf(island.getRatingAmount()));

            inventory.addItem(slot, ItemSkulls.getPlayerHead(getItemStack().build(player, false, placeholders), island.getOwner().getTextureValue())).setClick(event -> {

                if (this.visitorWarps) {
                    plugin.getCommands().dispatchSubCommand(player, "visit", island.getOwner().getName());
                } else {
                    plugin.getMenus().openWarpCategories(getSuperiorPlayer(player), null, island);
                }
            });
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return requestObjects(player).size();
    }

    protected List<Island> requestObjects(Player player) {
        // Tri configurable via global-warps-order (SSB 2026.3)
        SortingType sortingType = SortingTypes.getGlobalWarpsSorting();
        if (sortingType == null) sortingType = SortingTypes.BY_WORTH;
        return new SequentialListBuilder<Island>().sorted(sortingType.getComparator()).filter(island -> {
            if (this.visitorWarps) return island.getVisitorsPosition(null /* unused */) != null;
            else if (island.equals(getSuperiorPlayer(player).getIsland())) return !island.getIslandWarps().isEmpty();
            else return island.getIslandWarps().values().stream().anyMatch(islandWarp -> !islandWarp.hasPrivateFlag());
        }).build(plugin.getGrid().getIslands());
    }
}
