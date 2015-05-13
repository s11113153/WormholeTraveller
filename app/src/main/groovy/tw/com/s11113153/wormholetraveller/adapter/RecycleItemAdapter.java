package tw.com.s11113153.wormholetraveller.adapter;

import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.DataBaseManager;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.utils.CircleTransformation;
import tw.com.s11113153.wormholetraveller.view.SendingProgressView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class RecycleItemAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
  implements View.OnClickListener , View.OnTouchListener, View.OnLongClickListener {

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

  private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap();

  private Context context;

//  private int itemsCount = 0;

  private static final int NO_INIT_POSITION = -1;

  private boolean isInitAnimation = false;

  private OnBottomClickListener mBottomClickListener;

  private static final int VIEW_TYPE_DEFAULT = 1;
  private static final int VIEW_TYPE_LOADER = 2;

  private boolean showLoadingView = false;

  private int loadingViewSize;

  private List<WormholeTraveller> wormholeTravellers = new ArrayList();

  private float curLat;
  private float curLng;

  private static Typeface TYPEFACE_ROBOTO_BOLD_ITALIC;
  private static Typeface TYPEFACE_ROBOTO_LIGHT;

  private static final int SEARCH_DISTANCE = 500;

  public RecycleItemAdapter(Context context, float lat, float lng) {
    this.context = context;
    loadingViewSize = (int)Utils.doPx(context, Utils.PxType.DP_TO_PX, 200);
    curLat = lat;
    curLng = lng;
    bindTravel();
    TYPEFACE_ROBOTO_BOLD_ITALIC = Utils.getFont(context, Utils.FontType.ROBOTO_BOLD_ITALIC);
    TYPEFACE_ROBOTO_LIGHT = Utils.getFont(context, Utils.FontType.ROBOTO_LIGHT);
  }

  private void bindTravel() {
    wormholeTravellers.clear();
    List<WormholeTraveller> travellers = //= DataSupport.findAll(WormholeTraveller.class, true);
    DataSupport.order("date desc").find(WormholeTraveller.class, true);
    for (WormholeTraveller wt : travellers) {
      float lat = wt.getLat();
      float lng = wt.getLng();
      double distance = Utils.calculateDistance(curLat, curLng, lat, lng);
      if (distance <= SEARCH_DISTANCE && wt.isShow()) {
        wormholeTravellers.add(wt);
      }
    }
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
        holder.ivFeedCenter.setOnLongClickListener(this);
        holder.ibLike.setOnClickListener(this);
        holder.ivUserProfile.setOnClickListener(this);
        holder.scroll.setOnTouchListener(this);
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
        bindDefaultRecycleItem(holder, position);
        bindLoadingRecycleItem(holder);
        break;
    }
  }

  private void bindDefaultRecycleItem(final RecycleItemViewHolder holder, final int position) {

    updateLikesCounter(holder, false);
    updateHeartButton(holder, false);

    holder.ibComments.setTag(position);
    holder.ibMore.setTag(position);
    holder.ivFeedCenter.setTag(holder);
    holder.ibLike.setTag(holder);
    holder.ivUserProfile.setTag(holder);
    holder.scroll.setTag(holder.tvContent);

    if (likeAnimations.containsKey(holder))
      likeAnimations.get(holder).cancel();

    resetLikeAnimationState(holder);

    WormholeTraveller wt = wormholeTravellers.get(position);
    User user = wt.getUser();
    if (wt.getTravelPhotoPath() == null) {
      holder.ivFeedCenter.setImageDrawable(null);
      return;
    }

    bindWormholeTraveller(holder, wt);
    bindUserProfile(holder, user);
    bindContent(holder, wt);
    bindTitle(holder, wt);
    bindDate(holder, wt);
    bindAddress(holder, wt);
  }

  private void bindTitle(final RecycleItemViewHolder holder, final WormholeTraveller wt) {
//    Log.e(TAG, holder.tvTitle.getId() + ", bindTitle: " + holder.getLayoutPosition() + "," + wt.getTitle() + ", " + wt.getContent());
    holder.tvTitle.setText(wt.getTitle());
  }

  private void bindContent(final RecycleItemViewHolder holder, final WormholeTraveller wt) {
//    Log.e(TAG, holder.tvContent.getId() + ", bindContent: " + holder.getLayoutPosition() + "," + wt.getTitle() + ", " + wt.getContent());
    holder.tvContent.setText(wt.getContent());
  }

  private void bindWormholeTraveller(final RecycleItemViewHolder holder, final WormholeTraveller wt) {
    if (wt.getTravelPhotoPath() != null) {
      Picasso.with(context)
        .load(wt.getTravelPhotoPath())
        .fit()
        .into(holder.ivFeedCenter);
    }
  }

  private void bindUserProfile(final RecycleItemViewHolder holder, final User user) {
    Picasso.with(context)
      .load(user.getIconPath())
      .resize(96, 96)
      .placeholder(R.drawable.img_circle_placeholder)
      .centerCrop()
      .transform(new CircleTransformation())
      .into(holder.ivUserProfile);
  }

  private void bindDate(final RecycleItemViewHolder holder, final WormholeTraveller wt) {
    holder.tvDate.setText(wt.getDate());
  }

  private void bindAddress(final RecycleItemViewHolder holder, final WormholeTraveller wt) {
    String address;
    address = wt.getAddress();
    if (address == null) {
      address = Utils.getLocationInfo2(wt.getLat(), wt.getLng());
      wt.setAddress(address);
      DataBaseManager.updateWormholeTraveller(wt);
    }
    holder.tvAddress.setText(address);
  }

//  /** 若已經點讚，變成紅愛心，並移除該愛心事件 **/
//  private void bindIsLike(RecycleItemViewHolder holder, boolean b) {
//    if (!b) {
//      holder.ibLike.setImageResource(R.mipmap.ic_like_red);
//    }
//    else
//      holder.ibLike.setImageResource(R.mipmap.ic_like_white);
//  }

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
    holder.vSendingProgress.setOnLoadingFinishedListener(
      new SendingProgressView.OnLoadingFinishedListener() {
        @Override
        public void onLoadingFinished() {
          holder.vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(2000)
            .setStartDelay(100);

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
  public long getItemId(int position) {
    Log.e("t", ": " + wormholeTravellers.get(position).getTitle());
    return wormholeTravellers.get(position).getId();
  }

  @Override
  public int getItemCount() {
    return wormholeTravellers.size();
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

  /** RecycleView 包含 scrollView
   *  當 tvContent 高度 < scroll 跳過
   *  當使用者 touch scroll, 要求不允許攔截計算這個scroll高度
   **/
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    TextView tvContent = (TextView)v.getTag();
    if (tvContent.getHeight() < v.getHeight()) return true;

    float x =  Utils.doPx(context, Utils.PxType.DP_TO_PX, 50);
    float right = 2 * x;
    if (event.getX() < right) {
      v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
      return false;
    }
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ibComments:
        if (mBottomClickListener != null) {
          int position = (Integer) v.getTag();
          int travelId = wormholeTravellers.get(position).getId();
          mBottomClickListener.onCommentsClick(v, position, travelId);
        }
        break;
      case R.id.ibMore:
        if (mBottomClickListener != null)
          mBottomClickListener.onMoreClick(v, (Integer) v.getTag());
        break;
      case R.id.ibLike: {
        RecycleItemViewHolder holder = (RecycleItemViewHolder) v.getTag();
//        if (!likedPositions.contains(holder.getLayoutPosition())) {
//          likedPositions.add(holder.getLayoutPosition());
        updateHeartButton(holder, true);
//        }
      } break;

//      case R.id.ivFeedCenter: {
//        RecycleItemViewHolder holder = (RecycleItemViewHolder) v.getTag();
//        animateDeleteTravel(holder);
//
////        RecycleItemAdapter.ViewHolder holder = (RecycleItemAdapter.ViewHolder) v.getTag();
////        if (!likedPositions.contains(holder.getLayoutPosition())) {
////          likedPositions.add(holder.getLayoutPosition());
////          adnimatePhotoLike(holder);
////          updateLikesCounter(holder, true);
////          updateHeartButton(holder, false);
////        }
//      } break;

      case R.id.ivUserProfile: {
        if (mBottomClickListener != null) {
          RecycleItemViewHolder holder = (RecycleItemViewHolder) v.getTag();
          User u = wormholeTravellers.get(holder.getLayoutPosition()).getUser();
          mBottomClickListener.onProfileClick(v, u);
        }
        break;
      }
    }
  }

  @Override
  public boolean onLongClick(View v) {
    if (v.getId() == R.id.ivFeedCenter) {
      RecycleItemViewHolder holder = (RecycleItemViewHolder) v.getTag();
      animateDeleteTravel(holder);
      return true;
    }
    return false;
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
    @InjectView(R.id.ivTrashCan) ImageView ivTrashCan;
    @InjectView(R.id.tsLikesCounter) TextSwitcher tsLikesCounter;
    @InjectView(R.id.ivUserProfile) ImageView ivUserProfile;
    @InjectView(R.id.vImageRoot) FrameLayout vImageRoot;
    @InjectView(R.id.tvTitle) TextView tvTitle;
    @InjectView(R.id.tvDate) TextView tvDate;
    @InjectView(R.id.tvAddress) TextView tvAddress;
    @InjectView(R.id.scroll) ScrollView scroll;
    @InjectView(R.id.tvContent) TextView tvContent;

    SendingProgressView vSendingProgress;
    View vProgressBg;

    public RecycleItemViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
      tvTitle.setTypeface(TYPEFACE_ROBOTO_BOLD_ITALIC);
      tvDate.setTypeface(TYPEFACE_ROBOTO_BOLD_ITALIC);
      tvAddress.setTypeface(TYPEFACE_ROBOTO_BOLD_ITALIC);
      tvContent.setTypeface(TYPEFACE_ROBOTO_LIGHT);
    }
  }


  /**
   *  @param animated true,   Update Likes value
   *                  false,  initial Likes value
   */
  private void updateLikesCounter(RecycleItemViewHolder holder, boolean animated) {
    WormholeTraveller wt = wormholeTravellers.get(holder.getLayoutPosition());
    int likesCount = wt.getGoods();
    if (animated) {
      if (wt.isLike()) likesCount += 1;
      else likesCount -= 1;
    }

    /**
     *  param2 == 1, use one(quantity) and set value
     *          > 1, then use other(quantity) and set value
     *  ex : currentLikesCount == 1, use one   quantity
     *       currentLikesCount  > 1, use other quantity
     *  param3 : set value
     **/
    String likesCountText = context.getResources()
      .getQuantityString(R.plurals.likes_count, likesCount, likesCount);

    if (animated) {
      holder.tsLikesCounter.setText(likesCountText);
      WormholeTraveller w = wormholeTravellers.get(holder.getLayoutPosition());
      wt.setGoods(likesCount);
      DataBaseManager.updateWormholeTraveller(w);
    } else {
      holder.tsLikesCounter.setCurrentText(likesCountText);
    }
  }

  private void updateIsLikeState(boolean isLike, long id) {
    ContentValues values = new ContentValues();
    values.put("islike", isLike);
    DataSupport.update(WormholeTraveller.class, values, id);
  }

  private void updateWormholeTravellers(int wtId, int position) {
    WormholeTraveller updateWt = DataSupport.find(WormholeTraveller.class, wtId, true);
    wormholeTravellers.remove(position);
    wormholeTravellers.add(position, updateWt);
  }

  /**
   * (1) 更新資料庫 isLike
   * (2) 更新目前的 wt ArrayList（因為 isLike 已經改變了）
   **/
  private void updateHeartButton(final RecycleItemViewHolder holder, boolean animated) {
    if (animated) {
      if (!likeAnimations.containsKey(holder)) {

        final WormholeTraveller wt = wormholeTravellers.get(holder.getLayoutPosition());
        final boolean isLike = wt.isLike();

        updateIsLikeState(!isLike, wt.getId());
        updateWormholeTravellers(wt.getId(), holder.getLayoutPosition());

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
            updateLikesCounter(holder, true);
            if (!isLike) {
              holder.ibLike.setImageResource(R.mipmap.ic_like_red);
            }
            else {
              holder.ibLike.setImageResource(R.mipmap.ic_like_white);
            }
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
      boolean isLike = wormholeTravellers.get(holder.getLayoutPosition()).isLike();
      if (isLike)
        holder.ibLike.setImageResource(R.mipmap.ic_like_red);
      else
        holder.ibLike.setImageResource(R.mipmap.ic_like_white);

//      if (likedPositions.contains(holder.getLayoutPosition())) {
//        holder.ibLike.setImageResource(R.mipmap.ic_like_red);
//      } else {
//        holder.ibLike.setImageResource(R.mipmap.ic_like_white);
//      }
    }
  }

  private void resetLikeAnimationState(RecycleItemViewHolder holder) {
    likeAnimations.remove(holder);
    holder.vBgLike.setVisibility(View.GONE);
    holder.ivTrashCan.setVisibility(View.GONE);
  }

//  /** people of one article, set Like Value **/
//  private void fillLikesWithRandomValues() {
//    for (int i = 0; i < getItemCount(); i++)
//      likesCount.put(i, new Random().nextInt(100));
//  }

  /**
   * initial Items, play animation
   * update Items. don't play animation
   * */
  public void updateItems(boolean animated) {
    bindTravel();
    animateItems = animated;
    notifyDataSetChanged();
  }

  private void animateDeleteTravel(final RecycleItemViewHolder holder) {
    if (!likeAnimations.containsKey(holder)) {
      holder.vBgLike.setVisibility(View.VISIBLE);
      holder.ivTrashCan.setVisibility(View.VISIBLE);

      holder.vBgLike.setScaleY(0.1f);
      holder.vBgLike.setScaleX(0.1f);
      holder.vBgLike.setAlpha(1f);
      holder.ivTrashCan.setScaleY(0.1f);
      holder.ivTrashCan.setScaleX(0.1f);

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

      ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivTrashCan, "scaleY", 0.1f, 1f);
      imgScaleUpYAnim.setDuration(300);
      imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
      ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivTrashCan, "scaleX", 0.1f, 1f);
      imgScaleUpXAnim.setDuration(300);
      imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

      ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivTrashCan, "scaleY", 1f, 0f);
      imgScaleDownYAnim.setDuration(300);
      imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
      ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivTrashCan, "scaleX", 1f, 0f);
      imgScaleDownXAnim.setDuration(300);
      imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

      animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
      animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

      animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          resetLikeAnimationState(holder);
          int wtId = wormholeTravellers.get(holder.getLayoutPosition()).getId();
          deleteTravel(wtId);
        }
      });
      animatorSet.start();
    }
  }

  private void deleteTravel(int wtId) {
    if (onDeleteTravelListener != null)
        onDeleteTravelListener.onDelete(wtId);
  }

  public void showLoadingView() {
    showLoadingView = true;
    notifyItemChanged(0);
  }

  public interface OnBottomClickListener {
    void onCommentsClick(View v, int position, int travelId);
    void onMoreClick(View v, int position);
    void onProfileClick(View v, User user);
  }

  private OnDeleteTravelListener onDeleteTravelListener;

  public void setOnDeleteTravelListener(
    OnDeleteTravelListener onDeleteTravelListener) {
    this.onDeleteTravelListener = onDeleteTravelListener;
  }

  public interface OnDeleteTravelListener {
    void onDelete(int wtId);
  }
}