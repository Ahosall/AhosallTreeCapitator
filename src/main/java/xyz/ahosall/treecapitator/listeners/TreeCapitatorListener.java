package xyz.ahosall.treecapitator.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TreeCapitatorListener implements Listener {

  private JavaPlugin plugin;

  public TreeCapitatorListener(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();

    ItemStack itemHolding = player.getInventory().getItemInMainHand();
    String itemType = itemHolding.getType().toString();

    boolean holdingAxe = itemType.endsWith("_AXE");
    boolean itsNotSneaking = !player.isSneaking();
    boolean isSurvivalMode = player.getGameMode().equals(GameMode.SURVIVAL);

    if (isWood(block.getType()) && holdingAxe && itsNotSneaking && isSurvivalMode) {
      new BukkitRunnable() {
        @Override
        public void run() {
          Set<Block> processedBlocks = new HashSet<>();
          processConnectedWood(block, player, itemHolding, processedBlocks);

          this.cancel();
        }
      }.runTaskTimer(plugin, 0L, 1L);
    }
  }

  private void processConnectedWood(Block block, Player player, ItemStack tool, Set<Block> processedBlocks) {
    if (processedBlocks.contains(block))
      return;

    processedBlocks.add(block);
    Block[] adjacentBlocks = {
        block.getRelative(0, 1, 0),
        block.getRelative(0, -1, 0),
        block.getRelative(1, 0, 0),
        block.getRelative(-1, 0, 0),
        block.getRelative(0, 0, 1),
        block.getRelative(0, 0, -1)
    };

    for (Block adjacentBlock : adjacentBlocks) {
      int toolDurability = getDurability(tool);
      player.sendMessage("Durabilidade do item: " + tool.getType().toString() + " é de " + toolDurability);

      if (isWood(adjacentBlock.getType())) {
        if (toolDurability == 0) {
          player.getInventory().setItemInMainHand(null);
          adjacentBlock.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
          break;
        }

        adjacentBlock.getWorld().playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
        adjacentBlock.getWorld().dropItemNaturally(adjacentBlock.getLocation(), new ItemStack(adjacentBlock.getType()));
        adjacentBlock.setType(Material.AIR);

        updateDurability(tool);
        processConnectedWood(adjacentBlock, player, tool, processedBlocks);
      }
    }
  }

  private boolean isWood(Material material) {
    return material.toString().endsWith("_LOG") || material.toString().endsWith("_STEM");
  }

  private Integer getDurability(ItemStack tool) {
    if (tool != null && tool.getItemMeta() instanceof Damageable) {
      Damageable damageable = (Damageable) tool.getItemMeta();
      int damage = damageable.getDamage();
      int durability = tool.getType().getMaxDurability() - damage;

      return durability;
    }

    return null;
  }

  private void updateDurability(ItemStack tool) {
    if (tool != null && tool.getItemMeta() instanceof Damageable) {
      Damageable damageable = (Damageable) tool.getItemMeta();

      int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);

      if (unbreakingLevel == 0 || Math.random() > (1.0 / (unbreakingLevel + 1)))
        damageable.setDamage(damageable.getDamage() + 1);

      tool.setItemMeta(damageable);
    }
  }
}
