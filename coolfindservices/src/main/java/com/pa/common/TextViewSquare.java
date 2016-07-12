package com.pa.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TextViewSquare extends TextView
{
	private String		xmlns	= "http://schemas.android.com/apk/res/android";
	private Drawable	logo;
	private Context		ctx;

	public TextViewSquare(Context context)
	{
		super(context);
		ctx = context;


	}

	public TextViewSquare(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		ctx = context;


	}

	public TextViewSquare(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		ctx = context;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{	
		int width =0;
		int height=0;

		try{
		width= MeasureSpec.getSize(widthMeasureSpec);
		}
		catch(Exception e){
			Log.e("error",e.toString());
		}
		setMeasuredDimension(width, width);
	}

	

}