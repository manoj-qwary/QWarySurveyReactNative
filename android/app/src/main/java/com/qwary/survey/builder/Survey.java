package com.qwary.survey.builder;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.qwary.survey.activity.SurveyActivity;
import com.qwary.survey.helper.DBHelper;
import com.qwary.survey.model.ParamModel;

import java.util.ArrayList;

public class Survey {

    private String domain;
    private String token;
    private ArrayList<ParamModel> param;
    private Boolean loader;
    private Boolean modal = false;
    private long startAfter;
    private long repeatInterval;

    private Activity activity;

    private DBHelper dbHelper;

    public Survey(String domain, String token, Boolean loader, Boolean modal, long startAfter, long repeatInterval, Activity activity) {
        this.domain = domain;
        this.token = token;
        this.loader = loader;
        this.modal = modal;
        this.startAfter = startAfter;
        this.repeatInterval = repeatInterval;
        this.activity = activity;

        Intent intent = new Intent(this.activity, SurveyActivity.class);
        intent.putExtra("domain", this.domain);
        intent.putExtra("token", this.token);
        intent.putExtra("loader", this.loader);
        intent.putExtra("modal", this.modal);
        intent.putExtra("startAfter", this.startAfter);
        intent.putExtra("repeatInterval", this.repeatInterval);
        this.activity.startActivity(intent);
    }

    public Survey(Builder builder) {
        this.activity = builder.activity;
        this.domain = builder.domain;
        this.token = builder.token;
        this.param = builder.param;
        this.loader = builder.loader;
        this.modal = builder.modal;
        this.startAfter = builder.startAfter;
        this.repeatInterval = builder.repeatInterval;

        Intent intent = new Intent(this.activity, SurveyActivity.class);
        intent.putExtra("prepare", false);
        intent.putExtra("domain", this.domain);
        intent.putExtra("token", this.token);
        if (!this.param.isEmpty()) {
            for (int i = 0; i < this.param.size(); i++) {
                String[] temp = new String[]{this.param.get(i).getKey(), this.param.get(i).getValue()};
                intent.putExtra("param" + i, temp);
            }
        }
        intent.putExtra("loader", this.loader);
        intent.putExtra("modal", this.modal);
        intent.putExtra("startAfter", this.startAfter);
        intent.putExtra("repeatInterval", this.repeatInterval);
        this.activity.startActivity(intent);
    }

    public Survey(Builder builder, boolean prepare) {
        this.activity = builder.activity;
        this.domain = builder.domain;
        this.token = builder.token;
        this.param = builder.param;
        this.loader = builder.loader;
        this.modal = builder.modal;
        this.startAfter = builder.startAfter;
        this.repeatInterval = builder.repeatInterval;

        Intent intent = new Intent(this.activity, SurveyActivity.class);
        intent.putExtra("prepare", prepare);
        intent.putExtra("domain", this.domain);
        intent.putExtra("token", this.token);
        intent.putExtra("loader", this.loader);
        intent.putExtra("modal", this.modal);
        intent.putExtra("startAfter", this.startAfter);
        intent.putExtra("repeatInterval", this.repeatInterval);
        this.activity.startActivity(intent);
    }

    public Survey(String token, Builder builder) {
        dbHelper = new DBHelper(builder.activity);
        if (dbHelper.surveyExist(token)) {
            dbHelper.deleteSurvey(token);
            Toast.makeText(builder.activity, "Survey Deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(builder.activity, "Survey not exists", Toast.LENGTH_SHORT).show();

        }
    }

    public static class Builder {

        private Activity activity;
        private String domain;
        private String token;
        private ArrayList<ParamModel> param;
        private Boolean loader;
        private Boolean modal = false;
        private long startAfter = 0L;
        private long repeatInterval = 0L;

        public Builder() {
            this.param = new ArrayList<>();
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setParam(String key, String value) {
            this.param.add(new ParamModel(key, value));
            return this;
        }

        public Builder setLoader(Boolean loader) {
            this.loader = loader;
            return this;
        }

        public Builder setModal(Boolean modal) {
            this.modal = modal;
            return this;
        }

        public Builder setStartAfter(long startAfter) {
            this.startAfter = startAfter;
            return this;
        }

        public Builder setRepeatInterval(long repeatInterval) {
            this.repeatInterval = repeatInterval;
            return this;
        }

        public Survey prepare() {
            return new Survey(this, true);
        }

        public Survey showNow() {
            return new Survey(this);
        }

        public Survey clear(String token) {
            return new Survey(token, this);
        }
    }
}
