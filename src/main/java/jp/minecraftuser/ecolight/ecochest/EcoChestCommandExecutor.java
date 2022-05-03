package jp.minecraftuser.ecolight.ecochest;

import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoChestCommandExecutor
  implements CommandExecutor
{
  private static EcoChest plg = null;
  private int paracnt = 0;
  
  public EcoChestCommandExecutor(EcoChest plugin)
  {
    plg = plugin;
    plugin.getCommand("ec").setExecutor(this);
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    this.paracnt = 0;
    Player player = null;
    if ((sender instanceof Player)) {
      player = (Player)sender;
    }
    if (cmd.getName().equalsIgnoreCase("ec"))
    {
      if (player == null) {
        return false;
      }
      ChestLoggerDB db = plg.getDB();
      Date start = null;
      Date end = null;
      int page = 1;
      try
      {
        if (args.length == 0)
        {
          end = new Date();
          start = new Date(end.getTime() - 86400000L);
        }
        else if (args.length == 1)
        {
          end = new Date();
          start = new Date(end.getTime() - 86400000L);
          
          page = Integer.parseInt(args[this.paracnt]);
        }
        else if (args.length == 2)
        {
          start = CalendarFormatter.toDate(args[(this.paracnt + 1)]);
          end = new Date(start.getTime() + 86400000L);
          
          page = Integer.parseInt(args[this.paracnt]);
        }
        else
        {
          player.sendMessage(ChatColor.YELLOW + "[ECOChest] パラメータが多すぎます");
          return true;
        }
      }
      catch (Exception ex)
      {
        player.sendMessage(ChatColor.YELLOW + "[ECOChest] パラメータの解析に失敗しました:" + ex.getMessage());
        return true;
      }
      plg.setParam(player, new SearchParam(start, end, page));
      player.sendMessage(ChatColor.YELLOW + "[ECOChest] 対象のブロックまたはカートを右クリック、ロバの場合は乗馬の上インベントリを開くかSHIFT+右クリックしてください。");
    }
    if (cmd.getName().equalsIgnoreCase("ecreload"))
    {
      if ((player != null) && 
        (!player.isOp()))
      {
        m.info("[" + player.getName() + "]" + "EcoChest not Permissions : op");
        player.sendMessage(m.get("cmd_notperm", "EcoChest reload"));
        return false;
      }
      plg.setConfig();
      if (player == null)
      {
        m.info("EcoChest config reloaded.");
      }
      else
      {
        m.info("[" + player.getName() + "]" + "EcoChest config reloaded.");
        player.sendMessage(m.get("plg_reload"));
      }
    }
    return true;
  }
}
