package com.testing.contactpicker.navigation_drawer_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.testing.contactpicker.Preferences;
import com.testing.contactpicker.R;
import com.testing.contactpicker.lockscreen.LockScreen;
import com.testing.contactpicker.lockscreen.SharedPreferencesUtil;
import com.testing.contactpicker.multipickercontacts.ContactPickerActivity;
import com.testing.contactpicker.multipickercontacts.ContactResult;

import java.util.ArrayList;

/**
 * Created by Glenwin18 on 7/10/2016.
 */
public class SettingsFragment extends Fragment {
    TextView tvPrimary,tvSecondary;
    Button setPrimary,setSecondary;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        setPrimary = (Button) rootView.findViewById(R.id.setPrimaryContact);
        tvPrimary = (TextView)rootView.findViewById(R.id.tvPrimaryContact);
        setSecondary = (Button) rootView.findViewById(R.id.setSecondaryContact);
        tvSecondary = (TextView)rootView.findViewById(R.id.tvSecondaryContact);

        SharedPreferencesUtil.init(getActivity());

        SwitchCompat mSwitch = (SwitchCompat)rootView.findViewById(R.id.setLock);
        mSwitch.setTextOn("yes");
        mSwitch.setTextOff("no");
        boolean lockState = SharedPreferencesUtil.get(LockScreen.ISLOCK);
        if (lockState) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }
        String primaryContact = Preferences.getPrimaryContactNumber(getActivity());
        String secondaryContact = Preferences.getSecondaryContactNumber(getActivity());
        if(primaryContact.isEmpty()){
           tvPrimary.setText("No Primary Contact");
        }
        else{
            String[] contact = Preferences.getPrimaryContactNumber(getActivity()).split("@");
            tvPrimary.setText(contact[0] + "\n" + contact[1].replace(" ",""));
            setPrimary.setText("Change");
        }
        if(secondaryContact.isEmpty()){
            tvSecondary.setText("No Secondary Contact");
        }
        else{
            String[] contact = Preferences.getSecondaryContactNumber(getActivity()).split("@");
            tvSecondary.setText(contact[0] + "\n" + contact[1].replace(" ",""));
            setSecondary.setText("Change");
        }
        setPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ContactPickerActivity.class), 1302);
            }
        });
        setSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ContactPickerActivity.class), 1303);
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtil.setBoolean(LockScreen.ISLOCK, true);
                    LockScreen.getInstance(getActivity()).startLockscreenService();
                } else {
                    SharedPreferencesUtil.setBoolean(LockScreen.ISLOCK, false);
                    LockScreen.getInstance(getActivity()).stopLockscreenService();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1302 && getActivity().RESULT_OK == resultCode) {
            setPrimaryContact((ArrayList<ContactResult>)
                    data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT));
        }
        else if(requestCode == 1303 && getActivity().RESULT_OK == resultCode){
            setSecondaryContact((ArrayList<ContactResult>)
                    data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT));
        }else if(getActivity().RESULT_CANCELED == resultCode) {
            if (data != null && data.hasExtra("error")) {
                //mTvContacts.setText(data.getStringExtra("error"));
            } else {
                //mTvContacts.setText("Contact selection cancelled");
            }
        }
    }
    private void setPrimaryContact(ArrayList<ContactResult> contacts) {
        StringBuilder sb = new StringBuilder();
        for(ContactResult contactResult : contacts) {
            for(ContactResult.ResultItem item : contactResult.getResults()) {
                sb.append(item.getResult());
            }
        }
        Preferences.setPrimaryContactNumber(getActivity(),sb.toString());
        String[] contact = Preferences.getPrimaryContactNumber(getActivity()).split("@");
        tvPrimary.setText(contact[0] + "\n" + contact[1].replace(" ",""));
        setPrimary.setText("Change");

    }
    private void setSecondaryContact(ArrayList<ContactResult> contacts) {
        StringBuilder sb = new StringBuilder();
        for(ContactResult contactResult : contacts) {
            for(ContactResult.ResultItem item : contactResult.getResults()) {
                sb.append(item.getResult());
            }
        }
        Preferences.setSecondaryContactNumber(getActivity(),sb.toString());
        String[] contact = Preferences.getSecondaryContactNumber(getActivity()).split("@");
        tvSecondary.setText(contact[0] + "\n" + contact[1].replace(" ",""));
        setSecondary.setText("Change");
    }
}
