package tw.com.s11113153.wormholetraveller;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class CommentItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  final Context context;
  private int itemsCount = 3;
  private int lastAnimatedPosition = -1;
  private int userIconSize;

  private boolean animationsLocked = false;
  private boolean delayEnterAnimation = true;

  public CommentItemAdapter(Context context) {
    this.context = context;
    this.userIconSize = context.getResources().getDimensionPixelSize(R.dimen.iv_user_size);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    runEnterAnimation(viewHolder.itemView, position);
    final ViewHolder holder = (ViewHolder) viewHolder;
    AnimationDrawable drawable = (AnimationDrawable) holder.ivLoading.getBackground();
    String url = "https://www.facebook.com/xu.y.jen";

    switch (position % 3) {
      case 0:
        holder.tvComment.setText("HelloWorld.");
        break;
      case 1:
        url = "http://i.imgur.com/DvpvklR.png";
        holder.tvComment.setText("Groovy With Android");
        break;
      case 2:
        holder.tvComment.setText("Java is Deprecated");
        break;
    }

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
    drawable.start();
  }



  @Override
  public int getItemCount() {
    return itemsCount;
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
    itemsCount = 10;
    notifyDataSetChanged();
  }

  public void addItem() {
    itemsCount++;
    notifyItemInserted(itemsCount - 1);
  }

  public void setAnimationsLocked(boolean animationsLocked) {
    this.animationsLocked = animationsLocked;
  }

  public void setDelayEnterAnimation(boolean delayEnterAnimation) {
    this.delayEnterAnimation = delayEnterAnimation;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.ivUser)  ImageView ivUser;
    @InjectView(R.id.tvComment) TextView tvComment;
    @InjectView(R.id.ivLoading) ImageView ivLoading;

    private ViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }
}