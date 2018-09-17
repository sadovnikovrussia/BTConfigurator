package tech.sadovnikov.bt_configurator;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BluetoothFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "BluetoothFragment";
    String mKeySwitchBt = "mSwitchBt";

    Switch mSwitchBt;
    ListView mLvBtDevices;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothFragment() {
        Log.v(TAG, "onConstructor");
        // Required empty public constructor
    }

    interface BluetoothFragmentListener {

        void onSwitchBtStateChanged(boolean state);

        void onLvBtItemClicked(int i);

    }

    BluetoothFragmentListener bluetoothFragmentListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");

        // Inflate the layout for this fragment
        final View inflate = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        mLvBtDevices = inflate.findViewById(R.id.lv_bt_devices);
        mLvBtDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothFragmentListener.onLvBtItemClicked(i);
            }
        });
        mSwitchBt = inflate.findViewById(R.id.switch_bluetooth);
        if (mBluetoothAdapter.isEnabled()) {
            mSwitchBt.setChecked(true);
            bluetoothFragmentListener.onSwitchBtStateChanged(true);
        }
        if (savedInstanceState != null) {
            Log.d(TAG, "Восстанавливаем состояние: " + savedInstanceState.toString());
            Log.d(TAG, "SetChecked: " + String.valueOf(savedInstanceState.getBoolean(mKeySwitchBt)));
            mSwitchBt.setChecked(savedInstanceState.getBoolean(mKeySwitchBt));
        } else {
            Log.d(TAG, "savedInstanceState = null");
        }
        mSwitchBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "Switch: " + String.valueOf(b));
                if (b) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                    //mBluetoothAdapter.enable();
                    //showDevices(MainActivity.mBluetoothService.getBondedDevices());
                } else {
                    mBluetoothAdapter.disable();
                    hideDevices();
                }
                bluetoothFragmentListener.onSwitchBtStateChanged(b);

            }
        });

        BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);

        return inflate;
    }

    void showDevices(List<HashMap<String, String>> devices) {
        Log.d(TAG, "showDevices");
        SimpleAdapter mAdapter = new SimpleAdapter(getActivity().getApplicationContext(), devices,
                android.R.layout.simple_list_item_2, new String[]{"Name", "Address"},
                new int[]{android.R.id.text1, android.R.id.text2});
        mLvBtDevices.setAdapter(mAdapter);
        mLvBtDevices.setVisibility(View.VISIBLE);
    }

    void hideDevices(){
        Log.d(TAG, "hideDevices");
        mLvBtDevices.setVisibility(View.INVISIBLE);
    }



    // ---------------------------------------------------------------------------------------------
    // States
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bluetoothFragmentListener = (BluetoothFragmentListener) activity;
        Log.v(TAG, "onAttach");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
    }

    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach");

    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.v(TAG, "onInflate");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(mKeySwitchBt, mSwitchBt.isChecked());
        Log.d(TAG, "Сохраняем состояние: " + outState.toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
