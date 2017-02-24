package com.pa.landing;

import android.widget.ImageView;

import com.pa.pojo.PackageListItem;
import com.pa.pojo.ServiceCategory;

import java.util.ArrayList;

/**
 * Created by lenovo on 06-Feb-17.
 */

public class LandingDataType {

    private String viewType;


    public ArrayList<PackageListItem> homeBanner;
    public ArrayList<ServiceCategory> homeGrid;
    public ImageView empty;

    public LandingDataType() {

    }

    public void setViewType(String type){
        this.viewType = type;
    }

    public String getViewType(){
        return viewType;
    }

}
