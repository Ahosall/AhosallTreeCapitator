package xyz.ahosall.treecapitator;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import xyz.ahosall.treecapitator.modules.TreeCapitator;

public class EventsListener implements Listener {

  @EventHandler
  public void onBreakBlock(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();

    ItemStack holdingItem = player.getInventory().getItemInMainHand();

    String blockType = block.getType().toString();
    String itemType = holdingItem.getType().toString();

    boolean isTree = blockType.endsWith("_LOG") || blockType.endsWith("_STEM");
    boolean holdingAxe = itemType.endsWith("_AXE");
    boolean itsNotSneaking = !player.isSneaking();
    boolean isSurvivalMode = player.getGameMode().equals(GameMode.SURVIVAL);

    boolean allRight = isTree && holdingAxe && itsNotSneaking && isSurvivalMode;
    if (allRight) {
      TreeCapitator treeCapitator = new TreeCapitator();

      treeCapitator.process(player, block, holdingItem);
    }
  }
}
