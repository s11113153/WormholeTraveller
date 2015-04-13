package tw.com.s11113153.wormholetraveller.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBarActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gc.materialdesign.views.ButtonRectangle
import com.pnikosis.materialishprogress.ProgressWheel
import com.rengwuxian.materialedittext.MaterialEditText
import groovy.transform.CompileStatic
import org.litepal.crud.DataSupport
import tw.com.s11113153.wormholetraveller.R
import tw.com.s11113153.wormholetraveller.Utils
import tw.com.s11113153.wormholetraveller.db.table.User

/**
 * Created by xuyouren on 15/3/28.
 */
@Deprecated
@CompileStatic
public class SignUpActivity extends ActionBarActivity {
  private static final String TAG = SignUpActivity.class.getSimpleName()

  MaterialEditText mEtId, mEtPassword, mEtMail
  ProgressWheel mProgressWheel
  ButtonRectangle  mBtnLogin
  Animation animtionShakeError

  interface LoginImpl {
    void check(String id, String password, String mail)
    void show(boolean isTrue)
  }

  def mLoginImpl

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    init()
    setConfig()
  }

  void init() {
    mEtId = findViewById(R.id.etAccount) as MaterialEditText
    mEtPassword = findViewById(R.id.etPassword) as MaterialEditText
    mEtMail = findViewById(R.id.etMail) as MaterialEditText
    mProgressWheel = findViewById(R.id.progress_wheel) as ProgressWheel
    mBtnLogin = findViewById(R.id.btnLogin) as ButtonRectangle

    animtionShakeError = AnimationUtils.loadAnimation(this, R.anim.shake_error)

    mLoginImpl = [
      onClick : { View v ->
        String id = mEtId.text.toString().trim()
        String password = mEtPassword.text.toString().trim()
        String mail = mEtMail.text.toString().trim()

        if (id?.length() > 10 || password?.length() > 10 || mail?.length() > 30) {
          return
        }
        loading()
        ((mLoginImpl) as LoginImpl).show(true)
        ((mLoginImpl) as LoginImpl).check(
            mEtId.text.toString(),
            mEtPassword.text.toString(),
            mEtPassword.text.toString()
        )
      }
      ,
      check : { String id, String password, String mail ->
        List<User> userList = DataSupport.where("account = ?", id).find(User.class)
        if (userList.size() < 1) {
//          ((mLoginImpl) as LoginImpl).show(false)
        }
        else {
          new Handler().postDelayed(new Runnable() {

            @Override
            void run() {
              ((mLoginImpl) as LoginImpl).show(true)
              toMainActivity()
            }
          }, 2000)
        }
      },
      show : { boolean isTrue ->
        // 登入成功
        if (isTrue && mProgressWheel?.getVisibility() == View.GONE) {
          mProgressWheel.setVisibility(View.VISIBLE)
          mBtnLogin?.text = "Success"
        }
        // 登入失敗
        if (!isTrue && mProgressWheel?.getVisibility() == View.VISIBLE) {
          mProgressWheel.setVisibility(View.GONE)
          mBtnLogin?.text = getString(R.string.login)
          mBtnLogin.startAnimation(animtionShakeError)
        }
      }
    ]
  }

  private void loading() {
    mProgressWheel.visibility = View.VISIBLE
    mBtnLogin.text = ""
  }

  private void stop() {

  }

  void setConfig() {
    mBtnLogin.setOnClickListener(mLoginImpl as View.OnClickListener)
    setFontType()
  }

  void setFontType() {
    mEtId.setTypeface(Utils.getFont(this, Utils.FontType.ROBOTO_REGULAR))
    mEtId.setTextSize(Utils.doPx(this, Utils.PxType.SP_TO_PX, 8))
    mEtPassword.setTypeface(Utils.getFont(this, Utils.FontType.ROBOTO_REGULAR))
    mEtPassword.setTextSize(Utils.doPx(this, Utils.PxType.SP_TO_PX, 8))
    mEtMail.setTypeface(Utils.getFont(this, Utils.FontType.ROBOTO_REGULAR))
    mEtMail.setTextSize(Utils.doPx(this, Utils.PxType.SP_TO_PX, 8))
  }

  void toMainActivity() {
    Utils.switchActivity(this, MainActivity.class)
  }
}


