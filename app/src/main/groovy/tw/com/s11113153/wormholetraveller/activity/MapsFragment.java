package tw.com.s11113153.wormholetraveller.activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tw.com.s11113153.wormholetraveller.R;
import tw.com.s11113153.wormholetraveller.UserInfo;
import tw.com.s11113153.wormholetraveller.Utils;
import tw.com.s11113153.wormholetraveller.adapter.ViewPagerAdapter;
import tw.com.s11113153.wormholetraveller.db.table.User;
import tw.com.s11113153.wormholetraveller.db.table.WormholeTraveller;
import tw.com.s11113153.wormholetraveller.view.RevealBackgroundView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xuyouren on 15/4/20.
 */
public class MapsFragment
  extends MapFragment
  implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
  RevealBackgroundView.OnStateChangeListener {

  private static final String TAG = MapsFragment.class.getSimpleName();

  private static final int POPUP_POSITION_REFRESH_INTERVAL = 16;

  private static final int ANIMATION_DURATION = 500;

  private LatLng trackedPosition;

  private Runnable positionUpdaterRunnable;

  private Handler handler;

  private int popupXOffset;
  private int popupYOffset;

  private int markerHeight;

  private AbsoluteLayout.LayoutParams overlayLayoutParams;

  private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;

  private View infoWindowContainer;

  public static final String CURRENT_SEARCH_MODE = "current_search_mode";

  public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

  public static final String TRAVEL_ID = "travelId";

  private HashMap<String, WormholeTraveller> wormholeTravellerList = new LinkedHashMap();
  private static final String MARKER_Id = "m";
  private static final String MARKER_Id_0 = "m0";

  private static int mainTravelId;

  @InjectView(R.id.vRevealBackground) RevealBackgroundView vRevealBackground;

  @InjectView(R.id.ivFeedCenter) ImageView ivFeedCenter;

  @InjectView(R.id.tvTitle) TextView tvTitle;

  private int infoHeight, infoWidth;

  public static final int SEARCH_PEOPLE_TRAVEL = 0;
  public static final int SEARCH_ROUND = 1;
  private static int searchMode = SEARCH_ROUND;
  @IntDef({SEARCH_PEOPLE_TRAVEL, SEARCH_ROUND})
  public @interface SearchMode {}

  public static void changeMode() {
    DrawPolyline.getInstance().deleteInstance();
    switch (searchMode) {
      case SEARCH_ROUND :
        searchMode = SEARCH_PEOPLE_TRAVEL;
        break;
      case SEARCH_PEOPLE_TRAVEL:
        searchMode = SEARCH_ROUND;
        break;
    }
  }

  private static class SelectMarker {
    private User user;
    private WormholeTraveller wormholeTraveller;
    private static SelectMarker instance;

    public static SelectMarker getInstance() {
      if (instance == null) instance = new SelectMarker();
      return instance;
    }

    public void setUser(User user) {
      this.user = user;
    }

    public void setWormholeTraveller(WormholeTraveller wormholeTraveller) {
      this.wormholeTraveller = wormholeTraveller;
    }

    public User getUser() {
      return user;
    }

    public WormholeTraveller getWormholeTraveller() {
      return wormholeTraveller;
    }
  }

  private static class DrawPolyline {
    private static DrawPolyline instance;
    private static List<LatLng> polylineList = new ArrayList();

    private DrawPolyline() {
      polylineList.clear();
    }

    public static DrawPolyline getInstance() {
      if (instance == null)
        instance = new DrawPolyline();
      return instance;
    }

    private void addLatLng(@SearchMode int searchMode, LatLng latLng) {
      if (searchMode == SEARCH_PEOPLE_TRAVEL)
        polylineList.add(latLng);
    }

    public void drawPolyline(Context context, GoogleMap map) {
//      for (int i = 0; i < polylineList.size(); i++) {
//        LatLng latLng = polylineList.get(i);
//        Log.e(TAG, "i = " + i + ", " + String.valueOf(latLng.toString()));
//      }

      if (polylineList.size() < 2 || searchMode != SEARCH_PEOPLE_TRAVEL) return;
      map.addPolyline(
        new PolylineOptions()
          .color(context.getResources().getColor(R.color.style_color_primary_dark))
          .width(8).addAll(polylineList)
      );
    }

    public void deleteInstance() {
      instance = null;
    }
  }


  public static void startMapsFromLocation(
    FragmentManager fragmentManager,
    int[] startingLocation,
    int travelId,
    @SearchMode int mode
  ) {
    searchMode = mode;
    final MapsFragment fragment = new MapsFragment();
    Bundle bundle = new Bundle();
    bundle.putIntArray(MapsFragment.ARG_REVEAL_START_LOCATION, startingLocation);
    bundle.putInt(MapsFragment.TRAVEL_ID, travelId);
    fragment.setArguments(bundle);
    fragmentManager.beginTransaction()
      .addToBackStack("")
      .add(R.id.root, fragment)
      .commit();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragement_maps, container, false);
    FrameLayout containerMap = (FrameLayout) rootView.findViewById(R.id.container_map);
    View mapView = super.onCreateView(inflater, container, savedInstanceState);
    containerMap.addView(mapView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    infoWindowContainer = rootView.findViewById(R.id.cardView);
    infoWindowContainer.setVisibility(View.INVISIBLE);
    infoWindowLayoutListener = new InfoWindowLayoutListener();
    infoWindowContainer.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener);
    overlayLayoutParams = (AbsoluteLayout.LayoutParams) infoWindowContainer.getLayoutParams();
    return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);
    setUpRevealBackground(savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    positionUpdaterRunnable = new PositionUpdaterRunnable();
    handler.post(positionUpdaterRunnable);

    mainTravelId = getArguments().getInt(TRAVEL_ID);
    infoHeight = infoWidth = (int) Utils.doPx(getActivity(), Utils.PxType.DP_TO_PX, 162);
    markerHeight = (int) Utils.doPx(getActivity(), Utils.PxType.DP_TO_PX, 30);

    getMap().setOnMapClickListener(this);
    getMap().setOnMarkerClickListener(this);
    getMap().getUiSettings().setRotateGesturesEnabled(false);

    bindTravelData(searchMode);
  }

  private void setUpRevealBackground(Bundle savedInstanceState) {
    vRevealBackground.setOnStateChangeListener(this);
    if (savedInstanceState == null) {
      final int[] startingLocation = getArguments().getIntArray(MapsFragment.ARG_REVEAL_START_LOCATION);
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
    if (state == RevealBackgroundView.STATE_FINISHED) {
      vRevealBackground.setVisibility(View.GONE);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    infoWindowContainer.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener);
    handler.removeCallbacks(positionUpdaterRunnable);
    handler = null;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void bindTravelData(int mode) {
    wormholeTravellerList.clear();
    int i = 1;
    if (mode == SEARCH_ROUND) {
      LatLng userCurrentLatLng = UserInfo.getUserCurrentLatLng(getActivity());
      List<WormholeTraveller> travellers = DataSupport.findAll(WormholeTraveller.class, true);
      for (WormholeTraveller wt : travellers) {
        float lat = wt.getLat();
        float lng = wt.getLng();
        double distance = Utils.calculateDistance(
          userCurrentLatLng.latitude, userCurrentLatLng.longitude, lat, lng);
//        Log.e(TAG, "" + String.valueOf(wt.getId()));
        if (distance <= 10 && wt.isShow()) {
          if (wt.getId() == mainTravelId) {
            wormholeTravellerList.put(MARKER_Id_0, wt);
          } else {
            wormholeTravellerList.put(MARKER_Id + String.valueOf(i++), wt);
          }
        }
      }
    }
    else if (mode == SEARCH_PEOPLE_TRAVEL) {
      User u = SelectMarker.getInstance().getUser();
      List<WormholeTraveller> travellers =
        DataSupport.where("user_Id=?", String.valueOf(u.getId())).find(WormholeTraveller.class, true);
      for (WormholeTraveller wt : travellers) {
        if (!wt.isShow()) continue;

        if (wt.getId() == mainTravelId) {
          wormholeTravellerList.put(MARKER_Id_0, wt);
        } else {
          wormholeTravellerList.put(MARKER_Id + String.valueOf(i++), wt);
        }
      }

      /**以下是過濾旅行為當天**/
      Log.e(TAG, "woSize : " + String.valueOf(wormholeTravellerList.size()));
      String mainDate = wormholeTravellerList.get(MARKER_Id_0).getDate().split(" ")[0];
      List<String> removeList = new ArrayList();
      for (int j = 1; j < wormholeTravellerList.size(); j++) {
        WormholeTraveller w = wormholeTravellerList.get(MARKER_Id + j);
        String date = w.getDate().split(" ")[0];
        if (!mainDate.equals(date)) {
          removeList.add(MARKER_Id + j);
          Log.e(MARKER_Id + j, "remove" + w.getTitle());
        } else {
          Log.e(MARKER_Id + j, "noRemove" + w.getTitle());
        }
      }
      for (String s : removeList) {
        wormholeTravellerList.remove(s);
      }
      List<WormholeTraveller> temp = new ArrayList(wormholeTravellerList.values());
      wormholeTravellerList.clear();
      for (int k = 0; k < temp.size(); k++) {
        wormholeTravellerList.put(MARKER_Id + k, temp.get(k));
      }
    }

    if (wormholeTravellerList.size() > 0) {
      initMainTravel();
      initOtherTravel();
    }
  }

  private void searchSpecificData(double distance, WormholeTraveller wt) {

  }

  private void initMainTravel() {
    WormholeTraveller mainWt = wormholeTravellerList.get(MARKER_Id_0);
    LatLng latLng = new LatLng(mainWt.getLat(), mainWt.getLng());
    CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);
    getMap().moveCamera(center);
    getMap().animateCamera(zoom);
    MarkerOptions markerOptions = new MarkerOptions()
      .title(mainWt.getTitle())
      .position(latLng)
      .icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_GREEN)));
    Marker marker = getMap().addMarker(markerOptions);

    DrawPolyline.getInstance().addLatLng(searchMode, latLng);
  }

  private void initOtherTravel() {
    BitmapDescriptor bitmap;
    if (searchMode == SEARCH_PEOPLE_TRAVEL) {
      bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    } else {
      bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    }

    for (Map.Entry entry : wormholeTravellerList.entrySet()) {
      if (entry.getKey().equals(MARKER_Id_0)) continue;

//      Log.e(TAG, "" + String.valueOf("" + entry.getKey()));

      WormholeTraveller wt = (WormholeTraveller) entry.getValue();
      MarkerOptions ms = new MarkerOptions();
      LatLng latLng = new LatLng(wt.getLat(), wt.getLng());
      ms.title(wt.getTitle())
        .position(latLng)
        .icon(bitmap);
      getMap().addMarker(ms);

      DrawPolyline.getInstance().addLatLng(searchMode, latLng);
    }

    DrawPolyline.getInstance().drawPolyline(getActivity(), getMap());
  }

  private void infoWindow(Marker marker) {
    tvTitle.setText(marker.getTitle());
    Log.e("markId = ", "" + marker.getId());
    WormholeTraveller wt = wormholeTravellerList.get(marker.getId());
    Log.e("wt path", " : " + wt.getTravelPhotoPath());
    if (wt.getTravelPhotoPath() != null) {
      Picasso.with(getActivity())
        .load(wt.getTravelPhotoPath())
        .resize(infoWidth, infoHeight)
        .centerCrop()
        .into(ivFeedCenter);
    }
  }

  @Override
  public void onMapClick(LatLng latLng) {
    infoWindowContainer.setVisibility(INVISIBLE);
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    GoogleMap map = getMap();
    Projection projection = map.getProjection();
    trackedPosition = marker.getPosition();
    Point trackedPoint = projection.toScreenLocation(trackedPosition);
    trackedPoint.y -= popupYOffset / 2;
    LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
    map.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null);
    infoWindowContainer.setVisibility(VISIBLE);
    infoWindow(marker);
    setSelectMark(marker);
    return true;
  }

  private void setSelectMark(Marker mark) {
    String title = mark.getTitle();
    WormholeTraveller wt = DataSupport.where("title=?", title).find(WormholeTraveller.class, true).get(0);
    SelectMarker select = SelectMarker.getInstance();
    select.setWormholeTraveller(wt);
    select.setUser(wt.getUser());
  }

  private class InfoWindowLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    @Override
    public void onGlobalLayout() {
      popupXOffset = infoWindowContainer.getWidth() / 2;
      popupYOffset = infoWindowContainer.getHeight();
    }
  }

  private class PositionUpdaterRunnable implements Runnable {
    private int lastXPosition = Integer.MIN_VALUE;
    private int lastYPosition = Integer.MIN_VALUE;

    @Override
    public void run() {
      handler.postDelayed(this, POPUP_POSITION_REFRESH_INTERVAL);

      if (trackedPosition != null && infoWindowContainer.getVisibility() == VISIBLE) {
        Point targetPosition = getMap().getProjection().toScreenLocation(trackedPosition);

        if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y) {
          overlayLayoutParams.x = targetPosition.x - popupXOffset;
          overlayLayoutParams.y = targetPosition.y - popupYOffset - markerHeight - 30;
          infoWindowContainer.setLayoutParams(overlayLayoutParams);

          lastXPosition = targetPosition.x;
          lastYPosition = targetPosition.y;
        }
      }
    }
  }

  @OnClick(R.id.btnView)
  void onViewClick(View v) {
    ViewPagerActivity.startViewPager(
      getActivity(),
      SelectMarker.getInstance().getWormholeTraveller().getId(),
      searchMode
    );
  }

  @OnClick(R.id.btnSearch)
  void onSearchClick(View v) {
    int[] startingLocation = new int[2];
    v.getLocationOnScreen(startingLocation);
    SelectMarker select = SelectMarker.getInstance();
    int travelId = select.getWormholeTraveller().getId();
    switch (searchMode) {
      case SEARCH_ROUND:
        startMapsFromLocation(getFragmentManager(), startingLocation, travelId, SEARCH_PEOPLE_TRAVEL);
        break;
      case SEARCH_PEOPLE_TRAVEL:
        changeMode();
        startMapsFromLocation(getFragmentManager(), startingLocation, travelId, SEARCH_ROUND);
        break;
    }
  }
}
