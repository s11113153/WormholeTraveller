package tw.com.s11113153.wormholetraveller.adapter;

import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.activity.MapsFragment;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.utils.CircleTransformation;

/**
 * Created by xuyouren on 15/4/21.
 */
public class ViewPagerAdapter extends PagerAdapter {

  private static Context context;

  private int travelId;

  private int searchMode;

  private final int avatarSize = 96;

  private List<WormholeTraveller> travellers;

  private WormholeTraveller wormholeTraveller;

  private OnViewPagerPosition onViewPagerPosition;

  public ViewPagerAdapter(Context context, int travelId, int searchMode) {
    this.context = context;
    this.searchMode = searchMode;
    this.travelId = travelId;
    this.searchMode = searchMode;

    wormholeTraveller = DataSupport.find(WormholeTraveller.class, travelId, true);
    if (searchMode == MapsFragment.SEARCH_PEOPLE_TRAVEL)
        initUserTravelData();
  }


  private void initUserTravelData() {
    User u = wormholeTraveller.getUser();
    travellers = DataSupport.where("user_id =?",
        String.valueOf(u.getId())).find(WormholeTraveller.class, true);
  }


  @Override
  public int getCount() {
    if (searchMode == MapsFragment.SEARCH_ROUND) return 1;
    return travellers.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == ((CardView)object);
  }

  private void bindOneData(WormholeTraveller wt, PagerViewHolder holder) {
    User user = wt.getUser();
    Picasso.with(context)
      .load(user.getIconPath())
      .resize(96, 96)
      .placeholder(R.drawable.img_circle_placeholder)
      .centerCrop()
      .transform(new CircleTransformation())
      .into(holder.ivUserProfile);

    holder.tvAddress.setText(wt.getAddress());
    holder.tvContent.setText(wt.getContent());
    holder.tvTitle.setText(wt.getTitle());
    holder.tvDate.setText(wt.getDate());
    Picasso.with(context)
      .load(wt.getTravelPhotoPath())
      .fit()
      .into(holder.ivFeedCenter);
  }

  private void bindSearchTravel(PagerViewHolder holder, int position) {
    WormholeTraveller wt = travellers.get(position);
    bindOneData(wt, holder);
  }

  public void animateToTargetPosition() {
    if (searchMode == MapsFragment.SEARCH_ROUND) {
      onViewPagerPosition.target(0);
      return;
    }

    int targetPosition = 0;
    for (int i = 0; i < travellers.size(); i++)
      if (travellers.get(i).getId() == travelId) {
        targetPosition = i;
        break;
      }

    if (onViewPagerPosition != null) {
      onViewPagerPosition.target(targetPosition);
      onViewPagerPosition = null;
    }
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    View view = View.inflate(context, R.layout.item_viewpager, null);
    PagerViewHolder.setInstance(view);
    PagerViewHolder holder = PagerViewHolder.getInstance();

    switch (searchMode) {
      case MapsFragment.SEARCH_ROUND:
        bindOneData(wormholeTraveller, holder);
        break;
      case MapsFragment.SEARCH_PEOPLE_TRAVEL:
        bindSearchTravel(holder, position);
        break;
    }

    container.addView(view);
    return view;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((CardView)object);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return super.getPageTitle(position);
  }

  static class PagerViewHolder {
    @InjectView(R.id.ivUserProfile) ImageView ivUserProfile;
    @InjectView(R.id.tvTitle) TextView tvTitle;
    @InjectView(R.id.tvAddress) TextView tvAddress;
    @InjectView(R.id.tvDate) TextView tvDate;
    @InjectView(R.id.ivFeedCenter) ImageView ivFeedCenter;
    @InjectView(R.id.tvContent) TextView tvContent;

    static PagerViewHolder instance = null;

    PagerViewHolder(View view) {
      ButterKnife.inject(this, view);
      Typeface typefaceBoldItalic = Utils.getFont(context, Utils.FontType.ROBOTO_BOLD_ITALIC);
      Typeface typefaceLight = Utils.getFont(context, Utils.FontType.ROBOTO_LIGHT);
      tvTitle.setTypeface(typefaceBoldItalic);
      tvAddress.setTypeface(typefaceBoldItalic);
      tvDate.setTypeface(typefaceBoldItalic);
      tvContent.setTypeface(typefaceLight);
    }

    public static PagerViewHolder getInstance() {
      return instance;
    }

    public static void setInstance(View view) {
      instance = new PagerViewHolder(view);
    }
  }

  public void setOnViewPagerPosition(OnViewPagerPosition onViewPagerPosition) {
    this.onViewPagerPosition = onViewPagerPosition;
  }

  public interface OnViewPagerPosition {
    void target(int position);
  }
}
