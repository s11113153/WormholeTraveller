package tw.com.s11113153.wormholetraveller.activity;

import com.gc.materialdesign.views.ButtonRectangle;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.DataBaseManager;
import tw.com.s11113153.wormholetraveller.db.table.User;

/**
 * Created by xuyouren on 15/4/13.
 */
public class SignUpActivity2 extends Activity {

  @InjectView(R.id.etAccount) EditText mEtAccount;
  @InjectView(R.id.etPassword) EditText mEtPassword;
  @InjectView(R.id.etMail) EditText mEtMail;
  @InjectView(R.id.btnLogin) ButtonRectangle mBtnLogin;
  @InjectView(R.id.progress_wheel) ProgressWheel mProgressWheel;

  private Animation animtionShakeError;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    ButterKnife.inject(this);
    animtionShakeError = AnimationUtils.loadAnimation(this, R.anim.shake_error);
    setFontType();
    DataBaseManager.open().InitialSample(this);
    IsLogin();
  }

  @OnClick(R.id.btnLogin)
  void onLogin() {
    loading();
    String account = mEtAccount.getText().toString().trim();
    String password = mEtPassword.getText().toString().trim();
    String mail = mEtMail.getText().toString().trim();
    check(account, password, mail);
  }

  private void loading() {
    if (mProgressWheel.getVisibility() == View.GONE) {
      mProgressWheel.setVisibility(View.VISIBLE);
      mBtnLogin.setText("");
    }
  }

  private void showResult(final boolean isTrue, final User... u) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressWheel.getVisibility() == View.VISIBLE) {
          mProgressWheel.setVisibility(View.GONE);
        }
        if (isTrue) {
          mBtnLogin.setText("Success");
          UserInfo.setUser(SignUpActivity2.this, u[0]);
          toSignUpActivity();
        }
        else {
          mBtnLogin.setText("Login");
          mBtnLogin.startAnimation(animtionShakeError);
        }
      }
    });
  }

  private static final int DELAY_TIME = 500;

  public void check(final String account, final String password, final String mail) {
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        final List<User> users = DataSupport.where("account = ?", account).find(User.class);
        if (users.size() <= 0)
          showResult(false);
        else {
          User user = users.get(0);
          if (user.getPassword().equals(password)) {
            showResult(true, user);
          } else {
            showResult(false);
          }
        }
      }
    }, DELAY_TIME);
  }

  private void IsLogin() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        User user = UserInfo.getUser(SignUpActivity2.this);
        if (user.getAccount().equals("") && user.getPassword().equals("")){}
        else  toSignUpActivity();
      }
    }).start();
  }

  private void toSignUpActivity() {
    finish();
    Intent intent = new Intent()
      .setClass(this, MainActivity.class)
      .addFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TASK |
          Intent.FLAG_ACTIVITY_NEW_TASK
      );
    startActivity(intent);
    overridePendingTransition(0, 0);
  }

  public void setFontType() {
    float textSize = Utils.doPx(this, Utils.PxType.SP_TO_PX, 8);
    Typeface typeface = Utils.getFont(this, Utils.FontType.ROBOTO_REGULAR);
    mEtAccount.setTypeface(typeface);
    mEtAccount.setTextSize(textSize);
    mEtPassword.setTypeface(typeface);
    mEtPassword.setTextSize(textSize);
    mEtMail.setTypeface(typeface);
    mEtMail.setTextSize(textSize);
  }
}
