package tw.com.s11113153.wormholetraveller

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import groovy.transform.CompileStatic
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat

/**
 * Created by xuyouren on 15/3/27.
 */
@CompileStatic
final public class Utils {
  private static final String TAG = Utils.class.getSimpleName();

  private Utils() {
    // do nothing
  }

  static enum AnimationAttribute {
    PADDING(80),
    DURATION_SHORT(400),
    DURATION_LONG(800),
    DELAY_START(200)

    final long val
    AnimationAttribute(long val) {
      this.val = val
    }
  }

  static enum FontType {
    ROBOTO_LIGHT  ("Roboto-Light.ttf"),
    ROBOTO_MEDIUM ("Roboto-Medium.ttf"),
    ROBOTO_BOLD_ITALIC ("Roboto-BoldItalic.ttf"),
    ROBOTO_THIN("Roboto-Thin.ttf"),
    ROBOTO_REGULAR("Roboto-Regular.ttf")

    final String font
    FontType(String font) {
      this.font = font
    }
  }

  public static Typeface getFont(Context context, Utils.FontType fontType) {
    return Typeface.createFromAsset(context?.getAssets(), "fonts/" + fontType?.getFont())
  }

  static enum PxType {
    DP_TO_PX(TypedValue.COMPLEX_UNIT_DIP),
    SP_TO_PX(TypedValue.COMPLEX_UNIT_SP)

    final int val
    PxType(int val) {
      this.val = val
    }
  }

  public static float doPx(Context context, Utils.PxType type, int val) {
    return TypedValue.applyDimension(type.getVal(), val, context.getResources().getDisplayMetrics())
  }

  public static void switchActivity(Context context, Class<?> cls) {
    context.startActivity(new Intent(context, cls))
  }


  static int screenHeight = 0;
  public static int getScreenHeight(Context c) {
    if (screenHeight == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }
    return screenHeight;
  }

  static int screenWidth = 0;
  public static int getScreenWidth(Context c) {
    if (screenWidth == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenWidth = size.x;
    }
    return screenWidth;
  }

  public static void clearBackground(View v) {
    v.setBackground(null)
  }

  public static String getDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(new Date());
  }

  private static final double EARTH_RADIUS = 6378137.0;
  public static double calculateDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
    double radLat1 = (lat_a * Math.PI / 180.0);
    double radLat2 = (lat_b * Math.PI / 180.0);
    double a = radLat1 - radLat2;
    double b = (lng_a - lng_b) * Math.PI / 180.0;
    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
      + Math.cos(radLat1) * Math.cos(radLat2)
      * Math.pow(Math.sin(b / 2), 2)));
    s = s * EARTH_RADIUS;
    s = Math.round(s * 10000) / 10000;
    return s / 1000;
  }

//  static String address
//  public static String getAddress(Context context, float lat, float lng) {
//    address = ""
//    new Thread(new Runnable() {
//      @Override
//      void run() {
//        Geocoder gc = new Geocoder(context, Locale.TAIWAN);
//        List<Address> lstAddress = gc.getFromLocation(lat, lng, 1);
//        Address a = lstAddress.get(0)
//        address += a.adminArea + a.locality + a.thoroughfare + a.featureName + '號'
//      }
//    }).start()
//
//    while (address.is("")) {}
//    return address
//  }

  @Deprecated
  static String tmpAddress
  @Deprecated
  public static String getLocationInfo(float lat, float lng) {
    new Thread(new Runnable() {
      @Override
      void run() {
        HttpGet httpGet = new HttpGet(
          "http://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat+","+lng +"&sensor=false&language=zh-TW"
        );
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        try {
          response = client.execute(httpGet);
          HttpEntity entity = response.getEntity();
          InputStream stream = entity.getContent();
          BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));

          int b;
          while ((b = br.read()) != -1) {
            stringBuilder.append((char) b);
          }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject = new JSONObject(stringBuilder.toString());
          tmpAddress = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }).start()
    while (tmpAddress.is("")) {
    }
    String address = tmpAddress
    tmpAddress = ""
    return address
  }


  static final String GOOGLE_MAP_GEOCODE =
      "http://maps.googleapis.com/maps/api/geocode/json?latlng=_lat,_lng&sensor=false&language=zh-TW"

  public static String getLocationInfo2(float lat, float lng) {
    String address
    Thread.start {
      String url = GOOGLE_MAP_GEOCODE
            .replace("_lat", lat.toString())
            .replace("_lng", lng.toString())
      try {
        HttpGet httpGet = new HttpGet(url)
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
        int b;
        while ((b = br.read()) != -1) {
          stringBuilder.append((char) b);
        }
        address = new JSONObject(stringBuilder.toString())
            .getJSONArray("results").getJSONObject(0).getString("formatted_address");
        Log.e("finish", address)
      } catch (ex) {
        Log.e(TAG, "" + String.valueOf(ex));
      }
    }
    while (address == null) { Thread.sleep(50) }
    return address
  }
}
