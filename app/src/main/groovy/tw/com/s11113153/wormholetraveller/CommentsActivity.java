package tw.com.s11113153.wormholetraveller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyouren on 15/3/29.
 */
public class CommentsActivity extends ActionBarActivity {
  public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

  private static final int ANIM_PADDING = 80;
  private static final int ANIM_DURATION_SHORT = 400;
  private static final int ANIM_DURATION_LONG = 800;
  private static final int ANIM_DELAY_START = 200;

  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.rvComments) RecyclerView mRecyclerView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);
    ButterKnife.inject(this);
    setConfig();
  }

  private void setConfig() {
    setUpToolbar();
    setUpComments();
  }

  private void setUpToolbar() {
    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  private void setUpComments() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(new CommentItemAdapter(this));
  }
}
