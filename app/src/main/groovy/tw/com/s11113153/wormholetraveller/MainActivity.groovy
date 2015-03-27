package tw.com.s11113153.wormholetraveller

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.TextView

public class MainActivity extends Activity {
  Toolbar mToolbar
  TextView mTextView

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init()
    setConfig()
  }

  void init() {
    mToolbar = findViewById(R.id.toolbar) as Toolbar
    mTextView = findViewById(R.id.tvLogo) as TextView
  }

  void setConfig() {
    mTextView.setTypeface(Utils.getFont(this, Utils.FontType.ROBOTO_BOLD_ITALIC))
  }

  @Override
  public void onBackPressed() {
    moveTaskToBack(true);
  }
}
