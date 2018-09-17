package tech.sadovnikov.bt_configurator;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigBuoyFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "ConfigBuoyFragment";
    public static final String BUNDLE_CONTENT = "Bundle content";

    EditText etId;

    private String content;

    public ConfigBuoyFragment() {
        Log.v(TAG, "onConstructor");
        // Required empty public constructor
    }

    public static ConfigBuoyFragment newInstance(final String content) {
        final ConfigBuoyFragment configBuoyFragment = new ConfigBuoyFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CONTENT, content);
        configBuoyFragment.setArguments(bundle);
        return configBuoyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        // Inflate the layout for this fragment
        BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
        navigation.getMenu().getItem(1).setChecked(true);

        View inflate = inflater.inflate(R.layout.fragment_config_buoy, container, false);
        etId = inflate.findViewById(R.id.et_id);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTENT)) {
            content = getArguments().getString(BUNDLE_CONTENT);
            etId.setText(content);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }


        return inflate;
    }

    // ---------------------------------------------------------------------------------------------
    // States
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

}
