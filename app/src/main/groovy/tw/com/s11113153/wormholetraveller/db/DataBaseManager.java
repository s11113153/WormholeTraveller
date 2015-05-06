package tw.com.s11113153.wormholetraveller.db;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    createTravel2();
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
      .setName("Liang WanLin").setAccount("s11113109")
      .setPassword("123456").setMail("s11113109@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "ts880016"));
    ts880016.save();

    User s11113120 = new User()
      .setName("pang.shaocheng").setAccount("s11113120")
      .setPassword("123456").setMail("s11113120@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "pang.shaocheng"));
    s11113120.save();

    User s11113108 = new User()
      .setName("何盈利").setAccount("s11113108")
      .setPassword("123456").setMail("s11113108@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "100002834458571"));
    s11113108.save();

    User s11113150 = new User()
      .setName("林尚民").setAccount("s11113150")
      .setPassword("123456").setMail("s11113150@stu.edu.tw")
      .setIconPath(FACEBOOK_ICON_URL.replace(UID, "100000797140210"));
    s11113150.save();
  }

  public void createTravel2() {
    User userId1 = DataSupport.find(User.class, 1);

    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/SrTR3lk.jpg")
      .setDate("2013-09-06 10:29:35").setGoods(542).setShow(true)
      .setLat(22.631388f).setLng(120.301955f)
      .setUser(userId1)
      .setAddress("高雄市新興區中山一路115號B1")
      .setTitle("美麗島-光之穹頂")
      .setContent("終於親眼見到耗費4年半的偉大藝術品了").save();

    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/Cbpbxui.jpg")
      .setDate("2013-09-06 13:01:35").setGoods(416).setShow(true)
      .setLat(22.626848f).setLng(120.28684f)
      .setUser(userId1)
      .setAddress("高雄市鹽埕區中正四路272號")
      .setTitle("高雄市立歷史博物館")
      .setContent("增廣見聞之旅").save();

    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/0gmpcAb.jpg")
      .setDate("2013-09-06 14:32:08").setGoods(375).setShow(true)
      .setUser(userId1)
      .setTitle("打狗鐵道故事館")
      .setAddress("高雄市鼓山區鼓山一路32號")
      .setLng(120.27544f).setLat(22.62177f)
      .setContent("體驗復古懷舊風情").save();

    new WormholeTraveller()
      .setUser(userId1)
      .setDate("2013-09-06 16:00:17").setGoods(1768).setShow(true)
      .setTitle("駁二特區")
      .setAddress("高雄市鹽埕區大勇路1號")
      .setLng(120.281855f).setLat(22.620285f)
      .setTravelPhotoPath("http://i.imgur.com/OkqeTvs.jpg")
      .setContent("休憩、散步、看展、騎腳踏車").save();

    User userId2 = DataSupport.find(User.class, 2);

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2013-09-29 10:00:01").setGoods(178).setShow(true)
      .setTitle("屏東教育大學-南瓜樹屋")
      .setAddress("屏東市民生路4-18號")
      .setLng(120.503981f).setLat(22.664396f)
      .setTravelPhotoPath("http://i.imgur.com/5RCgTxm.jpg")
      .setContent("那裡的南瓜樹屋，我們最喜歡探個究竟").save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2013-09-29 12:55:41").setGoods(738).setShow(true)
      .setTitle("屏東孔廟")
      .setAddress("屏東市勝利路38號")
      .setLng(120.4920f).setLat(22.6786f)
      .setTravelPhotoPath("http://i.imgur.com/WwJLcUu.jpg")
      .setContent("屏東書院走走").save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2013-09-29 14:59:11").setGoods(188).setShow(true)
      .setTitle("斜張橋畔")
      .setAddress("高縣大樹鄉統領路72-50號")
      .setLng(120.409762f).setLat(22.670217f)
      .setTravelPhotoPath("http://i.imgur.com/kG5Y6Lu.jpg")
      .setContent("國內首座複合式斜張橋，亞洲最長之非對稱型單橋塔斜張橋採單塔非對稱型式").save();

    User userId4 = DataSupport.find(User.class, 4);

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-10-24 09:21:16").setGoods(261).setShow(true)
      .setTitle("凱統大飯店")
      .setAddress("台北市大安區忠孝東路三段8號")
      .setLng(121.533513f).setLat(25.041907f)
      .setTravelPhotoPath("http://i.imgur.com/OS1lbkM.jpg")
      .setContent("到台北第一天住的地方").save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-10-24 10:10:47").setGoods(616).setShow(true)
      .setTitle("大安森林公園")
      .setAddress("台北市大安區新生南路二段1號")
      .setLng(121.536076f).setLat(25.030511f)
      .setTravelPhotoPath("http://i.imgur.com/ney8xfY.jpg")
      .setContent("呼吸一下新鮮空氣").save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-10-24 12:30:23").setGoods(703).setShow(true)
      .setTitle("蘇杭小籠包")
      .setAddress("台北市中正區羅斯福路二段14號")
      .setLng(121.520921f).setLat(25.028734f)
      .setTravelPhotoPath("http://i.imgur.com/gTsUITD.jpg")
      .setContent("中午來朝聖一下").save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-10-24 14:20:07").setGoods(516).setShow(true)
      .setTitle("台北植物園")
      .setAddress("台北市中正區南海路53號")
      .setLng(121.510282f).setLat(25.031202f)
      .setTravelPhotoPath("http://i.imgur.com/w7U6IqS.jpg")
      .setContent("2000千多種植物").save();


    User userId3 = DataSupport.find(User.class, 3);

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-11-24 08:59:11").setGoods(311).setShow(true)
      .setTitle("橋頭糖廠")
      .setAddress("高雄市橋頭區糖廠路24號")
      .setLng(120.315139f).setLat(22.756829f)
      .setTravelPhotoPath("http://i.imgur.com/EI7ei4Y.png")
      .setContent("五分車慢慢走可以沿途欣賞風景").save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-11-24 11:20:37").setGoods(239).setShow(true)
      .setTitle("花田喜事")
      .setAddress("高雄縣橋頭鄉隆豐路1號")
      .setLng(120.305739f).setLat(22.757664f)
      .setTravelPhotoPath("http://i.imgur.com/ZrtkGxO.jpg")
      .setContent("希望大家都能有公德心阿 不要亂踐踏花海").save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-11-24 13:55:10").setGoods(399).setShow(true)
      .setTitle("橋頭白樹社區")
      .setAddress("高雄市橋頭區興樹路與建樹路交叉路口")
      .setLng(120.303902f).setLat(22.759900f)
      .setTravelPhotoPath("http://i.imgur.com/NOZIn5N.jpg")
      .setContent("畫風很可愛可惜有點小").save();



    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-11-29 09:30:02").setGoods(937).setShow(true)
      .setTitle("旗山糖廠")
      .setAddress("高雄市旗山區仁愛街一號")
      .setLng(120.492281f).setLat(22.878441f)
      .setTravelPhotoPath("http://i.imgur.com/VEkOOBv.jpg")
      .setContent("吃冰囉").save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-11-29 10:20:27").setGoods(1144).setShow(true)
      .setTitle("旗山武德殿")
      .setAddress("高雄市旗山區華中街")
      .setLng(120.480171f).setLat(22.888077f)
      .setTravelPhotoPath("http://i.imgur.com/DdGHWRQ.jpg")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-11-29 11:40:33").setGoods(539).setShow(true)
      .setTitle("加利利漂流木方舟教會")
      .setAddress("台南市玉井區三埔里83-1號")
      .setLng(120.495363f).setLat(23.092785f)
      .setTravelPhotoPath("http://i.imgur.com/sXD0dxi.jpg")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2013-11-29 12:15:07").setGoods(592).setShow(true)
      .setTitle("曾文青年活動中心")
      .setAddress("台南市楠西區密枝里70之1號")
      .setLng(120.496480f).setLat(23.217708f)
      .setTravelPhotoPath("http://i.imgur.com/7aEapWu.jpg")
      .save();



    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-12-01 11:50:22").setGoods(771).setShow(true)
      .setTitle("蜻蜓雅築與食在唯一")
      .setAddress("屏東縣三地門鄉中正路二段9號")
      .setLng(120.652573f).setLat(22.713448f)
      .setTravelPhotoPath("http://i.imgur.com/4BZB9rj.jpg")
      .setContent("很多排灣族特色琉璃珠")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-12-01 13:30:07").setGoods(244).setShow(true)
      .setTitle("萬金教堂")
      .setAddress("屏東縣萬巒鄉萬金村萬興路24號")
      .setLng(120.611222f).setLat(22.595085f)
      .setTravelPhotoPath("http://i.imgur.com/Hg6Y2eV.jpg")
      .setContent("台灣最古老的教堂")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-12-01 15:10:33").setGoods(472).setShow(true)
      .setTitle("涼山瀑布")
      .setAddress("屏東縣瑪家鄉")
      .setLng(120.644608f).setLat(22.682496f)
      .setTravelPhotoPath("http://i.imgur.com/LXChQmy.jpg")
      .setContent("溪水涼涼的很舒服")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2013-12-01 15:10:33").setGoods(472).setShow(true)
      .setTitle("涼山瀑布")
      .setAddress("屏東縣瑪家鄉")
      .setLng(120.644608f).setLat(22.682496f)
      .setTravelPhotoPath("http://i.imgur.com/LXChQmy.jpg")
      .setContent("溪水涼涼的很舒服")
      .save();

    //---new
    new WormholeTraveller()
      .setUser(userId1)
      .setDate("2015-04-25 09:10:33").setGoods(172).setShow(true)
      .setTitle("燕巢烏山頂泥火山")
      .setAddress("高雄市燕巢區金山村")
      .setLng(120.405971f).setLat(22.796035f)
      .setTravelPhotoPath("http://i.imgur.com/vWVsQIi.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId1)
      .setDate("2015-04-25 11:17:33").setGoods(492).setShow(true)
      .setTitle("新養女湖")
      .setAddress("高雄市燕巢區金山村")
      .setLng(120.406285f).setLat(22.804886f)
      .setTravelPhotoPath("http://i.imgur.com/VSOssOW.jpg")
      .setContent("新養女湖的地型屬泥火山型中的噴泥塘是一個標準的噴泥盆，湖形略呈圓形，真徑約4-6公尺噴出物稀如黑水，所以無法凝結成丘，就形成一個水池狀分布，夾帶隆隆噴發聲，老遠就聽的到，傳說是很久以前，養女湖附近的農家有個叫阿秀的養女與張姓青年相戀，後來被養母強迫嫁給殘癈的異姓哥哥，但阿秀死也不答應，便與張姓青年雙雙投湖，所以得其名。因為其噴泥的聲音就像熱水滾開的聲音一樣，所以還有一個別名為「滾水湖」。\n")
      .save();

    new WormholeTraveller()
      .setUser(userId1)
      .setDate("2015-04-25 13:20:53").setGoods(175).setShow(true)
      .setTitle("雞冠山土雞城")
      .setAddress("高雄市燕巢區金山里麒麟巷10號")
      .setLng(120.399811f).setLat(22.809877f)
      .setTravelPhotoPath("http://i.imgur.com/zzyrC10.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId1)
      .setDate("2015-04-25 15:38:53").setGoods(173).setShow(true)
      .setTitle("雞冠山")
      .setAddress("高雄市燕巢區金山里麒麟巷")
      .setLng(120.395924f).setLat(22.807066f)
      .setTravelPhotoPath("http://i.imgur.com/iemg6nQ.jpg")
      .setContent("岩層斜插入天，頂端裂成許多缺口，好似一把斜放的大鋸齒，山形像極了公雞頭上的大肉冠，無怪乎被人稱為「雞冠山」")
      .save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2015-04-26 09:20:53").setGoods(45).setShow(true)
      .setTitle("阿公店水庫自行車道")
      .setAddress("高雄市燕巢區西燕村工程路")
      .setLng(120.357209f).setLat(22.801761f)
      .setTravelPhotoPath("http://i.imgur.com/abRUvpt.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2015-04-26 11:36:53").setGoods(145).setShow(true)
      .setTitle("阿公店水庫風景區管理處")
      .setAddress("台灣高雄市燕巢區工程路1號")
      .setLng(120.356302f).setLat(22.803323f)
      .setTravelPhotoPath("http://i.imgur.com/7uZFGDo.jpg")
      .setContent("阿公店水庫以防洪為主，兼具灌溉、給水等多方面的功能，其天然的湖光山色更是具備觀光潛力。")
      .save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2015-04-26 13:36:53").setGoods(145).setShow(true)
      .setTitle("岡山河堤公園")
      .setAddress("高雄市岡山區岡山路")
      .setLng(120.295152f).setLat(22.785718f)
      .setTravelPhotoPath("http://i.imgur.com/ezR94bt.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId2)
      .setDate("2015-04-26 15:26:53").setGoods(115).setShow(true)
      .setTitle("大崗山")
      .setAddress("高雄市阿蓮區復安路")
      .setLng(120.344960f).setLat(22.859741f)
      .setTravelPhotoPath("http://i.imgur.com/VTyZtIX.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2015-05-02 08:26:53").setGoods(114).setShow(true)
      .setTitle("大崗山")
      .setAddress("高雄市阿蓮區復安路")
      .setLng(120.344960f).setLat(22.859741f)
      .setTravelPhotoPath("http://i.imgur.com/VTyZtIX.jpg")
      .setContent("空氣很新鮮")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2015-05-02 10:21:53").setGoods(84).setShow(true)
      .setTitle("小崗山")
      .setAddress("高雄市岡山區嘉新東路一段")
      .setLng(120.335966f).setLat(22.817640f)
      .setTravelPhotoPath("http://i.imgur.com/hsNngiK.jpg")
      .setContent("空氣很新鮮")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2015-05-02 14:21:53").setGoods(84).setShow(true)
      .setTitle("阿公店水庫風景區管理處")
      .setAddress("台灣高雄市燕巢區工程路1號")
      .setLng(120.356302f).setLat(22.803323f)
      .setTravelPhotoPath("http://i.imgur.com/7uZFGDo.jpg")
      .setContent("阿公店水庫以防洪為主，兼具灌溉、給水等多方面的功能，其天然的湖光山色更是具備觀光潛力。")
      .save();

    new WormholeTraveller()
      .setUser(userId3)
      .setDate("2015-05-02 16:41:53").setGoods(134).setShow(true)
      .setTitle("烏山頂泥火山自然保留區")
      .setAddress("高雄市燕巢區金山村")
      .setLng(120.405938f).setLat(22.796038f)
      .setTravelPhotoPath("http://i.imgur.com/Gs5Elt7.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2015-05-03 08:11:53").setGoods(124).setShow(true)
      .setTitle("中寮山")
      .setAddress("高雄市田寮區")
      .setLng(120.432457f).setLat(22.830098f)
      .setTravelPhotoPath("http://i.imgur.com/OkzTMbd.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2015-05-03 10:16:53").setGoods(124).setShow(true)
      .setTitle("烏山頂泥火山自然保留區")
      .setAddress("高雄市燕巢區金山村")
      .setLng(120.405938f).setLat(22.796038f)
      .setTravelPhotoPath("http://i.imgur.com/Gs5Elt7.jpg")
      .setContent("")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2015-05-03 14:17:33").setGoods(492).setShow(true)
      .setTitle("新養女湖")
      .setAddress("高雄市燕巢區金山村")
      .setLng(120.406285f).setLat(22.804886f)
      .setTravelPhotoPath("http://i.imgur.com/VSOssOW.jpg")
      .setContent("新養女湖的地型屬泥火山型中的噴泥塘是一個標準的噴泥盆，湖形略呈圓形，真徑約4-6公尺噴出物稀如黑水，所以無法凝結成丘，就形成一個水池狀分布，夾帶隆隆噴發聲，老遠就聽的到，傳說是很久以前，養女湖附近的農家有個叫阿秀的養女與張姓青年相戀，後來被養母強迫嫁給殘癈的異姓哥哥，但阿秀死也不答應，便與張姓青年雙雙投湖，所以得其名。因為其噴泥的聲音就像熱水滾開的聲音一樣，所以還有一個別名為「滾水湖」。\n")
      .save();

    new WormholeTraveller()
      .setUser(userId4)
      .setDate("2015-05-03 16:16:53").setGoods(124).setShow(true)
      .setTitle("雞冠山")
      .setAddress("高雄市燕巢區金山里麒麟巷")
      .setLng(120.395924f).setLat(22.807066f)
      .setTravelPhotoPath("http://i.imgur.com/AmOd9ih.jpg")
      .setContent("岩層斜插入天，頂端裂成許多缺口，好似一把斜放的大鋸齒，山形像極了公雞頭上的大肉冠，無怪乎被人稱為「雞冠山」")
      .save();
  }

//  http://maps.googleapis.com/maps/api/geocode/json?latlng=22.13816,120.5416&sensor=false&language=zh-TW
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
      .setLat(22.83816f).setLng(120.5416f)
      .setUser(userId_2)
      .setTitle("Double Kill").setContent("完成雙殺")
      .save();

    User userId_3 = DataSupport.find(User.class, 3);
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/VSOssOW.jpg")
      .setDate(Utils.getDate()).setGoods(4563)
      .setLat(22.13816f).setLng(120.5416f)
      .setUser(userId_3)
      .setTitle("Triple Kill").setContent("人品爆發")
      .save();

    User userId_4 = DataSupport.find(User.class, 4);
    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/vWVsQIi.jpg")
      .setDate(Utils.getDate()).setGoods(1525)
      .setLat(22.63816f).setLng(121.0416f)
      .setUser(userId_4)
      .setTitle("Quadra Kill").setContent("尾刀王")
      .save();

    new WormholeTraveller()
      .setTravelPhotoPath("http://i.imgur.com/vWVsQIi.jpg")
      .setDate(Utils.getDate()).setGoods(362)
      .setLat(22.53816f).setLng(121.1416f)
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
