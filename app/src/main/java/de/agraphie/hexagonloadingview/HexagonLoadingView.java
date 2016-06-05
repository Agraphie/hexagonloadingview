package de.agraphie.hexagonloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.LinkedHashSet;

/**
 * Created by Agraphie on 01.06.2016.
 * An animated view which can be display if something is loaded in the background.
 * This class can be copied as is and used.
 */
public class HexagonLoadingView extends View{
  private float radius;
  private float width, height;
  private Path hexagonUpperRight;
  private Path hexagonMiddleRight;
  private Path hexagonLowerRight;
  private Path hexagonLowerLeft;
  private Path hexagonMiddleLeft;
  private Path hexagonUpperLeft;
  private Path hexagonMiddleMiddle;
  private Paint hexagonPaintUpperRight = new Paint();
  private Paint hexagonPaintMiddleRight = new Paint();
  private Paint hexagonPaintLowerRight = new Paint();
  private Paint hexagonPaintLowerLeft = new Paint();
  private Paint hexagonPaintMiddleLeft = new Paint();
  private Paint hexagonPaintUpperLeft = new Paint();
  private Paint hexagonPaintMiddleMiddle = new Paint();
  private LinkedHashSet<Paint> hexagonPaints = new LinkedHashSet<>();
  private int inverted = 7;
  private boolean invertBack;
  int hexagonColour;
  ColorDrawable backgroundColour = (ColorDrawable) this.getBackground();
  TypedValue typedValue = new TypedValue();
  Resources.Theme theme = getContext().getTheme();
  private long mAnimationTime = 300;
  private boolean firstLoad = true;
  private PorterDuffColorFilter backgroundFilter = new PorterDuffColorFilter(hexagonColour, PorterDuff.Mode.MULTIPLY);

  @Override
  public void setBackgroundColor(int color) {
    super.setBackgroundColor(color);
  }

  public HexagonLoadingView(Context context) {
    super(context);
    init();
  }

  public HexagonLoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HexagonLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  /**
   * Initialise our hexagons which will make up our hexagon
   */
  private void init() {
    hexagonUpperLeft = calculatePath((int) -(radius), (int) -(radius * 1.7));
    hexagonUpperRight = calculatePath((int) (radius), ((int) -(radius * 1.7)));
    hexagonMiddleLeft = calculatePath((int) (-1.95 * radius), 0);
    hexagonMiddleMiddle = calculatePath(0, 0);
    hexagonMiddleRight = calculatePath((int) (1.95 * radius), 0);
    hexagonLowerLeft = calculatePath((int) -(radius), (int) (radius * 1.7));
    hexagonLowerRight = calculatePath((int) (radius), (int) (radius * 1.7));
  }

  @Override
  public void onDraw(Canvas c) {
    //Check if this is the first load, if so don't do anything and display only the background
    //for a while
    if(!firstLoad) {
      //Count the hexagons up i.e. down
      if (!invertBack) {
        for (Paint paint : hexagonPaints) {
          if (paint.getColorFilter() == null) {
            paint.setColorFilter(backgroundFilter);
            inverted++;
            break;
          }
        }
      } else if (invertBack) {
        for (Paint paint : hexagonPaints) {
          if (paint.getColorFilter() != null) {
            paint.setColorFilter(null);
            inverted--;
            break;
          }
        }
      }

      //if we inverted all hexagons, set the upper right one immediately to be visible
      //and start from the beginning
      if(inverted == 7){
        invertBack = true;
        inverted--;
        hexagonPaintUpperRight.setColorFilter(null);
      } else if(inverted == 0){
        invertBack = false;
      }
    }

    //Now draw our hexagons
    c.drawPath(hexagonUpperLeft, hexagonPaintUpperLeft);
    c.drawPath(hexagonUpperRight, hexagonPaintUpperRight);
    c.drawPath(hexagonMiddleRight, hexagonPaintMiddleRight);
    c.drawPath(hexagonLowerRight, hexagonPaintLowerRight);
    c.drawPath(hexagonLowerLeft, hexagonPaintLowerLeft);
    c.drawPath(hexagonMiddleLeft, hexagonPaintMiddleLeft);
    c.drawPath(hexagonMiddleMiddle, hexagonPaintMiddleMiddle);

    //invalidate our view after a specific time to create an animation
    postInvalidateDelayed(mAnimationTime);

    //Displaying only the background for a while is not needed anymore
    firstLoad = false;
  }


  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    //Fill our hash set up. The order here is important!
    hexagonPaints = new LinkedHashSet<>();
    hexagonPaints.add(hexagonPaintUpperRight);
    hexagonPaints.add(hexagonPaintMiddleRight);
    hexagonPaints.add(hexagonPaintLowerRight);
    hexagonPaints.add(hexagonPaintLowerLeft);
    hexagonPaints.add(hexagonPaintMiddleLeft);
    hexagonPaints.add(hexagonPaintUpperLeft);
    hexagonPaints.add(hexagonPaintMiddleMiddle);

    //Initially set all hexagons to be invisible
    for (Paint paint : hexagonPaints) {
      if (paint.getColorFilter() == null) {
        paint.setColorFilter(backgroundFilter);
      }
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    width = MeasureSpec.getSize(widthMeasureSpec);
    height = MeasureSpec.getSize(heightMeasureSpec);
    radius = height / 6;
    init();
    if(backgroundColour != null){
      hexagonColour = backgroundColour.getColor();
    }else{
      theme.resolveAttribute(R.attr.color, typedValue, true);
      hexagonColour = typedValue.data;
    }
  }


  private Path calculatePath(int xCenterScale, int yCenterScale) {
    float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
    float centerX = (width / 2) + xCenterScale;
    float centerY = (height / 2) + yCenterScale;
    Path hexagonPath = new Path();

    hexagonPath.moveTo(centerX, centerY + radius);
    hexagonPath.lineTo(centerX - triangleHeight, centerY + radius / 2);
    hexagonPath.lineTo(centerX - triangleHeight, centerY - radius / 2);
    hexagonPath.lineTo(centerX, centerY - radius);
    hexagonPath.lineTo(centerX + triangleHeight, centerY - radius / 2);
    hexagonPath.lineTo(centerX + triangleHeight, centerY + radius / 2);
    hexagonPath.moveTo(centerX, centerY + radius);

    invalidate();

    return hexagonPath;
  }

  public void activateRainbowColours(){
    hexagonPaintUpperRight.setColor(Color.RED);
    hexagonPaintMiddleRight.setColor(Color.parseColor("#FFA500"));
    hexagonPaintLowerRight.setColor(Color.YELLOW);
    hexagonPaintLowerLeft.setColor(Color.GREEN);
    hexagonPaintMiddleLeft.setColor(Color.BLUE);
    hexagonPaintUpperLeft.setColor(Color.parseColor("#4B0082"));
    hexagonPaintMiddleMiddle.setColor(Color.parseColor("#EE82EE"));
  }

  public long getAnimationTime() {
    return mAnimationTime;
  }

  public void setAnimationTime(long mAnimationTime) {
    this.mAnimationTime = mAnimationTime;
  }
}
