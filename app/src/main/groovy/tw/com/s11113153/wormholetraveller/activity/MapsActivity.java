package tw.com.s11113153.wormholetraveller.activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.view.RevealBackgroundView;

@Deprecated
public class MapsActivity extends FragmentActivity
  implements RevealBackgroundView.OnStateChangeListener,
  GoogleMap.InfoWindowAdapter {

  private static final String TAG = MapsActivity.class.getSimpleName();

  public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

  @InjectView(R.id.vRevealBackground) RevealBackgroundView vRevealBackground;

  private GoogleMap mMap;

  private LatLng userCurrentLatLng;

  private HashMap<String, WormholeTraveller> wormholeTravellerList = new LinkedHashMap();
  private static final String MARKER_Id = "m";
  private static final String MARKER_Id_0 = "m0";

  private static int mainTravelId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    ButterKnife.inject(this);
    setUpRevealBackground(savedInstanceState);
    userCurrentLatLng = UserInfo.getUserCurrentLatLng(this);
    bindTravel();
  }

  private void bindTravel() {
    int i = 1;
    List<WormholeTraveller> travellers = DataSupport.findAll(WormholeTraveller.class, true);
    for (WormholeTraveller wt : travellers) {
      float lat = wt.getLat();
      float lng = wt.getLng();
      double distance = Utils.calculateDistance(
        userCurrentLatLng.latitude, userCurrentLatLng.longitude, lat, lng);
      if (distance <= 500) {
        if (wt.getId() == mainTravelId) {
          wormholeTravellerList.put(MARKER_Id_0, wt);
        } else {
          wormholeTravellerList.put(MARKER_Id + String.valueOf(i++), wt);
        }
      }
    }
  }

  public static void startMapsFromLocation(
    int[] startingLocation, Activity startingActivity, int travelId
  ) {
    final Intent intent = new Intent(startingActivity, MapsActivity.class);
    intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
    startingActivity.startActivity(intent);
    mainTravelId = travelId;
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
    } else {
      vRevealBackground.setToFinishedFrame();
    }
  }

  @Override
  public void onStateChange(int state) {
    if (RevealBackgroundView.STATE_FINISHED == state) {
      vRevealBackground.setVisibility(View.GONE);
      setUpMapIfNeeded();
    }
  }

  private void setUpMapIfNeeded() {
    if (mMap == null) {
      mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
        .getMap();
      mMap.setInfoWindowAdapter(this);
      if (mMap != null) {
        setUpMap();
      }
    }
  }

  private void setUpMap() {
    initMainTravel();
    initOtherTravel();
  }

  private void initMainTravel() {
    WormholeTraveller mainWt = wormholeTravellerList.get(MARKER_Id_0);
    LatLng latLng = new LatLng(mainWt.getLat(), mainWt.getLng());
    CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
    mMap.moveCamera(center);
    mMap.animateCamera(zoom);
    MarkerOptions markerOptions = new MarkerOptions()
      .title(mainWt.getTitle())
      .position(latLng)
      .icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_GREEN)));
    mMap.addMarker(markerOptions).showInfoWindow();
  }

  private void initOtherTravel() {
    for (Map.Entry entry : wormholeTravellerList.entrySet()) {
      if (entry.getKey().equals(MARKER_Id_0)) continue;

      WormholeTraveller wt = (WormholeTraveller) entry.getValue();
      MarkerOptions ms = new MarkerOptions();
      LatLng latLng = new LatLng(wt.getLat(), wt.getLng());
      ms.title(wt.getTitle()).position(latLng);
      mMap.addMarker(ms);
    }
  }


  private void setPolyline() {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setAntiAlias(true);
    paint.setColor(getResources().getColor(R.color.style_color_primary_dark));
    paint.setStrokeWidth(8);
    paint.setStyle(Paint.Style.FILL);
    PolylineOptions polylineOptions = new PolylineOptions();
    polylineOptions.width(8);
    polylineOptions.geodesic(true);
    polylineOptions.add(userCurrentLatLng);
  }

  static class InfoViewHolder {
    private static ImageView ivFeedCenter = null;
    private static TextView tvTitle = null;
    private static View view = null;
    private static Button btnView = null;
    private static Button btnSearch = null;
    private static InfoViewHolder instance = null;
    private static int infoWidth;
    private static int infoHeight;

    public static InfoViewHolder getInstance(final Context context) {
      if (instance == null) {
        Typeface typeface = Utils.getFont(context, Utils.FontType.ROBOTO_BOLD_ITALIC);
        view = View.inflate(context, R.layout.view_maps_travel, null);
        ivFeedCenter = (ImageView) view.findViewById(R.id.ivFeedCenter);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        btnView = (Button) view.findViewById(R.id.btnView);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        tvTitle.setTypeface(typeface);
        btnView.setTypeface(typeface);
        btnSearch.setTypeface(typeface);
        instance = new InfoViewHolder();
        infoHeight = infoWidth = (int) Utils.doPx(context, Utils.PxType.DP_TO_PX, 140);
      }
      return instance;
    }

    public Button getBtnView() {
      return btnView;
    }

    public Button getBtnSearch() {
      return btnSearch;
    }

    public InfoViewHolder getInstance() {
      return instance;
    }

    public int getInfoWidth() {
      return infoWidth;
    }

    public int getInfoHeight() {
      return infoHeight;
    }

    public View getView() {
      return view;
    }

    public ImageView getIvFeedCenter() {
      return ivFeedCenter;
    }

    public TextView getTvTitle() {
      return tvTitle;
    }
  }

  @Override
  public View getInfoWindow(Marker marker) {
    final InfoViewHolder holder = InfoViewHolder.getInstance(this);
    holder.getTvTitle().setText(marker.getTitle());
    WormholeTraveller wt = wormholeTravellerList.get(marker.getId());
    if (wt.getTravelPhotoPath() != null) {
      Picasso.with(this)
        .load(wt.getTravelPhotoPath())
        .resize(holder.getInfoWidth(), holder.getInfoHeight())
        .centerCrop()
        .into(holder.getIvFeedCenter(), new MarkerCallback(marker));
    }

    return holder.getView();
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }

  private class MarkerCallback implements Callback {
    Marker marker = null;

    MarkerCallback(Marker marker) {
      this.marker = marker;
    }

    @Override
    public void onError() {}

    @Override
    public void onSuccess() {
      if (marker != null && marker.isInfoWindowShown()) {
        marker.hideInfoWindow();
        marker.showInfoWindow();
      }
    }
  }
}
