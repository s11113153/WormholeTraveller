package tw.com.s11113153.wormholetraveller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.com.s11113153.wormholetraveller.R;

/**
 * Created by xuyouren on 15/4/9.
 */
public class PhotoFiltersAdapter extends RecyclerView.Adapter {

  private Context mContext;

  private int itemsCount = 12;

  public PhotoFiltersAdapter(Context context) {
    mContext = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(mContext)
      .inflate(R.layout.item_photo_filter, parent, false);
    return new PhotoFilterViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    // do nothing
  }

  @Override
  public int getItemCount() {
    return itemsCount;
  }

  static class PhotoFilterViewHolder extends RecyclerView.ViewHolder {
    PhotoFilterViewHolder(View itemView) {
      super(itemView);
    }
  }
}
