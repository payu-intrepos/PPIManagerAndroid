package com.payu.payuppisdksample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.payu.onepayujssdk.OnePayUJSSDK;
import com.payu.onepayujssdk.listeners.OnePayUJSHashGenerationListener;
import com.payu.onepayujssdk.listeners.OnePayUJSListener;
import com.payu.onepayujssdk.models.OnePayUJSHashes;
import com.payu.onepayujssdk.models.OnePayUJSParams;

import java.security.MessageDigest;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnePayUJSListener {

    private static final String TAG = "OnePayUJSSDK";
    String merchantKey = "smsplus";
    String salt = "1b1b0";
    OnePayUJSParams onePayUJSParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public OnePayUJSParams prepareParams() {
        String requestId = System.currentTimeMillis() + "payu";
        onePayUJSParams = new OnePayUJSParams("PROD", merchantKey, "9528340384", "OLW", "1003097", requestId, "https://test.payu.in", null, null);
        return onePayUJSParams;
    }

    public void openCards(View view) {
        OnePayUJSSDK.showCards(this, prepareParams(), this);
    }

    private String calculateHash(String hashString) {
        Log.d(TAG, "calculateHash: " + hashString);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            return getHexString(mdbytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getHexString(byte[] array) {
        StringBuilder hash = new StringBuilder();
        for (byte hashByte : array) {
            hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hash.toString();
    }

    @Override
    public void generateHash(@NonNull HashMap<String, String> dataMap, @NonNull OnePayUJSHashGenerationListener onePayUJSHashGenerationListener) {
        String hashName = dataMap.get("hashName");
        String hashString = dataMap.get("hashString");
        Log.d(TAG, "generateHash: " + hashName + "-----------" + hashString);
        // Do not use this function to calculate hash, calculate hash on your server only
        String hash = calculateHash(hashString + salt);
        if (!TextUtils.isEmpty(hash)) {
            HashMap<String, String> hashMap = new HashMap();
            hashMap.put(hashName, hash);
            onePayUJSHashGenerationListener.onHashGenerated(hashMap);
        }
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel: ");
    }

    @Override
    public void onError(int i, @NonNull String s) {
        Log.d(TAG, "onError: " + i + "-------------" + s);
    }
}