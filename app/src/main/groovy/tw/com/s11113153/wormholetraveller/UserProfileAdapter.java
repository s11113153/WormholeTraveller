package tw.com.s11113153.wormholetraveller;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyouren on 15/4/3.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG = UserProfileAdapter.class.getSimpleName();

  public static final int TYPE_PROFILE_HEADER = 0;
  public static final int TYPE_PROFILE_OPTIONS = 1;
  public static final int TYPE_PHOTO = 2;

  private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
  private static final int MAX_PHOTO_ANIMATION_DELAY = 600;

  private static final int MIN_ITEMS_COUNT = 2;
  private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

  private final Context context;
  private final int cellSize;
  private final int avatarSize;

  private final String profilePhoto;
  private final List<String> photos;

  private boolean lockedAnimations = false;
  private long profileHeaderAnimationStartTime = 0;
  private int lastAnimatedItem = 0;



  public UserProfileAdapter(Context context) {
    this.context = context;
    this.cellSize = Utils.getScreenWidth(context) / 3;
    this.avatarSize = 300;
    this.profilePhoto = context.getString(R.string.user_profile_photo);
    this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
  }

  @Override
  public int getItemViewType(int position) {
    switch (position) {
      case 0: return TYPE_PROFILE_HEADER;
      case 1: return TYPE_PROFILE_OPTIONS;
      default: return TYPE_PHOTO;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view;
    final StaggeredGridLayoutManager.LayoutParams params;
    final LayoutInflater layoutInflater = LayoutInflater.from(context);
    switch (viewType) {
      case TYPE_PROFILE_HEADER:
        view = layoutInflater.inflate(R.layout.view_user_profile_header, parent, false);
        params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.setFullSpan(true);
        view.setLayoutParams(params);
        return new ProfileHeaderViewHolder(view);
      case TYPE_PROFILE_OPTIONS:
        view =layoutInflater.inflate(R.layout.view_user_profile_options, parent, false);
        params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.setFullSpan(true);
        view.setLayoutParams(params);
        return new ProfileOptionsViewHolder(view);
      case TYPE_PHOTO:
        view = layoutInflater.inflate(R.layout.item_photo, parent, false);
        params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = cellSize;
        params.width = cellSize;
        params.setFullSpan(false);
        view.setLayoutParams(params);
        return new PhotoViewHolder(view);
      default:
        Log.e(TAG, "" + String.valueOf("onCreateViewHolder viewType is error"));
        return null;
    }
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
    final int viewType = getItemViewType(position);
    switch (viewType) {
      case TYPE_PROFILE_HEADER:
        bindProfileHeader((ProfileHeaderViewHolder)holder);
        break;
      case TYPE_PROFILE_OPTIONS:
        bindProfileOptions((ProfileOptionsViewHolder)holder);
        break;
      case TYPE_PHOTO:
        bindPhoto((PhotoViewHolder)holder, position);
        break;
    }
  }

  /** bind User Photo Icon **/
  private void bindProfileHeader(final ProfileHeaderViewHolder holder) {
    Picasso.with(context)
      .load("http://www.youxituoluo.com/wp-content/uploads/2013/11/264_4a561152e5358.jpg")
      .placeholder(R.drawable.img_circle_placeholder)
      .resize(avatarSize, avatarSize)
      .centerCrop()
      .transform(new CircleTransformation())
      .into(holder.ivUserProfilePhoto);

    holder.vUserProfileRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        holder.vUserProfileRoot.getViewTreeObserver().removeOnPreDrawListener(this);
        animateUserProfileHeader(holder);
        return true;
      }
    });
  }

  private void animateUserProfileHeader(ProfileHeaderViewHolder holder) {
    if (lockedAnimations) return;
    profileHeaderAnimationStartTime = System.currentTimeMillis();
    holder.vUserProfileRoot.setTranslationY(-holder.vUserProfileRoot.getHeight());
    holder.ivUserProfilePhoto.setTranslationY(-holder.ivUserProfilePhoto.getHeight());
    holder.vUserDetails.setTranslationY(-holder.vUserDetails.getHeight());
    holder.vUserStats.setAlpha(0);

    holder.vUserProfileRoot.animate().translationY(0).setDuration(500).setInterpolator(INTERPOLATOR);
    holder.ivUserProfilePhoto.animate().translationY(0).setDuration(800).setStartDelay(0).setInterpolator(INTERPOLATOR);
    holder.vUserDetails.animate().translationY(0).setDuration(800).setStartDelay(0).setInterpolator(INTERPOLATOR);
    holder.vUserStats.animate().alpha(1).setDuration(200).setStartDelay(800).setInterpolator(INTERPOLATOR).start();
  }

  private void bindProfileOptions(final ProfileOptionsViewHolder holder) {
    holder.vButtons.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        holder.vButtons.getViewTreeObserver().removeOnPreDrawListener(this);
        holder.vUnderline.getLayoutParams().width = holder.btnGrid.getWidth();
        holder.vUnderline.requestLayout();
        animateUserProfileOptions(holder);
        return true;
      }
    });
  }

  private void animateUserProfileOptions(final ProfileOptionsViewHolder holder) {
    if (lockedAnimations) return;
    holder.vButtons.setTranslationY(-holder.vButtons.getHeight());
    holder.vUnderline.setScaleX(0);

    holder.vButtons.animate()
      .translationY(0)
      .setDuration(500)
      .setStartDelay(USER_OPTIONS_ANIMATION_DELAY)
      .setInterpolator(INTERPOLATOR);

    holder.vUnderline.animate()
      .scaleX(1)
      .setDuration(500)
      .setStartDelay(USER_OPTIONS_ANIMATION_DELAY + 300)
      .setInterpolator(INTERPOLATOR).start();
  }

  private void bindPhoto(final PhotoViewHolder holder, int position) {
    Picasso.with(context)
      .load(photos.get(position - MIN_ITEMS_COUNT))
      .resize(cellSize, cellSize)
      .centerCrop()
      .into(holder.ivPhoto, new Callback() {
        @Override
        public void onSuccess() {
          animatePhoto(holder);
        }
        @Override
        public void onError() {
        }
      });

    if (lastAnimatedItem < position)
        lastAnimatedItem = position;
  }

  private void animatePhoto(final PhotoViewHolder holder) {
    if (lockedAnimations) return;
    if (lastAnimatedItem == holder.getLayoutPosition()) {
      setLockedAnimations(true);
    }

    long animationDelay = profileHeaderAnimationStartTime
            + MAX_PHOTO_ANIMATION_DELAY
            - System.currentTimeMillis();

    if (profileHeaderAnimationStartTime == 0) {
      animationDelay = holder.getLayoutPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
    } else if (animationDelay < 0) {
      animationDelay = holder.getLayoutPosition() * 30;
    } else {
      animationDelay += holder.getLayoutPosition() * 30;
    }

    holder.flRoot.setScaleY(0);
    holder.flRoot.setScaleX(0);
    holder.flRoot.animate()
      .scaleY(1)
      .scaleX(1)
      .setDuration(200)
      .setInterpolator(INTERPOLATOR)
      .setStartDelay(animationDelay)
      .start();
  }

  public void setLockedAnimations(boolean lockedAnimations) {
    this.lockedAnimations = lockedAnimations;
  }

  @Override
  public int getItemCount() {
    return MIN_ITEMS_COUNT + photos.size();
  }

  static class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @InjectView(R.id.vUserDetails)
    View vUserDetails;
    @InjectView(R.id.btnFollow)
    Button btnFollow;
    @InjectView(R.id.vUserStats)
    View vUserStats;
    @InjectView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    public ProfileHeaderViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }

  static class ProfileOptionsViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.btnGrid)
    ImageButton btnGrid;
    @InjectView(R.id.btnList)
    ImageButton btnList;
    @InjectView(R.id.btnMap)
    ImageButton btnMap;
    @InjectView(R.id.btnTagged)
    ImageButton btnComments;
    @InjectView(R.id.vUnderline)
    View vUnderline;
    @InjectView(R.id.vButtons)
    View vButtons;

    public ProfileOptionsViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }

  static class PhotoViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.flRoot)
    FrameLayout flRoot;
    @InjectView(R.id.ivPhoto)
    ImageView ivPhoto;

    public PhotoViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }
}
