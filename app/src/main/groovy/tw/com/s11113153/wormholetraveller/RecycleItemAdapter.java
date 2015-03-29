package tw.com.s11113153.wormholetraveller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class RecycleItemAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

  private Context context;
  private int itemsCount = 10;
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
    holder.ivBottom.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_id));
    holder.ivBottom.setOnClickListener(this);
    holder.ivBottom.setTag(position);
  }

  @Override
  public int getItemCount() {
    return itemsCount;
  }

  private void runEnterAnimation(View view, int position) {
    if (position > NO_INIT_POSITION) {
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
    if (v.getId() == R.id.ivBottom) {
      if (mBottomClickListener != null)
          mBottomClickListener.onCommentsClick(v, (Integer) v.getTag());
    }
  }

  public void setOnBottomClickListener(OnBottomClickListener bottomClickListener) {
    mBottomClickListener = bottomClickListener;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    final SquaredImageView ivSquare;
    final ImageView ivBottom;

    ViewHolder(View itemView) {
      super(itemView);
      ivSquare = (SquaredImageView)itemView.findViewById(R.id.ivSquare);
      ivBottom = (ImageView)itemView.findViewById(R.id.ivBottom);
    }
  }

  public interface OnBottomClickListener {
    void onCommentsClick(View v, int position);
  }
}