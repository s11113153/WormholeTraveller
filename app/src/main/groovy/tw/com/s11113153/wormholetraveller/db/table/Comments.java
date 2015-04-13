package tw.com.s11113153.wormholetraveller.db.table;

import org.litepal.crud.DataSupport;

/**
 * Created by xuyouren on 15/4/12.
 */
public class Comments extends DataSupport {
  private WormholeTraveller wormholeTraveller;
  private String content;
  private String description;

  public Comments setWormholeTraveller(WormholeTraveller wormholeTraveller) {
    this.wormholeTraveller = wormholeTraveller;
    return this;
  }

  public Comments setContent(String content) {
    this.content = content;
    return this;
  }

  public Comments setDescription(String description) {
    this.description = description;
    return this;
  }

  public WormholeTraveller getWormholeTraveller() {
    return wormholeTraveller;
  }

  public String getContent() {
    return content;
  }

  public String getDescription() {
    return description;
  }
}
