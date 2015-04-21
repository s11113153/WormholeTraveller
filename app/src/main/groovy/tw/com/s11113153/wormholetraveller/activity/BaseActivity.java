package tw.com.s11113153.wormholetraveller.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.db.DataBaseManager;
import tw.com.s11113153.wormholetraveller.utils.DrawerLayoutInstaller;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.view.GlobalMenuView;

/**
 * Created by xuyouren on 15/4/3.
 */
public class BaseActivity extends ActionBarActivity implements
    GlobalMenuView.OnHeaderClickListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private static final String TAG = BaseActivity.class.getSimpleName();

  @Optional
  @InjectView(R.id.toolbar) Toolbar mToolbar;

  @Optional
  @InjectView(R.id.tvLogo)  TextView mTvLogo;

  private DrawerLayout mDrawerLayout;

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

  private Location mLastLocation;

  private GoogleApiClient mGoogleApiClient;

  private LocationRequest mLocationRequest;

  private static int UPDATE_INTERVAL = 30000; // 30 sec
  private static int FATEST_INTERVAL = 10000; // 10 sec
  private static int DISPLACEMENT = 100; // 100 meters

  private Typeface TYPEFACE_ROBOTO_BOLD_ITALIC;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.inject(this);
    setUpToolbar();
    if (shouldInstallDrawer())
        setUpDrawer();
  }

  protected void setUpToolbar() {
    if (mToolbar == null) return;
    if (mTvLogo != null) mTvLogo.setTypeface(TYPEFACE_ROBOTO_BOLD_ITALIC);
    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.mipmap.ic_menu);
  }

  protected boolean shouldInstallDrawer() {
    return true;
  }

  public Toolbar getToolbar() {
    return mToolbar;
  }

  public TextView getTvLogo() {
    return mTvLogo;
  }

  private void setUpDrawer() {
    GlobalMenuView menuView = new GlobalMenuView(this);
    menuView.setOnHeaderClickListener(this);

    mDrawerLayout = DrawerLayoutInstaller.from(this)
      .drawerRoot(R.layout.drawer_root)
      .drawerLeftView(menuView)
      .drawerLeftWidth((int) Utils.doPx(this, Utils.PxType.DP_TO_PX, 50))
      .withNavigationIconToggler(getToolbar())
      .build();
  }

  @Override
  public void onGlobalMenuHeaderClick(final View v) {
    mDrawerLayout.closeDrawer(Gravity.START);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseActivity.this,
            UserInfo.getUser(BaseActivity.this));
      }
    }, 200);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (checkPlayServices()) {
      buildGoogleApiClient();
      createLocationRequest();
    }
    TYPEFACE_ROBOTO_BOLD_ITALIC = Utils.getFont(this, Utils.FontType.ROBOTO_BOLD_ITALIC);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mGoogleApiClient.isConnected()) {
      startLocationUpdates();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mGoogleApiClient.isConnected()) {
      stopLocationUpdates();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  protected void displayLocation() {
    mLastLocation = LocationServices.FusedLocationApi
      .getLastLocation(mGoogleApiClient);
    if (mLastLocation != null) {
      float lat = (float) mLastLocation.getLatitude();
      float lng = (float) mLastLocation.getLongitude();
      Log.e(TAG, "lat = " + String.valueOf(lat));
      Log.e(TAG, "lng =" + String.valueOf(lng));
      if (mOnLatLngGetListener != null)
          mOnLatLngGetListener.get(lat, lng);
    } else {
      Log.e("", "mLastLocation is null");
    }
  }

  protected void startLocationUpdates() {
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
  }

  protected void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
  }

  private synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .addApi(LocationServices.API).build();
  }

  private void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setFastestInterval(FATEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
  }

  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil
      .isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this,
          PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
        finish();
      }
      return false;
    }
    return true;
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
      + result.getErrorCode());
  }

  @Override
  public void onConnected(Bundle arg0) {
    displayLocation();
//    startLocationUpdates();
  }

  @Override
  public void onConnectionSuspended(int arg0) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onLocationChanged(Location location) {
    mLastLocation = location;
    Log.e(TAG, "" + String.valueOf("Location Changed"));
    displayLocation();
  }

  private OnLatLngGetListener mOnLatLngGetListener;

  public void setOnLatLngGetListener(OnLatLngGetListener onLatLngGetListener) {
    mOnLatLngGetListener = onLatLngGetListener;
  }

  public void removeOnLatLngGetListener() {
    if (mOnLatLngGetListener != null)
        mOnLatLngGetListener = null;
    stopLocationUpdates();
    mGoogleApiClient.disconnect();
  }

  public interface OnLatLngGetListener {
    void get(float lat, float lng);
  }
}
