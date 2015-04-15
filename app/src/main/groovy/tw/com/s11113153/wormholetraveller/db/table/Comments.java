package tw.com.s11113153.wormholetraveller.db.table;

import org.litepal.crud.DataSupport;

/**
 * Created by xuyouren on 15/4/12.
 */
public class Comments extends DataSupport {
  private int wormholetraveller_id;
  private int user_id;
  private String content;

  public Comments setWormholetraveller_id(int wormholetraveller_id) {
    this.wormholetraveller_id = wormholetraveller_id;
    return this;
  }

  public Comments setUser_id(int user_id) {
    this.user_id = user_id;
    return this;
  }

  public Comments setContent(String content) {
    this.content = content;
    return this;
  }

  public int getWormholetraveller_id() {
    return wormholetraveller_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public String getContent() {
    return content;
  }
}
