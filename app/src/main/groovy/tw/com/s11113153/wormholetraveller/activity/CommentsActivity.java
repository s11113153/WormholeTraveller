package tw.com.s11113153.wormholetraveller.activity;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.litepal.crud.DataSupport;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.adapter.CommentItemAdapter;
import tw.com.s11113153.wormholetraveller.db.DataBaseManager;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;

/**
 * Created by xuyouren on 15/3/29.
 */
public class CommentsActivity extends BaseActivity {
  public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
  public static final String TRAVEL_ID = "travel_id";
  private static final int MESSAGE_CHARACTER_LIMIT = 30;

  @InjectView(R.id.contentRoot) LinearLayout mContentRoot;
  @InjectView(R.id.rvComments) RecyclerView mRecyclerView;
  @InjectView(R.id.llAddComment) LinearLayout mllAddComments;
  @InjectView(R.id.btnSendComment) ButtonRectangle mButtonRectangle;
  @InjectView(R.id.etMessage) MaterialEditText mEtMessage;

  private CommentItemAdapter mAdapter;

  private int drawingStartLocation;

  private int travelId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);
    drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
    travelId = getIntent().getIntExtra(TRAVEL_ID, -1);
    setUpComments();
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

  private void setUpComments() {
    mAdapter = new CommentItemAdapter(this, travelId);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(mAdapter);
    // 清除滑動到底部OR頂端時所出現的陰影
    mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
      .setDuration(200)
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          finish();
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

    if (mRecyclerView.getChildAt(0) != null)
        mRecyclerView.smoothScrollBy
            (0, mRecyclerView.getChildAt(0).getHeight() * mAdapter.getItemCount());

    if (!isValidComments())
      mButtonRectangle.startAnimation(
        AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.shake_error));
    else {
      handleSendMessage(mEtMessage);
    }
  }

  private boolean isValidComments() {
    if (TextUtils.isEmpty(mEtMessage.getText())) return false;
    if (mEtMessage.getText().length() > MESSAGE_CHARACTER_LIMIT) return false;
    return true;
  }

  private void handleSendMessage(MaterialEditText editText) {
    WormholeTraveller wt = DataSupport.find(WormholeTraveller.class, travelId);
    User u = UserInfo.getUser(this);
    DataBaseManager.updateComments(wt, u, editText.getText().toString().trim());
    editText.setText("");
    mAdapter.updateItems();
  }

}
