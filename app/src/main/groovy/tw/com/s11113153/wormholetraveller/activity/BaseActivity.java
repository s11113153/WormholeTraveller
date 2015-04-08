package tw.com.s11113153.wormholetraveller.activity;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import tw.com.s11113153.wormholetraveller.utils.DrawerLayoutInstaller;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.view.GlobalMenuView;

/**
 * Created by xuyouren on 15/4/3.
 */
public class BaseActivity extends ActionBarActivity implements GlobalMenuView.OnHeaderClickListener {

  @Optional
  @InjectView(R.id.toolbar) Toolbar mToolbar;

  @Optional
  @InjectView(R.id.tvLogo)  TextView mTvLogo;

  private DrawerLayout mDrawerLayout;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.inject(this);
    setUpToolbar();
    if (shouldInstallDrawer())
        setUpDrawer();
  }

  protected void setUpToolbar() {
    if (mToolbar == null) return;

    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  protected boolean shouldInstallDrawer() {
    return true;
  }

  public Toolbar getToolbar() {
    return mToolbar;
  }

  public TextView getTvLogo() {
    return mTvLogo;
  }

  private void setUpDrawer() {
    GlobalMenuView menuView = new GlobalMenuView(this);
    menuView.setOnHeaderClickListener(this);

    mDrawerLayout = DrawerLayoutInstaller.from(this)
      .drawerRoot(R.layout.drawer_root)
      .drawerLeftView(menuView)
      .drawerLeftWidth((int) Utils.doPx(this, Utils.PxType.DP_TO_PX, 300))
      .withNavigationIconToggler(getToolbar())
      .build();
  }

  @Override
  public void onGlobalMenuHeaderClick(final View v) {
    mDrawerLayout.closeDrawer(Gravity.START);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseActivity.this);
      }
    }, 200);
  }
}
