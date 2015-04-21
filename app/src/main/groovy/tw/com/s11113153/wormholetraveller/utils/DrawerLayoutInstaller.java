package tw.com.s11113153.wormholetraveller.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * Created by xuyouren on 15/4/7.
 */
public class DrawerLayoutInstaller {
  public static final int DEFAULT_LEFT_DRAWER_WIDTH_DP = 120;

  public static DrawerBuilder from(Activity activity) {
    return new DrawerBuilder(activity);
  }

  public static class DrawerBuilder {
    private final Activity activity;
    private int drawerRootResId;
    private Toolbar toolbar;
    private DrawerLayout.DrawerListener drawerListener;
    private View drawerLeftView;
    private int drawerLeftWidth;

    public DrawerBuilder(Activity activity) {
      this.activity = activity;
    }

    public DrawerBuilder drawerRoot(int drawerRootResId) {
      this.drawerRootResId = drawerRootResId;
      return this;
    }

    public DrawerBuilder withNavigationIconToggler(Toolbar toolbar) {
      this.toolbar = toolbar;
      return this;
    }

    public DrawerBuilder drawerLeftView(View drawerLeftView) {
      this.drawerLeftView = drawerLeftView;
      return this;
    }

    public DrawerBuilder drawerLeftWidth(int width) {
      this.drawerLeftWidth = width;
      return this;
    }

    public DrawerLayout build() {
      DrawerLayout drawerLayout = createDrawerLayout();
      addDrawerToActivity(drawerLayout);
      setUpToggler(drawerLayout);
      setUpDrawerLeftView(drawerLayout);
      drawerLayout.setDrawerListener(drawerListener);
      return drawerLayout;
    }

    private DrawerLayout createDrawerLayout() {
      if (this.drawerRootResId != 0)
        return (DrawerLayout) LayoutInflater.from(activity)
               .inflate(drawerRootResId, null);
      else {
        DrawerLayout drawerLayout = new DrawerLayout(activity);
        FrameLayout contentView = new FrameLayout(activity);
        drawerLayout.addView(
          contentView,
          new DrawerLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
        );

        FrameLayout leftDrawer = new FrameLayout(activity);

        int drawerWidth = drawerLeftWidth != 0 ? drawerLeftWidth : DEFAULT_LEFT_DRAWER_WIDTH_DP;

        final ViewGroup.LayoutParams leftDrawerParams = new DrawerLayout.LayoutParams(
          (int)(drawerWidth * Resources.getSystem().getDisplayMetrics().density),
          ViewGroup.LayoutParams.MATCH_PARENT,
          Gravity.START
        );
        drawerLayout.addView(leftDrawer, leftDrawerParams);
        return drawerLayout;
      }
    }


    private void addDrawerToActivity(DrawerLayout drawerLayout) {
      ViewGroup drawerContentRoot = (ViewGroup) drawerLayout.getChildAt(0);
      ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
      View contentView = rootView.getChildAt(0);
      rootView.removeView(contentView);

      drawerContentRoot.addView(contentView, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      ));

      rootView.addView(drawerLayout, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      ));
    }

    private void setUpToggler(final DrawerLayout drawerLayout) {
      if (toolbar == null) throw new RuntimeException("toolbar is null");
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (drawerLayout.isDrawerOpen(Gravity.START))
              drawerLayout.closeDrawer(Gravity.START);
          else
            drawerLayout.openDrawer(Gravity.START);
        }
      });
    }

    private void setUpDrawerLeftView(DrawerLayout drawerLayout) {
      if (drawerLeftView == null) throw new RuntimeException("drawerLeftView is null");
      ((ViewGroup)drawerLayout.getChildAt(1))
        .addView(drawerLeftView, new DrawerLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }
  }
}
