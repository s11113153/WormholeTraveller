package tw.com.s11113153.wormholetraveller.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;

/**
 * Created by xuyouren on 15/3/31.
 */
public class FeedContextMenu extends LinearLayout {
  private static final String TAG = FeedContextMenu.class.getSimpleName();

  private final int CONTEXT_MENU_WIDTH;

  private int feedItem = -1;

  private OnFeedContextMenuItemClickListener onItemClickListener;

  public FeedContextMenu(Context context) {
    super(context);
    CONTEXT_MENU_WIDTH = (int) Utils.doPx(context, Utils.PxType.DP_TO_PX, 180);
    init();
  }

  public void setOnFeedMenuItemClickListener(
          OnFeedContextMenuItemClickListener onItemClickListener
  ) {
    this.onItemClickListener = onItemClickListener;
  }

  /** FeedContextMenu add elements from view_context_menu.xml  **/
  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
    setBackgroundResource(R.mipmap.bg_container_shadow);
    setOrientation(VERTICAL);
    setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  public void bindToItem(int feedItem) {
    this.feedItem = feedItem;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    ButterKnife.inject(this);
  }

  public void dismiss() {
    Log.v(TAG, "" + String.valueOf(this));
    Log.v(TAG, "" + String.valueOf(getParent()));
    ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
  }


  @OnClick(R.id.btnReport)
  public void onReportClick() {
    if (onItemClickListener != null)
        onItemClickListener.onReportClick(feedItem);
  }

  public interface OnFeedContextMenuItemClickListener {
    public void onReportClick(int feedItem);
    public void onSharePhotoClick(int feedItem);
    public void onCopyShareUrlClick(int feedItem);
    public void onCancelClick(int feedItem);
  }
}
