package tech.sadovnikov.bt_configurator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import android.os.Handler;

public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int WHAT_LOG = 10;

    private BluetoothAdapter mBluetoothAdapter;

    private List<HashMap<String, String>> mDevices;

    // Потоки
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Thread analyzeThread;

    private Handler mHandler;
    DataAnalyzer dataAnalyzer;

    BluetoothService(Handler handler) {
        Log.v(TAG, "OnConstructor");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        dataAnalyzer = new DataAnalyzer(mHandler);
    }

    void enableBt() {
        mBluetoothAdapter.enable();
    }

    void disableBt() {
        mBluetoothAdapter.disable();
    }

    List<HashMap<String, String>> getBondedDevices() {
        // Получение списка спаренных устройств
        Set<BluetoothDevice> mBondedDevices = mBluetoothAdapter.getBondedDevices();
        mDevices = new ArrayList<>();
        for (BluetoothDevice device : mBondedDevices) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Name", device.getName());
            hashMap.put("Address", device.getAddress());
            mDevices.add(hashMap);
        }
        return mDevices;
    }

    void connectTo(int i) {
        HashMap<String, String> devAndAddr = mDevices.get(i);
        String address = devAndAddr.get("Address");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.d(TAG, "onConnecting to: " + device.getName());
        onConnecting(device);
    }

    private synchronized void onConnecting(BluetoothDevice device) {
        Log.d(TAG, "Connecting to: " + device);

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to onConnecting with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private synchronized void onConnected(BluetoothSocket socket) {
        Log.d(TAG, "onConnected to Socket: " + socket.toString());
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mSocket;
        BluetoothDevice mDevice;

        ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "Create ConnectThread");
            BluetoothSocket tmp = null;
            mDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID);
                Log.d(TAG, "Получаем socket c помощью createRfcommSocketToServiceRecord(UUID): " + "BluetoothSocket = " + tmp.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Socket's createRfcommSocketToServiceRecord(UUID) method failed", e);
            }
            mSocket = tmp;
        }

        @Override
        synchronized public void run() {
            setName("ConnectThread");
            Log.d(TAG, "Started thread " + "\"" + getName() + "\"");
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Выключили поиск: " + "mBluetoothAdapter.isDiscovering() = " + mBluetoothAdapter.isDiscovering());
            try {
                Log.d(TAG, "Пробуем mSocket.onConnecting()");
                mSocket.connect();
                Message message = new Message();
                message.obj = mDevice.getName();
                mHandler.sendMessage(message);
                Log.d(TAG, "mSocket is onConnected? " + String.valueOf(mSocket.isConnected()));
            } catch (IOException e) {
                Log.d(TAG, "Не получилось. mSocket is onConnected? " + String.valueOf(mSocket.isConnected()) + ", " + e.getMessage());
                e.printStackTrace();
                try {
                    Log.d(TAG, "Закрываем socket");
                    mSocket.close();
                    Log.d(TAG, "Закрыли socket");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.d(TAG, "Не удалось закрыть socket: " + e1.getMessage());
                }
                return;
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            onConnected(mSocket);
        }

        void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "close() of onConnecting " + " socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BufferedReader readerSerial;
        private final PrintWriter writerSerial;

        ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "Create ConnectedThread");
            mmSocket = socket;
            BufferedReader tmpReaderSerial = null;
            PrintWriter tmpWriterSerial = null;

            // Get the BluetoothSocket readerSerial and output streams
            try {
                Log.d(TAG, "Пытаемся получить InputStream");
                tmpReaderSerial = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d(TAG, "Получили InputStream: " + tmpReaderSerial.toString());
            } catch (IOException e) {
                Log.d(TAG, "Не удалось получить InputStream: " + e.getMessage());
            }
            readerSerial = tmpReaderSerial;
            try {
                Log.d(TAG, "Пытаемся создать OutputStream");
                tmpWriterSerial = new PrintWriter(socket.getOutputStream());
                Log.d(TAG, "Получили OutputStream: " + tmpWriterSerial.toString());
            } catch (IOException e) {
                Log.d(TAG, "Не удалось создать OutputStream: ", e);
            }
            writerSerial = tmpWriterSerial;
        }

        public void run() {
            setName("ConnectedThread");
            Log.d(TAG, "Start thread " + getName());
            String line;
            try {
                Log.d(TAG, "Пытаемся прочитать из потока");
                while ((line = readerSerial.readLine()) != null) {
                    Log.d(TAG, line);
                    dataAnalyzer.analyze(line);
                    //Message message = new Message();
                    //message.obj = line;
                    //message.what = WHAT_LOG;
                    //mUiHandler.sendMessage(message);
                }
            } catch (IOException e) {
                Log.d(TAG, "Не удалось прочитать из потока", e);
            }
        }

        synchronized void write(String data) {
            Log.d(TAG, "Пишем в порт: " + data);
            writerSerial.write(data);
            writerSerial.flush();
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "close() of onConnecting socket failed", e);
            }
        }
    }

    void sendData(String data) {
        if (mConnectedThread != null) {
            mConnectedThread.write(data);
        }
    }

    //String readFromDevice(String line){
    //    return line;
    //}

}
