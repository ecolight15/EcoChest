package jp.minecraftuser.ecolight.ecochest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ChestLoggerDB
{
  private Connection con = null;

  private BlockingQueue<ChestLogEntry> logQueue = new LinkedBlockingQueue<>();
  private ExecutorService executorService = Executors.newSingleThreadExecutor();
  
  public ChestLoggerDB(EcoChest plg)
  {
    String msgDBpath = plg.getDataFolder().getPath() + "/chest.db";
    try
    {
      Class.forName("org.sqlite.JDBC");
      this.con = DriverManager.getConnection("jdbc:sqlite:" + msgDBpath);
      this.con.setAutoCommit(false);
      
      Statement stmt = this.con.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACT(ACTID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TIME INTEGER, LOCATION TEXT, PLAYER TEXT, ACTION TEXT);");
      stmt.executeUpdate("CREATE INDEX IF NOT EXISTS LOCATION ON TRANSACT(TIME, LOCATION);");
      stmt.close();
      m.info("DataBase Loaded.");
    }
    catch (SQLException ex)
    {
      m.Warn("コネクション失敗:" + ex.getMessage());
    }
    catch (ClassNotFoundException ex)
    {
      m.Warn("DBシステム異常:" + ex.getMessage());
    }
    //非同期処理
    executorService.submit(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          //take()にて要素が追加されるまで待機して、追加された場合は要素の値をentryに設定して要素を削除
          ChestLogEntry entry = logQueue.take();
          //上記の要素をDBに書き込み
          actLoggingDB(entry.getLocation(), entry.getPlayer(), entry.getAction());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  public void ChestLoggerDB_()
  {
    Finalize();
  }
  
  public void Finalize()
  {
    if (this.con == null) {
      return;
    }
    try
    {
      this.con.close();
      this.con = null;
    }
    catch (SQLException ex)
    {
      m.Warn("DBシステム異常:" + ex.getMessage());
    }
  }
  public void actLogging(String location, String player, String act)
  {
    try {
      logQueue.put(new ChestLogEntry(location,player,act));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void actLoggingDB(String location, String player, String act)
  {
    if (this.con == null) {
      return;
    }
    try
    {
      Date date = new Date();
      PreparedStatement prep = this.con.prepareStatement("INSERT INTO TRANSACT(TIME, LOCATION, PLAYER, ACTION) VALUES (?, ?, ?, ?);");
      prep.setLong(1, date.getTime());
      prep.setString(2, location);
      prep.setString(3, player);
      prep.setString(4, act);
      prep.executeUpdate();
      this.con.commit();
      prep.close();
    }
    catch (SQLException ex) {}
  }
  
  public ArrayList<String> actSearch(String location, Date start, Date end, int page, int count)
  {
    if (this.con == null) {
      return null;
    }
    ArrayList<String> msglist = null;
    try
    {
      int total = 0;
      PreparedStatement prep = this.con.prepareStatement("SELECT COUNT(*) FROM TRANSACT WHERE LOCATION = ? AND TIME >= ? AND TIME <= ?;");
      prep.setString(1, location);
      prep.setLong(2, start.getTime());
      prep.setLong(3, end.getTime());
      ResultSet rs = prep.executeQuery();
      rs.next();
      total = rs.getInt(1);
      rs.close();
      prep.close();
      
      int startcount = page * count - count;
      int endcount = page * count;
      prep = this.con.prepareStatement("SELECT * FROM TRANSACT WHERE LOCATION = ? AND TIME >= ? AND TIME <= ? ORDER BY TIME ASC LIMIT ? OFFSET ?;");
      prep.setString(1, location);
      prep.setLong(2, start.getTime());
      prep.setLong(3, end.getTime());
      prep.setInt(4, count);
      prep.setInt(5, startcount);
      rs = prep.executeQuery();
      
      msglist = new ArrayList();
      if (total < endcount) {
        endcount = total;
      }
      if (startcount > endcount)
      {
        endcount = 0;startcount = 0;
      }
      msglist.add("=== [" + location + "] " + page + "ページ目:全" + total + "件中" + (startcount + 1) + "～" + endcount + "件目 ===");
      SimpleDateFormat sdf = new SimpleDateFormat("[MM/dd_HH:mm:ss] ");
      while (rs.next())
      {
        Date date = new Date(rs.getLong("TIME"));
        msglist.add("ID:" + rs.getInt("ACTID") + "," + sdf.format(date) + "<" + rs.getString("PLAYER") + "> " + rs.getString("LOCATION"));
        msglist.add("        " + rs.getString("ACTION"));
      }
      msglist.add("===================================================");
      rs.close();
      prep.close();
    }
    catch (SQLException ex)
    {
      m.info(ex.getLocalizedMessage());
      m.info(ex.getMessage());
      m.info(ex.getSQLState());
    }
    return msglist;
  }
  
  public void actDelete(String location)
  {
    if (this.con == null) {
      return;
    }
    try
    {
      PreparedStatement prep = this.con.prepareStatement("DELETE FROM TRANSACT WHERE LOCATION < ?;");
      prep.setString(1, location);
      prep.executeUpdate();
      this.con.commit();
      prep.close();
    }
    catch (SQLException ex) {}
  }
  
  public void oldDelete()
  {
    if (this.con == null) {
      return;
    }
    try
    {
      Date date = new Date();
      PreparedStatement prep = this.con.prepareStatement("DELETE FROM TRANSACT WHERE TIME = ?;");
      prep.setLong(1, date.getTime() + -1875767296L);
      prep.executeUpdate();
      this.con.commit();
      prep.close();
    }
    catch (SQLException ex) {}
  }
}
