package tw.com.s11113153.wormholetraveller.adapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.db.table.Comments;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.utils.RoundedTransformation;
import tw.com.s11113153.wormholetraveller.Utils;

/**
 * Created by xuyouren on 15/3/29.
 */
public class CommentItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final String TAG = CommentItemAdapter.class.getSimpleName();
  final Context context;
//  private int itemsCount = 3;
  private int lastAnimatedPosition = -1;
  private int userIconSize;

  private boolean animationsLocked = false;
  private boolean delayEnterAnimation = true;

  private final int travelId;

  private List<Comments> comments;

  public CommentItemAdapter(Context context, int travelId) {
    this.context = context;
    this.userIconSize = context.getResources().getDimensionPixelSize(R.dimen.iv_user_size);
    this.travelId = travelId;
    bindComments();
  }

  private void bindComments() {
    //    comments = DataSupport.where("wormholetraveller_id=?", String.valueOf(travelId))
    //                          .find(Comments.class, true);
    comments = DataSupport.find(WormholeTraveller.class, travelId, true).getComments();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
    return new CommentsViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    runEnterAnimation(viewHolder.itemView, position);
    final CommentsViewHolder holder = (CommentsViewHolder) viewHolder;
    int userId = comments.get(position).getUser_id();
    User u = DataSupport.find(User.class, userId);
    bindUserProfile(holder, u);
    bindUserContent(holder, position);
  }

  private void bindUserProfile(final CommentsViewHolder holder, final User u) {
    final AnimationDrawable drawable = (AnimationDrawable) holder.ivLoading.getBackground();
    String url = u.getIconPath();
    Picasso.with(context)
      .load(url)
      .centerCrop()
      .resize(userIconSize, userIconSize)
      .error(R.mipmap.ic_default_comment_people)
      .transform(new RoundedTransformation()).into(holder.ivUser, new Callback() {
      @Override
      public void onSuccess() {
        Utils.clearBackground(holder.ivLoading);
      }

      @Override
      public void onError() {
        Utils.clearBackground(holder.ivLoading);
      }
    });

    if (drawable != null)
        drawable.start();
  }

  private void bindUserContent(final CommentsViewHolder holder, final int position) {
    holder.tvComment.setText(comments.get(position).getContent());
  }

  @Override
  public int getItemCount() {
    return comments.size();
  }

  private void runEnterAnimation(View view, int position) {
    if (animationsLocked) return;

    /** 確保每個 position 都會執行一次 **/
    if (position > lastAnimatedPosition) {
      lastAnimatedPosition = position;
      view.setTranslationY(500);
      view.setAlpha(0.1f);

      view.animate()
        .translationY(0)
        .alpha(1.0f)
        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
        .setInterpolator(new DecelerateInterpolator(5.0f))
        .setDuration(Utils.AnimationAttribute.DURATION_SHORT.getVal())
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            animationsLocked = true;
          }
        })
        .start();
    }
  }

  public void updateItems() {
    bindComments();
    notifyDataSetChanged();
  }

  public void addItem() {
//    itemsCount++;
//    notifyItemInserted(itemsCount - 1);
  }

  public void setAnimationsLocked(boolean animationsLocked) {
    this.animationsLocked = animationsLocked;
  }

  public void setDelayEnterAnimation(boolean delayEnterAnimation) {
    this.delayEnterAnimation = delayEnterAnimation;
  }

  static class CommentsViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.ivUser)  ImageView ivUser;
    @InjectView(R.id.tvComment) TextView tvComment;
    @InjectView(R.id.ivLoading) ImageView ivLoading;

    private CommentsViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }
}
