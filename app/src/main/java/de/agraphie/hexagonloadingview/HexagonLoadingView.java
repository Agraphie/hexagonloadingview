package de.agraphie.hexagonloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Agraphie on 01.06.2016.
 */
public class HexagonLoadingView  extends View {

  private Path hexagonPath;
  private float radius;
  private float width, height;


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


  private void init() {
    hexagonPath = new Path();
  }


  private void calculatePath(int xCenterScale, int yCenterScale) {
    float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
    float centerX = width / 2 + xCenterScale;
    float centerY = height / 2 + yCenterScale;

    hexagonPath.moveTo(centerX, centerY + radius);
    hexagonPath.lineTo(centerX - triangleHeight, centerY + radius / 2);
    hexagonPath.lineTo(centerX - triangleHeight, centerY - radius / 2);
    hexagonPath.lineTo(centerX, centerY - radius);
    hexagonPath.lineTo(centerX + triangleHeight, centerY - radius / 2);
    hexagonPath.lineTo(centerX + triangleHeight, centerY + radius / 2);
    hexagonPath.moveTo(centerX, centerY + radius);

    invalidate();
  }


  @Override
  public void onDraw(Canvas c) {
    float circleSpacing = 4;

//    float radius=(getWidth()-circleSpacing*2)/6;
//    float x = getWidth()/ 2-(radius*2+circleSpacing);
//    float y=getHeight() / 2;
//    for (int i = 0; i < 3; i++) {
//      canvas.save();
//      float translateX=x+(radius*2)*i+circleSpacing*i;
//      canvas.translate(translateX, y);
//      canvas.scale(scaleFloats[i], scaleFloats[i]);
//      paint.setAlpha(alphas[i]);
//      canvas.drawCircle(0, 0, radius, paint);
//      canvas.restore();
//    }

    for (int i = 0; i < 3; i++) {
      calculatePath(50*i, 0);
      c.clipPath(hexagonPath);
      c.drawColor(Color.GREEN);
      c.save();
    }
  }

  // getting the view size and default radius
  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    width = MeasureSpec.getSize(widthMeasureSpec);
    height = MeasureSpec.getSize(heightMeasureSpec);
    radius = 40;
  }
}
