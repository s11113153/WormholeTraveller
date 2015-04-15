package tw.com.s11113153.wormholetraveller.db.table;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xuyouren on 15/4/12.
 */
public class WormholeTraveller extends DataSupport {
  private int id;
  private User user;
  private String travelPhotoPath; // 拍照時可能不是Path
  private boolean like = false;
  private float lat;
  private float lng;
  private String date;
  private boolean show = true;
  private String title;
  private String content;
  private int goods;
  private String address;

  public WormholeTraveller setId(int id) {
    this.id = id;
    return this;
  }

  public WormholeTraveller setUser(User user) {
    this.user = user;
    return this;
  }

  public WormholeTraveller setTravelPhotoPath(String travelPhotoPath) {
    this.travelPhotoPath = travelPhotoPath;
    return this;
  }

  public WormholeTraveller setLike(boolean like) {
    this.like = like;
    return this;
  }

  public WormholeTraveller setGoods(int goods) {
    this.goods = goods;
    return this;
  }

  public WormholeTraveller setLat(float lat) {
    this.lat = lat;
    return this;
  }

  public WormholeTraveller setLng(float lng) {
    this.lng = lng;
    return this;
  }

  public WormholeTraveller setDate(String date) {
    this.date = date;
    return this;
  }

  public WormholeTraveller setShow(boolean show) {
    this.show = show;
    return this;
  }

  public WormholeTraveller setTitle(String title) {
    this.title = title;
    return this;
  }

  public WormholeTraveller setContent(String content) {
    this.content = content;
    return this;
  }

  public WormholeTraveller setAddress(String address) {
    this.address = address;
    return this;
  }

  public int getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getTravelPhotoPath() {
    return travelPhotoPath;
  }

  public boolean isLike() {
    return like;
  }

  public int getGoods() {
    return goods;
  }

  public float getLat() {
    return lat;
  }

  public float getLng() {
    return lng;
  }

  public String getDate() {
    return date;
  }

  public boolean isShow() {
    return show;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getAddress() {
    return address;
  }
}
