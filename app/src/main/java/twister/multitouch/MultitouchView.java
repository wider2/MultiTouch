package twister.multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultitouchView extends View {

    private static final int SIZE = 50;

    private SparseArray<PointF> mActivePointers;
    //private SparseArray<PointF> mActiveRings;
    private List<RingPoint> activeRings;

    private Paint mPaint, paint;
    private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW};

    private Paint textPaint;

    private int countTouch;
    private int ringX, ringY, ringRadius;
    private boolean touchRing;
    private boolean stopEvent = false;
    private OnTwisterClickedListener twisterListener;


    public MultitouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setListener(OnTwisterClickedListener twisterListener) {
        this.twisterListener = twisterListener;
    }

    public void setStopEvent(boolean stopEvent) {
        this.stopEvent = stopEvent;
    }

    private void initView() {
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);

        //set default game values
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);


        activeRings = new ArrayList<RingPoint>();
        RingPoint ringPoint = new RingPoint(200, 200, 50, Color.CYAN);
        activeRings.add(ringPoint);

        ringPoint = new RingPoint(300, 500, 60, Color.BLUE);
        activeRings.add(ringPoint);

        ringPoint = new RingPoint(400, 600, 55, Color.RED);
        activeRings.add(ringPoint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!stopEvent) {

            int pointerIndex = event.getActionIndex();

            int pointerId = event.getPointerId(pointerIndex);

            int maskedAction = event.getActionMasked();

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    PointF f = new PointF();
                    f.x = event.getX(pointerIndex);
                    f.y = event.getY(pointerIndex);
                    mActivePointers.put(pointerId, f);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                        PointF point = mActivePointers.get(event.getPointerId(i));
                        if (point != null) {
                            point.x = event.getX(i);
                            point.y = event.getY(i);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointers.remove(pointerId);
                    break;
                }
            }
            invalidate();

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PointF point;
        RingPoint ringPoint;

        touchRing = false;
        ringRadius = SIZE;
        countTouch = 0;

        for (int r = 0; r < activeRings.size(); r++) {
            ringPoint = activeRings.get(r);
            if (ringPoint != null) {
                paint.setColor(ringPoint.getColor());
                canvas.drawCircle(ringPoint.getX(), ringPoint.getY(), ringPoint.getRadius(), paint);
            }
        }

        // draw all pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            point = mActivePointers.valueAt(i);
            if (point != null) {
                mPaint.setColor(colors[i % 9]);
                canvas.drawCircle(point.x, point.y, SIZE, mPaint);

                for (int r = 0; r < activeRings.size(); r++) {
                    ringPoint = activeRings.get(r);
                    if (ringPoint != null) {
                        ringX = ringPoint.getX();
                        ringY = ringPoint.getY();
                        ringRadius = ringPoint.getRadius();
                        if ((Math.abs(ringX - point.x) <= ringRadius - 1) && (Math.abs(ringY - point.y) <= ringRadius - 1)) {
                            touchRing = true;
                            countTouch += 1;
                        }
                    }
                }
            }
        }
        //canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40, textPaint);
        canvas.drawText("Total pointers: " + mActivePointers.size() + "\n\rtouchRing: " + touchRing + "; countTouch: " + countTouch, 10, 40, textPaint);

        if (activeRings.size() == countTouch) {
            twisterListener.onGameRequestClicked(countTouch);
        }
    }
}