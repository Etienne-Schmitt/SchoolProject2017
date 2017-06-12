package fr.vampire142.pibluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import fr.vampire142.pibluetooth.R;

public class ConnectToDevice extends AppCompatActivity {

    private static final String TAG = "ConnectToDevice: ";
    private final UUID uuidRFCOMM = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
    char TypeAlarm;
    char dataSize;
    int dataSizeInt;
    String data;
    String checksum;
    int checksumInt;
    private BluetoothDevice mmDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private boolean detailedInfo = false;
    private boolean freezed = false;
    private String nameRaspberry;
    private String addrRaspberry;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView textViewer;
    private String formattedOutput;
    private TextView dataType;
    private ToggleButton buttonDisConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_device);

        setupUI();
    } // Done

    private void setupUI() {
        Log.d(TAG, "setupUI:");

        textViewer = (TextView) findViewById(R.id.textViewer);
        dataType = (TextView) findViewById(R.id.textType);
        TextView textDeviceFound = (TextView) findViewById(R.id.textDeviceFound);
        Switch switchType = (Switch) findViewById(R.id.switchType);
        buttonDisConnect = (ToggleButton) findViewById(R.id.toggleButtonConnectDisconnect);
        ToggleButton buttonFreeze = (ToggleButton) findViewById(R.id.toggleButtonFreeze);

        Bundle bundle = getIntent().getExtras();

        nameRaspberry = bundle.getString("Name");
        addrRaspberry = bundle.getString("Address");

        textDeviceFound.setText(R.string.state);
        textDeviceFound.append(" " + nameRaspberry);


        switchType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataType.setText(R.string.DetailedChosen);
                    detailedInfo = true;
                } else {
                    dataType.setText(R.string.RawChosen);
                    detailedInfo = false;
                }
            }
        });


        buttonDisConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(ConnectToDevice.this, R.string.btOpen, Toast.LENGTH_LONG).show();
                    buttonDisConnect.setClickable(false);
                    findDevice();
                    connectBT();
                } else {
                    closeBT();
                }
            }
        });

        buttonFreeze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "freeze = true");
                    freezed = true;
                } else {
                    Log.i(TAG, "freeze = false");
                    freezed = false;
                }
            }
        });
    } // Done

    private void findDevice() {
        Log.d(TAG, "findDevice:");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (Objects.equals(device.getName(), nameRaspberry)) {
                Log.i(TAG, "openBT: " + "\n" +
                        "nameRaspberry: " + nameRaspberry + "\n" +
                        "addrRaspberry: " + addrRaspberry);
                mmDevice = device;
                break;
            }
        }
    } // Done

    private void connectBT() {
        Log.d(TAG, "connectBT: ");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(mmDevice);
        mConnectThread.start();
    } // Done

    private void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected:");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConnectToDevice.this,
                        getResources().getString(R.string.btConnectedTo) + " " + nameRaspberry,
                        Toast.LENGTH_LONG).show();
            }
        });

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

        buttonDisConnect.setClickable(true);
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    } // Done

    private void formatMessage(String msgRead) {

        if (!detailedInfo) {
            formattedOutput = msgRead;
        } else {
            Log.i(TAG, "msgRead.length = " + msgRead.length());
            if (msgRead.length() > 4 && msgRead.length() < 19) {

                TypeAlarm = msgRead.charAt(0);
                dataSize = msgRead.charAt(1);

                switch (TypeAlarm) {
                    case '0':
                        formattedOutput = "Alarme sans communication phonique: " + TypeAlarm + "\n";
                        break;
                    case '1':
                        formattedOutput = "Alarme avec communication phonique: " + TypeAlarm + "\n";
                        break;
                    case '2':
                        formattedOutput = "Défaut opérationne: " + TypeAlarm + "\n";
                        break;
                    case '3':
                        formattedOutput = "Alarme d'auto-test: " + TypeAlarm + "\n";
                        break;
                    case '4':
                        formattedOutput = "Donnée utilisateur: " + TypeAlarm + "\n";
                        break;
                    case '5':
                        formattedOutput = "Donnée utilisateur - donnée: " + TypeAlarm + "\n";
                        break;
                    default:
                        formattedOutput = getString(R.string.badtrame) + "\n";
                        return;
                }

                switch (dataSize) {
                    case '0':
                        data = "0";
                        dataSizeInt = 0;
                        break;
                    case '1':
                        data = msgRead.substring(2, 3);
                        dataSizeInt = 1;
                        break;
                    case '2':
                        data = msgRead.substring(2, 4);
                        dataSizeInt = 2;
                        break;
                    case '3':
                        data = msgRead.substring(2, 5);
                        dataSizeInt = 3;
                        break;
                    case '4':
                        data = msgRead.substring(2, 6);
                        dataSizeInt = 4;
                        break;
                    case '5':
                        dataSizeInt = 5;
                        data = msgRead.substring(2, 7);
                        break;
                    case '6':
                        dataSizeInt = 6;
                        data = msgRead.substring(2, 8);
                        break;
                    case '7':
                        dataSizeInt = 7;
                        data = msgRead.substring(2, 9);
                        break;
                    case '8':
                        dataSizeInt = 8;
                        data = msgRead.substring(2, 10);
                        break;
                    case '9':
                        dataSizeInt = 9;
                        data = msgRead.substring(2, 11);
                        break;
                    case 'A':
                        dataSizeInt = 10;
                        data = msgRead.substring(2, 12);
                        break;
                    case 'B':
                        dataSizeInt = 11;
                        data = msgRead.substring(2, 13);
                        break;
                    case 'C':
                        dataSizeInt = 12;
                        data = msgRead.substring(2, 14);
                        break;
                    case 'D':
                        dataSizeInt = 13;
                        data = msgRead.substring(2, 16);
                        break;
                    case 'E':
                        dataSizeInt = 14;
                        data = msgRead.substring(2, 17);
                        break;
                    case 'F':
                        dataSizeInt = 15;
                        data = msgRead.substring(2, 18);
                        break;
                    default:
                        formattedOutput = getString(R.string.badtrame) + "\n";
                        return;
                }

                if (msgRead.length() < (1 + 1 + dataSizeInt + 2)) {
                    formattedOutput = getString(R.string.badtrame) + "\n";
                } else {

                    formattedOutput = formattedOutput + "Taille des données: " + dataSizeInt + "\n";

                    if (!Objects.equals(data, "0")) {
                        formattedOutput = formattedOutput + "Donnée: " + data + "\n";
                    }


                    checksum = msgRead.substring(1 + 1 + dataSizeInt, 1 + 1 + dataSizeInt + 2);

                    formattedOutput = formattedOutput + "Checksun: " + checksum + "\n";

                    String tmpStr = msgRead.substring(0, dataSizeInt + 2);

                    Log.i(TAG, "format message: \n");
                    Log.i(TAG, "Typealarm :" + TypeAlarm + "\n");
                    Log.i(TAG, "dataSize:" + dataSize + "\n");
                    Log.i(TAG, "checksum: " + checksum + "\n");

                    int[] sumArray = new int[tmpStr.length()];

                    for (int i = 0; i < tmpStr.length(); i++) {

                        switch (tmpStr.charAt(i)) {
                            case '0':
                                sumArray[i] = 0;
                                break;
                            case '1':
                                sumArray[i] = 1;
                                break;
                            case '2':
                                sumArray[i] = 2;
                                break;
                            case '3':
                                sumArray[i] = 3;
                                break;
                            case '4':
                                sumArray[i] = 4;
                                break;
                            case '5':
                                sumArray[i] = 5;
                                break;
                            case '6':
                                sumArray[i] = 6;
                                break;
                            case '7':
                                sumArray[i] = 7;
                                break;
                            case '8':
                                sumArray[i] = 8;
                                break;
                            case '9':
                                sumArray[i] = 9;
                                break;
                            case 'A':
                                sumArray[i] = 10;
                                break;
                            case 'B':
                                sumArray[i] = 11;
                                break;
                            case 'C':
                                sumArray[i] = 12;
                                break;
                            case 'D':
                                sumArray[i] = 13;
                                break;
                            case 'E':
                                sumArray[i] = 14;
                                break;
                            case 'F':
                                sumArray[i] = 15;
                                break;
                            default:
                                formattedOutput = getString(R.string.badtrame) + "\n";
                                return;
                        }
                    }

                    int sum = 0;

                    for (int aSumArray : sumArray) sum += aSumArray;


                    String checksumChecked = Integer.toString(sum, 16);

                    if (checksumChecked.length() == 1) {
                        checksumChecked = checksumChecked + "0";

                        checksumChecked = String.valueOf(new StringBuilder(checksumChecked).reverse());
                    }

                    checksumChecked = checksumChecked.toUpperCase();

                    if (Objects.equals(checksum, checksumChecked)) {
                        formattedOutput = formattedOutput + getResources().getString(R.string.checksumOK) + "\n";
                    } else {
                        formattedOutput = formattedOutput + getResources().getString(R.string.checksumFail) + "\n" +
                                getResources().getString(R.string.checksumCorrect) + checksumChecked + "\n";
                    }
                }
            } else {
                formattedOutput = getString(R.string.badtrame) + "\n";
            }
        }
    }

    private void closeBT() {
        Log.d(TAG, "closeBT:");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonDisConnect.setClickable(true);
                buttonDisConnect.setChecked(false);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConnectToDevice.this, nameRaspberry + " " + getResources().getString(R.string.disconnected),
                        Toast.LENGTH_LONG).show();
            }
        });
    } // Done

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(uuidRFCOMM);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread:");
            setName("ConnectThread");

            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                    closeBT();

                    mConnectThread = null;
                    return;
                }
                Log.e(TAG, "unable to create() socket", e);
                closeBT();

                mConnectThread = null;
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (ConnectThread.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    } // Done

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private boolean printOnce = false;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            StringBuilder readMessage = new StringBuilder();

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    closeBT();
                    break;
                }
                String msgRead = new String(buffer, 0, bytes);

                readMessage.append(msgRead);

                StringTokenizer tokens = new StringTokenizer(msgRead, "\n");
                msgRead = tokens.nextToken();
                Log.i(TAG, "msgReaded: " + msgRead);

                formatMessage(msgRead);

                if (!freezed) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewer.setText(formattedOutput);
                        }
                    });
                } else {
                    if (!printOnce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewer.setText(formattedOutput);
                            }
                        });
                        printOnce = true;
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    } // Done
}
