package com.hitech.levelstate;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceSelectionActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "hitechPrefsELS";
    Context mContext;
    MainActivity mainActivity;
    GridLayout mGridLayout;
    RecyclerView mRecyclerView;
    String remove_address;
    String remove_device_Name;
    Set<String> setNames;
    Set<String> setAddrs;
    SharedPreferences prefs;
    CardView cvAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selection);

        Intent newint = getIntent();
        remove_address = newint.getStringExtra(ItemAdapter.REMOVE_ADDRESS);
        remove_device_Name = newint.getStringExtra(ItemAdapter.REMOVE_DEVICE_NAME);

        setNames = new HashSet<String>();
        setAddrs = new HashSet<String>();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        setNames = prefs.getStringSet("DEVICE_NAME_LIST", null);

        setAddrs = prefs.getStringSet("ADDR_LIST", null);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyle_view);

        cvAdd = (CardView) findViewById(R.id.cardviewAdd);

        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceSelectionActivity.this, AddDeviceActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        if(remove_address!=null)
        {
            //msg("remove");
            remove_room(remove_device_Name,remove_address);
        }

        load_devices();


    }

    private void remove_room(String devicename,String deviceaddr)
    {
        //
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,  MODE_PRIVATE).edit();
        //Set<String> set = new HashSet<String>();

        //Set<String> setNames = prefs.getStringSet("ROOM_NAME_LIST", null);


        setNames.remove(devicename);
        editor.remove("DEVICE_NAME_LIST");
        editor.apply();
        editor.remove("ADDR_LIST");
        editor.apply();
        if(setNames.size()==0) {}
        else                   editor.putStringSet("DEVICE_NAME_LIST",setNames);
        editor.apply();

        editor = getSharedPreferences(MY_PREFS_NAME,  MODE_PRIVATE).edit();
        //Set<String> setAddrs = prefs.getStringSet("ADDR_LIST", null);
        setAddrs.remove(deviceaddr);
        if(setAddrs.size()==0)  {}
        else                    editor.putStringSet("ADDR_LIST", setAddrs);
        editor.apply();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Set<String> setAddrs = prefs.getStringSet("ADDR_LIST", null);
//        msg("Size="+setAddrs.size());

    }

    private void load_devices()
    {

        List<String> itemList = new ArrayList<>();

        //Toast.makeText(this, remove_device_Name, Toast.LENGTH_SHORT).show();

        if (setNames != null) {
            for (String devices : setNames) {

                itemList.add(devices);
            }
        }

        List<String> itemListAddr = new ArrayList<>();

        if (setAddrs != null) {
            for (String device_addrs : setAddrs) {

                itemListAddr.add(device_addrs);
            }
        }
        ItemAdapter itemAdapter = new ItemAdapter(itemList,itemListAddr, DeviceSelectionActivity.this);
        GridLayoutManager gridlayoutmanager = new GridLayoutManager(DeviceSelectionActivity.this, 2);
        mRecyclerView.setAdapter(itemAdapter);
        mRecyclerView.setLayoutManager(gridlayoutmanager);

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}