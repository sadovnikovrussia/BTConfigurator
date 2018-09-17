package tech.sadovnikov.bt_configurator;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BluetoothFragment.BluetoothFragmentListener, ConfigurationFragment.ConfigurationFragmentListener, ConsoleFragment.ConsoleFragmentListener {

    private static final String TAG = "MainActivity";

    FrameLayout container;
    BottomNavigationView navigation;

    BluetoothFragment bluetoothFragment;
    ConfigurationFragment configurationFragment;
    ConfigBuoyFragment configBuoyFragment;
    ConfigMainFragment configMainFragment;
    ConfigNavigationFragment configNavigationFragment;
    ConsoleFragment consoleFragment;

    BluetoothService mBluetoothService;

    Handler mUiHandler;

    ConfigurationViewModel viewModel;
    Database database;

    public MainActivity() {
        Log.v(TAG, "onConstructor");
    }

    // Приемник изменения состояния Bluetooth от системы
    final BroadcastReceiver mReceiverBluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        Log.d(TAG, "ACTION_FOUND");
                        BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String name = d.getName();
                        String address = d.getAddress();
                        Log.d(TAG, "Founded device: name = " + name + ", address = " + address);
                        break;
                    case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        Log.d(TAG, "ACTION_PAIRING_REQUEST");
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        Log.d(TAG, "ACTION_BOND_STATE_CHANGED");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        Log.d(TAG, "ACTION_DISCOVERY_STARTED");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        switch (state) {
                            // BT включился
                            case BluetoothAdapter.STATE_ON:
                                Log.d(TAG, "STATE_ON");
                                bluetoothFragment.mSwitchBt.setChecked(true);
                                bluetoothFragment.showDevices(mBluetoothService.getBondedDevices());
                                break;
                            // BT выключился
                            case BluetoothAdapter.STATE_OFF:
                                Log.d(TAG, "STATE_OFF");
                                bluetoothFragment.mSwitchBt.setChecked(false);
                                bluetoothFragment.hideDevices();
                                break;
                            // BT включается
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(TAG, "STATE_TURNING_ON");
                                break;
                            // BT выключается
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(TAG, "STATE_TURNING_OFF");
                                break;
                        }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");

        mUiHandler = new UiHandler(this);
        mBluetoothService = new BluetoothService(mUiHandler);

        // Регистрация ресиверов
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiverBluetooth, intentFilter);

        // Initialise Database
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "database").build();

        //
        viewModel = ViewModelProviders.of(this).get(ConfigurationViewModel.class);
        LiveData<String> liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Log.i(TAG, "onChanged: " + s);
            }
        });
        viewModel.setLiveData("QUQAREQ");
        viewModel.setLiveData("mafaca");

        // Установка UI
        setupUi();
    }

    void setupUi() {
        container = findViewById(R.id.container);
        bluetoothFragment = new BluetoothFragment();
        configurationFragment = new ConfigurationFragment();
        consoleFragment = new ConsoleFragment();

        //-----------------------------------------------
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_bluetooth:
                        setFragment(bluetoothFragment);
                        return true;
                    case R.id.navigation_configuration:
                        setFragment(configurationFragment);
                        return true;
                    case R.id.navigation_console:
                        setFragment(consoleFragment);
                        return true;
                }
                return false;
            }
        });
        setFragment(this.bluetoothFragment);

    }

    @Override
    public void onSwitchBtStateChanged(boolean state) {
        if (state) {
            mBluetoothService.enableBt();
            bluetoothFragment.showDevices(mBluetoothService.getBondedDevices());
        } else {
            bluetoothFragment.hideDevices();
            mBluetoothService.disableBt();
        }
    }

    @Override
    public void onLvBtItemClicked(int i) {
        mBluetoothService.connectTo(i);
    }

    @Override
    public void onClickBtnSendCommand(String line) {
        mBluetoothService.sendData(line);
    }

    @Override
    public void onLvConfigsItemClick(int i) {

        switch (i) {
            case 0:
                configBuoyFragment = ConfigBuoyFragment.newInstance("103");
                setFragment(configBuoyFragment);
                break;
            case 1:
                configMainFragment = new ConfigMainFragment();
                setFragment(configMainFragment);
                break;
            case 2:
                configNavigationFragment = new ConfigNavigationFragment();
                setFragment(configNavigationFragment);
                break;
        }

    }

    static class UiHandler extends Handler {

        WeakReference<Activity> activityWeakReference;

        UiHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "Получили в handle");
            MainActivity activity = (MainActivity) activityWeakReference.get();
            if (activity != null) {
                Object obj = msg.obj;
                switch (msg.what) {
                    case DataAnalyzer.WHAT_MAIN_LOG:
                        activity.consoleFragment.showLog((String) obj);
                        break;
                    case DataAnalyzer.WHAT_COMMAND_DATA:
                        HashMap msgData = (HashMap) obj;
                        String data = (String) msgData.get(DataAnalyzer.DATA);
                        int resourceId = (Integer) msgData.get(DataAnalyzer.RESOURCE_ID);
                        activity.drawCommandResult(data, resourceId);
                        break;
                }
            }
        }
    }

    void drawCommandResult(String data, int resourceId) {
        Log.v(TAG, "drawCommandResult");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        unregisterReceiver(mReceiverBluetooth);
    }

    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        //navigation.setSelectedItemId(fragment.getId());
        fragmentTransaction.commit();
    }

}
