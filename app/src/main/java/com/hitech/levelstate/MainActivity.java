package com.hitech.levelstate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String device_address = "F7";
    private static final String StartData_send = "|";
    private static final String StartData = "?";
    private static final String EndDataWithCRC = "#";
    public static final String MY_PREFS_NAME = "hitechPrefsELS";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    public static final String DEVICE_NAME = "device_name";
    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice hc05Device;
    private InputStream inputStream;
    String address;
    Context mContext;
    String device_Name;
    public int[] LED_status = {0,0,0,0,0,0,0,0,0,0};
    TextView deviceNameHeading;
    BottomNavigationView bottomNavigation;
    private TextView textViewDeviceName,text_recv;
    private TextView textView_output;
    Button sendButton,writeButton;
    private Thread dataReceiveThread;
    public boolean dashboard_on = false;
    private Handler handlerDashBd;
    private Runnable runnableDashbd;
    private int intervalMillis = 4000; // 10 second interval
    public String received_data_string="";
    public String received_data_string_new="";
    ProgressDialog progressDialog;
    private Handler handler = new Handler();
    public static final String SHARED_PREFS = "sharedPrefs";
    public int received_login_data;
    public int myString;
    public int bytes=0;
    public byte[] newData;
    public byte[] dataWithoutStartEnd;
    public String Final_CRC;
    public String Final_receivedData;
    ConstraintLayout constraintLayout;
    public static final String PREFS_NAME = "MyPrefsFile";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Set the message to be displayed
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by pressing outside of it
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Set the style to be a spinning circle

        //showProgressDialog();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        constraintLayout = findViewById(R.id.constraintLayout);
        mContext = this;
        Intent newint = getIntent();
        address = newint.getStringExtra(ItemAdapter.EXTRA_DEVICE_ADDRESS);
        device_Name = newint.getStringExtra(ItemAdapter.DEVICE_NAME);

        textViewDeviceName = findViewById(R.id.textViewDeviceName);
        textViewDeviceName.setText(device_Name);
        textView_output = findViewById(R.id.txt_cmd);
      //  sendButton = findViewById(R.id.button_send_cmd);
      //  writeButton = findViewById(R.id.button_write);
        text_recv = findViewById(R.id.txt_recv);
        store_str_values("LAST_DEVICE_NAME", device_Name);
        store_str_values("LAST_BT_ADDR", address);
        // Set your string value here
        //checkPermission();
        bottomNavigation= findViewById(R.id.bottom_navigation);

        deviceNameHeading = (TextView) findViewById(R.id.txtHeading);
        dashboard_on=false;


        bottomNavigation = findViewById(R.id.bottom_navigation);
        ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.navigation_home);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        showProgressDialog("Connecting ...");
        View rootView = findViewById(android.R.id.content);

        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This method will be called when the layout is complete
                // You can run your function here
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        connectToHC05();



                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressDialog();
                                //cardContainer.invalidate();
                            }
                        });
                    }
                });
                thread.start();
                // Make sure to remove the listener to prevent multiple calls
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        //
    }
    public int getMyString() {
        String access_data = "?0000!";
        sendCommand_First(access_data);
        delayRoutine(100);
        try {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                bytes = inputStream.read(buffer);
//                new String(buffer, 0, bytes);
                if(buffer[0]!=0){
                    myString=1;
                }
                else{
                    myString=0;
                }
                return myString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myString;
    }
    private boolean read_operation(String startingRegister,String no_of_Registers)
    {
        received_data_string ="";
        prepareReadCommand(startingRegister,no_of_Registers);
        delayRoutine(100);

        Date cmdSendTime = Calendar.getInstance().getTime();
        int elapsed_time = 0;
        while (received_data_string.equals("") && elapsed_time<3) {
            Date currentTime = Calendar.getInstance().getTime();
            long millis = currentTime.getTime() - cmdSendTime.getTime();
            elapsed_time = (int) (millis / (1000));
        }
        if(received_data_string.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int get_received_login_data(String recvd_str)
    {
        int no_of_bytes=0;
        String tmp="";
        String[] data_str = new String[30];
        if(recvd_str.length()<2)
            return 0;

        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7"))
            return 0;          //address ok
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03"))      return 0;      //command type ok

        if (recvd_str.length() > 6)
        {
            no_of_bytes = hexToDec(recvd_str.substring(6, 8));
            int r_str_len = recvd_str.length();
            if (r_str_len > (11 + (no_of_bytes * 3))) {
                for (int i = 0; i < no_of_bytes / 2; i++) {
                    data_str[i * 2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                    data_str[i * 2 + 1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                    tmp += data_str[i * 2] + data_str[i * 2 + 1];
                }

                String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);

                byte[] hexBytes = hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                int crcValue = calculateCRC(hexBytes);
                String crc_str = Integer.toHexString(crcValue).toUpperCase();

                if (crc_str.length() < 4) crc_str = "0" + crc_str;
                if (crc_str.length() == 4) {
                    crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                    if (crc_str.equals(crc_recv)) {
                        int tmporary = Integer.parseInt(data_str[0] + data_str[1],16);
                        received_login_data = tmporary;
                        return 1;
                    }
                    else{
                        return 0;
                    }
                }
            }
        }
       return 0;
    }
    public void showProgressDialog(String msg) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void prepareReadCommand(String start_register, String no_of_registers)
    {
        String cmd_type = "03"; //read
        String command_string = device_address + cmd_type + start_register + no_of_registers;

        byte[] hexBytes = hexStringToByteArray(command_string);
        int crcValue = calculateCRC(hexBytes);
        //text_recv.setText(""+Integer.toHexString(crcValue).toUpperCase());
        String crcStr = Integer.toHexString(crcValue).toUpperCase();

        while(crcStr.length()==3) crcStr="0" + crcStr;
        String crc_str = crcStr.substring(2, 4) + crcStr.substring(0, 2);

       // sendHexDataToHC05("F70300010001C15C");
       // sendHexDataToHC05("?0001!");
        //textView_output.setText("" + command_string + crc_str);
        sendHexDataToHC05(command_string + crc_str);
    }

    public void prepare_write_command(String registerNo, String data)
    {
        String cmd = device_address + "10" + registerNo + "0001" + "02" + data;
        byte[] hexBytes = hexStringToByteArray(cmd);
        int crcValue = calculateCRC(hexBytes);
        String crcStr = Integer.toHexString(crcValue).toUpperCase();
        while(crcStr.length()==3) crcStr="0" + crcStr;
        String crc_str = crcStr.substring(2, 4) + crcStr.substring(0, 2);
        sendHexDataToHC05(cmd + crc_str);
    }
    public static int calculateCRC(byte[] data) {
        int crc = 0xFFFF;

        for (int i = 0; i < data.length; i++) {
            crc ^= (int) data[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) == 1) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }

        // Swap low and high bytes to match Modbus endianness
        // crc = (crc << 8) | ((crc >> 8) & 0xFF);

        return crc;
    }

    private boolean checkBluetoothPermission() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }

        return true;
    }

    public void openFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId()==R.id.navigation_home)  {

                        bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

                            openFragment(HomeFragment.newInstance("", ""));
                            dashboard_on = true;

                    }
                    else if(item.getItemId()==R.id.navigation_config)  {

                        bottomNavigation.getMenu().findItem(R.id.navigation_config).setChecked(true);
                            openFragment(ConfigFragment.newInstance("", ""));
                        //bottomNavigation.setSelectedItemId(R.id.navigation_config);
                        dashboard_on=false;
                    }
                    else if(item.getItemId()==R.id.navigation_settings)  {
                        bottomNavigation.getMenu().findItem(R.id.navigation_settings).setChecked(true);
                        openFragment(SettingsFragment.newInstance("", ""));
                        //bottomNavigation.setSelectedItemId(R.id.navigation_settings);
                        dashboard_on=false;
                    }
                    else if(item.getItemId()==R.id.navigation_admin)  {
                        bottomNavigation.getMenu().findItem(R.id.navigation_admin).setChecked(true);
                        openFragment(AdminFragment.newInstance("", ""));
                        dashboard_on=false;
                    }
                    else
                    {
                        bottomNavigation.getMenu().findItem(R.id.navigation_logout).setChecked(true);
                        new AlertDialog.Builder(mContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Closing Confirmation")
                                .setMessage("Are you sure you want to exit?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dashboard_on = false;
                                        delayRoutine(1000);
                                        finish();

                                        System.exit(0);
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    return false;
                }
            };

    @Override
    protected  void onStart() {

        super.onStart();
        //progress = ProgressDialog.show(this, "Connecting..", "Please wait!!!");  //show a progress dialog
        //showProgressDialog();
            dashboard_on = true;
            //connectToHC05();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {

               // handlerDashBd.removeCallbacks(runnableDashbd);
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            startDataReceiveThread();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDataReceiveThread();
        //handlerDashBd.removeCallbacks(runnableDashbd);
    }

    private void startDataReceiveThread() {
        //dataReceiveThread = new Thread(new DataReceiveThread());
   //     startListening();
    }

    public void delayRoutine(long x)
    {
        try {
            Thread.sleep(x);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void stopDataReceiveThread() {
        if (dataReceiveThread != null) {
            dataReceiveThread.interrupt();
            dataReceiveThread = null;
        }
    }
    public void PrepareCommandDatasend(String data){
        String FinalCmdCalculateCRC = data;
        byte[] Asciibyte = FinalCmdCalculateCRC.getBytes(StandardCharsets.US_ASCII);
        int CalculateCRC = calculateChecksum(Asciibyte);
        String CRC_Final = checksumToAscii(CalculateCRC);
        String Final_Command = StartData_send + FinalCmdCalculateCRC + CRC_Final + EndDataWithCRC;
        sendSettingCommand(Final_Command);
    }
    public void PrepareReadCommandChannelStat(String data){
        String FinalCmdCalculateCRC = data + ",";
        byte[] Asciibyte = FinalCmdCalculateCRC.getBytes(StandardCharsets.US_ASCII);
        int CalculateCRC = calculateChecksum(Asciibyte);
        String CRC_Final = checksumToAscii(CalculateCRC);
        String Final_Command = StartData + FinalCmdCalculateCRC + CRC_Final + EndDataWithCRC;
        sendCommandchannelstat(Final_Command);
    }

    public void PrepareReadCommandNew(String data){
        String FinalCmdCalculateCRC = data + ",";
        byte[] Asciibyte = FinalCmdCalculateCRC.getBytes(StandardCharsets.US_ASCII);
        int CalculateCRC = calculateChecksum(Asciibyte);
        String CRC_Final = checksumToAscii(CalculateCRC);
        String Final_Command = StartData + FinalCmdCalculateCRC + CRC_Final + EndDataWithCRC;
        sendCommand(Final_Command);
    }
    public void sendCommand_First(String command){
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {

                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(command.getBytes());
                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send hex data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to " + textViewDeviceName.getText().toString() + "..reconnecting", Toast.LENGTH_SHORT).show();
        }
    }
    public void sendCommandchannelstat(String command){
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(command.getBytes());
                outputStream.flush();
                delayRoutine(200);
                Date cmdSendTime = Calendar.getInstance().getTime();
                int count = 0;
                while(command.length()<8 && count<15){
                    Date currentTime = Calendar.getInstance().getTime();
                    long millis = currentTime.getTime() - cmdSendTime.getTime();
                    count = (int) (millis / (1000));
                }

                receiveResponse_channelstat();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send hex data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to " + textViewDeviceName.getText().toString() + "..reconnecting", Toast.LENGTH_SHORT).show();
        }
    }
    public void sendSettingCommand(String command) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(command.getBytes());
            //    outputStream.flush();
                delayRoutine(200);
                Date cmdSendTime = Calendar.getInstance().getTime();
                int count = 0;
                while(command.length()<8 && count<15){
                    Date currentTime = Calendar.getInstance().getTime();
                    long millis = currentTime.getTime() - cmdSendTime.getTime();
                    count = (int) (millis / (1000));
                }

                receiveSettingResponse();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send hex data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to " + textViewDeviceName.getText().toString() + "..reconnecting", Toast.LENGTH_SHORT).show();
        }
    }
    public void sendCommand(String command) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(command.getBytes());
                outputStream.flush();
                delayRoutine(200);
                Date cmdSendTime = Calendar.getInstance().getTime();
                int count = 0;
                while(command.length()<8 && count<15){
                    Date currentTime = Calendar.getInstance().getTime();
                    long millis = currentTime.getTime() - cmdSendTime.getTime();
                    count = (int) (millis / (1000));
                }

                receiveResponse();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send hex data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to " + textViewDeviceName.getText().toString() + "..reconnecting", Toast.LENGTH_SHORT).show();
        }
    }
    public void receiveResponse_channelstat(){
        byte[] buffer = new byte[1024];
        int bytesRead;
        int i;
        try{
            bytesRead = inputStream.read(buffer);
            if(bytesRead != -1){
                if(bytesRead>200){
                    for (i = 0; i <= bytesRead; i++) {
                        if (buffer[i] == 35) { // Example condition for finding a particular byte
                            // Start storing data from this point onwards
                            processReceivedData(buffer, i + 1, bytesRead);
                            break; // Exit the loop after processing
                        }
                    }
                    dataWithoutStartEnd = new byte[newData.length - 2];
                    System.arraycopy(newData, 1, dataWithoutStartEnd, 0, newData.length- 2);
                    Final_receivedData = new String(dataWithoutStartEnd,StandardCharsets.US_ASCII);
                    process_data_New(Final_receivedData);
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveResponse(){
    byte[] buffer = new byte[1024]; // Adjust the buffer size as needed
    int bytesRead;
    int i;
        try {
        bytesRead = inputStream.read(buffer);
        if (bytesRead != -1) {
            // Ensure there is enough data to remove start and end byte
            if (bytesRead > 485)

               {
                for (i = 0; i <= bytesRead; i++) {
                    if (buffer[i] == 35) { // Example condition for finding a particular byte
                        // Start storing data from this point onwards
                        processReceivedData(buffer, i + 1, bytesRead);
                        break; // Exit the loop after processing
                    }
                }
                dataWithoutStartEnd = new byte[newData.length - 2];
                System.arraycopy(newData, 1, dataWithoutStartEnd, 0, newData.length- 2);
                Final_receivedData = new String(dataWithoutStartEnd,StandardCharsets.US_ASCII);
                process_data_New(Final_receivedData);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    public void receiveSettingResponse(){
        byte[] buffer = new byte[1024]; // Adjust the buffer size as needed
        int bytesRead;
        try {
            bytesRead = inputStream.read(buffer);
            if (bytesRead != -1) {
                // Ensure there is enough data to remove start and end byte
                if (bytesRead > 2)
                {
                    processReceivedData(buffer, 0, bytesRead);
                    dataWithoutStartEnd = new byte[bytesRead - 2];
                    System.arraycopy(buffer, 1, dataWithoutStartEnd, 0, bytesRead- 2);
                    Final_receivedData = new String(dataWithoutStartEnd,StandardCharsets.US_ASCII);
                    process_data_New(Final_receivedData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processReceivedData(byte[] buffer, int startIndex, int length) {
        int Final_length = length - startIndex;
        // Example: Store data from startIndex to the end of the buffer
        if (Final_length == 0) {
            newData = new byte[length];
            System.arraycopy(buffer, Final_length, newData, 0, newData.length);
        }
        else{
            newData = new byte[Final_length];
            System.arraycopy(buffer, startIndex, newData, 0, newData.length);
        }
    }
        // Method to calculate the checksum by summing the bytes in an array
        public static int calculateChecksum(byte[] byteArray) {
            int checksum = 0;

            // Iterate through the byte array and sum the bytes
            for (byte b : byteArray) {
                checksum += (b & 0xFF); // Convert to unsigned int and add to checksum
            }

            // If you want to constrain the checksum to 16 bits (0-65535), you can do:
             checksum = checksum & 0xFFFF;

            return checksum;
        }
    public static String checksumToAscii(int checksum) {
        // Convert the checksum to a hexadecimal string
        String hexString = Integer.toHexString(checksum).toUpperCase();

        // If the hex string length is less than 4, pad with leading zeros
        while (hexString.length() < 4) {
            hexString = "0" + hexString;
        }

        // Convert the hexadecimal string to its ASCII representation
        StringBuilder asciiString = new StringBuilder();
        for (char hexChar : hexString.toCharArray()) {
            asciiString.append(hexChar);
        }

        return asciiString.toString();
    }

    private void store_str_values(String name, String val){

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,  MODE_PRIVATE).edit();
        editor.putString(name,val);
        editor.commit();
    }

    @SuppressLint("SuspiciousIndentation")
    private void connectToHC05() {
        hc05Device = bluetoothAdapter.getRemoteDevice(address);
        boolean connection_successful=false;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = hc05Device.createRfcommSocketToServiceRecord(HC05_UUID);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();

            //Toast.makeText(this, "Connected to HC-05", Toast.LENGTH_SHORT).show();
            connection_successful=true;


        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                public void run() {
                    dismissProgressDialog();
                    Toast.makeText(getBaseContext(),"Connection failed",Toast.LENGTH_SHORT).show();
                }
            });
            connection_successful=false;
            //
        }
        finally {
            if(connection_successful) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(),"Connected",Toast.LENGTH_SHORT).show();
                    }
                });
             //   startListening();
            //    openFragment(HomeFragment.newInstance("", ""));
//                if(myString==0) {
//                    openFragment(HomeFragment.newInstance("", ""));
//                    dashboard_on = false;
//                }else{
//                    openFragment(HomeFragment.newInstance("", ""));
//                   dashboard_on = false;
//                }
                dismissProgressDialog();
            }
        }
    }


    private void showMessege(String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button clicked
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel button clicked
                    }
                })
                .show();

    }
    private boolean stopThread;
    private byte[] buffer;
    private String hexString="";
    private void startListening() {
        stopThread = false;
        buffer = new byte[1024];

        new Thread(() -> {
            while (!stopThread) {
                try {
                    int bytes = inputStream.read(buffer);
                    byte[] receivedData = Arrays.copyOf(buffer, bytes);
                    String hexString = bytesToHex(receivedData);
                    process_data(hexString);
                    // Update UI with received data
                    runOnUiThread(() -> text_recv.setText(hexString));
                } catch (IOException e) {
                    e.printStackTrace();
                    stopThread = true;
                }
            }
        }).start();
    }
    private void receiveBluetoothComms() {

        buffer = new byte[1024];
        try {
            int bytes = inputStream.read(buffer);
            byte[] receivedData = Arrays.copyOf(buffer, bytes);
            hexString += bytesToHex(receivedData);
            if(hexString.length()>14) {
                process_data(hexString);
                // Update UI with received data
                runOnUiThread(() -> text_recv.setText(hexString));
                hexString ="";
            }
        } catch (IOException e) {
            e.printStackTrace();
            stopThread = true;
        }
    }
    private void process_data_New(String recvd_str_New) {
        String[] data_str = new String[512];
        int no_of_bytes = 0;
        String tmp = "";
        received_data_string_new = recvd_str_New;
    }
    private void process_data(String recvd_str)
    {
        String[] data_str = new String[512];
        int no_of_bytes=0;
        String tmp="";
        received_data_string=recvd_str;
        /*
        if(recvd_str.length()>1) {
            String dev_addr = recvd_str.substring(0, 2);
            if (dev_addr.equals("F7")) {            //address ok
                if(recvd_str.length()>4)
                {
                    String ctyp = recvd_str.substring(3, 5);
                    if(ctyp.equals("03"))           //command type ok
                    {
                        if(recvd_str.length()>6)
                        {
                            no_of_bytes = hexToDec(recvd_str.substring(6, 8));
                            int r_str_len = recvd_str.length();
                            if(r_str_len>(11 + (no_of_bytes * 3))) {
                                for (int i = 0; i < no_of_bytes/2; i++) {
                                    data_str[i*2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                                    data_str[i*2+1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                                    tmp += data_str[i*2] + data_str[i*2+1];
                                }
                                //String data_str = recvd_str.substring(9, 9 + no_of_bytes*3 + 1);
                                String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);;


                                byte[] hexBytes = hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                                int crcValue = calculateCRC(hexBytes);
                                String crc_str = Integer.toHexString(crcValue).toUpperCase();

                                if(crc_str.length()<4) crc_str = "0" + crc_str;
                                if(crc_str.length()==4) {
                                    crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                                    if (crc_str.equals(crc_recv)) {
                                        for (int i = 0; i < no_of_bytes / 2; i++)
                                            LED_status[i] = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1]);
                                    }
                                    else
                                    {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        */
//        if(recvd_str.length()>5) {
//            int no_of_bytes_of_data = Integer.parseInt(recvd_str.substring(4, 5));
//            //runOnUiThread(() -> recvText02.setText(recvd_str.substring(9,10 + no_of_bytes*2)));
//
//            if (dashboard_on) {
//                for (int i = 0; i < no_of_bytes_of_data; i++) {
//                    LED_status[i] = Integer.parseInt(recvd_str.substring(6 + i * 4, 10 + i * 4));
//                }
//            } else {
//
//            }
//        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X ", b));
        }
        return stringBuilder.toString();
    }

    private void sendHexDataToHC05(String hexString) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                byte[] hexBytes = hexStringToByteArray(hexString);
                outputStream.write(hexBytes);
                //Toast.makeText(this, "Hex data sent: " + hexString, Toast.LENGTH_SHORT).show();
                delayRoutine(500);
                Date cmdSendTime = Calendar.getInstance().getTime();
                int count = 0;
                while(hexString.length()<15 && count<15){
                    Date currentTime = Calendar.getInstance().getTime();
                    long millis = currentTime.getTime() - cmdSendTime.getTime();
                    count = (int) (millis / (1000));
                }
                receiveBluetoothComms();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send hex data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to "+textViewDeviceName.getText().toString() + "..reconnecting", Toast.LENGTH_SHORT).show();
            connectToHC05();

        }
    }

    public byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public int hexToDec(String hexString) {
        try {
            return Integer.parseInt(hexString, 16);
        } catch (NumberFormatException e) {
            // Handle the case when the hexString is invalid
            e.printStackTrace();
            return -1; // Or any other value to indicate an error
        }
    }

    public void FullScreencall() {
        bottomNavigation.setVisibility(View.GONE);
        constraintLayout.setBackgroundColor(Color.parseColor("#000000"));
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}