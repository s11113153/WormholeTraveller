package tw.com.s11113153.wormholetraveller.adapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.utils.CircleTransformation;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;

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

  private static Context context;
  private final int cellSize;
  private final int avatarSize;

//  private final String profilePhoto;
  private final List<String> photos = new ArrayList();

  private boolean lockedAnimations = false;
  private long profileHeaderAnimationStartTime = 0;
  private int lastAnimatedItem = 0;

  private static User user;

  private List<WormholeTraveller> wormholeTravellers;

  public UserProfileAdapter(Context context, User user) {
    this.context = context;
    this.cellSize = Utils.getScreenWidth(context) / 3;
    this.avatarSize = 300;
//    this.profilePhoto = profilePhoto.getString(R.string.user_profile_photo);
//    this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    this.user = user;
    wormholeTravellers = DataSupport.find(User.class, user.getId(), true).getWormholeTravellers();
    for (WormholeTraveller w : wormholeTravellers)
      photos.add(w.getTravelPhotoPath());
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
        bindProfileHeader((ProfileHeaderViewHolder) holder);
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
      .load(user.getIconPath())
      .resize(avatarSize, avatarSize)
      .placeholder(R.drawable.img_circle_placeholder)
      .centerCrop()
      .transform(new CircleTransformation())
      .into(holder.ivUserProfilePhoto);

    holder.tvUserName.setText(user.getName());
    holder.tvUserMail.setText(user.getMail());
    holder.tvOutline.setText(user.getOutline());
    setPosts(holder);
    setTotalLikes(holder);

    holder.vUserProfileRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        holder.vUserProfileRoot.getViewTreeObserver().removeOnPreDrawListener(this);
        animateUserProfileHeader(holder);
        return true;
      }
    });
  }

  private void setPosts(final ProfileHeaderViewHolder holder) {
    holder.tvPosts.setText(String.valueOf(wormholeTravellers.size()));
  }

  private void setTotalLikes(final ProfileHeaderViewHolder holder) {
    int likes = 0;
    for (WormholeTraveller w : wormholeTravellers) {
      likes += w.getGoods();
    }
    holder.tvTotalLikes.setText(String.valueOf(likes));
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

  static class ProfileHeaderViewHolder extends RecyclerView.ViewHolder
               implements View.OnClickListener {
    @InjectView(R.id.ivUserProfilePhoto) ImageView ivUserProfilePhoto;
    @InjectView(R.id.vUserDetails) View vUserDetails;
    @InjectView(R.id.btSendMail) Button btSendMail;
    @InjectView(R.id.vUserStats) View vUserStats;
    @InjectView(R.id.vUserProfileRoot) View vUserProfileRoot;
    @InjectView(R.id.tvUserName) TextView tvUserName;
    @InjectView(R.id.tvOutline) TextView tvOutline;
    @InjectView(R.id.tvUserMail) TextView tvUserMail;
    @InjectView(R.id.tvTotalLikes) TextView tvTotalLikes;
    @InjectView(R.id.tvPosts) TextView tvPosts;

    private final Typeface TYPEFACE_ROBOTO_LIGHT;
    public ProfileHeaderViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
      btSendMail.setOnClickListener(this);
      TYPEFACE_ROBOTO_LIGHT = Utils.getFont(context, Utils.FontType.ROBOTO_LIGHT);
      tvUserName.setTypeface(TYPEFACE_ROBOTO_LIGHT);
      tvUserMail.setTypeface(TYPEFACE_ROBOTO_LIGHT);
      tvOutline.setTypeface(TYPEFACE_ROBOTO_LIGHT);
      btSendMail.setTypeface(TYPEFACE_ROBOTO_LIGHT);
      tvPosts.setTypeface(TYPEFACE_ROBOTO_LIGHT);
      tvTotalLikes.setTypeface(TYPEFACE_ROBOTO_LIGHT);
    }

    @Override
    public void onClick(View v) {
      try {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse(user.getMail()));
        sendIntent
          .setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
//      sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "test@gmail.com" });
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "大綱");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "內文");
        context.startActivity(sendIntent);
      } catch (Exception e) {
        Toast.makeText(context, "Gmail App not found", Toast.LENGTH_LONG).show();
      }
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
