package tw.com.s11113153.wormholetraveller.activity;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.InjectView;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.adapter.PhotoFiltersAdapter;
import tw.com.s11113153.wormholetraveller.view.RevealBackgroundView;

/**
 * Created by xuyouren on 15/4/9.
 */

public class TakePhotoActivity
  extends BaseActivity
  implements RevealBackgroundView.OnStateChangeListener, CameraHostProvider {

  private static final String TAG = TakePhotoActivity.class.getSimpleName();

  public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

  private static final int PORTRAIT = 90;

  private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
  private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

  @IntDef({STATE_TAKE_PHOTO, STATE_SETUP_PHOTO})
  @Retention(RetentionPolicy.SOURCE)
  private  @interface PhotoState {}
  private static final int STATE_TAKE_PHOTO = 0;
  private static final int STATE_SETUP_PHOTO = 1;

  private boolean pendingIntro;

  private int currentState;

  private File photoPath;

  @InjectView(R.id.vRevealBackground) RevealBackgroundView vRevealBackground;
  @InjectView(R.id.vPhotoRoot) View vTakePhotoRoot;
  @InjectView(R.id.vShutter) View vShutter;
  @InjectView(R.id.ivTakenPhoto) ImageView ivTakenPhoto;
  @InjectView(R.id.vUpperPanel) ViewSwitcher vUpperPanel;
  @InjectView(R.id.vLowerPanel) ViewSwitcher vLowerPanel;
  @InjectView(R.id.cameraView) CameraView cameraView;
  @InjectView(R.id.rvFilters) RecyclerView rvFilters;
  @InjectView(R.id.btnTakePhoto) Button btnTakePhoto;

  public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
    startingActivity.startActivity(new Intent(
        startingActivity, TakePhotoActivity.class)
        .putExtra(ARG_REVEAL_START_LOCATION, startingLocation)
    );
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_photo);
    updateState(STATE_TAKE_PHOTO);
    setUpRevealBackground(savedInstanceState);
//    setUpPhotoFilters();

    vUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        vUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
        pendingIntro = true;
        vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
        vLowerPanel.setTranslationY(-vLowerPanel.getHeight());
        return true;
      }
    });
  }

  @OnClick(R.id.btnAccept)
  void onAcceptClick() {
    if (photoPath != null) {
      PublishActivity.openWithPhotoUrl(TakePhotoActivity.this, Uri.fromFile(photoPath));
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    cameraView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    cameraView.onPause();
  }

  private void setUpRevealBackground(Bundle savedInstanceState) {
    vRevealBackground.setOnStateChangeListener(this);
    if (savedInstanceState == null) {
      final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
      vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
          vRevealBackground.startFromLocation(startingLocation);
          return true;
        }
      });
    }
    else vRevealBackground.setToFinishedFrame();
  }

  private void setUpPhotoFilters() {
    PhotoFiltersAdapter photoFiltersAdapter = new PhotoFiltersAdapter(this);
    rvFilters.setHasFixedSize(true);
    rvFilters.setAdapter(photoFiltersAdapter);
    rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
  }

  private void updateState(@PhotoState int state) {
    currentState = state;
    switch (currentState) {
      case STATE_TAKE_PHOTO:
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            ivTakenPhoto.setVisibility(View.GONE);
          }
        }, 400);
        break;
      case STATE_SETUP_PHOTO:
        ivTakenPhoto.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override
  protected boolean shouldInstallDrawer() {
    return false;
  }

  @Override
  public void onStateChange(int state) {
    if (RevealBackgroundView.STATE_FINISHED == state) {
      vTakePhotoRoot.setVisibility(View.VISIBLE);
      if (pendingIntro) startIntroAnimation();
    }
    else vTakePhotoRoot.setVisibility(View.INVISIBLE);
  }

  private void startIntroAnimation() {
    vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
    vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
  }

  @OnClick(R.id.btnTakePhoto)
  void onTakePhotoClick() {
    btnTakePhoto.setEnabled(false);
    cameraView.takePicture(true, true);
    animateShutter();
  }

  private void animateShutter() {
    vShutter.setVisibility(View.VISIBLE);
    vShutter.setAlpha(0f);

    ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
    alphaInAnim.setDuration(100);
    alphaInAnim.setStartDelay(100);
    alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

    ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
    alphaOutAnim.setDuration(200);
    alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        vShutter.setVisibility(View.GONE);
      }
    });
    animatorSet.start();
  }

  @Override
  public CameraHost getCameraHost() {
    return new MyCameraHost(this);
  }

  class MyCameraHost extends SimpleCameraHost {

    private Camera.Size previewSize;

    MyCameraHost(Context context) {
      super(context);
    }

    @Override
    public boolean useFullBleedPreview() {
      return true;
    }

    @Override
    public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
      return previewSize;
    }

    @Override
    public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
      for(Camera.Size size : parameters.getSupportedPictureSizes())
        if (size.height <=1200 && size.width <= 1200) {
          previewSize = size;
          break;
        }
      return super.adjustPreviewParameters(parameters);
    }

    @Override
    public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {
      parameters.setRotation(90);
      return super.adjustPictureParameters(xact, parameters);
    }

    @Override
    public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          showTakePicture(bitmap);
        }
      });
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
      super.saveImage(xact, image);
      photoPath = getPhotoPath();
    }
  }

  private void showTakePicture(Bitmap bitmap) {
    vUpperPanel.showNext();
    ivTakenPhoto.setImageBitmap(bitmap);
    updateState(STATE_SETUP_PHOTO);
  }

  @Override
  public void onBackPressed() {
    if (currentState == STATE_SETUP_PHOTO) {
      btnTakePhoto.setEnabled(true);
      vUpperPanel.showNext();
      updateState(STATE_TAKE_PHOTO);
    }
    else {
      super.onBackPressed();
    }
  }
}
