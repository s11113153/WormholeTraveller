package tw.com.s11113153.wormholetraveller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast

/**
 * Created by xuyouren on 15/3/26.
 */
public class LoadingActivity extends Activity {
  private static final String TAG = LoadingActivity.class.getSimpleName();
  static final int DURATION = 1

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState)
    new Handler().postDelayed([
      run : {
            Intent intent = new Intent()
            intent.setClass(this, MainActivity.class)
            intent.addFlags(
              Intent.FLAG_ACTIVITY_CLEAR_TASK |
              Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
      }
    ] as Runnable, DURATION)
  }
}
