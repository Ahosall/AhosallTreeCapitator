package xyz.ahosall.treecapitator.modules;

import org.bukkit.block.Block;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TreeCapitator {

  private Integer getDamageChance(ItemStack item) {
    if (item != null && item.getType().getMaxDurability() > 0) {
      ItemMeta meta = item.getItemMeta();

      if (meta instanceof Damageable) {
        Damageable damageable = (Damageable) meta;

        // Get the current damage value
        int damage = damageable.getDamage();

        if (Math.random() < 0.5) {
          damageable.setDamage(damage + 1);
          item.setItemMeta((ItemMeta) damageable);

          return damage + 1;
        }
      }
    }

    return null;
  }

  public void process(Player player, Block block, ItemStack item) {
    double damageChance = getDamageChance(item);
    double maxDurability = item.getType().getMaxDurability();

    System.out.println(damageChance);
    System.out.println(maxDurability);
  }
}
