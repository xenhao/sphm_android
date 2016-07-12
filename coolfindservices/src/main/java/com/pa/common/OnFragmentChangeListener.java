package com.pa.common;

import android.support.v4.app.Fragment;

public interface OnFragmentChangeListener {
	public void doFragmentChange(Fragment f,boolean history,String title);
	public void doLogout();

}
