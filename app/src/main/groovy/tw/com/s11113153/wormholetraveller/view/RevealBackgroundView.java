package tw.com.s11113153.wormholetraveller.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tw.com.s11113153.wormholetraveller.R;

/**
 * Created by xuyouren on 15/4/3.
 */
public class RevealBackgroundView extends View {
  private static final String TAG = RevealBackgroundView.class.getSimpleName();

  public static final int STATE_NOT_STARTED = 0;
  public static final int STATE_FILL_STARTED = 1;
  public static final int STATE_FINISHED = 2;
  @IntDef({STATE_NOT_STARTED, STATE_FILL_STARTED, STATE_FINISHED})
  @Retention(RetentionPolicy.SOURCE)
  private  @interface Duration {}

  private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();

  private static final int FILL_TILE = 500;

  private int state = STATE_NOT_STARTED;

  private Paint fillPaint;

  private int currentRadius;

  ObjectAnimator revealAnimator;

  private int startLocationX;

  private int startLocationY;

  private OnStateChangeListener onStateChangeListener;

  public RevealBackgroundView(Context context) {
    super(context);
    init();
  }

  public RevealBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public RevealBackgroundView(
              Context context,
              AttributeSet attrs,
              int defStyleAttr,
              int defStyleRes)
  {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    fillPaint = new Paint();
    fillPaint.setStyle(Paint.Style.FILL);
    fillPaint.setColor(getResources().getColor(R.color.style_color_primary_thin_color));
    fillPaint.setAntiAlias(true);
  }

  public void setOnStateChangeListener(
    OnStateChangeListener onStateChangeListener) {
    this.onStateChangeListener = onStateChangeListener;
  }

  public void startFromLocation(int[] tapLocationOnScreen) {
    changeState(STATE_FILL_STARTED);
    startLocationX = tapLocationOnScreen[0];
    startLocationY = tapLocationOnScreen[1];
    Log.e("startLocationX = " + startLocationX, "startLocationY = " + startLocationY);
    revealAnimator = ObjectAnimator.ofInt(
        this, "currentRadius", 0, getWidth() + getHeight());
    revealAnimator.setDuration(FILL_TILE);
    revealAnimator.setInterpolator(INTERPOLATOR);
    revealAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        changeState(STATE_FINISHED);
      }
    });
    revealAnimator.start();
  }

  public void setFillPaintColor(int color) {
    fillPaint.setColor(color);
  }

  public void setToFinishedFrame() {
    changeState(STATE_FINISHED);
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (state == STATE_FINISHED) {
      fillPaint.setColor(Color.WHITE);
      canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
    }
    else canvas.drawCircle(startLocationX, startLocationY, currentRadius, fillPaint);
  }

  private void changeState(@Duration int state) {
    if (this.state == state) return;

    this.state = state;
    if (onStateChangeListener != null)
        onStateChangeListener.onStateChange(state);
    else
        Log.e(TAG, "" + String.valueOf("no set OnStateChangeListener"));
  }


  /** @hide setting for propertyName **/
  public void setCurrentRadius(int radius) {
    this.currentRadius = radius;
    invalidate();
  }

  public interface OnStateChangeListener {
    void onStateChange(int state);
  }
}
