package com.hitech.levelstate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddDeviceActivity extends AppCompatActivity {

   // private static final int REQUEST_ENABLE_BT = 1;
    //private static final int REQUEST_LOCATION_PERMISSION = 2;

    //  private static final int REQUEST_FINE_LOCATION = 2;
    //private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN = 3;
    public static final String MY_PREFS_NAME = "hitechPrefsELS";
    private static final int PERMISSION_REQUEST_CODE = 100;
    ProgressBar progressbar;
    EditText deviceName;
    Button exitBtn, addDevice;
    private ArrayAdapter mNewDevicesArrayAdapter;
    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private Spinner spn;
    private ArrayAdapter aAdapter;
    int Clickstate = 0;
    int nothingselcted ;

    ArrayList listNames = new ArrayList();
    ArrayList listAddr = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);
        deviceName = (EditText) findViewById(R.id.editTextRoomName);
        exitBtn = (Button) findViewById(R.id.btnBackAddRoomScreen);
        addDevice = (Button) findViewById(R.id.btnSaveRoom);
        checkAndRequestPermissions();

       if (bAdapter == null) {
            // Bluetooth is not supported
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if Bluetooth is enabled; if not, prompt the user to enable it
        if (bAdapter.isEnabled()) {
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE);
                } else {

                    // Permissions already granted, proceed with your Bluetooth operations
                }
            }
            startActivityForResult(enableBtIntent, PERMISSION_REQUEST_CODE);
        }


        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    return;
                } else {

                    // Permissions already granted, proceed with your Bluetooth operations
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {

            }
        }*/
      /*  if (bAdapter.isEnabled()) {

        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                    return;
                } else {

                    // Permissions already granted, proceed with your Bluetooth operations
                }
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
        TextView scan_dev = (TextView) findViewById(R.id.scan_new);
        scan_dev.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                doDiscovery();
                progressbar.setVisibility(View.VISIBLE);
            }
        });

        mNewDevicesArrayAdapter = new ArrayAdapter(this, R.layout.device_name);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        spn = (Spinner) findViewById(R.id.spinner_bt_device);
        deviceName.setImeActionLabel("OK", KeyEvent.KEYCODE_ENTER);
        deviceName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    spn.requestFocus();
                    return true;
                }
                return false;
            }
        });
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               // selectedItemText = parentView.getItemAtPosition(position).toString();
                // Do something with the selected item text
                nothingselcted = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
                nothingselcted = 0;
            }
        });

     /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //if (deviceName.contains("HC-05")) {
                    listNames.add(deviceName);
                    listAddr.add(deviceHardwareAddress);

                //}
            }


            aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listNames);
            spn.setAdapter(aAdapter);
        }

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (deviceName.getText().toString().length() == 0 ) {

                        msg("Enter Device Name");

                    } if (nothingselcted == 0 && Clickstate == 0) {

                        msg("Select Bluetooth device");

                    }  if (deviceName.getText().toString().length()>0 && nothingselcted ==1 && Clickstate ==1) {
                        if (duplicate_check(toTitleCase(deviceName.getText().toString()), listAddr.get((int) spn.getSelectedItemId()).toString()))
                            add_device(listAddr.get((int) spn.getSelectedItemId()).toString());
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        nothingselcted= 0;
                        Clickstate = 0;
                    }

                     if (deviceName.getText().toString().length()>0 && nothingselcted ==0 && Clickstate ==1) {
                        if (duplicate_check(toTitleCase(deviceName.getText().toString()), listAddr.get((int) spn.getSelectedItemId()).toString()))
                            add_device(listAddr.get((int) spn.getSelectedItemId()).toString());
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        nothingselcted= 0;
                        Clickstate = 0;
                    }

                     if (deviceName.getText().toString().length()>0 && nothingselcted ==1 && Clickstate ==0) {
                        if (duplicate_check(toTitleCase(deviceName.getText().toString()), listAddr.get((int) spn.getSelectedItemId()).toString()))
                            add_device(listAddr.get((int) spn.getSelectedItemId()).toString());
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        nothingselcted= 0;
                        Clickstate = 0;
                    }
                }

                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        );

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }
    private void checkAndRequestPermissions() {
        try {
        String[] permissions = {
                Manifest.permission.BLUETOOTH_SCAN,
                // Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT
                // Add other permissions if needed
        };
        // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        // Check if permissions are already granted

            for (String permission : permissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        // Permission not granted, request it
                        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                        return;
                    }
                }
            }
            // All permissions already granted
            // Proceed with your functionality here
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (Exception e) {
            msg("Restrat the app");
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     try {
         if (requestCode == PERMISSION_REQUEST_CODE) {
             // Check if all permissions are granted
             boolean allPermissionsGranted = true;
             for (int grantResult : grantResults) {
                 if (grantResult != PackageManager.PERMISSION_GRANTED) {
                     allPermissionsGranted = false;
                     break;
                 }
             }

             if (allPermissionsGranted) {
                 // All permissions granted, proceed with your functionality
             } else {
                 // Permission denied, show a message or take appropriate action
                 Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
             }
         }
     }
     catch (Exception e){

     }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
               // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE);
                    } else {

                        // Permissions already granted, proceed with your Bluetooth operations
                    }
                }

              //  startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("Select Device");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }

        }
    };

    private void doDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_CODE);
            } else {

                // Permissions already granted, proceed with your Bluetooth operations
            }
        }
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // If we're already discovering, stop it


        if (bAdapter.isDiscovering()) {
            bAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        bAdapter.startDiscovery();
    }

    private boolean duplicate_check(String device_name,String addrx)
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Set<String> setNames = prefs.getStringSet("DEVICE_NAME_LIST", null);
        Set<String> setAddrs = prefs.getStringSet("ADDR_LIST", null);

        if(setNames!=null) {
            if (setNames.contains(device_name)) {
                msg("Device with Same Name Exists");
                return false;
            } else if (setAddrs.contains(addrx)) {
                msg("This Bluetooth is already associated with another device");
                return false;
            }
        }

        return true;


    }

    private void add_device(String addr)
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,  MODE_PRIVATE).edit();
        Set<String> set = new HashSet<String>();

        Set<String> setNames = prefs.getStringSet("DEVICE_NAME_LIST", null);

        Set<String> setAddrs = prefs.getStringSet("ADDR_LIST", null);

        String device_name_tc = toTitleCase(deviceName.getText().toString());
        set.add(addr);
        if(setAddrs!=null)
            set.addAll(setAddrs);
        editor.putStringSet("ADDR_LIST",set);
        editor.commit();

        set = new HashSet<String>();
        set.add(device_name_tc);
        if(setNames!=null)
            set.addAll(setNames);
        editor.putStringSet("DEVICE_NAME_LIST",set);
        editor.commit();



        msg("Device Saved");

        Intent intent = new Intent(this, DeviceSelectionActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
   /* private void checkLocationPermission() {
        // Check if we have fine location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else {

            // Permission already granted
           // startBluetoothScanning();
        }
    }*/
  /*  public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
            } else {
                // Permission denied
            }
        }
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your Bluetooth operations

            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable Bluetooth features)
            }
        }
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your Bluetooth operations

            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable Bluetooth features)
            }
        }
    }
/*

    /*public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your Bluetooth operations

            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable Bluetooth features)
            }
        }
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Location permission is required to scan for Bluetooth devices", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bAdapter.cancelDiscovery();
            progressbar.setVisibility(View.INVISIBLE);
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Create the result Intent and include the MAC address
            Clickstate = 1;
            if (deviceName.getText().toString().length() == 0 ) {
                msg("Enter device Name");
            } else {
                try {
                BluetoothDevice device = bAdapter.getRemoteDevice(address);

                    if (duplicate_check(toTitleCase(deviceName.getText().toString()), address)) {
                        device.createBond();
                        add_device(address);
                    }

                    //createBond(device);
                   // add_device(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    };


}