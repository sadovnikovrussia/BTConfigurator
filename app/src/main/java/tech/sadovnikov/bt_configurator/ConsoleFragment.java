package tech.sadovnikov.bt_configurator;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConsoleFragment extends Fragment {

    private static final String TAG = "ConsoleFragment";

    TextView tvLog;
    Button btnSendCommand;
    EditText etCommandLine;

    public ConsoleFragment() {
        // Required empty public constructor
        Log.v(TAG, "onConstructor");
    }

    interface ConsoleFragmentListener{

        void onClickBtnSendCommand(String line);

    }

    ConsoleFragmentListener consoleFragmentListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_console, container, false);
        tvLog = inflate.findViewById(R.id.tvLog);
        tvLog.setMovementMethod(new ScrollingMovementMethod());
        etCommandLine = inflate.findViewById(R.id.etCommandLine);
        btnSendCommand = inflate.findViewById(R.id.btnSendCommand);
        btnSendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command = etCommandLine.getText().toString();
                if (command.length() != 0) {
                    //MainActivity.mBluetoothService.sendData(etCommandLine.getText().toString());
                    consoleFragmentListener.onClickBtnSendCommand(command);
                }
            }
        });
        BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);

        return inflate;
    }

    void showLog(String line) {
        if (tvLog != null) {
            tvLog.append(line + "\r\n");
            Editable editable = tvLog.getEditableText();
            Selection.setSelection(editable, editable.length());
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // ---------------------------------------------------------------------------------------------
    // States
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");
        consoleFragmentListener = (ConsoleFragmentListener) activity;
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

}
