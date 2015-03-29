package tw.com.s11113153.wormholetraveller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
  private int avatarSize;

  private boolean animationsLocked = false;
  private boolean delayEnterAnimation = true;


  public CommentItemAdapter(Context context) {
    this.context = context;
    this.avatarSize = 112;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    runEnterAnimation(viewHolder.itemView, position);
    ViewHolder holder = (ViewHolder) viewHolder;
    switch (position % 3) {
      case 0:
        holder.tvComment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
        break;
      case 1:
        holder.tvComment.setText("Cupcake ipsum dolor sit amet bear claw.");
        break;
      case 2:
        holder.tvComment.setText("Cupcake ipsum dolor sit. Amet gingerbread cupcake. Gummies ice cream dessert icing marzipan apple pie dessert sugar plum.");
        break;
    }

//      Picasso.with(context)
//        .load(R.mipmap.ic_default_comment_people)
//        .centerInside()
//        .resize(112, 112)
//        .transform(new RoundedTransformation()).into(holder.ivUser);
  }

  @Override
  public int getItemCount() {
    return itemsCount;
  }
  private void runEnterAnimation(View view, int position) {
    if (animationsLocked) return;

    if (position > lastAnimatedPosition) {
      lastAnimatedPosition = position;
      /***/
      view.setTranslationY(500);
      view.setAlpha(0.1f);

      view.animate()
        .translationY(0)
        .alpha(1.0f)
        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
        .setInterpolator(new AccelerateInterpolator(5.0f))
        .setDuration(800)
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
    @InjectView(R.id.ivUser)
    ImageView ivUser;
    @InjectView(R.id.tvComment)
    TextView tvComment;

    private ViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }
}
