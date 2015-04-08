package tw.com.s11113153.wormholetraveller.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import tw.com.s11113153.wormholetraveller.R;

/**
 * Created by xuyouren on 15/4/10.
 */
public class SendingProgressView extends View {

  public static final int STATE_NOT_STARTED = 0;
  public static final int STATE_PROGRESS_STARTED = 1;
  public static final int STATE_DONE_STARTED = 2;
  public static final int STATE_FINISHED = 3;
  @IntDef({STATE_NOT_STARTED, STATE_PROGRESS_STARTED, STATE_DONE_STARTED, STATE_FINISHED})
  @interface State {}

  private int state = STATE_NOT_STARTED;

  private float currentProgress = 0;

  private static final int PROGRESS_STROKE_SIZE = 10;

  private static final int INNER_CIRCLE_PADDING = 30;

  private static final int MAX_DONE_BG_OFFSET = 800;

  private static final int MAX_DONE_IMG_OFFSET = 400;

  private float currentDoneBgOffset = MAX_DONE_BG_OFFSET;

  private float currentCheckmarkOffset = MAX_DONE_IMG_OFFSET;

  private Paint progressPaint;

  private Paint doneBgPaint;

  private Paint maskPaint;

  private RectF progressBounds;

  private Bitmap checkmarkBitmap;

  private Bitmap innerCircleMaskBitmap;

  private int checkmarkXPosition = 0;
  private int checkmarkYPosition = 0;

  private Paint checkmarkPaint;

  private Bitmap tempBitmap;

  private Canvas tempCanvas;

  private ObjectAnimator simulateProgressAnimator;

  private ObjectAnimator doneBgAnimator;

  private ObjectAnimator checkmarkAnimator;

  private OnLoadingFinishedListener onLoadingFinishedListener;

  public SendingProgressView(Context context) {
    super(context);
  }

  public void setOnLoadingFinishedListener(
    OnLoadingFinishedListener onLoadingFinishedListener) {
    this.onLoadingFinishedListener = onLoadingFinishedListener;
  }

  private void init() {
    setUpProgressPaint();
    setUpDonePaints();
    setupSimulateProgressAnimator();
    setupDoneAnimators();
  }

  private void setUpProgressPaint() {
    progressPaint = new Paint();
    progressPaint.setAntiAlias(true);
    progressPaint.setStyle(Paint.Style.STROKE);
    progressPaint.setColor(0xffffffff);
    progressPaint.setStrokeWidth(PROGRESS_STROKE_SIZE);
  }

  private void setupSimulateProgressAnimator() {
    simulateProgressAnimator = ObjectAnimator.ofFloat(this, "currentProgress", 0, 100).setDuration(2000);
    simulateProgressAnimator.setInterpolator(new AccelerateInterpolator());
    simulateProgressAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        changeState(STATE_DONE_STARTED);
      }
    });
  }

  private void setCurrentProgress(float currentProgress) {
    this.currentProgress = currentProgress;
    postInvalidate();
  }

  private void setCurrentDoneBgOffset(float currentDoneBgOffset) {
    this.currentDoneBgOffset = currentDoneBgOffset;
    postInvalidate();
  }

  public void setCurrentCheckmarkOffset(float currentCheckmarkOffset) {
    this.currentCheckmarkOffset = currentCheckmarkOffset;
    postInvalidate();
  }

  private void simulateProgress() {
    changeState(STATE_PROGRESS_STARTED);
  }

  private void setUpDonePaints() {
    doneBgPaint = new Paint();
    doneBgPaint.setAntiAlias(true);
    doneBgPaint.setStyle(Paint.Style.FILL);
    doneBgPaint.setColor(0xff39cb72);

    checkmarkPaint = new Paint();

    maskPaint = new Paint();
    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
  }

  private void setupDoneAnimators() {
    doneBgAnimator = ObjectAnimator.ofFloat(this, "currentDoneBgOffset", MAX_DONE_BG_OFFSET, 0).setDuration(300);
    doneBgAnimator.setInterpolator(new DecelerateInterpolator());

    checkmarkAnimator = ObjectAnimator.ofFloat(this, "currentCheckmarkOffset", MAX_DONE_IMG_OFFSET, 0).setDuration(300);
    checkmarkAnimator.setInterpolator(new OvershootInterpolator());
    checkmarkAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        changeState(STATE_FINISHED);
      }
    });
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updateProgressBounds();
    setUpCheckmarkBitmap();
    setUpDoneMaskBitmap();
    resetTempCanvas();
  }

  private void updateProgressBounds() {
    progressBounds = new RectF(
      PROGRESS_STROKE_SIZE,
      PROGRESS_STROKE_SIZE,
      getWidth() - PROGRESS_STROKE_SIZE,
      getWidth() - PROGRESS_STROKE_SIZE
    );
  }

  private void setUpCheckmarkBitmap() {
    checkmarkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_done_white_48dp);
    checkmarkXPosition = getWidth() / 2 - checkmarkBitmap.getWidth() / 2;
    checkmarkYPosition = getWidth() / 2 - checkmarkBitmap.getHeight() / 2;
  }


  private void setUpDoneMaskBitmap() {
    innerCircleMaskBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
    Canvas srcCanvas = new Canvas(innerCircleMaskBitmap);
    srcCanvas.drawCircle(
      getWidth() / 2,
      getWidth() / 2,
      getWidth() / 2 - INNER_CIRCLE_PADDING,
      new Paint()
    );
  }

  private void resetTempCanvas() {
    tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
    tempCanvas = new Canvas(tempBitmap);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (state == STATE_PROGRESS_STARTED) {
      drawArcForCurrentProgress();
    } else if (state == STATE_DONE_STARTED) {
      drawFrameForDoneAnimation();
      postInvalidate();
    } else if (state == STATE_FINISHED) {
      drawFinishedState();
    }

    canvas.drawBitmap(tempBitmap, 0, 0, null);
  }

  private void drawArcForCurrentProgress() {
    tempCanvas.drawArc(progressBounds, -90f, 360 * currentProgress / 100, false, progressPaint);
  }

  private void drawFrameForDoneAnimation() {
    tempCanvas.drawCircle(getWidth() / 2, getWidth() / 2 + currentDoneBgOffset, getWidth() / 2 - INNER_CIRCLE_PADDING, doneBgPaint);
    tempCanvas.drawBitmap(checkmarkBitmap, checkmarkXPosition, checkmarkYPosition + currentCheckmarkOffset, checkmarkPaint);
    tempCanvas.drawBitmap(innerCircleMaskBitmap, 0, 0, maskPaint);
    tempCanvas.drawArc(progressBounds, 0, 360f, false, progressPaint);
  }

  private void drawFinishedState() {
    tempCanvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - INNER_CIRCLE_PADDING, doneBgPaint);
    tempCanvas.drawBitmap(checkmarkBitmap, checkmarkXPosition, checkmarkYPosition, checkmarkPaint);
    tempCanvas.drawArc(progressBounds, 0, 360f, false, progressPaint);
  }

  private void changeState(@State int state) {
    if (this.state == state) return;

    tempBitmap.recycle();
    resetTempCanvas();

    this.state = state;
    if (state == STATE_PROGRESS_STARTED) {
      setCurrentProgress(0);
      simulateProgressAnimator.start();
    } else if (state == STATE_DONE_STARTED) {
      setCurrentDoneBgOffset(MAX_DONE_BG_OFFSET);
      setCurrentCheckmarkOffset(MAX_DONE_IMG_OFFSET);
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playSequentially(doneBgAnimator, checkmarkAnimator);
      animatorSet.start();
    } else if (state == STATE_FINISHED) {
      if (onLoadingFinishedListener != null) {
        onLoadingFinishedListener.onLoadingFinished();
      }
    }

  }

  public interface OnLoadingFinishedListener {
    void onLoadingFinished();
  }
}