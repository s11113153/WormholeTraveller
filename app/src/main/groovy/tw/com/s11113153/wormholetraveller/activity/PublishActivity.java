package tw.com.s11113153.wormholetraveller.activity;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;

/**
 * Created by xuyouren on 15/4/10.
 */
public class PublishActivity extends BaseActivity
  implements BaseActivity.OnLatLngGetListener {
  private static final String TAG = PublishActivity.class.getSimpleName();

  private static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

  @InjectView(R.id.tbSkip) ToggleButton tbSkip;
  @InjectView(R.id.tbDirect) ToggleButton tbDirect;
  @InjectView(R.id.ivPhoto) ImageView ivPhoto;
  @InjectView(R.id.tvDate) TextView tvData;
  @InjectView(R.id.tvAddress) TextView tvAddress;
  @InjectView(R.id.root) LinearLayout root;
  @InjectView(R.id.etContent) EditText etContent;
  @InjectView(R.id.etDescription) EditText etDescription;
  @InjectView(R.id.progress_wheel) ProgressWheel progress_wheel;

  private Uri photoUri;

  private int photoSize;

  private static final int STATUS_INIT = 0;
  private static final int STATUS_LOAD_COMPLETED = 1;

  private int statusPhoto = STATUS_INIT;
  private int statusPicInfo = STATUS_INIT;


  private static String publish_address = null;

  private static String publish_date = null;

  private static float publish_lat = -1f;

  private static float publish_lng = -1f;

  public static void openWithPhotoUrl(Activity openingActivity, Uri photoUri) {
    openingActivity.startActivity(new Intent(
        openingActivity, PublishActivity.class)
        .putExtra(ARG_TAKEN_PHOTO_URI, photoUri)
    );
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_publish);
    init(savedInstanceState);
    setFont();
    loadThumbnailPhoto();
    setOnLatLngGetListener(this);
  }

  private void init(Bundle savedInstanceState) {
    getToolbar().setNavigationIcon(getResources().getDrawable(R.mipmap.ic_back));
    getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    photoSize = (int)Utils.doPx(this, Utils.PxType.DP_TO_PX, 96);
    if (savedInstanceState == null) {
      photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
    }
    else {
      photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
    }

    progress_wheel.setBarColor(getResources().getColor(R.color.style_color_primary));
    progress_wheel.setBarWidth(10);
    progress_wheel.setSpinSpeed(30);
    progress_wheel.setProgress(1f);
    progress_wheel.spin();

//    ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//      @Override
//      public boolean onPreDraw() {
//        ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
//        loadThumbnailPhoto();
//        return true;
//      }
//    });
  }

  private void setFont() {
    final Typeface tf = Utils.getFont(this, Utils.FontType.ROBOTO_BOLD_ITALIC);
    tvData.setTypeface(tf);
    tvAddress.setTypeface(tf);
    etContent.setTypeface(tf);
    tbDirect.setTypeface(tf);
    tbSkip.setTypeface(tf);
    etDescription.setTypeface(tf);
  }

  private void loadThumbnailPhoto() {
    ivPhoto.setScaleX(0);
    ivPhoto.setScaleY(0);
    Picasso.with(this)
      .load(photoUri)
      .centerCrop()
      .resize(photoSize, photoSize)
      .into(ivPhoto, new Callback() {
        @Override
        public void onSuccess() {
          ivPhoto.animate()
            .scaleX(1.f).scaleY(1.f)
            .setInterpolator(new OvershootInterpolator())
            .setDuration(800)
            .setStartDelay(200)
            .start();
          statusPhoto = STATUS_LOAD_COMPLETED;
          display();
        }
        @Override
        public void onError() {
          Toast.makeText(PublishActivity.this, "Load Error", Toast.LENGTH_LONG).show();
          statusPhoto = STATUS_LOAD_COMPLETED;
          display();
        }
      });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_publish, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_publish:
        updateDataToDB();
//        bringMainActivityToTop();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void updateDataToDB() {
    User user = DataSupport.find(User.class, UserInfo.getUser(this).getId());
    String title = etDescription.getText().toString().trim();
    String content = etContent.getText().toString().trim();
    boolean show = tbDirect.isChecked();

    if (title.equals("") || content.equals("")) {
      Toast.makeText(
        PublishActivity.this, String.valueOf("title or content is not allowed empty !"),
        Toast.LENGTH_SHORT
      ).show();
      return;
    }

    WormholeTraveller wt = new WormholeTraveller()
      .setTravelPhotoPath(photoUri.toString())
      .setDate(publish_date)
      .setLat(publish_lat)
      .setLng(publish_lng)
      .setAddress(publish_address)
      .setUser(user)
      .setTitle(title)
      .setShow(show)
      .setContent(content);

    boolean b = wt.save();
    if (b)
        bringMainActivityToTop();
    else
        Toast.makeText(PublishActivity.this, String.valueOf("Save Error"), Toast.LENGTH_LONG).show();
  }

  private void bringMainActivityToTop() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.setAction(MainActivity.ACTION_SHOW_LOADING_PHOTO);
    startActivity(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
  }

  @Override
  protected boolean shouldInstallDrawer() {
    return false;
  }

  @OnCheckedChanged(R.id.tbSkip)
  void onSkipCheckedChanged(boolean checked) {
    tbDirect.setChecked(!checked);
  }

  @OnCheckedChanged(R.id.tbDirect)
  public void onDirectCheckedChange(boolean checked) {
    tbSkip.setChecked(!checked);
  }

  @Override
  public void get(final float lat, final float lng) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        int count = 3;
        while (count >= 0) {
          publish_address = Utils.getLocationInfo2(lat, lng);
          publish_date = Utils.getDate();
          if (publish_address != null && publish_date != null) {
            break;
          } else {
            count--;
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {}
          }
        }

        statusPicInfo = STATUS_LOAD_COMPLETED;
        display();

        publish_lat = lat;
        publish_lng = lng;
        publish_address = (publish_address == null) ? "null" : publish_address;
        publish_date = (publish_date == null) ? "null" : publish_date;

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            tvAddress.setText(publish_address);
            tvData.setText(publish_date);
          }
        });

        removeOnLatLngGetListener();

      }
    }).start();
  }

  private void display() {
    if (statusPhoto == STATUS_LOAD_COMPLETED && statusPicInfo == STATUS_LOAD_COMPLETED) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          progress_wheel.setVisibility(View.GONE);
          tvAddress.setVisibility(View.VISIBLE);
          tvData.setVisibility(View.VISIBLE);
          etDescription.setVisibility(View.VISIBLE);
        }
      });
    }
  }
}

