package fr.maxlego08.superiorskyblock.buttons;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.values.BlockValue;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BlockValueButton extends SuperiorButton {

    private final BigInteger maxStack = BigInteger.valueOf(64);
    private final Key block;

    public BlockValueButton(SuperiorSkyblockPlugin plugin, Key block) {
        super(plugin);
        this.block = block;
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        placeholders.register("player-name", player.getName());

        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        Island island = superiorPlayer.getIsland();
        placeholders.register("worth", Formatters.NUMBER_FORMATTER.format(island.getWorth()));
        placeholders.register("worth-formatted", Formatters.FANCY_NUMBER_FORMATTER.format(island.getWorth(), superiorPlayer.getUserLocale()));
    }

    @Override
    public ItemStack getCustomItemStack(Player player, boolean useCache, Placeholders placeholders) {

        SuperiorPlayer superiorPlayer = getSuperiorPlayer(player);
        Island island = superiorPlayer.getIsland();

        BigDecimal amount = new BigDecimal(block.getGlobalKey().contains("SPAWNER") ? island.getExactBlockCountAsBigInteger(block) : island.getBlockCountAsBigInteger(block));

        BlockValue blockValue = plugin.getBlockValues().getBlockValue(block);
        BigDecimal blockWorth = blockValue.getWorth();
        BigDecimal blockLevel = blockValue.getLevel();

        Placeholders buttonPlaceholders = placeholders.child();
        buttonPlaceholders.register("quantity", String.valueOf(amount));
        buttonPlaceholders.register("worth", Formatters.NUMBER_FORMATTER.format(blockWorth.multiply(amount)));
        buttonPlaceholders.register("level", Formatters.NUMBER_FORMATTER.format(blockLevel.multiply(amount)));
        buttonPlaceholders.register("worth-formatted", Formatters.FANCY_NUMBER_FORMATTER.format(blockWorth.multiply(amount), superiorPlayer.getUserLocale()));
        buttonPlaceholders.register("level-formatted", Formatters.FANCY_NUMBER_FORMATTER.format(blockLevel.multiply(amount), superiorPlayer.getUserLocale()));

        ItemStack itemStack = getItemStack().build(player, false, buttonPlaceholders);
        itemStack.setAmount(BigInteger.ONE.max(maxStack.min(amount.toBigInteger())).intValue());
        return itemStack;
    }
}
