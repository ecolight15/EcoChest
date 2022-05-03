package jp.minecraftuser.ecolight.ecochest;

import java.util.Date;

public class SearchParam
{
  private Date start = null;
  private Date end = null;
  private int page = 1;
  
  public SearchParam(Date st, Date en, int p)
  {
    this.start = st;
    this.end = en;
    this.page = p;
  }
  
  public Date getStart()
  {
    return this.start;
  }
  
  public Date getEnd()
  {
    return this.end;
  }
  
  public int getPage()
  {
    return this.page;
  }
}
