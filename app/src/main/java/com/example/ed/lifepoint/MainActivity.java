package com.example.ed.lifepoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends Activity implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private ZXingScannerView mScannerView;
    Button btn;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.button1);
        txt = (TextView)findViewById(R.id.textView1);

    }

    public void onClick(View v)
    {
        txt = (TextView)findViewById(R.id.textView1);
        txt.setText("It has changed.");
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    protected void onPause()
    {
        super.onPause();
        //mScannerView.stopCamera();
    }

    public void handleResult(Result result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(result.getText().equals("Nurse Joy"))
        {
            builder.setTitle("Welcome Caregiver:");
            builder.setMessage(result.getText());
            AlertDialog dialog = builder.create();
            dialog.show();

            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }
        else
        {
            builder.setTitle("Incorrect ID");
            builder.setMessage(result.getText());
            AlertDialog dialog = builder.create();
            dialog.show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        //mScannerView.resumeCameraPreview(this);
    }
}
