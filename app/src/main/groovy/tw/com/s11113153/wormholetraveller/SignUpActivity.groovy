package tw.com.s11113153.wormholetraveller

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBarActivity
import android.view.View
import com.gc.materialdesign.views.ButtonRectangle
import com.pnikosis.materialishprogress.ProgressWheel
import com.rengwuxian.materialedittext.MaterialEditText
import groovy.transform.CompileStatic

/**
 * Created by xuyouren on 15/3/28.
 */
@CompileStatic
public class SignUpActivity extends ActionBarActivity {
  private static final String TAG = SignUpActivity.class.getSimpleName()

  MaterialEditText mEtId, mEtPassword, mEtMail
  ProgressWheel mProgressWheel
  ButtonRectangle  mBtnLogin

  interface LoginImpl {
    void check(String id, String password, String mail)
    void show(boolean isTrue)
  }

  def mLoginImpl

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    toMainActivity()
    return
    init()
    setConfig()
  }

  void init() {
    mEtId = findViewById(R.id.etId) as MaterialEditText
    mEtPassword = findViewById(R.id.etPassword) as MaterialEditText
    mEtMail = findViewById(R.id.etMail) as MaterialEditText
    mProgressWheel = findViewById(R.id.progress_wheel) as ProgressWheel
    mBtnLogin = findViewById(R.id.btnLogin) as ButtonRectangle

    mLoginImpl = [
      onClick : { View v ->
        String id = mEtId.text.toString().trim()
        String password = mEtPassword.text.toString().trim()
        String mail = mEtMail.text.toString().trim()

        if (id?.length() > 10 || password?.length() > 10 || mail?.length() > 30) {
          return
        }

        ((mLoginImpl) as LoginImpl).show(true)
        ((mLoginImpl) as LoginImpl).check("", "", "")
      }
      ,
      check : { String id, String password, String mail ->
        new Handler().postDelayed(new Runnable() {
          @Override
          void run() {
            ((mLoginImpl) as LoginImpl).show(false)
            toMainActivity()
          }
        }, 2000)
      },
      show : { boolean isTrue ->
        // start
        if (isTrue && mProgressWheel?.getVisibility() == View.GONE) {
          mProgressWheel.setVisibility(View.VISIBLE)
          mBtnLogin?.text = ""
        }
        // close
        if (!isTrue && mProgressWheel?.getVisibility() == View.VISIBLE) {
          mProgressWheel.setVisibility(View.GONE)
          mBtnLogin?.text = getString(R.string.login)
        }
      }
    ]
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


