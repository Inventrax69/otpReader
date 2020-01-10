package com.myapp.otpreader;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements
        SMSReceiver.OTPReceiveListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SMSReceiver smsReceiver;
    TextView otp_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AppSignatureHelper appSignatureHashHelper = new AppSignatureHelper(this);

        // This code requires one time to get Hash keys do comment and share key
        //Log.i(TAG, "HashKeyY: " + appSignatureHashHelper.getAppSignatures().get(0));

        otp_txt = (TextView) findViewById(R.id.otp_txt);


        startSMSListener();
    }

    public static boolean isNumeric(String ValueToCheck)
    {
        try
        {
            Double result = Double.parseDouble(ValueToCheck);
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }


    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                    Toast.makeText(MainActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onOTPReceived(String otp) {
        showToast(otp);

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
        Toast.makeText(this, "OTP Time out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOTPReceivedError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }


    private void showToast(String msg) {

        String[] otp = msg.split(" ");

        for(String s : otp){

            if(isNumeric(s)){
                otp_txt.setText (s);
            }
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}