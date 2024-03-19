package jp.minecraftuser.ecolight.ecochest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestTransaction
{
  private EcoChest plg = null;
  private boolean cart = false;
  private boolean horse = false;
  private Location loc = null;
  private UUID id = null;
  private boolean opened = false;
  private HashMap<String, Integer> trade;
  private Material type = null;
  
  public ChestTransaction(EcoChest plg, Location loc)
  {
    this.plg = plg;
    this.loc = loc;
  }
  
  public void setCartType(boolean cart)
  {
    this.cart = cart;
  }
  
  public void setHorseType(boolean horse)
  {
    this.horse = horse;
  }
  
  public void setUUID(UUID id)
  {
    this.id = id;
  }
  
  public void setOpened(boolean opened)
  {
    this.opened = opened;
  }
  
  public boolean isOpened()
  {
    return this.opened;
  }
  
  public void setType(Material type)
  {
    this.type = type;
  }
  
  public String makeInfo()
  {
    StringBuilder sb = new StringBuilder();
    if (this.cart) {
      sb.append("CART,");
    } else if (this.horse) {
      sb.append("HORSE,");
    } else {
      sb.append("BLOCK,");
    }
    switch (this.type)
    {
    case CHEST: 
      sb.append("CHEST,");
      break;
    case TRAPPED_CHEST: 
      sb.append("TRAPPED_CHEST,");
      break;
    case DISPENSER: 
      sb.append("DISPENSER,");
      break;
    case FURNACE: 
      sb.append("FURNACE,");
      break;
    case BREWING_STAND: 
      sb.append("BREWING_STAND,");
      break;
    case HOPPER: 
      sb.append("HOPPER,");
      break;
    case DROPPER: 
      sb.append("DROPPER,");
      break;
    default: 
      sb.append("NONE,");
    }
    if (this.id == null) {
      sb.append("loc:{" + this.loc.getBlockX() + " " + this.loc.getBlockY() + " " + this.loc.getBlockZ() + "}");
    } else {
      sb.append("id:{" + this.id.toString() + "}");
    }
    return sb.toString();
  }
  
  private String makeItemStackString(ItemStack item)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(item.getType());
    Map<Enchantment, Integer> enchantments = item.getEnchantments();
    if (enchantments == null) {
      return sb.toString();
    }
    boolean first = true;
    for (Enchantment ent : enchantments.keySet())
    {
      if (!first) {
        sb.append(",");
      } else {
        first = false;
      }
      sb.append(ent.toString() + ":" + enchantments.get(ent));
    }
    return sb.toString();
  }
  
  private String makeChestActionString(ClickType type)
  {
    boolean first = true;
    StringBuilder sb = new StringBuilder();
    sb.append("KEY{");
    sb.append(type.name());
    sb.append("}");
    return sb.toString();
  }
  
  public void updateTransaction(HumanEntity pl, Inventory inv, Inventory plinv, int pos, ItemStack currentItem, ItemStack cursor, ClickType type)
  {
    StringBuilder sb = new StringBuilder();
    if (pos < inv.getSize()) {
      sb.append("CHEST(" + pos + "),");
    } else {
      sb.append("USER(" + pos + "),");
    }
    sb.append(ChatColor.DARK_AQUA + makeChestActionString(type) + ",");
    if (currentItem == null) {
      sb.append(ChatColor.YELLOW + "[handle: null],");
    } else {
      sb.append(ChatColor.YELLOW + "[handle: " + makeItemStackString(currentItem) + "],");
    }
    if (cursor == null) {
      sb.append(ChatColor.RED + "[inv: null]");
    } else {
      sb.append(ChatColor.RED + "[inv: " + makeItemStackString(cursor) + "]");
    }
    this.plg.getDB().actLogging(makeInfo(), pl.getName(), sb.toString());
  }
}
