package jp.minecraftuser.ecolight.ecochest;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EcoChest
  extends JavaPlugin
{
  private static EcoChestCommandExecutor myExecutor = null;
  private static EcoChestEventListener myListener = null;
  private static ChestLoggerDB db = null;
  private static HashMap<Player, SearchParam> searchmap = null;
  private static boolean enablelwc = false;
  
  public void onEnable()
  {
    setConfig();
    m.info("EcoChest config loaded.");
    
    myExecutor = new EcoChestCommandExecutor(this);
    myListener = new EcoChestEventListener(this);
    m.info("EcoChest executor/listener registered.");
  }
  
  public void onDisable()
  {
    db.Finalize();
  }
  
  public void setConfig()
  {
    reloadConfig();
    
    getConfig().options().copyDefaults(true);
    saveConfig();
    
    m.msgLoad(null, this);
    
    searchmap = new HashMap();
    
    db = new ChestLoggerDB(this);
    
    db.oldDelete();
    
    Plugin pl = getServer().getPluginManager().getPlugin("LWC");
    if (pl != null) {
      enablelwc = true;
    }
  }
  
  public boolean isLWC()
  {
    return enablelwc;
  }
  
  public ChestLoggerDB getDB()
  {
    return db;
  }
  
  public void setParam(Player pl, SearchParam param)
  {
    if (searchmap.containsKey(pl)) {
      searchmap.remove(pl);
    }
    searchmap.put(pl, param);
  }
  
  public boolean isParam(Player pl)
  {
    return searchmap.containsKey(pl);
  }
  
  public SearchParam getParam(Player pl)
  {
    if (!searchmap.containsKey(pl)) {
      return null;
    }
    SearchParam param = (SearchParam)searchmap.get(pl);
    searchmap.remove(pl);
    return param;
  }
}
