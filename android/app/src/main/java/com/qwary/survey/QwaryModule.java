package com.qwary.survey;

import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class QwaryModule extends ReactContextBaseJavaModule {

    public QwaryModule(ReactApplicationContext context) {
        super(context);
    }

    //Mandatory function getName that specifies the module name
    @Override
    public String getName() {
        return "Device";
    }

    //Custom function that we are going to export to JS
    @ReactMethod
    public void runToast(Callback cb) {
        Toast.makeText(getReactApplicationContext(), "This is Makhan toast", Toast.LENGTH_SHORT).show();
        try{
            cb.invoke(null, android.os.Build.MODEL);
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }
}
