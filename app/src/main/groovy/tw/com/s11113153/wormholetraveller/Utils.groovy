package tw.com.s11113153.wormholetraveller

import android.content.Context
import android.content.Intent;
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue;

/**
 * Created by xuyouren on 15/3/27.
 */
final public class Utils {

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
}
