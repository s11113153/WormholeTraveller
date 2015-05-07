package tw.com.s11113153.wormholetraveller.activity;

import com.google.android.gms.maps.MapFragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.adapter.GlobalMenuAdapter;
import tw.com.s11113153.wormholetraveller.adapter.RecycleItemAdapter;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.view.FeedContextMenu;
import tw.com.s11113153.wormholetraveller.view.FeedContextMenuManager;
import tw.com.s11113153.wormholetraveller.view.GlobalMenuView;

public class MainActivity
  extends BaseActivity
  implements RecycleItemAdapter.OnBottomClickListener,
  FeedContextMenu.OnFeedContextMenuItemClickListener,
  BaseActivity.OnLatLngGetListener,
  RecycleItemAdapter.OnDeleteTravelListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  public static final String ACTION_SHOW_LOADING_PHOTO = "action_show_loading_item";

  @InjectView(R.id.rvFeed) RecyclerView mRecyclerView;
  @InjectView(R.id.ibAddAlbum) ImageButton mIbAddAlbum;

  private MenuItem lineMenuItem, squareMenuItem;

  private RecycleItemAdapter mAdapter;

  private boolean isLine = true;

  private static int EXTRA_LAYOUT_SPACE = 800;

  private static boolean loadAnimation;

  private float lat, lng;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setOnLatLngGetListener(this);
//    setUpRecycleAdapter();
    if (savedInstanceState == null) {
      loadAnimation = true;
      Log.v(TAG, "" + String.valueOf("savedInstanceState == null"));
    }
    else
      mAdapter.updateItems(false);
  }

  @Override
  public void get(float lat, float lng) {
    this.lat = lat;
    this.lng = lng;
    if (mAdapter == null) {
      setUpRecycleAdapter(lat, lng);
    }
    UserInfo.updateUserCurrentLatLng(this, lat, lng);
  }

  private void setUpRecycleAdapter(float lat, float lng) {
    mAdapter = new RecycleItemAdapter(this, lat, lng);
    mAdapter.setOnBottomClickListener(this);
    mAdapter.setOnDeleteTravelListener(this);

    // 提高使用者效能體驗
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
      @Override
      protected int getExtraLayoutSpace(RecyclerView.State state) {
        return EXTRA_LAYOUT_SPACE;
      }
    });
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
      }
    });
  }

/* TODO 暫時移除分頁功能 */
//  @Override
//  public boolean onPrepareOptionsMenu(Menu menu) {
//    if (isLine) {
//      lineMenuItem.setVisible(false);
//      squareMenuItem.setVisible(true);
//    } else {
//      lineMenuItem.setVisible(true);
//      squareMenuItem.setVisible(false);
//    }
//    return true;
//  }

/* TODO 暫時移除分頁功能 */
//  @Override
//  public boolean onOptionsItemSelected(final MenuItem item) {
//    switch (item.getItemId()) {
//      case R.id.action_square_article:
//        isLine = false;
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
//          @Override
//          protected int getExtraLayoutSpace(RecyclerView.State state) {
//            return EXTRA_LAYOUT_SPACE;
//          }
//
//          /* TODO Grid Layout error */
//          @Override
//          public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
//            int widthSpec,
//            int heightSpec) {
//            super.onMeasure(recycler, state, widthSpec, heightSpec);
//          }
//        });
//        break;
//      case R.id.action_line_article:
//        isLine = true;
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
//          @Override
//          protected int getExtraLayoutSpace(RecyclerView.State state) {
//            return EXTRA_LAYOUT_SPACE;
//          }
//        });
//        break;
//    }
//    mRecyclerView.invalidate();
//    invalidateOptionsMenu();
//    return true;
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
/* TODO 暫時移除分頁功能 */
//    lineMenuItem = menu.findItem(R.id.action_line_article);
//    squareMenuItem = menu.findItem(R.id.action_square_article);

    if (loadAnimation) {
      loadAnimation = false;
      startIntroAnimation();
    }
    return true;
  }

  private void startIntroAnimation() {
    int paddingSize = (int) Utils.doPx(
      this, Utils.PxType.DP_TO_PX, (int) Utils.AnimationAttribute.PADDING.getVal());

    mIbAddAlbum.setTranslationY(3 * getResources().getDimensionPixelOffset(R.dimen.btn_add_album_size));
    getToolbar().setTranslationY(-paddingSize);
    getTvLogo().setTranslationY(-paddingSize);

    getToolbar().animate()
      .translationY(0)
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal());

    getTvLogo().animate()
      .translationY(0)
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal())
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          startContentAnimation();
        }
      });
  }

  private void startContentAnimation() {
    mIbAddAlbum.animate()
      .translationY(0)
      .setInterpolator(new OvershootInterpolator(3.0f))
      .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal())
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .start();
    mAdapter.updateItems(true);
  }

  @Override
  public void onCommentsClick(View v, int position, int travelId) {
    Log.d(TAG, "com" + String.valueOf(travelId));
    // ivComments 在整個螢幕的座標
    int[] startingLocation = new int[2];
    v.getLocationOnScreen(startingLocation);

    final Intent intent = new Intent(this, CommentsActivity.class);
    intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
    intent.putExtra(CommentsActivity.TRAVEL_ID, travelId);
    startActivity(intent);

    // no anim when finish and ready to change activity
    overridePendingTransition(0, 0);
  }

  @Override
  public void onMoreClick(View v, int position) {
    FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
  }

  @Override
  public void onProfileClick(View v, User u) {
    int [] startingLocation = new int[2];
    v.getLocationOnScreen(startingLocation);
    startingLocation[0] += v.getWidth() / 2;
    UserProfileActivity.startUserProfileFromLocation(startingLocation, this, u);
    overridePendingTransition(0, 0);
  }


  @OnClick(R.id.ibAddAlbum)
  public void onTakePhotoClick() {
    int[] startingLocation = new int[2];
    mIbAddAlbum.getLocationOnScreen(startingLocation);
    startingLocation[0] += mIbAddAlbum.getWidth() / 2;
    TakePhotoActivity.startCameraFromLocation(startingLocation, this);
    overridePendingTransition(0, 0);
  }

  @Override
  public void onShowMap(final View v, final int feedItem) {
    final int travelId = (int)mAdapter.getItemId(feedItem);
    mRecyclerView.smoothScrollToPosition(feedItem);
    FeedContextMenuManager.getInstance().hideContextMenu();
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        MapsFragment.startMapsFromLocation(getFragmentManager(), startingLocation, travelId, MapsFragment.SEARCH_ROUND, false);
//        final MapsFragment fragment = new MapsFragment();
//        Bundle bundle = new Bundle();
//        bundle.putIntArray(MapsFragment.ARG_REVEAL_START_LOCATION, startingLocation);
//        bundle.putInt(MapsFragment.TRAVEL_ID, travelId);
//        fragment.setArguments(bundle);
//        getFragmentManager()
//          .beginTransaction()
//          .addToBackStack("")
//          .add(R.id.root, fragment)
//          .commit();
        overridePendingTransition(0, 0);
      }
    }, 200);
  }



  @Override
  public void onPlay(int feedItem) {
  }

  @Override
  public void onAddFavorite(int feedItem) {

  }

  @Override
  public void onReturn(int feedItem) {
    FeedContextMenuManager.getInstance().hideContextMenu();
  }

  @Override
  public void onBackPressed() {
    int count = getFragmentManager().getBackStackEntryCount();
    if (count == 0) {
      moveTaskToBack(true);
    } else {
      MapsFragment.changeMode();
      getFragmentManager().popBackStack();
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (ACTION_SHOW_LOADING_PHOTO.equals(intent.getAction())) {
      showRecycleLoadingItemDelayed();
    }
  }

  private void showRecycleLoadingItemDelayed() {
    mRecyclerView.smoothScrollToPosition(0);
    mAdapter.showLoadingView();
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mAdapter.updateItems(false);
//        mAdapter = new RecycleItemAdapter(MainActivity.this, lat ,lng);
//        mRecyclerView.setAdapter(mAdapter);
      }
    }, 2000);
  }

  @Override
  public void onDelete(final int wtId) {
    mRecyclerView.smoothScrollToPosition(0);
    mAdapter.showLoadingView();

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        WormholeTraveller.delete(WormholeTraveller.class, wtId);
        mAdapter.updateItems(false);
//        mAdapter = new RecycleItemAdapter(MainActivity.this, lat ,lng);
//        mRecyclerView.setAdapter(mAdapter);
      }
    }, 2000);
  }
}