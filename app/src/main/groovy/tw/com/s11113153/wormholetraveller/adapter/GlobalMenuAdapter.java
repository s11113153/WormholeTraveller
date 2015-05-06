package tw.com.s11113153.wormholetraveller.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;

/**
 * Created by xuyouren on 15/4/6.
 */
public class GlobalMenuAdapter extends ArrayAdapter<GlobalMenuAdapter.GlobalMenuItem> {
  private static final String TAG = GlobalMenuAdapter.class.getSimpleName();

  private static final int TYPE_MENU_ITEM = 0;
  private static final int TYPE_DIVIDER = 1;
  private static final int TYPE_VIEW_COUNT_MAX = 2;

  private final LayoutInflater mInflater;

  private final List<GlobalMenuItem> mMenuItems = new ArrayList();

  private static Typeface TYPE_FACE_ROBOTO_BOLD_ITALIC;


  public GlobalMenuAdapter(Context context) {
    super(context, 0);
    TYPE_FACE_ROBOTO_BOLD_ITALIC = Utils.getFont(context, Utils.FontType.ROBOTO_BOLD_ITALIC);

    mInflater = LayoutInflater.from(context);
    setUpMenuItems();
  }

  private void setUpMenuItems() {
    mMenuItems.add(new GlobalMenuItem(R.mipmap.ic_global_favorite, "Favorite"));
    mMenuItems.add(new GlobalMenuItem(R.mipmap.ic_logout, "LogOut"));
    mMenuItems.add(new GlobalMenuItem(0, "About"));
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mMenuItems.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public GlobalMenuItem getItem(int position) {
    return mMenuItems.get(position);
  }

  @Override
  public int getItemViewType(int position) {
    return mMenuItems.get(position).isDivider ? TYPE_DIVIDER : TYPE_MENU_ITEM;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_VIEW_COUNT_MAX;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_MENU_ITEM:
        final MenuItemViewHolder holder;
        if (convertView == null) {
          convertView = mInflater.inflate(R.layout.item_global_menu, parent, false);
          holder = new MenuItemViewHolder(convertView);
          convertView.setTag(holder);
        } else {
          holder = (MenuItemViewHolder) convertView.getTag();
        }

        GlobalMenuItem item = getItem(position);
        holder.tvLabel.setText(item.label);
        holder.ivIcon.setImageResource(item.iconResId);
        holder.ivIcon.setVisibility(item.iconResId == 0 ? View.GONE : View.VISIBLE);
        return convertView;

      case TYPE_DIVIDER:
        return mInflater.inflate(R.layout.item_menu_divider, parent, false);

      default:
        throw new RuntimeException("getItemViewType is error");
    }
  }

  @Override
  public boolean isEnabled(int position) {
    return !mMenuItems.get(position).isDivider;
  }

  public static class MenuItemViewHolder {
    @InjectView(R.id.ivIcon)  ImageView ivIcon;
    @InjectView(R.id.tvLabel) TextView tvLabel;

    public MenuItemViewHolder(View view) {
      ButterKnife.inject(this, view);
      tvLabel.setTypeface(TYPE_FACE_ROBOTO_BOLD_ITALIC);
    }
  }

  public static class GlobalMenuItem {
    public int iconResId;
    public String label;
    public boolean isDivider;

    private GlobalMenuItem() {
      // do nothing
    }

    public GlobalMenuItem(int iconResId, String label) {
      this.iconResId = iconResId;
      this.label = label;
      this.isDivider = false;
    }

    public static GlobalMenuItem dividerMenuItem() {
      GlobalMenuItem globalMenuItem = new GlobalMenuItem();
      globalMenuItem.isDivider = true;
      return globalMenuItem;
    }
  }
}
