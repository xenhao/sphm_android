package com.pa.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ImageViewSquare extends ImageView
{
	private String		xmlns	= "http://schemas.android.com/apk/res/android";
	private Drawable	logo;
	private Context		ctx;

	public ImageViewSquare(Context context)
	{
		super(context);
		ctx = context;


	}

	public ImageViewSquare(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		ctx = context;

		int mSrc = attrs.getAttributeResourceValue(xmlns, "src", 0);
		setImageResource(mSrc);

	}

	public ImageViewSquare(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		ctx = context;

		int mSrc = attrs.getAttributeResourceValue(xmlns, "src", 0);
		setImageResource(mSrc);
	}
	@Override
	public void setImageDrawable(Drawable drawable) {
		// TODO Auto-generated method stub
		super.setImageDrawable(drawable);
		logo=drawable;
	};
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{	
		int width =0;
		int height=0;

		try{
		width= MeasureSpec.getSize(widthMeasureSpec);
		height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
		}
		catch(Exception e){
			Log.e("error",e.toString());
		}
		setMeasuredDimension(width, width);
	}

	public void setImageResource(int imgRes)
	{
		logo = ctx.getResources().getDrawable(imgRes);
		setImageDrawable(logo);
	}

	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(bm);
		logo = new BitmapDrawable(bm);
		setImageDrawable(logo);
	}
	

}