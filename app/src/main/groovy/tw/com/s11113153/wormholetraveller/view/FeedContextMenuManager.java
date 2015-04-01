package tw.com.s11113153.wormholetraveller.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xuyouren on 15/3/31.
 */
public class FeedContextMenuManager
            extends RecyclerView.OnScrollListener
            implements View.OnAttachStateChangeListener {

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

  @Override
  public void onViewAttachedToWindow(View v) {

  }

  @Override
  public void onViewDetachedFromWindow(View v) {

  }
}
