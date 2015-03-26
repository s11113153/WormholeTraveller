package tw.com.s11113153.wormholetraveller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TimeUtils
import com.pnikosis.materialishprogress.ProgressWheel
import groovy.transform.CompileStatic

/**
 * Created by xuyouren on 15/3/26.
 */

@CompileStatic
public class LoadingActivity extends Activity {
  private static final String TAG = LoadingActivity.class.getSimpleName();

  static final float DURATION_TIME = 2.5f /** 2.5 sec **/
  static final float SPEED_TIME = 1.0f / DURATION_TIME as float

  private ProgressWheel mProgressWheel

  private static boolean isLaunch = false

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_loading)

    mProgressWheel = findViewById(R.id.progress_wheel) as ProgressWheel
    mProgressWheel.setProgress(1.0f)
    mProgressWheel.setSpinSpeed(SPEED_TIME)
    mProgressWheel.setCallback([
      onProgressUpdate : { float val ->
        if (val == 1.0f && !isLaunch) {
          toMainActivity()
          isLaunch = true
        } else {
          Log.v(TAG, "" + String.valueOf(val));
        }
      }
    ] as ProgressWheel.ProgressCallback)
  }

  void toMainActivity() {
    new Handler().post([
      run : {
        Intent intent = new Intent()
            .setClass(this, MainActivity.class)
            .addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        startActivity(intent)
        Log.d(TAG, "" + String.valueOf("Run"));
      }
    ] as Runnable)
  }
}
