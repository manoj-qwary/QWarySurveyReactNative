package com.qwary.survey.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.qwary.R;
import com.qwary.survey.fragment.SurveyFragment;
import com.qwary.survey.helper.DBHelper;
import com.qwary.survey.interfaces.OnWebViewResponseListener;

import org.json.JSONObject;

public class SurveyActivity extends AppCompatActivity implements OnWebViewResponseListener {

    private DBHelper dbHelper;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("domain")) {
                dbHelper = new DBHelper(getApplicationContext());

                if(!dbHelper.surveyExist(extras.getString("domain"), extras.getString("token"))) {
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SurveyFragment surveyFragment = new SurveyFragment();
                    surveyFragment.setIntent(getIntent());

                    fragmentTransaction.add(R.id.surveyContainer, surveyFragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Survey already taken", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResponseEvent(JSONObject data) {

    }
}