package tw.com.s11113153.wormholetraveller.activity;

import com.viewpagerindicator.UnderlinePageIndicator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.adapter.ViewPagerAdapter;

/**
 * Created by xuyouren on 15/4/21.
 */
public class ViewPagerActivity extends BaseActivity
        implements ViewPagerAdapter.OnViewPagerListener {

  private static final String TRAVEL_ID = "travel_id";

  private ViewPagerAdapter adapter;

  @InjectView(R.id.pager) ViewPager pager;
  @InjectView(R.id.indicator) UnderlinePageIndicator indicator;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewpager);
    ButterKnife.inject(this);
    if (savedInstanceState == null) {
      int travelId = getIntent().getIntExtra(TRAVEL_ID, -1);
      int searchMode = getIntent().getIntExtra(MapsFragment.CURRENT_SEARCH_MODE, -1);
      adapter = new ViewPagerAdapter(this, travelId, searchMode);
      adapter.setOnViewPagerListener(this);
//      adapter.animateToTargetPosition();
    }
  }

  public static void startViewPager(
    Context startingActivity,
    int travelId,
    @MapsFragment.SearchMode int mode
  ) {
    Intent i = new Intent(startingActivity, ViewPagerActivity.class);
    i.putExtra(TRAVEL_ID, travelId);
    i.putExtra(MapsFragment. CURRENT_SEARCH_MODE, mode);
    startingActivity.startActivity(i);
  }

  @Override
  public void target(int position) {
    pager.setAdapter(adapter);
    pager.setCurrentItem(position, true);
    indicator.setFadeDelay(300);
    indicator.setFades(false);
    indicator.setSelectedColor(getResources().getColor(R.color.style_color_primary));
    indicator.setViewPager(pager);
  }
}
