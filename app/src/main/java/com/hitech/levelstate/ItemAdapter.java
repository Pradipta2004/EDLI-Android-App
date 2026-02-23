package com.hitech.levelstate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String MY_PREFS_NAME = "hitechPrefsELS";
    public static String DEVICE_NAME = "device_name";
    private List<String> mItemList;
    private List<String> mAddrList;
    private Context mContext;
    public static String REMOVE_ADDRESS = "raddress";
    public static String REMOVE_DEVICE_NAME = "rname";
    ProgressDialog progressDialog;
    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int PERMISSION_REQUEST_CODE = 100;
    public ItemAdapter(List<String> itemList, List<String> addrList, Context context) {

        this.mItemList = itemList;
        this.mAddrList = addrList;
        this.mContext = context;

    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);


        progressDialog = new ProgressDialog(mContext);
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.setMessage("Connecting..."); // Set the message to be displayed
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by pressing outside of it
        // progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Set the style to be a spinning circle
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.addContentView(new ProgressBar(mContext), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));




        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String item = mItemList.get(position);
        String itemAddr = mAddrList.get(position);
        holder.textView.setText(item);
        holder.textViewAddr.setText(itemAddr);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            // open another activity on item click
                //showProgressDialog();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(EXTRA_DEVICE_ADDRESS, mAddrList.get(position).toString());
                intent.putExtra(DEVICE_NAME, mItemList.get(position).toString());
                // put image data in Intent
                mContext.startActivity(intent); // start Intent
            }
        });

        holder.imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete device")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Toast.makeText(mContext, holder.textViewAddr.getText().toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, DeviceSelectionActivity.class);
                                intent.putExtra(REMOVE_ADDRESS, mAddrList.get(position).toString());
                                intent.putExtra(REMOVE_DEVICE_NAME, mItemList.get(position).toString());
                                mContext.startActivity(intent);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }


    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
/*
    private void showProgressDialog(String message) {
        TextView messageTextView = progressDialog.findViewById(R.id.messageTextView);
        ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);

        messageTextView.setText(message);

        progressDialog.show();
    }
*/
    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private int dptopx(int dp) {

        float px = dp * mContext.getResources().getDisplayMetrics().density;
        return (int)px;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder  {
        private CardView cardView;
        private TextView textView,textViewAddr;
        private ImageButton imagebtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recyle_view);
            textView=itemView.findViewById(R.id.text_view);
            textViewAddr=itemView.findViewById(R.id.text_view_addr);
            imagebtn = itemView.findViewById(R.id.remove_btn);

        }
    }

}
