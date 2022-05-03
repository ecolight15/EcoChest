package jp.minecraftuser.ecolight.ecochest;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class EcoChestEventListener
  implements Listener
{
  private static EcoChest plg = null;
  private static HashMap<Player, ChestTransaction> transaction = null;
  
  public EcoChestEventListener(EcoChest plugin)
  {
    plg = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    transaction = new HashMap();
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerInteract(PlayerInteractEvent event)
  {
    Block b = event.getClickedBlock();
    if (b == null) {
      return;
    }
    if ((b.getType() != Material.CHEST) &&
        (b.getType() != Material.FURNACE) &&
        (b.getType() != Material.BREWING_STAND) &&
        (b.getType() != Material.DISPENSER) &&
        (b.getType() != Material.DROPPER) &&
        (b.getType() != Material.TRAPPED_CHEST) &&
        (b.getType() != Material.HOPPER)
       )
    {
      return;
    }
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    ChestTransaction tra = new ChestTransaction(plg, b.getLocation());
    tra.setType(b.getType());
    Player pl = event.getPlayer();
    if (plg.isParam(pl))
    {
      SearchParam p = plg.getParam(pl);
      event.setCancelled(true);
      if (plg.isLWC())
      {
        Plugin plugin = plg.getServer().getPluginManager().getPlugin("LWC");
        LWC lwc = ((LWCPlugin)plugin).getLWC();
        Protection prot = lwc.findProtection(b);
        if (!lwc.canAccessProtection(pl, prot))
        {
          pl.sendMessage(ChatColor.YELLOW + "[ECOChest] チェストログ参照権限がありません。再度操作して下さい。");
          return;
        }
      }
      ArrayList<String> msg = plg.getDB().actSearch(tra.makeInfo(), p.getStart(), p.getEnd(), p.getPage(), 5);
      for (String m : msg) {
        event.getPlayer().sendMessage(m);
      }
    }
    else
    {
      transaction.put(event.getPlayer(), tra);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerInteractEntity(PlayerInteractEntityEvent event)
  {
    Entity ent = event.getRightClicked();
    if ((ent.getType() != EntityType.MINECART_HOPPER) && (ent.getType() != EntityType.MINECART_CHEST) && (ent.getType() != EntityType.HORSE)) {
      return;
    }
    ChestTransaction tra = new ChestTransaction(plg, ent.getLocation());
    tra.setUUID(ent.getUniqueId());
    switch (ent.getType())
    {
    case MINECART_HOPPER: 
      tra.setType(Material.HOPPER);
      tra.setCartType(true);
      break;
    case MINECART_CHEST: 
      tra.setType(Material.CHEST);
      tra.setCartType(true);
      break;
    case HORSE: 
      tra.setType(Material.CHEST);
      tra.setHorseType(true);
    }
    Player pl = event.getPlayer();
    if (plg.isParam(pl))
    {
      SearchParam p = plg.getParam(pl);
      event.setCancelled(true);
      
      ArrayList<String> msg = plg.getDB().actSearch(tra.makeInfo(), p.getStart(), p.getEnd(), p.getPage(), 5);
      for (String m : msg) {
        event.getPlayer().sendMessage(m);
      }
    }
    else
    {
      transaction.put(event.getPlayer(), tra);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void InventoryOpen(InventoryOpenEvent event)
  {
    Inventory i = event.getInventory();
    Player pl = plg.getServer().getPlayer(event.getPlayer().getName());
    if (pl == null) {
      return;
    }
    if ((i.getType() != InventoryType.CHEST) && (i.getType() != InventoryType.DISPENSER) && (i.getType() != InventoryType.FURNACE) && (i.getType() != InventoryType.BREWING) && (i.getType() != InventoryType.HOPPER)) {
      return;
    }
    if ((i.getType() == InventoryType.CHEST) && (i.getSize() == 17)) {
      for (Entity e : pl.getWorld().getEntities())
      {
        Entity pas = e.getPassenger();
        if ((pas != null) && 
          (pas.equals(pl)))
        {
          ChestTransaction tra = new ChestTransaction(plg, e.getLocation());
          tra.setHorseType(true);
          tra.setUUID(e.getUniqueId());
          tra.setType(Material.CHEST);
          if (plg.isParam(pl))
          {
            SearchParam p = plg.getParam(pl);
            event.setCancelled(true);
            
            ArrayList<String> msg = plg.getDB().actSearch(tra.makeInfo(), p.getStart(), p.getEnd(), p.getPage(), 5);
            for (String m : msg) {
              pl.sendMessage(m);
            }
          }
          else
          {
            transaction.put(pl, tra);
          }
        }
      }
    }
    if (!transaction.containsKey(pl))
    {
      Location loc = pl.getLocation();
      m.Warn("謎オープン、取りこぼし。(" + pl.getName() + ")[" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "]");
      return;
    }
    ((ChestTransaction)transaction.get(pl)).setOpened(true);
  }
  
  public void InventoryClose(InventoryCloseEvent event)
  {
    if ((event.getInventory().getType() != InventoryType.CHEST) && (event.getInventory().getType() != InventoryType.DISPENSER) && (event.getInventory().getType() != InventoryType.FURNACE) && (event.getInventory().getType() != InventoryType.BREWING) && (event.getInventory().getType() != InventoryType.HOPPER)) {
      return;
    }
    HumanEntity pl = event.getPlayer();
    if ((!transaction.containsKey(pl)) || (!((ChestTransaction)transaction.get(pl)).isOpened()))
    {
      Location loc = pl.getLocation();
      m.Warn("謎クローズ、取りこぼし。(" + pl.getName() + ")[" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "]");
      return;
    }
    ((ChestTransaction)transaction.remove(pl)).setOpened(false);
  }
  
    @EventHandler(priority=EventPriority.LOWEST)
    public void InventoryClick(InventoryClickEvent event)
    {
        if ((event.getInventory().getType() == InventoryType.CHEST) || (event.getInventory().getType() == InventoryType.BREWING) || (event.getInventory().getType() == InventoryType.DISPENSER) || (event.getInventory().getType() == InventoryType.FURNACE) || (event.getInventory().getType() == InventoryType.HOPPER)) {
            switch (event.getSlotType())
            {
            case ARMOR: 
            case CONTAINER: 
            case FUEL: 
            case QUICKBAR: 
            case CRAFTING: 
            case OUTSIDE: 
            case RESULT:
                if (!transaction.containsKey(event.getWhoClicked()))
                {
                    HumanEntity ent = event.getWhoClicked();
                    m.Warn("謎ユーザー取引(" + ent.getName() + ")[" + ent.getLocation().getBlockX() + "," + ent.getLocation().getBlockY() + "," + ent.getLocation().getBlockZ() + "]");
                    return;
                }
                ((ChestTransaction)transaction.get(event.getWhoClicked())).updateTransaction(
                        event.getWhoClicked(),
                        event.getInventory(),
                        event.getWhoClicked().getInventory(),
                        event.getRawSlot(),
                        event.getCurrentItem(),
                        event.getCursor(),
                        event.getClick());
            }
        }
    }
}
