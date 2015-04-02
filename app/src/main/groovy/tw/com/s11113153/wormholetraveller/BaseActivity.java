package tw.com.s11113153.wormholetraveller;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyouren on 15/4/3.
 */
public class BaseActivity extends ActionBarActivity {

  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.tvLogo)  TextView mTvLogo;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.inject(this);
    setUpToolbar();
  }

  protected void setUpToolbar() {
    if (mToolbar == null) return;

    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  public Toolbar getToolbar() {
    return mToolbar;
  }

  public TextView getTvLogo() {
    return mTvLogo;
  }
}
