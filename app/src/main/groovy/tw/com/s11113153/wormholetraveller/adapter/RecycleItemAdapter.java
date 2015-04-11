package tw.com.s11113153.wormholetraveller.adapter;

import com.squareup.picasso.Picasso;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.utils.RoundedTransformation;
import tw.com.s11113153.wormholetraveller.view.SendingProgressView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class RecycleItemAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
  implements View.OnClickListener {

  private static final String TAG = RecycleItemAdapter.class.getSimpleName();

  /** 動畫開始比較快, 然後逐漸減速 **/
  private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR
    = new DecelerateInterpolator();
  /** 動畫開始比較慢, 然後逐漸加速 **/
  private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR
    = new AccelerateInterpolator();
  /** 向前甩一定的值, 再回到原來位置 **/
  private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR
    = new OvershootInterpolator(4);

  /* TODO */
  private boolean animateItems = false;

  private final Map<Integer, Integer> likesCount = new HashMap();

  private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap();

  private final ArrayList<Integer> likedPositions = new ArrayList();

  private Context context;

  private int itemsCount = 0;

  private static final int NO_INIT_POSITION = -1;

  private boolean isInitAnimation = false;

  private OnBottomClickListener mBottomClickListener;

  private static final int VIEW_TYPE_DEFAULT = 1;
  private static final int VIEW_TYPE_LOADER = 2;

  @IntDef({VIEW_TYPE_DEFAULT, VIEW_TYPE_LOADER})
  @Retention(RetentionPolicy.SOURCE)
  private @interface ViewType {}

  private boolean showLoadingView = false;

  private int loadingViewSize;

  public RecycleItemAdapter(Context context) {
    this.context = context;
    loadingViewSize = (int)Utils.doPx(context, Utils.PxType.DP_TO_PX, 200);
  }

  @Override
  public int getItemViewType(int position) {
    if (showLoadingView && position == 0)
      return VIEW_TYPE_LOADER;
    else
      return VIEW_TYPE_DEFAULT;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(context).inflate(R.layout.item_recycle, parent, false);
    final RecycleItemViewHolder holder = new RecycleItemViewHolder(view);
    switch (viewType) {
      case VIEW_TYPE_DEFAULT:
        holder.ibComments.setOnClickListener(this);
        holder.ibMore.setOnClickListener(this);
        holder.ivFeedCenter.setOnClickListener(this);
        holder.ibLike.setOnClickListener(this);
        holder.ivUserProfile.setOnClickListener(this);
        break;
      case VIEW_TYPE_LOADER:
        View bgView = new View(context);
        bgView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bgView.setBackgroundColor(0x77ffffff);

        holder.vImageRoot.addView(bgView);
        holder.vProgressBg = bgView;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadingViewSize, loadingViewSize);
        params.gravity = Gravity.CENTER;

        SendingProgressView sendingProgressView = new SendingProgressView(context);
        sendingProgressView.setLayoutParams(params);
        holder.vImageRoot.addView(sendingProgressView);
        holder.vSendingProgress = sendingProgressView;
        break;
    }
    return holder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    /* TODO */
    if (!isInitAnimation && position == 0)
      runEnterAnimation(viewHolder.itemView, position);

    final RecycleItemViewHolder holder = (RecycleItemViewHolder)viewHolder;

    switch (getItemViewType(position)) {
      case VIEW_TYPE_DEFAULT:
        bindDefaultRecycleItem(holder, position);
        break;
      case VIEW_TYPE_LOADER:
        bindLoadingRecycleItem(holder);
        break;
    }
  }

  private void bindDefaultRecycleItem(RecycleItemViewHolder holder, int position) {

    updateLikesCounter(holder, false);
    updateHeartButton(holder, false);

    holder.ibComments.setTag(position);
    holder.ibMore.setTag(position);
    holder.ivFeedCenter.setTag(holder);
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
    if (url.equals("")) {
      holder.ivFeedCenter.setImageDrawable(null);
      return;
    }

    Picasso.with(context)
      .load(url)
      .fit()
      .into(holder.ivFeedCenter);

    Picasso.with(context)
      .load("http://www.youxituoluo.com/wp-content/uploads/2013/11/264_4a561152e5358.jpg")
      .centerCrop()
      .resize(96, 96)
      .transform(new RoundedTransformation())
      .into(holder.ivUserProfile);
  }

  private void bindLoadingRecycleItem(final RecycleItemViewHolder holder) {
//    holder.ivFeedCenter.setImageResource(R.drawable.ic_launcher);
//    holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
    holder.vSendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        holder.vSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
        holder.vSendingProgress.simulateProgress();
        return true;
      }
    });
    holder.vSendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
      @Override
      public void onLoadingFinished() {
        holder.vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(2000).setStartDelay(100);

        holder.vProgressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              showLoadingView = false;
              holder.vSendingProgress.setScaleX(1);
              holder.vSendingProgress.setScaleY(1);
              holder.vProgressBg.setAlpha(1);
              notifyItemChanged(0);
            }
          })
          .start();
      }
    });
  }


  @Override
  public int getItemCount() {
    return itemsCount;
  }

  private void runEnterAnimation(View view, int position) {
    if (!isInitAnimation && position > NO_INIT_POSITION) {
      view.setTranslationY(Utils.getScreenHeight(context));
      view.animate()
        .translationY(0)
        .setInterpolator(new DecelerateInterpolator(3.0f))
        .setDuration(Utils.AnimationAttribute.DURATION_LONG.getVal())
        .setStartDelay(Utils.AnimationAttribute.DELAY_START.getVal());
      isInitAnimation = true;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ibComments:
        if (mBottomClickListener != null)
          mBottomClickListener.onCommentsClick(v, (Integer) v.getTag());
        break;
      case R.id.ibMore:
        if (mBottomClickListener != null)
          mBottomClickListener.onMoreClick(v, (Integer) v.getTag());
        break;
      case R.id.ibLike: {
        RecycleItemViewHolder holder = (RecycleItemViewHolder) v.getTag();
        if (!likedPositions.contains(holder.getLayoutPosition())) {
          likedPositions.add(holder.getLayoutPosition());
          updateLikesCounter(holder, true);
          updateHeartButton(holder, true);
        }
      } break;

      case R.id.ivFeedCenter: {
//        RecycleItemAdapter.ViewHolder holder = (RecycleItemAdapter.ViewHolder) v.getTag();
//        if (!likedPositions.contains(holder.getLayoutPosition())) {
//          likedPositions.add(holder.getLayoutPosition());
//          animatePhotoLike(holder);
//          updateLikesCounter(holder, true);
//          updateHeartButton(holder, false);
//        }
      } break;

      case R.id.ivUserProfile: {
        if (mBottomClickListener != null)
          mBottomClickListener.onProfileClick(v);
      }
    }
  }

  public void setOnBottomClickListener(OnBottomClickListener bottomClickListener) {
    mBottomClickListener = bottomClickListener;
  }

  static class RecycleItemViewHolder extends RecyclerView.ViewHolder{
    @InjectView(R.id.ivFeedCenter) ImageView ivFeedCenter;
    @InjectView(R.id.ibComments) ImageButton ibComments;
    @InjectView(R.id.ibLike) ImageButton ibLike;
    @InjectView(R.id.ibMore) ImageButton ibMore;
    @InjectView(R.id.vBgLike) View vBgLike;
    @InjectView(R.id.ivPersonTravel) ImageView ivPersonTravel;
    @InjectView(R.id.tsLikesCounter) TextSwitcher tsLikesCounter;
    @InjectView(R.id.ivUserProfile) ImageView ivUserProfile;
    @InjectView(R.id.vImageRoot) FrameLayout vImageRoot;

    SendingProgressView vSendingProgress;
    View vProgressBg;

    public RecycleItemViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }


  /**
   *  @param animated true,   Update Likes value
   *                  false,  initial Likes value
   */
  private void updateLikesCounter(RecycleItemViewHolder holder, boolean animated) {
    /* TODO +1 ? */
    int currentLikesCount = likesCount.get(holder.getLayoutPosition());
    /**
     *  param2 == 1, use one(quantity) and set value
     *          > 1, then use other(quantity) and set value
     **/
    String likesCountText = context.getResources()
      .getQuantityString(R.plurals.likes_count, currentLikesCount, currentLikesCount);

    if (animated) {
      holder.tsLikesCounter.setText(likesCountText);
    } else {
      holder.tsLikesCounter.setCurrentText(likesCountText);
    }

    // update values
    likesCount.put(holder.getLayoutPosition(), currentLikesCount);
  }

  private void updateHeartButton(final RecycleItemViewHolder holder, boolean animated) {
    if (animated) {
      if (!likeAnimations.containsKey(holder)) {
        AnimatorSet animatorSet = new AnimatorSet();
        likeAnimations.put(holder, animatorSet);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.ibLike, "rotation", 360f, 0f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.ibLike, "scaleX", 0.1f, 1f);
        bounceAnimX.setDuration(600);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.ibLike, "scaleY", 0.1f, 1f);
        bounceAnimY.setDuration(600);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            holder.ibLike.setImageResource(R.mipmap.ic_like_red);
          }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            resetLikeAnimationState(holder);
          }
        });
        animatorSet.start();
      }
    }
    else {
      if (likedPositions.contains(holder.getLayoutPosition())) {
        holder.ibLike.setImageResource(R.mipmap.ic_like_red);
      } else {
        holder.ibLike.setImageResource(R.mipmap.ic_like_white);
      }
    }
  }

  private void resetLikeAnimationState(RecycleItemViewHolder holder) {
    likeAnimations.remove(holder);
    holder.vBgLike.setVisibility(View.GONE);
    holder.ivPersonTravel.setVisibility(View.GONE);
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

  private void animatePhotoLike(final RecycleItemViewHolder holder) {
    if (!likeAnimations.containsKey(holder)) {
      holder.vBgLike.setVisibility(View.VISIBLE);
      holder.ivPersonTravel.setVisibility(View.VISIBLE);

      holder.vBgLike.setScaleY(0.1f);
      holder.vBgLike.setScaleX(0.1f);
      holder.vBgLike.setAlpha(1f);
      holder.ivPersonTravel.setScaleY(0.1f);
      holder.ivPersonTravel.setScaleX(0.1f);

      AnimatorSet animatorSet = new AnimatorSet();
      likeAnimations.put(holder, animatorSet);

      ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
      bgScaleYAnim.setDuration(200);
      bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
      ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
      bgScaleXAnim.setDuration(200);
      bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
      ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
      bgAlphaAnim.setDuration(200);
      bgAlphaAnim.setStartDelay(150);
      bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

      ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivPersonTravel, "scaleY", 0.1f, 1f);
      imgScaleUpYAnim.setDuration(300);
      imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
      ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivPersonTravel, "scaleX", 0.1f, 1f);
      imgScaleUpXAnim.setDuration(300);
      imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

      ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivPersonTravel, "scaleY", 1f, 0f);
      imgScaleDownYAnim.setDuration(300);
      imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
      ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivPersonTravel, "scaleX", 1f, 0f);
      imgScaleDownXAnim.setDuration(300);
      imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

      animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
      animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

      animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          resetLikeAnimationState(holder);
        }
      });
      animatorSet.start();
    }
  }


  public void showLoadingView() {
    showLoadingView = true;
    notifyItemChanged(0);
  }


  public interface OnBottomClickListener {
    void onCommentsClick(View v, int position);
    void onMoreClick(View v, int position);
    void onProfileClick(View v);
  }
}