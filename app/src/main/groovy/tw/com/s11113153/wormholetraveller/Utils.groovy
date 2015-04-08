package tw.com.s11113153.wormholetraveller

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import groovy.transform.CompileStatic

/**
 * Created by xuyouren on 15/3/27.
 */
@CompileStatic
final public class Utils {
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
}
