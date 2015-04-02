package tw.com.s11113153.wormholetraveller.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by xuyouren on 15/3/31.
 */
public class FeedContextMenuManager
            extends RecyclerView.OnScrollListener
            implements View.OnAttachStateChangeListener {

  private static final String TAG = FeedContextMenuManager.class.getSimpleName();

  private static FeedContextMenuManager instance;

  private FeedContextMenu contextMenuView;

  private boolean isContextMenuDismissing;
  private boolean isContextMenuShowing;


  private FeedContextMenuManager() {
  }

  public static FeedContextMenuManager getInstance() {
    if (instance == null)
            instance = new FeedContextMenuManager();
    return instance;
  }

  public void toggleContextMenuFromView(
          View openingView,
          int feedItem,
          FeedContextMenu.OnFeedContextMenuItemClickListener listener
  ) {
    if (contextMenuView == null)
        showContextMenuFromView(openingView, feedItem, listener);
    else
        hideContextMenu();
  }

  private void showContextMenuFromView(
          final View openingView,
          int feedItem,
          FeedContextMenu.OnFeedContextMenuItemClickListener listener
  ) {
    if (!isContextMenuShowing) {
      isContextMenuShowing = true;
      contextMenuView = new FeedContextMenu(openingView.getContext());
      contextMenuView.bindToItem(feedItem);
      contextMenuView.addOnAttachStateChangeListener(this);
      contextMenuView.setOnFeedMenuItemClickListener(listener);

      ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(contextMenuView);

      contextMenuView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          contextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);
          setupContextMenuInitialPosition(openingView);
          performShowAnimation();
          return false;
        }
      });
    }
  }

  /** set feed context menu position by use ibMore Location **/
  private void setupContextMenuInitialPosition(View openingView) {
    final int[] openingViewLocation = new int[2];
    openingView.getLocationOnScreen(openingViewLocation);
    contextMenuView.setTranslationX(openingViewLocation[0]);
    contextMenuView.setTranslationY(openingViewLocation[1] - contextMenuView.getHeight());
  }


  /**  物件中心開始放大  **/
  private void performShowAnimation() {
    contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
    contextMenuView.setPivotY(contextMenuView.getHeight() / 2);
    contextMenuView.setScaleX(0.1f);
    contextMenuView.setScaleY(0.1f);

    contextMenuView.animate()
      .scaleX(1f).scaleY(1f)
      .setDuration(300)
      .setInterpolator(new OvershootInterpolator())
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          isContextMenuShowing = false;
        }
      });
  }

  public void hideContextMenu() {
    if (!isContextMenuDismissing) {
      isContextMenuDismissing = true;
      performDismissAnimation();
    }
  }

  private void performDismissAnimation() {
    contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
    contextMenuView.setPivotY(contextMenuView.getHeight());
    contextMenuView.animate()
      .scaleX(0.1f).scaleY(0.1f)
      .setDuration(150)
      .setInterpolator(new AccelerateInterpolator())
      .setStartDelay(100)
      .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          if (contextMenuView != null) {
            contextMenuView.dismiss();
          }
          isContextMenuDismissing = false;
        }
      });
  }

  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    if (contextMenuView != null) {
      hideContextMenu();
      contextMenuView.setTranslationY(contextMenuView.getTranslationY() - dy);
    }
  }

  @Override
  public void onViewAttachedToWindow(View v) {
    Log.d(TAG, "" + String.valueOf("onViewAttachedToWindow"));
  }

  @Override
  public void onViewDetachedFromWindow(View v) {
    contextMenuView = null;
    Log.d(TAG, "" + String.valueOf("onViewDetachedFromWindow"));
  }
}
