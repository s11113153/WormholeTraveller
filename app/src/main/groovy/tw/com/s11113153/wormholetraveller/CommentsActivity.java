package tw.com.s11113153.wormholetraveller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xuyouren on 15/3/29.
 */
public class CommentsActivity extends ActionBarActivity {
  public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.contentRoot) LinearLayout mContentRoot;
  @InjectView(R.id.rvComments) RecyclerView mRecyclerView;
  @InjectView(R.id.llAddComment) LinearLayout mllAddComments;

  private CommentItemAdapter mAdapter;

  private int drawingStartLocation;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);
    ButterKnife.inject(this);
    setConfig();

    drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
    if (savedInstanceState == null) {
      //取得正要準備繪製佈局的callback
      mContentRoot.getViewTreeObserver()
        .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
          @Override
          public boolean onPreDraw() {
            mContentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
            startIntroAnimation();
            return true;
          }
        });
    }
  }

  private void startIntroAnimation() {
    Toast.makeText(CommentsActivity.this, String.valueOf("Hello"), Toast.LENGTH_LONG).show();
    // 動畫準備
    mContentRoot.setScaleY(0.1f); // 設置縮放起始點為0.1倍
    mContentRoot.setPivotY(drawingStartLocation); // 當 view 設置 setScaleY, 此函數必須使用
    mllAddComments.setTranslationY(100); // view原本的位置在向下移動100px

    // 執行動畫
    mContentRoot.animate()
      .scaleY(1)
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .setInterpolator(new AccelerateInterpolator())
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          animateContent();
        }
      })
      .start();
  }

  private void animateContent() {
    mllAddComments.animate()
      .translationY(0) // 回到 view 最初始的 y 座標
      .setInterpolator(new DecelerateInterpolator())
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .start();
  }

  private void setConfig() {
    setUpToolbar();
    setUpComments();
  }

  private void setUpToolbar() {
    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  private void setUpComments() {
    mAdapter = new CommentItemAdapter(this);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setHasFixedSize(true); //告知 View Content為固定 Size, 優化RecycleView
    // 防止滑動出現動畫
    mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
          mAdapter.setAnimationsLocked(true);
        }
      }
    });
  }

  @Override
  public void onBackPressed() {
    mContentRoot.animate()
      .translationY(Utils.getScreenHeight(this))
      .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          CommentsActivity.super.onBackPressed();
          overridePendingTransition(0, 0);
        }
      })
      .start();
  }

  /* TODO */
  @OnClick(R.id.btnSendComment)
  public void onSendCommentClick() {
    mAdapter.setAnimationsLocked(false);
    mAdapter.setDelayEnterAnimation(false);
    mRecyclerView.smoothScrollBy(
      0, mRecyclerView.getChildAt(0).getHeight() * mAdapter.getItemCount()
    );
    Toast.makeText(this, "No implements function", Toast.LENGTH_SHORT).show();
  }
}
