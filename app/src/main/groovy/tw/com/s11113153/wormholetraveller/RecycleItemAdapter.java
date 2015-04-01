package tw.com.s11113153.wormholetraveller;

import com.squareup.picasso.Picasso;

import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class RecycleItemAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

  /** 動畫開始比較快, 然後逐漸減速 **/
  private static final DecelerateInterpolator DECELERATE_INTERPOLATOR
          = new DecelerateInterpolator();
  /** 動畫開始比較慢, 然後逐漸加速 **/
  private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR
          = new AccelerateDecelerateInterpolator();
  /** 向前甩一定的值, 再回到原來位置 **/
  private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR
          = new OvershootInterpolator(4);

  private static final int ANIMATED_ITEMS_COUNT = 2;

  private boolean animateItems = false;

  private final Map<Integer, Integer> likesCount = new HashMap();

  private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap();

  private final ArrayList<Integer> likedPositions = new ArrayList();

  private Context context;

  private int itemsCount = 0;

  private static final int NO_INIT_POSITION = -1;

  private boolean isAnimation = false;

  private OnBottomClickListener mBottomClickListener;

  public RecycleItemAdapter(Context context) {
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(context).inflate(R.layout.item_recycle, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    if (!isAnimation && position == 0)
        runEnterAnimation(viewHolder.itemView, position);

    ViewHolder holder = (ViewHolder) viewHolder;
    updateLikesCounter(holder, false);
    updateHeartButton(holder, false);

    /** register click listener **/
    holder.ibComments.setOnClickListener(this);
    holder.ibComments.setTag(position);
    holder.ibMore.setOnClickListener(this);
    holder.ibMore.setTag(position);
    holder.ivFeedCenter.setOnClickListener(this);
    holder.ivFeedCenter.setTag(holder);
    holder.ibLike.setOnClickListener(this);
    holder.ibLike.setTag(holder);

    if (likeAnimations.containsKey(holder))
        likeAnimations.get(holder).cancel();

    resetLikeAnimationState(holder);

    String url = "";
    switch (position) {
      case 0:
        url = "http://i.imgur.com/zzyrC10.jpg";
        break;
      case 1:
        url = "http://i.imgur.com/iemg6nQ.jpg";
        break;
      case 2:
        url = "http://i.imgur.com/VSOssOW.jpg";
        break;
      case 3:
        url = "http://i.imgur.com/vWVsQIi.jpg";
        break;
      case 4:
        break;
    }
    if (url == null || url.equals("")) {
      holder.ivFeedCenter.setImageDrawable(null);
      return;
    }

    Picasso.with(context)
      .load(url)
      .fit()
      .into(holder.ivFeedCenter);
  }

  @Override
  public int getItemCount() {
    return itemsCount;
  }

  private void runEnterAnimation(View view, int position) {
    if (!isAnimation && position > NO_INIT_POSITION) {
      view.setTranslationY(Utils.getScreenHeight(context));
      view.animate()
        .translationY(0)
        .setInterpolator(new DecelerateInterpolator(3.0f))
        .setDuration(Utils.AnimationAttribute.DURATION_LONG.getVal())
        .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal());
      isAnimation = true;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ibComments:
        if (mBottomClickListener != null)
          mBottomClickListener.onCommentsClick(v, (Integer) v.getTag());
        break;
      case R.id.ibLike:
        break;
      case R.id.ibMore:
        if (mBottomClickListener != null)
          mBottomClickListener.onMoreClick(v, (Integer) v.getTag());
        break;
    }
  }

  public void setOnBottomClickListener(OnBottomClickListener bottomClickListener) {
    mBottomClickListener = bottomClickListener;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.ivFeedCenter) ImageView ivFeedCenter;
    @InjectView(R.id.ibComments) ImageButton ibComments;
    @InjectView(R.id.ibLike) ImageButton ibLike;
    @InjectView(R.id.ibMore) ImageButton ibMore;
    @InjectView(R.id.vBgLike) View vBgLike;
    @InjectView(R.id.ivLove) ImageView ivLike;
    @InjectView(R.id.tsLikesCounter) TextSwitcher tsLikesCounter;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }


  /**
   *  @param animated true,   Update Likes value
   *                  false,  initial Likes value
   */
  private void updateLikesCounter(ViewHolder holder, boolean animated) {
    /* TODO +1 ? */
    int currentLikesCount = likesCount.get(holder.getLayoutPosition() + 1);

    /** param2 > 1 then use other(quantity) and set value **/
    String likesCountText = context.getResources()
      .getQuantityString(R.plurals.likes_count, currentLikesCount, currentLikesCount);

    if (animated) {
      holder.tsLikesCounter.setText(likesCountText);
    } else {
      holder.tsLikesCounter.setCurrentText(likesCountText);
    }
    likesCount.put(holder.getLayoutPosition(), currentLikesCount);
  }

  private void updateHeartButton(final RecycleItemAdapter.ViewHolder holder, boolean animated) {
    if (animated) {

    }
  }

  private void resetLikeAnimationState(RecycleItemAdapter.ViewHolder holder) {
    likeAnimations.remove(holder);
    holder.vBgLike.setVisibility(View.GONE);
    holder.ivLike.setVisibility(View.GONE);
  }

  /** people of one article, set Like Value **/
  private void fillLikesWithRandomValues() {
    for (int i = 0; i < getItemCount(); i++)
        likesCount.put(i, new Random().nextInt(100));
  }


  /**
   * initial Items, play animation
   * update Items. don't play animation
   * */
  public void updateItems(boolean animated) {
    itemsCount = 10;
    animateItems = animated;
    fillLikesWithRandomValues();
    notifyDataSetChanged();
  }

  public interface OnBottomClickListener {
    void onCommentsClick(View v, int position);
    void onMoreClick(View v, int position);
  }
}