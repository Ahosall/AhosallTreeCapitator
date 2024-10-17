package xyz.ahosall.treecapitator.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.HashMap;

public class TreeCapitator {

  private JavaPlugin plugin;

  public TreeCapitator(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  private Location findNextBlock(HashMap<Location, Boolean> logLocationsMap) {
    for (Location loc : logLocationsMap.keySet()) {
      if (logLocationsMap.get(loc)) {
        return loc;
      }
    }

    return null;
  }

  private Location getNextBlockLocation(Location blockLocation) {
    return new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY() + 1, blockLocation.getZ());
  }

  private Location getNeighborLocation(Location nextBlockLocation, int x, int y, int z) {
    return new Location(
        nextBlockLocation.getWorld(),
        nextBlockLocation.getX() + x, nextBlockLocation.getY() + y,
        nextBlockLocation.getZ() + z);
  }

  private Integer getItemDamage(ItemStack item) {
    if (item != null && item.getType().getMaxDurability() > 0) {
      ItemMeta meta = item.getItemMeta();

      if (meta instanceof Damageable) {
        Damageable damageable = (Damageable) meta;

        // Get the current damage value
        int damage = damageable.getDamage();

        return damage;
      }
    }

    return null;
  }

  private Integer getDamageChance(ItemStack item) {
    if (item != null && item.getType().getMaxDurability() > 0) {
      ItemMeta meta = item.getItemMeta();

      if (meta instanceof Damageable) {
        Damageable damageable = (Damageable) meta;

        // Get the current damage value
        int damage = damageable.getDamage();

        if (Math.random() < 0.5)
          return damage + 1;
      }
    }

    return 0;
  }

  private Double updateItemDurability(ItemStack item, Double currentDamage, Double maxDurability, Double damageChance) {
    double randomChance = Math.floor(Math.random() * 100) + 1;

    if (damageChance >= randomChance && currentDamage < maxDurability) {
      return currentDamage + 1.0;
    } else if (currentDamage == maxDurability) {
      return -1.0;
    } else {
      return currentDamage;
    }
  }

  private void brokenLogs(Player player, Block block, HashMap<Location, Boolean> logLocationsMap) {
    new BukkitRunnable() {
      @Override
      public void run() {
        plugin.getLogger().info(logLocationsMap.toString());
        if (logLocationsMap.size() == 0)
          this.cancel();

        for (Map.Entry<Location, Boolean> entry : logLocationsMap.entrySet()) {
          Location location = entry.getKey();

          if (location != null) {
            Block doubleCheckBlock = player.getWorld().getBlockAt(location);
            plugin.getLogger().info(doubleCheckBlock.getType().toString() + " | " + location.toString());

            if (block.getType().equals(doubleCheckBlock.getType())) {
              doubleCheckBlock.setType(Material.AIR);
              player.getWorld().dropItem(location, new ItemStack(block.getType()));
              player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 5, 1);
            }

            logLocationsMap.remove(location);
          } else {
            this.cancel();
          }
        }
      }
    }.runTaskTimer(plugin, 0L, 1L);
  }

  public void process(Player player, Block block, ItemStack item) {
    HashMap<Location, Boolean> logLocationsMap = new HashMap<>();

    double damageChance = getDamageChance(item);
    double currentDamage = getItemDamage(item);
    double maxDurability = item.getType().getMaxDurability();

    Location blockLocation = block.getLocation();
    Location nextBlockLocation = getNextBlockLocation(blockLocation);

    plugin.getLogger().info("Next Block Location" + nextBlockLocation.toString());

    int limit = 0;
    int iterationCount = 0;
    int maxIterations = 200;
    while (limit < 164 && iterationCount < maxIterations) {
      iterationCount++;


      if (logLocationsMap.containsKey(nextBlockLocation) == false) {
        logLocationsMap.put(nextBlockLocation, true);
      }

      Block currentBlock = player.getWorld().getBlockAt(nextBlockLocation);

      if (currentBlock != null && currentBlock.getType().equals(block.getType())) {
        for (int y = -1; y <= 1; y++) {
          for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
              Location neighborLocation = getNeighborLocation(nextBlockLocation, x, y, z);
              Block neighborBlock = player.getWorld().getBlockAt(neighborLocation);

              if (neighborBlock != null && neighborBlock.getType().equals(block.getType())) {
                limit++;
                updateItemDurability(item, currentDamage, maxDurability, damageChance);

                if (currentDamage == -1)
                  return;

                logLocationsMap.put(neighborLocation, false);
              }
            }
          }
        }

        Block nextBlock = player.getWorld().getBlockAt(nextBlockLocation);
        if (nextBlock.getType().equals(block.getType())) {
          nextBlockLocation = getNextBlockLocation(nextBlock.getLocation());
        } else {
          nextBlockLocation = findNextBlock(logLocationsMap);
        }

        if (nextBlockLocation == null)
          break;
      }

      ItemMeta meta = item.getItemMeta();
      if (meta instanceof Damageable) {
        Damageable damageable = (Damageable) meta;

        // Get the current damage value
        int damage = damageable.getDamage();

        if (Math.random() < 0.5) {

          damageable.setDamage(damage);
          item.setItemMeta((ItemMeta) damageable);
        }
      }

      brokenLogs(player, block, logLocationsMap);
    }
  }
}
