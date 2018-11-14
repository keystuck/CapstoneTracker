package com.example.emily.simplehealthtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


//from
// https://blog.davidmedenjak.com/android/2015/11/10/recyclerview-with-decorations-basic-guide.html
public class TaskDecoration extends RecyclerView.ItemDecoration {

    private final Paint mPaint;


    public TaskDecoration(Context context){
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        mPaint.setStrokeWidth(4);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        final int position = params.getViewAdapterPosition();

        if (position < state.getItemCount()) {
            outRect.set(0, 0, 0, (int) mPaint.getStrokeWidth());
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int offset = (int) (mPaint.getStrokeWidth()/2);

        for (int i = 0; i < parent.getChildCount(); i++){
            final View view = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            final int position = params.getViewAdapterPosition();
            if (position < state.getItemCount()){
                c.drawLine(view.getLeft(), view.getBottom() + offset, view.getRight(), view.getBottom() + offset, mPaint);
            }
        }
    }
}
