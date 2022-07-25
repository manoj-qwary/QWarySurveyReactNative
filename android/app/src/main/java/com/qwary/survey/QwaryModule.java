package com.qwary.survey;

import android.app.Activity;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.qwary.survey.builder.Survey;

import java.util.concurrent.TimeUnit;

public class QwaryModule extends ReactContextBaseJavaModule {

    Activity mActivity;

    public QwaryModule(ReactApplicationContext context) {
        super(context);
        mActivity = getCurrentActivity();

    }

    //Mandatory function getName that specifies the module name
    @Override
    public String getName() {
        return "Survey";
    }

    //Custom function that we are going to export to JS
    @ReactMethod
    public void runSurvey(String message, Callback cb) {
        Toast.makeText(getReactApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();

        Survey vn = new Survey.Builder()
                .setActivity(mActivity)
                .setDomain("https://survey.qwary.com/form/")
                .setToken("3Y6A066rNaDrV17TDvQBN6VHVe0P5jrXTClr9qVwer0=")
                .setParam("email", "jondoe@acmeinc.com")
                .setParam("planId", "trial1")
                .setLoader(true)
                .setModal(false)
                .setStartAfter(TimeUnit.HOURS.toMillis(1L))
                .showNow();
        try{
            cb.invoke(null, android.os.Build.MODEL);
        }catch (Exception e){
            cb.invoke(e.toString(), null);
        }
    }
}
