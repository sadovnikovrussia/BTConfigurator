package tech.sadovnikov.bt_configurator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

public class ConfigurationViewModel extends ViewModel {
    private static final String TAG = "MyViewModel";

    MutableLiveData<String> liveData;

    public LiveData<String> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            loadLiveData();
        }
        return liveData;
    }

    public ConfigurationViewModel() {
        super();
        Log.v(TAG, "OnConstructor");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private void loadLiveData() {
        liveData.setValue("1805");
    }

    void setLiveData(String data) {
        liveData.setValue(data);
        Log.d(TAG, "LiveData: " + liveData.getValue());
    }
}
