package com.pa.common;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class AccordionGroup {

    private LinearLayout accordionGroupView;
    private ArrayList<LinearLayout> children;

    public AccordionGroup(LinearLayout accordionGroupView) {
        this.accordionGroupView = accordionGroupView;
        children = new ArrayList<>();
    }

    public void setup() {
        int count = accordionGroupView.getChildCount();
        LinearLayout child;
        for(int i=0; i<count; i++) {
            child = (LinearLayout) accordionGroupView.getChildAt(i);
            children.add(child);

            LinearLayout headerView = (LinearLayout) child.getChildAt(0);
            headerView.setTag(i);
            final View containerView = child.getChildAt(1);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (containerView.getVisibility() == View.GONE) {
                        closeAll();
                        open((int) v.getTag());
                    } else
                        close((int) v.getTag());
                }
            });
        }
    }

    public void disable() {
        for (int i = 0; i < children.size(); i++)
            children.get(i).getChildAt(0).setOnClickListener(null);
    }

    private void open(int index) {
        Log.d("accordion", "open " + index);
        LinearLayout child = children.get(index);

        // Show tick icon
        ((LinearLayout)child.getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);

        // Show content
        child.getChildAt(1).setVisibility(View.VISIBLE);
    }

    private void close(int index) {
        Log.d("accordion", "close " + index);
        LinearLayout child = children.get(index);

        // Hide tick icon
        ((LinearLayout)child.getChildAt(0)).getChildAt(0).setVisibility(View.INVISIBLE);

        // Hide content
        child.getChildAt(1).setVisibility(View.GONE);
    }

    private void closeAll() {
        for (int i = 0; i < children.size(); i++)
            close(i);
    }
}
