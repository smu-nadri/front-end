package com.example.nadri4_edit1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandGridView extends GridView
{

    public ExpandGridView(Context context)
    {
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
