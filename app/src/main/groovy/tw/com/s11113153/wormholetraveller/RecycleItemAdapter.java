package tw.com.s11113153.wormholetraveller;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.view.SquaredImageView;

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
    holder.ibComments.setOnClickListener(this);
    holder.ibComments.setTag(position);
    holder.ibMore.setOnClickListener(this);
    holder.ibMore.setTag(position);


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
      holder.ivSquare.setImageDrawable(null);
      return;
    }

    Picasso.with(context)
      .load(url)
      .fit()
      .into(holder.ivSquare);
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
    @InjectView(R.id.ivSquare)
    SquaredImageView ivSquare; // photo image
    @InjectView(R.id.ibLike) ImageButton ibLike;
    @InjectView(R.id.ibComments) ImageButton ibComments;
    @InjectView(R.id.ibMore) ImageButton ibMore;

    ViewHolder(View view) {
      super(view);
      ButterKnife.inject(this, view);
    }
  }

  public interface OnBottomClickListener {
    void onCommentsClick(View v, int position);
    void onMoreClick(View v, int position);
  }
}