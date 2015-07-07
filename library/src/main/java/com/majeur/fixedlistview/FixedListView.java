package com.majeur.fixedlistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FixedListView extends ViewGroup implements GestureDetector.OnGestureListener {

    public static final int DIVIDERS_NONE = 0;
    public static final int DIVIDERS_MIDDLE = 12;
    public static final int DIVIDERS_END = 23;
    public static final int DIVIDERS_BEGINNING = 34;

    private int DEFAULT_DIVIDER_HEIGHT, DEFAULT_DIVIDER_COLOR;

    private int mDividersType;
    private Paint mDividersPaint;
    private FixedListViewAdapter mAdapter;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private GestureDetector mGestureDetector;

    public FixedListView(Context context) {
        super(context);
        init(context, null);
    }

    public FixedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FixedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        DEFAULT_DIVIDER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, displayMetrics);
        DEFAULT_DIVIDER_COLOR = Color.parseColor("#B6B6B6");

        mDividersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedListView);
            mDividersPaint.setColor(typedArray.getColor(R.styleable.FixedListView_dividerColor, DEFAULT_DIVIDER_COLOR));
            mDividersPaint.setStrokeWidth(typedArray.getDimension(R.styleable.FixedListView_dividerHeight, DEFAULT_DIVIDER_HEIGHT));
            mDividersType = typedArray.getInt(R.styleable.FixedListView_dividersType, DIVIDERS_NONE);
            typedArray.recycle();
        } else {
            mDividersPaint.setColor(DEFAULT_DIVIDER_COLOR);
            mDividersPaint.setStrokeWidth(DEFAULT_DIVIDER_HEIGHT);
            mDividersType = DIVIDERS_NONE;
        }

        mGestureDetector = new GestureDetector(context, this);

        // Used for preview in IDE
        if (isInEditMode())
            setAdapter(new FixedListViewAdapter() {
                @Override
                public int getCount() {
                    return 5;
                }

                @Override
                public View getView(@NonNull FixedListView parent, int position) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                    TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                    textView1.setText("Item " + position);
                    TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                    textView2.setText("Sub item " + position);
                    return view;
                }
            });
    }

    /**
     * Setting the adapter of the list view.
     *
     * @param adapter Adapter to set
     */
    public void setAdapter(FixedListViewAdapter adapter) {
        if (adapter == null) {
            removeAllViews();
        } else {
            mAdapter = adapter;
            loadChildViews();
        }
    }

    /**
     * Reload all views from the adapter
     */
    public void reloadViews() {
        loadChildViews();
    }

    /**
     * Set the item click listener. If set, child will never receive touch events.
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Set the item long click listener. If set, child will never receive touch events.
     *
     * @param listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    private void loadChildViews() {
        removeAllViews();

        for (int i = 0; i < mAdapter.getCount(); i++)
            addView(mAdapter.getView(this, i));
    }

    @Override
    protected void onLayout(boolean b, int i1, int i2, int i3, int i4) {
        if (mAdapter == null || mAdapter.getCount() == 0)
            return;

        // We increment yOffset by each child measured height to layout them as a list.
        int yOffset = getPaddingTop();
        int left = getSupportPaddingStart();
        int right = getWidth() - left - getSupportPaddingEnd();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childHeight = child.getMeasuredHeight();

            child.layout(left, yOffset, right, yOffset + childHeight);

            yOffset += childHeight;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSupportPaddingStart() {
        return Utils.isApi17() ? getPaddingStart() : getPaddingLeft();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSupportPaddingEnd() {
        return Utils.isApi17() ? getPaddingEnd() : getPaddingRight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth = 0, finalHeight = 0;

        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                // No limit, we take the width of the screen arbitrary.
                finalWidth = getResources().getDisplayMetrics().widthPixels;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                //TODO Handle AT_MOST in the same way used for height.
                finalWidth = widthSize;
                break;
        }

        int childWidth = finalWidth - getSupportPaddingEnd() - getSupportPaddingStart();
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);

        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                // No limit, we measure children with UNSPECIFIED and set total plus paddings to measured height.
                finalHeight = getUnspecifiedTotalChildHeight(childWidthMeasureSpec) + getYPaddings();
                break;
            case MeasureSpec.AT_MOST:
                int unspecHeight = getUnspecifiedTotalChildHeight(childWidthMeasureSpec) + getYPaddings();

                // We have to be under the given height
                // If children total height with UNSPECIFIED is lower than given height, we keep it,
                // else we measure all children to fit given height.
                if (unspecHeight < heightSize) {
                    finalHeight = unspecHeight;
                } else {
                    finalHeight = heightSize;
                    measureChildEqually(childWidthMeasureSpec, finalHeight - getYPaddings());
                }
                break;
            case MeasureSpec.EXACTLY:
                // No choice, we measure all children equally to match the given height.
                finalHeight = heightSize;
                measureChildEqually(childWidthMeasureSpec, finalHeight - getYPaddings());
                break;
        }

        setMeasuredDimension(finalWidth, finalHeight);
    }

    /**
     * Returns height of all children in case of unlimited height.
     */
    private int getUnspecifiedTotalChildHeight(int childWidthMeasureSpec) {
        int childHeightUnspecifiedMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        measureChildren(childWidthMeasureSpec, childHeightUnspecifiedMeasureSpec);

        int height = 0;
        for (int i = 0; i < getChildCount(); i++)
            height += getChildAt(i).getMeasuredHeight();

        return height;
    }

    /**
     * Measure children with same height for a given total height
     */
    private void measureChildEqually(int childWidthMeasureSpec, int heightSize) {
        int childHeight = heightSize / mAdapter.getCount();

        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

        measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getYPaddings() {
        return getPaddingBottom() + getPaddingTop();
    }

    public void setDividerHeight(float width) {
        mDividersPaint.setStrokeWidth(width);
    }

    public void setDividerColor(int color) {
        mDividersPaint.setColor(color);
    }

    public void setDividersType(int type) {
        mDividersType = type;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = getSupportPaddingStart();
        int right = getWidth() - left - getSupportPaddingEnd();

        if (Utils.checkFlag(mDividersType, DIVIDERS_MIDDLE)) {
            int yOffset = getPaddingTop();
            for (int i = 0; i < getChildCount() - 1; i++) {

                yOffset += getChildAt(i).getMeasuredHeight();

                canvas.drawLine(left, yOffset, right, yOffset, mDividersPaint);
            }
        }
        if (Utils.checkFlag(mDividersType, DIVIDERS_BEGINNING)) {
            float yOffset = mDividersPaint.getStrokeWidth() / 2 + getPaddingTop();

            canvas.drawLine(left, yOffset, right, yOffset, mDividersPaint);
        }
        if (Utils.checkFlag(mDividersType, DIVIDERS_END)) {
            float yOffset = getHeight() - mDividersPaint.getStrokeWidth() / 2 - getPaddingBottom();

            canvas.drawLine(left, yOffset, right, yOffset, mDividersPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mItemClickListener != null || mItemLongClickListener != null;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private int getPositionForY(int y) {
        int yOffset = 0;
        for (int i = 0; i < getChildCount(); i++) {
            yOffset += getChildAt(i).getMeasuredHeight();
            if (y < yOffset)
                return i;
        }
        return -1;
    }

    private void performItemClick(MotionEvent event) {
        int position = getPositionForY((int) event.getY());
        if (position != -1 && mItemClickListener != null)
            mItemClickListener.onItemClick(this, getChildAt(position), position);
    }

    private void performLongItemClick(MotionEvent event) {
        int position = getPositionForY((int) event.getY());
        if (position != -1 && mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(this, getChildAt(position), position))
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        performItemClick(event);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        performLongItemClick(event);
    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event2, float v, float v2) {
        return false;
    }
}
