package tw.com.s11113153.wormholetraveller.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBarActivity
import android.widget.TextView
import com.pnikosis.materialishprogress.ProgressWheel
import groovy.transform.CompileStatic
import tw.com.s11113153.wormholetraveller.R

/**
 * Created by xuyouren on 15/3/26.
 */

@CompileStatic
public class LoadingActivity extends ActionBarActivity {
  private static final String TAG = LoadingActivity.class.getSimpleName()

  static final float DURATION_TIME = 0.5f /** 2.5 sec **/
  static final float SPEED_TIME = 1.0f / DURATION_TIME as float

  private ProgressWheel mProgressWheel
  TextView mTextView
  Typeface mTypeface

  private static boolean isLaunch = false

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_loading)

    mProgressWheel = findViewById(R.id.progress_wheel) as ProgressWheel
    mTextView = findViewById(R.id.textView) as TextView
    mTypeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
    mTextView.setTypeface(mTypeface)

    mProgressWheel.setProgress(1.0f)
    mProgressWheel.setSpinSpeed(SPEED_TIME)
    mProgressWheel.setCallback([
      onProgressUpdate : { float val ->
        if (val == 1.0f && !isLaunch) {
          toSignUpActivity()
          isLaunch = true
        } else {
          String content = '' + (mTextView.getText() as String)
          content = content?.split(' ')[0]
          switch (content.lastIndexOf('.')) {
            case [-1, 7, 8] : content += '.'
              break
            case 9 : content = 'loading.'
              break
          }
          int progress = val * 100 as int
         mTextView.setText(String.format("%-14s %3d", content, progress))
        }
      }
    ] as ProgressWheel.ProgressCallback)
  }

  void toSignUpActivity() {
    new Handler().post([
      run : {
        Intent intent = new Intent()
            .setClass(this, SignUpActivity.class)
            .addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        startActivity(intent)
      }
    ] as Runnable)
  }
}
