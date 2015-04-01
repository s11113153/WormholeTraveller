package tw.com.s11113153.wormholetraveller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.view.FeedContextMenuManager;

public class MainActivity
        extends ActionBarActivity
        implements RecycleItemAdapter.OnBottomClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.rvFeed) RecyclerView mRecyclerView;
  @InjectView(R.id.ibAddAlbum) ImageButton mIbAddAlbum;
  @InjectView(R.id.tvLogo) TextView mTvLogo;

  private MenuItem lineMenuItem, squareMenuItem;

  private RecycleItemAdapter mAdapter;

  private boolean isLine = true;

  private static int EXTRA_LAYOUT_SPACE = 800;

  private static boolean loadAnimation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    setConfig();
    if (savedInstanceState == null) {
      loadAnimation = true;
      Log.v(TAG, "" + String.valueOf("savedInstanceState == null"));
    }
    else
        mAdapter.updateItems(false);
  }

  private void setConfig() {
    setUpToolbar();
    setUpRecycleAdapter();
  }

  private void setUpToolbar() {
    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  private void setUpRecycleAdapter() {
    mAdapter = new RecycleItemAdapter(this);
    mAdapter.setOnBottomClickListener(this);

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

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (isLine) {
      lineMenuItem.setVisible(false);
      squareMenuItem.setVisible(true);
    } else {
      lineMenuItem.setVisible(true);
      squareMenuItem.setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_square_article:
        isLine = false;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
          @Override
          protected int getExtraLayoutSpace(RecyclerView.State state) {
            return EXTRA_LAYOUT_SPACE;
          }

          /* TODO Grid Layout error */
          @Override
          public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
            int widthSpec,
            int heightSpec) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
          }
        });
        break;
      case R.id.action_line_article:
        isLine = true;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
          @Override
          protected int getExtraLayoutSpace(RecyclerView.State state) {
            return EXTRA_LAYOUT_SPACE;
          }
        });
        break;
    }
    mRecyclerView.invalidate();
    invalidateOptionsMenu();
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    lineMenuItem = menu.findItem(R.id.action_line_article);
    squareMenuItem = menu.findItem(R.id.action_square_article);

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
    mToolbar.setTranslationY(-paddingSize);
    mTvLogo.setTranslationY(-paddingSize);

    mToolbar.animate()
      .translationY(0)
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal());

    mTvLogo.animate()
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
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal());
    mAdapter.updateItems(true);
  }

    @Override
  public void onCommentsClick(View v, int position) {
    // ivComments 在整個螢幕的座標
    int[] startingLocation = new int[2];
    v.getLocationOnScreen(startingLocation);

    final Intent intent = new Intent(this, CommentsActivity.class);
    intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
    startActivity(intent);

    // no anim when finish and ready to change activity
    overridePendingTransition(0, 0);
  }

  @Override
  public void onMoreClick(View v, int position) {
  }

  @Override
  public void onBackPressed() {
    moveTaskToBack(true);
  }
}