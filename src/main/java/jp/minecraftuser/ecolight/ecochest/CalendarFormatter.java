package jp.minecraftuser.ecolight.ecochest;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public final class CalendarFormatter
{
  public static Date toDate(String strDate)
  {
    return toCalendar(strDate).getTime();
  }
  
  public static Calendar toCalendar(String strDate)
  {
    strDate = format(strDate);
    Calendar cal = Calendar.getInstance();
    cal.setLenient(false);
    int yyyy = Integer.parseInt(strDate.substring(0, 4));
    int MM = Integer.parseInt(strDate.substring(5, 7));
    int dd = Integer.parseInt(strDate.substring(8, 10));
    int HH = cal.get(11);
    int mm = cal.get(12);
    int ss = cal.get(13);
    int SSS = cal.get(14);
    cal.clear();
    cal.set(yyyy, MM - 1, dd);
    int len = strDate.length();
    switch (len)
    {
    case 10: 
      break;
    case 16: 
      HH = Integer.parseInt(strDate.substring(11, 13));
      mm = Integer.parseInt(strDate.substring(14, 16));
      cal.set(11, HH);
      cal.set(12, mm);
      break;
    case 19: 
      HH = Integer.parseInt(strDate.substring(11, 13));
      mm = Integer.parseInt(strDate.substring(14, 16));
      ss = Integer.parseInt(strDate.substring(17, 19));
      cal.set(11, HH);
      cal.set(12, mm);
      cal.set(13, ss);
      break;
    case 23: 
      HH = Integer.parseInt(strDate.substring(11, 13));
      mm = Integer.parseInt(strDate.substring(14, 16));
      ss = Integer.parseInt(strDate.substring(17, 19));
      SSS = Integer.parseInt(strDate.substring(20, 23));
      cal.set(11, HH);
      cal.set(12, mm);
      cal.set(13, ss);
      cal.set(14, SSS);
      break;
    default: 
      throw new IllegalArgumentException("引数の文字列[" + strDate + "]は日付文字列に変換できません");
    }
    return cal;
  }
  
  private static String format(String str)
  {
    if ((str == null) || (str.trim().length() < 8)) {
      throw new IllegalArgumentException("引数の文字列[" + str + "]は日付文字列に変換できません");
    }
    str = str.trim();
    String yyyy = null;String MM = null;String dd = null;
    String HH = null;String mm = null;
    String ss = null;String SSS = null;
    if ((str.indexOf("/") == -1) && (str.indexOf("-") == -1))
    {
      if (str.length() == 8)
      {
        yyyy = str.substring(0, 4);
        MM = str.substring(4, 6);
        dd = str.substring(6, 8);
        return yyyy + "/" + MM + "/" + dd;
      }
      yyyy = str.substring(0, 4);
      MM = str.substring(4, 6);
      dd = str.substring(6, 8);
      HH = str.substring(9, 11);
      mm = str.substring(12, 14);
      ss = str.substring(15, 17);
      return yyyy + "/" + MM + "/" + dd + " " + HH + ":" + mm + ":" + ss;
    }
    StringTokenizer token = new StringTokenizer(str, "_/-:. ");
    StringBuffer result = new StringBuffer();
    for (int i = 0; token.hasMoreTokens(); i++)
    {
      String temp = token.nextToken();
      switch (i)
      {
      case 0: 
        yyyy = fillString(str, temp, "L", "20", 4);
        result.append(yyyy);
        break;
      case 1: 
        MM = fillString(str, temp, "L", "0", 2);
        result.append("/" + MM);
        break;
      case 2: 
        dd = fillString(str, temp, "L", "0", 2);
        result.append("/" + dd);
        break;
      case 3: 
        HH = fillString(str, temp, "L", "0", 2);
        result.append(" " + HH);
        break;
      case 4: 
        mm = fillString(str, temp, "L", "0", 2);
        result.append(":" + mm);
        break;
      case 5: 
        ss = fillString(str, temp, "L", "0", 2);
        result.append(":" + ss);
        break;
      case 6: 
        SSS = fillString(str, temp, "R", "0", 3);
        result.append("." + SSS);
      }
    }
    return result.toString();
  }
  
  private static String fillString(String strDate, String str, String position, String addStr, int len)
  {
    if (str.length() > len) {
      throw new IllegalArgumentException("引数の文字列[" + strDate + "]は日付文字列に変換できません");
    }
    return fillString(str, position, len, addStr);
  }
  
  private static String fillString(String str, String position, int len, String addStr)
  {
    if ((addStr == null) || (addStr.length() == 0)) {
      throw new IllegalArgumentException("挿入する文字列の値が不正です。" + addStr);
    }
    if (str == null) {
        str = "";
    }
    StringBuffer buffer = new StringBuffer(str);
    while (len > buffer.length()) {
      if (position.equalsIgnoreCase("1"))
      {
      int sum = buffer.length() + addStr.length();
        if (sum > len)
        {
          addStr = addStr.substring(0, addStr.length() - (sum - len));
          
          buffer.insert(0, addStr);
        }
        else
        {
          buffer.insert(0, addStr);
        }
      }
      else
      {
        buffer.append(addStr);
      }
    }
    if (buffer.length() == len) {
      return buffer.toString();
    }
    return buffer.toString().substring(0, len);
  }
}
