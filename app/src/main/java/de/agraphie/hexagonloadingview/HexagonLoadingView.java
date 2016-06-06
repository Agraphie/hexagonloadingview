package de.agraphie.hexagonloadingview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.LinkedHashSet;

/**
 * Created by Agraphie on 01.06.2016.
 * An animated view which can be display if something is loaded in the background.
 * This class can be copied as is and used.
 */
public class HexagonLoadingView extends View {
  /**
   * The radius of each hexagon.
   */
  private float mRadius;

  /**
   * The width and height of each hexagon.
   */
  private float mWidth, mHeight;

  /**
   * The various hexagons as {@link Path} objects.
   */
  private Path mHexagonUpperRight;
  private Path mHexagonMiddleRight;
  private Path mHexagonLowerRight;
  private Path mHexagonLowerLeft;
  private Path mHexagonMiddleLeft;
  private Path mHexagonUpperLeft;
  private Path mHexagonMiddleMiddle;

  /**
   * The {@link Paint} objects for each hexagon. Every hexagon can have its own colour.
   */
  private Paint mHexagonPaintUpperRight = new Paint();
  private Paint mHexagonPaintMiddleRight = new Paint();
  private Paint mHexagonPaintLowerRight = new Paint();
  private Paint mHexagonPaintLowerLeft = new Paint();
  private Paint mHexagonPaintMiddleLeft = new Paint();
  private Paint mHexagonPaintUpperLeft = new Paint();
  private Paint mHexagonPaintMiddleMiddle = new Paint();

  /**
   * {@link LinkedHashSet} for storing the hexagon paints so they can be changed at every iteration.
   * The order is important in which the hexagons get inserted.
   */
  private LinkedHashSet<Paint> mHexagonPaints = new LinkedHashSet<>();

  /**
   * Count of how many hexagon objects do not have the background colour.
   */
  private int mInverted = 7;

  /**
   * Field for identifying if hexagons should be currently set to the background colour or to their
   * given colour.
   */
  private boolean mRemoveBackgroundFilter;

  /**
   * Necessary objects for determining the background colour of the current view.
   */
  private int mBackgroundColour;
  private ColorDrawable mBackgroundColourDrawable = (ColorDrawable) this.getBackground();
  private TypedValue mTypedValue = new TypedValue();
  private Resources.Theme mAppTheme = getContext().getTheme();

  /**
   * The animation time i.e. how long the invalidation of the view will be delayed.
   */
  private long mAnimationTime = 300;

  /**
   * Using this so that on first load the hexagons are invisible. Afterwards the normal cycle commences.
   */
  private boolean mFirstLoad = true;

  /**
   * Filter for setting the hexagons to the background colour.
   */
  private PorterDuffColorFilter mBackgroundColourFilter = new PorterDuffColorFilter(mBackgroundColour, PorterDuff.Mode.MULTIPLY);

  public HexagonLoadingView(Context context) {
    super(context);
  }

  public HexagonLoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public HexagonLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * Initialise all hexagons which will make up our big hexagon.
   */
  private void calculateHexagons() {
    mHexagonUpperLeft = calculatePath((int) -(mRadius), (int) -(mRadius * 1.7));
    mHexagonUpperRight = calculatePath((int) (mRadius), ((int) -(mRadius * 1.7)));
    mHexagonMiddleLeft = calculatePath((int) (-1.95 * mRadius), 0);
    mHexagonMiddleMiddle = calculatePath(0, 0);
    mHexagonMiddleRight = calculatePath((int) (1.95 * mRadius), 0);
    mHexagonLowerLeft = calculatePath((int) -(mRadius), (int) (mRadius * 1.7));
    mHexagonLowerRight = calculatePath((int) (mRadius), (int) (mRadius * 1.7));
  }

  @Override
  public void onDraw(Canvas c) {
    //Check if this is the first load, if so don't do anything and display only the background
    //for a while
    if (!mFirstLoad) {
      //Count the hexagons up i.e. down
      if (!mRemoveBackgroundFilter) {
        for (Paint paint : mHexagonPaints) {
          if (paint.getColorFilter() == null) {
            paint.setColorFilter(mBackgroundColourFilter);
            mInverted++;
            break;
          }
        }
      } else {
        for (Paint paint : mHexagonPaints) {
          if (paint.getColorFilter() != null) {
            paint.setColorFilter(null);
            mInverted--;
            break;
          }
        }
      }

      //if we inverted all hexagons, set the upper right one immediately to be visible
      //and start from the beginning
      if (mInverted == 7) {
        mRemoveBackgroundFilter = true;
        mInverted--;
        mHexagonPaintUpperLeft.setColorFilter(null);
      } else if (mInverted == 0) {
        mRemoveBackgroundFilter = false;
      }
    }

    //Now draw our hexagons
    c.drawPath(mHexagonUpperLeft, mHexagonPaintUpperLeft);
    c.drawPath(mHexagonUpperRight, mHexagonPaintUpperRight);
    c.drawPath(mHexagonMiddleRight, mHexagonPaintMiddleRight);
    c.drawPath(mHexagonLowerRight, mHexagonPaintLowerRight);
    c.drawPath(mHexagonLowerLeft, mHexagonPaintLowerLeft);
    c.drawPath(mHexagonMiddleLeft, mHexagonPaintMiddleLeft);
    c.drawPath(mHexagonMiddleMiddle, mHexagonPaintMiddleMiddle);

    //invalidate our view after a specific time to create an animation
    postInvalidateDelayed(mAnimationTime);

    //Displaying only the background for a while is not needed anymore
    mFirstLoad = false;
  }


  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    //Fill our hash set up. The order here is important as it is the order in which the
    //hexagons will be changed.
    mHexagonPaints = new LinkedHashSet<>();
    mHexagonPaints.add(mHexagonPaintUpperLeft);
    mHexagonPaints.add(mHexagonPaintUpperRight);
    mHexagonPaints.add(mHexagonPaintMiddleRight);
    mHexagonPaints.add(mHexagonPaintLowerRight);
    mHexagonPaints.add(mHexagonPaintLowerLeft);
    mHexagonPaints.add(mHexagonPaintMiddleLeft);
    mHexagonPaints.add(mHexagonPaintMiddleMiddle);

    //Initially set all hexagons to be invisible
    for (Paint paint : mHexagonPaints) {
      if (paint.getColorFilter() == null) {
        paint.setColorFilter(mBackgroundColourFilter);
      }
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mWidth = MeasureSpec.getSize(widthMeasureSpec);
    mHeight = MeasureSpec.getSize(heightMeasureSpec);
    mRadius = mHeight / 6;
    calculateHexagons();
    if (mBackgroundColourDrawable != null) {
      mBackgroundColour = mBackgroundColourDrawable.getColor();
    } else {
      mAppTheme.resolveAttribute(R.attr.color, mTypedValue, true);
      mBackgroundColour = mTypedValue.data;
    }
  }


  /**
   * Calculate the path for a hexagon.
   *
   * @param xCenterScale The offset in the x direction.
   * @param yCenterScale The offset in the y direction.
   * @return The calculated hexagon as {@link Path} object.
   */
  private Path calculatePath(int xCenterScale, int yCenterScale) {
    float triangleHeight = (float) (Math.sqrt(3) * mRadius / 2);
    float centerX = (mWidth / 2) + xCenterScale;
    float centerY = (mHeight / 2) + yCenterScale;
    Path hexagonPath = new Path();

    hexagonPath.moveTo(centerX, centerY + mRadius);
    hexagonPath.lineTo(centerX - triangleHeight, centerY + mRadius / 2);
    hexagonPath.lineTo(centerX - triangleHeight, centerY - mRadius / 2);
    hexagonPath.lineTo(centerX, centerY - mRadius);
    hexagonPath.lineTo(centerX + triangleHeight, centerY - mRadius / 2);
    hexagonPath.lineTo(centerX + triangleHeight, centerY + mRadius / 2);
    hexagonPath.moveTo(centerX, centerY + mRadius);

    invalidate();

    return hexagonPath;
  }

  /**
   * Call this method to give the hexagons rainbow colours.
   */
  public void activateRainbowColours() {
    mHexagonPaintUpperRight.setColor(Color.RED);
    mHexagonPaintMiddleRight.setColor(Color.parseColor("#FFA500"));
    mHexagonPaintLowerRight.setColor(Color.YELLOW);
    mHexagonPaintLowerLeft.setColor(Color.GREEN);
    mHexagonPaintMiddleLeft.setColor(Color.BLUE);
    mHexagonPaintUpperLeft.setColor(Color.parseColor("#4B0082"));
    mHexagonPaintMiddleMiddle.setColor(Color.parseColor("#EE82EE"));
  }
}
