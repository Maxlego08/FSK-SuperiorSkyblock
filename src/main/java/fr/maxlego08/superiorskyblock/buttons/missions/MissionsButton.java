package fr.maxlego08.superiorskyblock.buttons.missions;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.missions.IMissionsHolder;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.mission.MissionData;
import com.bgsoftware.superiorskyblock.mission.MissionsManagerImpl;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.superiorskyblock.buttons.SuperiorPaginateButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Liste paginée des missions de la catégorie en cache (équivalent de MissionsPagedObjectButton dans SSB).
 */
public class MissionsButton extends SuperiorPaginateButton {

    private final boolean sortByCompletion;
    private final boolean removeCompleted;
    private final GameSound completedSound;
    private final GameSound notCompletedSound;
    private final GameSound canCompleteSound;
    private final GameSound lockedSound;

    public MissionsButton(SuperiorSkyblockPlugin plugin, boolean sortByCompletion, boolean removeCompleted,
                          GameSound completedSound, GameSound notCompletedSound, GameSound canCompleteSound, GameSound lockedSound) {
        super(plugin);
        this.sortByCompletion = sortByCompletion;
        this.removeCompleted = removeCompleted;
        this.completedSound = completedSound;
        this.notCompletedSound = notCompletedSound;
        this.canCompleteSound = canCompleteSound;
        this.lockedSound = lockedSound;
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
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        MissionCategory missionCategory = getMissionCategory(player);
        placeholders.register("category", missionCategory == null ? "" : missionCategory.getName());
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);

        Pagination<Mission<?>> pagination = new Pagination<>();
        List<Mission<?>> missions = pagination.paginate(requestObjects(player), this.slots.size(), inventory.getPage());

        for (int i = 0; i != Math.min(missions.size(), this.slots.size()); i++) {
            int slot = this.slots.get(i);
            Mission<?> mission = missions.get(i);

            ItemStack itemStack = buildMissionItem(player, superiorPlayer, mission);
            // Mission d'île sans île : SSB affiche de l'air
            if (itemStack == null) continue;

            inventory.addItem(slot, itemStack).setClick(event -> onMissionClick(player, mission));
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return requestObjects(player).size();
    }

    private ItemStack buildMissionItem(Player player, SuperiorPlayer superiorPlayer, Mission<?> mission) {
        MissionsManagerImpl missionsManager = this.plugin.getMissions();
        Optional<MissionData> missionDataOptional = missionsManager.getMissionData(mission);

        // Aucune icône configurée côté SSB : on utilise l'item du bouton zMenu
        if (!missionDataOptional.isPresent()) {
            return getItemStack() == null ? null : getItemStack().build(player, false, new Placeholders());
        }

        MissionData missionData = missionDataOptional.get();
        IMissionsHolder missionsHolder = mission.getIslandMission() ? superiorPlayer.getIsland() : superiorPlayer;

        if (missionsHolder == null) return null;

        int percentage = calculatePercentage(mission.getProgress(superiorPlayer));
        int progressValue = mission.getProgressValue(superiorPlayer);
        int amountCompleted = missionsHolder.getAmountMissionCompleted(mission);

        ItemBuilder itemBuilder;

        if (!missionsHolder.canCompleteMissionAgain(mission))
            itemBuilder = missionData.getCompleted();
        else if (missionData.hasLocked() && !missionsManager.hasAllRequirements(mission, superiorPlayer))
            itemBuilder = missionData.getLocked();
        else if (missionsManager.canComplete(superiorPlayer, mission))
            itemBuilder = missionData.getCanComplete();
        else
            itemBuilder = missionData.getNotCompleted();

        ItemStack itemStack = itemBuilder
                .replaceAll("{0}", percentage + "")
                .replaceAll("{1}", progressValue + "")
                .replaceAll("{2}", amountCompleted + "")
                .build(superiorPlayer);

        mission.formatItem(superiorPlayer, itemStack);

        return itemStack;
    }

    private void onMissionClick(Player player, Mission<?> mission) {
        MissionsManagerImpl missionsManager = this.plugin.getMissions();
        Optional<MissionData> missionDataOptional = missionsManager.getMissionData(mission);

        if (!missionDataOptional.isPresent()) return;

        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        MissionData missionData = missionDataOptional.get();
        IMissionsHolder missionsHolder = mission.getIslandMission() ? superiorPlayer.getIsland() : superiorPlayer;

        if (missionsHolder == null) return;

        boolean canComplete = missionsManager.canComplete(superiorPlayer, mission);

        GameSound gameSound;
        if (!missionsHolder.canCompleteMissionAgain(mission))
            gameSound = this.completedSound;
        else if (this.lockedSound != null && missionData.hasLocked() && !missionsManager.hasAllRequirements(mission, superiorPlayer))
            gameSound = this.lockedSound;
        else if (canComplete)
            gameSound = this.canCompleteSound;
        else
            gameSound = this.notCompletedSound;

        GameSoundImpl.playSound(player, gameSound);

        if (!canComplete) return;

        missionsManager.rewardMission(mission, superiorPlayer, false, false, result -> {
            // Le callback est appelé en async par SSB, on rafraichit l'inventaire sur le thread principal
            if (result) BukkitExecutor.ensureMain(() -> {
                if (player.isOnline()) this.menuManager.getInventoryManager().updateInventory(player);
            });
        });
    }

    private List<Mission<?>> requestObjects(Player player) {
        MissionCategory missionCategory = getMissionCategory(player);
        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        if (missionCategory == null || superiorPlayer == null) return Collections.emptyList();

        SequentialListBuilder<Mission<?>> listBuilder = new SequentialListBuilder<>();

        if (this.sortByCompletion)
            listBuilder.sorted(Comparator.comparingInt(mission -> getCompletionStatus(superiorPlayer, mission)));

        return listBuilder
                .filter(mission -> this.plugin.getMissions().canDisplayMission(mission, superiorPlayer, this.removeCompleted))
                .build(missionCategory.getMissions());
    }

    /**
     * Non complétée (0) -> Peut être complétée (1) -> Complétée (2), comme dans SSB.
     */
    private int getCompletionStatus(SuperiorPlayer superiorPlayer, Mission<?> mission) {
        IMissionsHolder missionsHolder = mission.getIslandMission() ? superiorPlayer.getIsland() : superiorPlayer;
        return missionsHolder == null ? 0 :
                !missionsHolder.canCompleteMissionAgain(mission) ? 2 :
                        this.plugin.getMissions().canComplete(superiorPlayer, mission) ? 1 : 0;
    }

    /**
     * La catégorie est récupérée par son nom car le module missions recrée ses objets lors d'un reload.
     */
    private MissionCategory getMissionCategory(Player player) {
        MissionCategory missionCategory = getCache(player).getMissionCategory();
        if (missionCategory == null) return null;
        MissionCategory currentCategory = this.plugin.getMissions().getMissionCategory(missionCategory.getName());
        return currentCategory == null ? missionCategory : currentCategory;
    }

    private static int calculatePercentage(double progress) {
        return Math.round((float) Math.min(1.0, progress) * 100);
    }
}
