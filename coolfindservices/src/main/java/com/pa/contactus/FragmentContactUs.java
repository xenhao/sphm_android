package com.pa.contactus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.landing.ActivityLanding;
import com.coolfindservices.androidconsumer.R;

public class FragmentContactUs  extends MyFragment {

    private OnFragmentChangeListener mListener;

    public FragmentContactUs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, null);
        v.findViewById(R.id.btnMenu).setOnClickListener(fragmentListener);
        v.findViewById(R.id.btnContactUs).setOnClickListener(fragmentListener);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        analytic.trackScreen("Contact Us");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnFragmentChangeListener){
            mListener=(OnFragmentChangeListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    View.OnClickListener fragmentListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btnContactUs:
                    showSpinnerSelection(new String[]{"Malaysia", "Singapore"}, "Contact Us", new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            generalDialog.dismiss();

                            String message = "";
                            switch (position) {
                                case 0:
                                    message = "+60 3 2630 8430";
                                    break;
                                case 1:
                                    message = "+65 68505451";
                                    break;
                            }

                            // Use the Builder class for convenient dialog construction
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(message)
                                    .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            switch (position) {
                                                case 0:
                                                    dialNumber("tel:+60326308430");
                                                    break;
                                                case 1:
                                                    dialNumber("tel:+6568505451");
                                                    break;
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    break;

                case R.id.btnMenu:
                    ActivityLanding parent = (ActivityLanding) getActivity();
                    parent.menuClick();

                    break;


            }
        }
    };
}
