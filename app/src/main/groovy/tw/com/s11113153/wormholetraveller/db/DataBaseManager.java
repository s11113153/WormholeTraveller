package tw.com.s11113153.wormholetraveller.db;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.util.Log;

import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;

/**
 * Created by xuyouren on 15/4/13.
 */
public class DataBaseManager {
  private static final String FACEBOOK_ICON_URL = "https://graph.facebook.com/UID/picture?type=large";
  private static final String UID = "UID";

  private DataBaseManager() {

  }

  private static DataBaseManager instance;

  private void init() {
    Connector.getDatabase();
//    createUser();
//    createTravel();
//    query();
  }

  public static DataBaseManager open() {
    if (instance == null)
      instance = new DataBaseManager();
    instance.init();
    return instance;
  }

  private void createUser() {
    User s11113153 = new User()
      .setName("XuYouRen").setAccount("s11113153")
      .setPassword("123456").setMail("s11113153@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "xu.y.jen"));
    Log.e("", "" + s11113153.save());

    User yoWu = new User()
      .setName("YoWu").setAccount("s11113142")
      .setPassword("123456").setMail("s11113142@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "yo.wu.148"));
    yoWu.save();

    User ts880016 = new User()
      .setName("梁婉琳").setAccount("s11113109")
      .setPassword("123456").setMail("s11113109@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "ts880016"));
    ts880016.save();

    User s11113120 = new User()
      .setName("pang.shaocheng").setAccount("s11113120")
      .setPassword("123456").setMail("s11113120@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "pang.shaocheng"));
    s11113120.save();
  }

  public void createTravel() {
    User userId_1 = DataSupport.find(User.class, 1);
    WormholeTraveller wormholeTraveller = new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/zzyrC10.jpg")
      .setDate(Utils.getDate()).setGoods(362)
      .setLat(22.82816f).setLng(120.3416f)
      .setUser(userId_1)
      .setTitle("HelloWorld").setContent("Say Good Bye");

    boolean b = wormholeTraveller.save();
    Log.e("b = ", "" + b);
  }

  private void query() {
    WormholeTraveller traveller = DataSupport.find(WormholeTraveller.class, 1, true);
    Log.e("", "" + traveller.getLat());
    Log.e("", "" + traveller.getLng());
    Log.e("", "" + traveller.getDate());
    Log.e("", "" + traveller.getGoods());
    Log.e("", "" + traveller.isShow());
    Log.e("", "" + traveller.isLike());
    Log.e("", "" + traveller.getTitle());
    Log.e("", "" + traveller.getContent());

    User u = traveller.getUser();

    Log.e("", "" + u.getName());

  }
}
