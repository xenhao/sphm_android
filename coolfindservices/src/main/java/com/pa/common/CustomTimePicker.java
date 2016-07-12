package com.pa.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CustomTimePicker extends TimePicker {

	public CustomTimePicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	public CustomTimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	public final static int TIME_PICKER_INTERVAL = 5;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		try {
			Class<?> classForid = Class.forName("com.android.internal.R$id");
			//Field timePickerField = classForid.getField("timePicker");
			//this.timePicker = (TimePicker) findViewById(timePickerField
			//		.getInt(null));
			
			Field field = classForid.getField("minute");

			NumberPicker mMinuteSpinner = (NumberPicker) findViewById(field
					.getInt(null));
			mMinuteSpinner.setMinValue(0);
			mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
			List<String> displayedValues = new ArrayList<String>();
			for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
				displayedValues.add(String.format("%02d", i));
			}
			mMinuteSpinner.setDisplayedValues(displayedValues
					.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}