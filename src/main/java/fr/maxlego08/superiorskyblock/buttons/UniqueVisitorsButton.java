package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemSkulls;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class UniqueVisitorsButton extends SuperiorPaginateButton {

    private final Function<Pair<SuperiorPlayer, Long>, UniqueVisitorInfo> VISITOR_INFO_MAPPER = visitor -> new UniqueVisitorInfo(visitor.getKey(), visitor.getValue());

    public UniqueVisitorsButton(Plugin plugin) {
        super((SuperiorSkyblockPlugin) plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        placeholders.register("visitor", String.valueOf(getCache(player).getIsland().getUniqueVisitorsWithTimes().size()));
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

        Pagination<UniqueVisitorInfo> pagination = new Pagination<>();
        List<UniqueVisitorInfo> uniqueVisitorInfos = pagination.paginate(requestObjects(player), this.slots.size(), inventory.getPage());

        Locale locale = getSuperiorPlayer(player).getUserLocale();
        String noneOwner = Message.ISLAND_OWNER_NONE.getMessage(locale);
        String noneName = Message.ISLAND_NAME_NONE.getMessage(locale);
        if (noneOwner == null) noneOwner = "None";
        if (noneName == null) noneName = "None";

        for (int i = 0; i != Math.min(uniqueVisitorInfos.size(), this.slots.size()); i++) {

            int slot = slots.get(i);
            UniqueVisitorInfo uniqueVisitorInfo = uniqueVisitorInfos.get(i);

            SuperiorPlayer visitor = uniqueVisitorInfo.getVisitor();
            Island island = visitor.getIsland();
            String islandOwner = island != null ? island.getOwner().getName() : noneOwner;
            String islandName = island != null ? island.getName().isEmpty() ? islandOwner : island.getName() : noneName;

            Placeholders placeholders = new Placeholders();
            placeholders.register("player", visitor.getName());
            placeholders.register("island-owner", islandOwner);
            placeholders.register("island-name", islandName);
            placeholders.register("time", Formatters.DATE_FORMATTER.format(new Date(uniqueVisitorInfo.getVisitTime())));

            inventory.addItem(slot, ItemSkulls.getPlayerHead(getItemStack().build(player, false, placeholders), visitor.getTextureValue()));
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return requestObjects(player).size();
    }

    protected List<UniqueVisitorInfo> requestObjects(Player player) {
        return new SequentialListBuilder<UniqueVisitorInfo>().build(getCache(player).getIsland().getUniqueVisitorsWithTimes(), VISITOR_INFO_MAPPER);
    }

    public static class UniqueVisitorInfo {

        private final SuperiorPlayer visitor;
        private final long visitTime;

        public UniqueVisitorInfo(SuperiorPlayer visitor, long visitTime) {
            this.visitor = visitor;
            this.visitTime = visitTime;
        }

        public SuperiorPlayer getVisitor() {
            return visitor;
        }

        public long getVisitTime() {
            return visitTime;
        }

    }
}
