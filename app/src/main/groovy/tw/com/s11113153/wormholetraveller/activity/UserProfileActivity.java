package tw.com.s11113153.wormholetraveller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.adapter.UserProfileAdapter;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.view.RevealBackgroundView;

/**
 * Created by xuyouren on 15/4/3.
 */
public class UserProfileActivity
            extends BaseActivity
            implements RevealBackgroundView.OnStateChangeListener {

  private static final String TAG = UserProfileActivity.class.getSimpleName();

  public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

  @InjectView(R.id.vRevealBackground) RevealBackgroundView vRevealBackground;
  @InjectView(R.id.rvUserProfile) RecyclerView rvUserProfile;

  private UserProfileAdapter userPhotosAdapter;

  private static User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);
    setUpUserProfileGrid();
    setUpRevealBackground(savedInstanceState);
  }

  public static void startUserProfileFromLocation(
          int[] startingLocation, Activity startingActivity, User user
  ) {
    final Intent intent = new Intent(startingActivity, UserProfileActivity.class);
    intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
    startingActivity.startActivity(intent);
    mUser = user;
  }


  private void setUpUserProfileGrid() {
    final StaggeredGridLayoutManager layoutManager
                = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
    rvUserProfile.setLayoutManager(layoutManager);
    rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        userPhotosAdapter.setLockedAnimations(true);
      }
    });
  }

  private void setUpRevealBackground(Bundle savedInstanceState) {
    vRevealBackground.setOnStateChangeListener(this);
    if (savedInstanceState == null) {
      final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
      vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
          vRevealBackground.startFromLocation(startingLocation);
          return true;
        }
      });
    } else {
      vRevealBackground.setToFinishedFrame();
      userPhotosAdapter.setLockedAnimations(true);
    }
  }

  @Override
  public void onStateChange(int state) {
    if (RevealBackgroundView.STATE_FINISHED == state) {
      userPhotosAdapter = new UserProfileAdapter(this, mUser);
      rvUserProfile.setAdapter(userPhotosAdapter);
      rvUserProfile.setVisibility(View.VISIBLE);
    } else
      rvUserProfile.setVisibility(View.INVISIBLE);
  }
}
