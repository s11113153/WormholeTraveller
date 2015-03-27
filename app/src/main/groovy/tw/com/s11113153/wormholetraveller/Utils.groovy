package tw.com.s11113153.wormholetraveller

import android.content.Context;
import android.graphics.Typeface
import android.util.Log;

/**
 * Created by xuyouren on 15/3/27.
 */
final public class Utils {

  static enum FontType {
    ROBOTO_LIGHT  ("Roboto-Light.ttf"),
    ROBOTO_MEDIUM ("Roboto-Medium.ttf"),
    ROBOTO_BOLD_ITALIC ("Roboto-BoldItalic.ttf")

    final String font
    FontType(String font) {
      this.font = font
    }
  }

  static Typeface getFont(Context context, Utils.FontType fontType) {
    return Typeface.createFromAsset(context?.getAssets(), "fonts/" + fontType?.getFont())
  }

}
