package tw.com.s11113153.wormholetraveller.db.table;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyouren on 15/4/12.
 */
public class User extends DataSupport {
  private int id;
  private String name;
  private String account;
  private String password;
  private String mail;
  private String iconPath;
  private String outline = "what do you thinking ？";

  private List<WormholeTraveller> wormholeTravellers = new ArrayList();
  private List<Comments> comments = new ArrayList();

  public User setId(int id) {
    this.id = id;
    return this;
  }

  public User setName(String name) {
    this.name = name;
    return this;
  }

  public User setAccount(String account) {
    this.account = account;
    return this;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public User setMail(String mail) {
    this.mail = mail;
    return this;
  }

  public User setIconPath(String iconPath) {
    this.iconPath = iconPath;
    return this;
  }

  public User setOutline(String outline) {
    this.outline = outline;
    return this;
  }

  public User setWormholeTravellers(List<WormholeTraveller> wormholeTravellers) {
    this.wormholeTravellers = wormholeTravellers;
    return this;
  }

  public User setComments(List<Comments> comments) {
    this.comments = comments;
    return this;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAccount() {
    return account;
  }

  public String getPassword() {
    return password;
  }

  public String getMail() {
    return mail;
  }

  public String getIconPath() {
    return iconPath;
  }

  public List<WormholeTraveller> getWormholeTravellers() {
    return wormholeTravellers;
  }

  public String getOutline() {
    return outline;
  }

  public List<Comments> getComments() {
    return comments;
  }
}
