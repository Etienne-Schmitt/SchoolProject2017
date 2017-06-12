package fr.vampire142.pibluetooth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import fr.vampire142.pibluetooth.R;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 3;
    private BluetoothAdapter btAdapter;
    private String btAddrSelected;
    private String btNameSelected;
    private ListView listDevicePaired;
    private ArrayList<String> mDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        checkBTState();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            findPairedDevice();
        } else {
            Log.d(TAG, "Bluetooth désactiver");
            finish();
        }
    }


    private void setupUI() {

        listDevicePaired = (ListView) findViewById(R.id.listDevicePaired);

        listDevicePaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String btNameAddress = (String) parent.getItemAtPosition(position);

                StringTokenizer tokens = new StringTokenizer(btNameAddress, "\n");
                btNameSelected = tokens.nextToken();
                btAddrSelected = tokens.nextToken();
                Log.i(TAG, "Nom client :" + btNameSelected + "\n"+ "Adresse client :" + btAddrSelected);

                Intent connectIntent = new Intent(MainActivity.this, ConnectToDevice.class);
                connectIntent.putExtra("Name", btNameSelected);
                connectIntent.putExtra("Address", btAddrSelected);
                startActivity(connectIntent);

            }
        });

    }

    private void checkBTState() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // device doesn't support bluetooth
        } else {
            // bluetooth is off, ask user to on it.
            if (!btAdapter.isEnabled()) {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, REQUEST_ENABLE_BT);
            } else {
                findPairedDevice();
            }
        }
    }
    private void findPairedDevice() {
        Set<BluetoothDevice> all_devices = btAdapter.getBondedDevices();
        if (all_devices.size() > 0) {
            for (BluetoothDevice currentDevice : all_devices) {
                mDeviceList.add(currentDevice.getName() + "\n" + currentDevice.getAddress());
            }
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDeviceList);
        listDevicePaired.setAdapter(adapter);
    }

}
