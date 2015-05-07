package tw.com.s11113153.wormholetraveller.view;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.activity.SignUpActivity2;
import tw.com.s11113153.wormholetraveller.adapter.GlobalMenuAdapter;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.utils.CircleTransformation;

/**
 * Created by xuyouren on 15/4/4.
 */
public class GlobalMenuView extends ListView implements View.OnClickListener {

  private OnHeaderClickListener mOnHeaderClickListener;

  private GlobalMenuAdapter mGlobalMenuAdapter;

  private static final int ITEM_FAVORITE = 1;
  private static final int ITEM_LOG_OUT = 2;


  @Optional
  @InjectView(R.id.ivUserProfilePhoto) ImageView mIvUserProfilePhoto;

  @Optional
  @InjectView(R.id.tvName) TextView tvName;

  private int avatarSize;

  private String mProfilePhoto;

  public GlobalMenuView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setChoiceMode(CHOICE_MODE_SINGLE);
    setDivider(getResources().getDrawable(android.R.color.transparent));
    setDividerHeight(0);
    setBackgroundColor(Color.WHITE);
    setUpHeader();
    setUpAdapter();
  }

  private void setUpAdapter() {
    mGlobalMenuAdapter = new GlobalMenuAdapter(getContext());
    setAdapter(mGlobalMenuAdapter);
  }

  private void setUpHeader() {
    this.avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
    User u = UserInfo.getUser(getContext());
    this.mProfilePhoto = u.getIconPath();

    setHeaderDividersEnabled(true);
    setFooterDividersEnabled(true);
    View vHeader = View.inflate(getContext(), R.layout.view_global_menu_header, null);
    ButterKnife.inject(this, vHeader);

    Picasso.with(getContext())
      .load(mProfilePhoto)
      .placeholder(R.drawable.img_circle_placeholder)
      .resize(avatarSize, avatarSize)
      .centerCrop()
      .transform(new CircleTransformation())
      .into(mIvUserProfilePhoto);

    tvName.setText(u.getName());

    addHeaderView(vHeader);
    vHeader.setOnClickListener(this);
    setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case ITEM_FAVORITE:
            //do nothing
            break;
          case ITEM_LOG_OUT:
            doLogOut();
            break;
        }
      }
    });
  }

  private void doLogOut() {
    User user = UserInfo.getUser(getContext());
    user.setPassword("");
    user.setAccount("");
    UserInfo.setUser(getContext(), user);
    ((Activity)getContext()).finish();
    getContext().startActivity(
      new Intent(getContext(), SignUpActivity2.class)
    );
  }

  @Override
  public void onClick(View v) {
    if (mOnHeaderClickListener != null)
      mOnHeaderClickListener.onGlobalMenuHeaderClick(v);
  }

  public interface OnHeaderClickListener {
    void onGlobalMenuHeaderClick(View v);
  }

  public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
    mOnHeaderClickListener = onHeaderClickListener;
  }

}
