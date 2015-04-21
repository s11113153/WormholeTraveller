package tw.com.s11113153.wormholetraveller.activity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;

/**
 * Created by xuyouren on 15/4/10.
 */
public class PublishActivity extends BaseActivity {
  private static final String TAG = PublishActivity.class.getSimpleName();

  private static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

  @InjectView(R.id.tbSkip) ToggleButton tbSkip;
  @InjectView(R.id.tbDirect) ToggleButton tbDirect;
  @InjectView(R.id.ivPhoto) ImageView ivPhoto;

  private Uri photoUri;

  private int photoSize;

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
  }

  private void init(Bundle savedInstanceState) {
    getToolbar().setNavigationIcon(getResources().getDrawable(R.mipmap.ic_back));
    photoSize = (int)Utils.doPx(this, Utils.PxType.DP_TO_PX, 96);
    if (savedInstanceState == null) {
      photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
    }
    else {
      photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
    }

    ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
        loadThumbnailPhoto();
        return true;
      }
    });
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
        }
        @Override
        public void onError() {
          Toast.makeText(PublishActivity.this, "Load Error", Toast.LENGTH_LONG).show();
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
        bringMainActivityToTop();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
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
}
