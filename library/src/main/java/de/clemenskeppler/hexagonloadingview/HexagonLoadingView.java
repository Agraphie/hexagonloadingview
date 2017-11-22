package de.clemenskeppler.hexagonloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Agraphie on 01.06.2016.
 * An animated view which can be display if something is loaded in the background.
 * This class can be copied as is and used.
 */
public class HexagonLoadingView extends View {
  public static final int NUMBER_OF_HEXAGONS = 7;
  /**
   * Constants for changing the number of appearance/disappearance of the hexagons.
   * {@link HexagonLoadingView#HEXAGON_UPPER_LEFT_APPEARANCE_POSITION} = 0 means the hexagon will
   * start appearing in the upper left corner.
   * {@link HexagonLoadingView#HEXAGON_MIDDLE_MIDDLE_APPEARANCE_POSITION} = 6 means this hexagon
   * will appear last and disappear first.
   */
  public static final int HEXAGON_UPPER_LEFT_APPEARANCE_POSITION = 0;
  public static final int HEXAGON_UPPER_RIGHT_APPEARANCE_POSITION = 1;
  public static final int HEXAGON_MIDDLE_LEFT_APPEARANCE_POSITION = 5;
  public static final int HEXAGON_MIDDLE_MIDDLE_APPEARANCE_POSITION = 6;
  public static final int HEXAGON_MIDDLE_RIGHT_APPEARANCE_POSITION = 2;
  public static final int HEXAGON_LOWER_RIGHT_APPEARANCE_POSITION = 3;
  public static final int HEXAGON_LOWER_LEFT_APPEARANCE_POSITION = 4;
  /**
   * Increase this for a slower animation i.e. decrease this for a faster animation.
   */
  public static final int APPEARANCE_SPEED_COEFFICIENT = 20;

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
   * Field for identifying if hexagons should be currently set to the background colour or to their
   * given colour.
   */
  private boolean displayHexagons = true;

  private float mRadiusStep;
  private float[] mHexagonRadius;

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
   * Method for calculating the hexagons, taking into account the current radius of the specified hexagon.
   */
  private void calculateHexagons() {
    mHexagonUpperLeft = calculatePath((int) -(mRadius), (int) -(mRadius * 1.7), mHexagonRadius[HEXAGON_UPPER_LEFT_APPEARANCE_POSITION]);
    mHexagonUpperRight = calculatePath((int) (mRadius), ((int) -(mRadius * 1.7)), mHexagonRadius[HEXAGON_UPPER_RIGHT_APPEARANCE_POSITION]);
    mHexagonMiddleLeft = calculatePath((int) (-1.95 * mRadius), 0, mHexagonRadius[HEXAGON_MIDDLE_LEFT_APPEARANCE_POSITION]);
    mHexagonMiddleMiddle = calculatePath(0, 0, mHexagonRadius[HEXAGON_MIDDLE_MIDDLE_APPEARANCE_POSITION]);
    mHexagonMiddleRight = calculatePath((int) (1.95 * mRadius), 0, mHexagonRadius[HEXAGON_MIDDLE_RIGHT_APPEARANCE_POSITION]);
    mHexagonLowerLeft = calculatePath((int) -(mRadius), (int) (mRadius * 1.7), mHexagonRadius[HEXAGON_LOWER_LEFT_APPEARANCE_POSITION]);
    mHexagonLowerRight = calculatePath((int) (mRadius), (int) (mRadius * 1.7), mHexagonRadius[HEXAGON_LOWER_RIGHT_APPEARANCE_POSITION]);
  }

  @Override
  public void onDraw(Canvas c) {
    //Check if this is the first load, if so don't do anything and display only the background
    //for a while
    calculateHexagons();

    //Count the hexagons up i.e. down i.e. make them appear or disappear.
    //Increase always only one hexagon at a time which has not been fully drawn yet.
    //Also check which hexagons have been completed.
    int completedHexagons = 0;
    if (displayHexagons) {
      for (int i = 0; i < mHexagonRadius.length; i++) {
        if (mHexagonRadius[i] < mRadius) {
          mHexagonRadius[i] += mRadiusStep;
          break;
        }
        completedHexagons++;
      }
    } else {
      for (int i = 0; i < mHexagonRadius.length; i++) {
        if (mHexagonRadius[i] > 0) {
          mHexagonRadius[i] = (mHexagonRadius[i] + (mRadiusStep * -1) < 0) ? 0 : mHexagonRadius[i] + (mRadiusStep * -1);
          break;
        }
        completedHexagons++;
      }
    }

    checkDrawingMode(completedHexagons);

    //Now draw our hexagons
    c.drawPath(mHexagonUpperLeft, mHexagonPaintUpperLeft);
    c.drawPath(mHexagonUpperRight, mHexagonPaintUpperRight);
    c.drawPath(mHexagonMiddleRight, mHexagonPaintMiddleRight);
    c.drawPath(mHexagonLowerRight, mHexagonPaintLowerRight);
    c.drawPath(mHexagonLowerLeft, mHexagonPaintLowerLeft);
    c.drawPath(mHexagonMiddleLeft, mHexagonPaintMiddleLeft);
    c.drawPath(mHexagonMiddleMiddle, mHexagonPaintMiddleMiddle);
  }

  /**
   * Method for checking how many hexagons are completed in their drawing.
   * If all hexagons are completed (i.e. all appeared or disappeared), invert the drawing mode
   * to the opposite of what it was.
   */
  private void checkDrawingMode(int completedHexagons) {
    if (completedHexagons == NUMBER_OF_HEXAGONS) {
      displayHexagons = !displayHexagons;
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    mHexagonRadius = new float[NUMBER_OF_HEXAGONS];
    mHexagonRadius[HEXAGON_UPPER_LEFT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_UPPER_RIGHT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_MIDDLE_RIGHT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_LOWER_RIGHT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_LOWER_LEFT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_MIDDLE_LEFT_APPEARANCE_POSITION] = 0;
    mHexagonRadius[HEXAGON_MIDDLE_MIDDLE_APPEARANCE_POSITION] = 0;
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mWidth = MeasureSpec.getSize(widthMeasureSpec);
    mHeight = MeasureSpec.getSize(heightMeasureSpec);
    mRadius = mHeight / 6;
    mRadiusStep = mRadius / APPEARANCE_SPEED_COEFFICIENT;
  }


  /**
   * Calculate the path for a hexagon.
   *
   * @param xCenterScale The offset in the x direction.
   * @param yCenterScale The offset in the y direction.
   * @return The calculated hexagon as {@link Path} object.
   */
  private Path calculatePath(int xCenterScale, int yCenterScale, float radius) {
    float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
    float centerX = (mWidth / 2) + xCenterScale;
    float centerY = (mHeight / 2) + yCenterScale;
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
