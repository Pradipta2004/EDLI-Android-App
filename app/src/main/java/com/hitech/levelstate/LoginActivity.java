package com.hitech.levelstate;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "hitechPrefsELS";
    BluetoothAdapter mBluetoothAdapter;
    EditText ed1, ed2;
    Button b1;
    boolean rem_flag,show_pass_flag;
    CheckBox chkrem,chkshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        b1 = (Button) findViewById(R.id.signin_btn);
        ed1 = (EditText) findViewById(R.id.txt_username);
        ed2 = (EditText) findViewById(R.id.txt_password);

        //ed1.setText("admin");
        //ed2.setText("admin");

        // TextView signUp_text = findViewById(R.id.signUp_text);
        chkrem = (CheckBox) findViewById(R.id.chkRemember);
        chkshow = (CheckBox) findViewById(R.id.chkshowPass);

        rem_flag=get_stored_bool_values("REMEMBER_FLAG");
        show_pass_flag=get_stored_bool_values("SHOW_PASSWORD_FLAG");




        if(show_pass_flag)
        {
            chkshow.setChecked(true);
            ed2.setTransformationMethod(null);
        }
        else {
            chkshow.setChecked(false);
            ed2.setTransformationMethod(new PasswordTransformationMethod());
        }

        if(rem_flag)
        {
            ed1.setText(get_stored_str_values("ADMIN_ID"));
            ed2.setText(get_stored_str_values("PASSWD"));
            chkrem.setChecked(true);
        }
        else
        {
            chkrem.setChecked(false);
            chkrem.setEnabled(false);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ed2.getText().toString().length()>0 && ed1.getText().toString().length()>0)
                    chkrem.setEnabled(true);
                else {
                    chkrem.setEnabled(false);
                    chkrem.setChecked(false);
                }
            }
        });

        ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ed2.getText().toString().length()>0 && ed1.getText().toString().length()>0)
                    chkrem.setEnabled(true);
                else {
                    chkrem.setEnabled(false);
                    chkrem.setChecked(false);
                }
            }
        });
        chkshow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store_bool_values("SHOW_PASSWORD_FLAG", true);
                    ed2.setTransformationMethod(null);
                }
                else
                {
                    store_bool_values("SHOW_PASSWORD_FLAG", false);
                    ed2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        chkrem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && ed1.getText().toString().length()>0 && ed2.getText().toString().length()>0) {
                    store_bool_values("REMEMBER_FLAG", true);
                    store_str_values("ADMIN_ID",ed1.getText().toString().trim());
                    store_str_values("PASSWD",ed2.getText().toString().trim());
                }
                else {
                    store_bool_values("REMEMBER_FLAG", false);
                    store_str_values("ADMIN_ID", "");
                    store_str_values("PASSWD", "");
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().equals("admin") && ed2.getText().toString().equals("levelstate@1234")) {

                    Intent intent = new Intent(LoginActivity.this, DeviceSelectionActivity.class);
                    startActivity(intent);
                    finish();
                }

                else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void store_bool_values(String name, boolean val) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(name, val);
        editor.commit();
    }

    private boolean get_stored_bool_values(String namex) {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean restoredText = prefs.getBoolean(namex, false);
        return restoredText;
    }
    private void store_str_values(String name, String val) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString(name, val);
        editor.commit();
    }
    private String get_stored_str_values(String namex) {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(namex, null);
        if (restoredText != null) {
            String name = prefs.getString(namex, "No name defined");//"No name defined" is the default value.
            return name;
        } else
            return null;
    }


}