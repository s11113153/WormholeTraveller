package tw.com.s11113153.wormholetraveller.db;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.table.Comments;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;

/**
 * Created by xuyouren on 15/4/13.
 */
public class DataBaseManager {
  private static final String FACEBOOK_ICON_URL = "https://graph.facebook.com/UID/picture?type=large";
  private static final String UID = "UID";

  private static final String DEMO = "demo";
  private static final String IS_INITIAL_SAMPLE = "is_initial_sample";

  private DataBaseManager() {

  }

  private static DataBaseManager instance;

  private void init() {
    Connector.getDatabase();
  }

  public void InitialSample(Context context) {
    SharedPreferences sharedPref = context.getSharedPreferences(DEMO, Context.MODE_PRIVATE);
    boolean b = sharedPref.getBoolean(IS_INITIAL_SAMPLE, false);
    if (b) return;

    createUser();
    createTravel();
    sharedPref.edit().putBoolean(IS_INITIAL_SAMPLE, true).apply();
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
      .setOutline("提供最棒的旅程，Every Things in Every Day")
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
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/zzyrC10.jpg")
      .setDate(Utils.getDate()).setGoods(178)
      .setLat(22.53816f).setLng(120.5416f)
      .setUser(userId_1)
      .setTitle("First Blood").setContent("搶到首殺feffewflwef"
      + "fkewkf[ojwefwefewf"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "wefewfwefwefwefwefew"
      + "ffewfewf"
      + "wefwefewfwefewfwef")
      .save();

    User userId_2 = DataSupport.find(User.class, 2);
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/iemg6nQ.jpg")
      .setDate(Utils.getDate()).setGoods(685)
      .setLat(22.53816f).setLng(120.5416f)
      .setUser(userId_2)
      .setTitle("Double Kill").setContent("完成雙殺")
      .save();

    User userId_3 = DataSupport.find(User.class, 3);
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/VSOssOW.jpg")
      .setDate(Utils.getDate()).setGoods(4563)
      .setLat(22.53816f).setLng(120.5416f)
      .setUser(userId_3)
      .setTitle("Triple Kill").setContent("人品爆發")
      .save();

    User userId_4 = DataSupport.find(User.class, 4);
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/vWVsQIi.jpg")
      .setDate(Utils.getDate()).setGoods(1525)
      .setLat(22.53816f).setLng(120.5416f)
      .setUser(userId_4)
      .setTitle("Quadra Kill").setContent("尾刀王")
      .save();

    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/vWVsQIi.jpg")
      .setDate(Utils.getDate()).setGoods(362)
      .setLat(22.53816f).setLng(120.5416f)
      .setUser(userId_1)
      .setTitle("Penta Kill").setContent("Good Hand")
      .save();
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

  public static void updateWormholeTraveller(WormholeTraveller wt) {
    wt.update(wt.getId());
  }

  public static void updateComments(WormholeTraveller wt, User u, String content) {
    Comments comments = new Comments()
      .setContent(content)
      .setUser_id(u.getId())
      .setWormholetraveller_id(wt.getId());
    comments.save();
  }
}
