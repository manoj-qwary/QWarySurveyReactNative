package com.qwary.survey.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qwary.survey.helper.DBHelper;
import com.qwary.survey.interfaces.OnWebViewResponseListener;
import com.qwary.survey.model.ParamModel;
import com.qwary.survey.model.SurveyModel;
import com.qwary.survey.notification.NotifyMe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SetJavaScriptEnabled")
public final class SurveyFragment extends Fragment {

    public static final String DEBUG_LOG = "DEBUG_LOG";

    private DBHelper dbHelper;

    private ProgressBar progressBar;
    private ObjectAnimator progressBarAnimator;

    private OnWebViewResponseListener responseListener;

    private ProgressBar mProg;

    private String domain;
    private String token;
    private ArrayList<ParamModel> param;

    private Boolean loader;
    private Boolean modal;
    private Boolean isPrepare = false;

    private Boolean isAlreadyTaken = false;
    private long startAfter;
    private long repeatInterval;

    Calendar now = Calendar.getInstance();

    WebView qwWebView;

    public SurveyFragment() {
        param = new ArrayList<>();
        //  public, no-arg constructor
    }

    public void setIntent(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            this.domain = extras.getString("domain");
            this.token = extras.getString("token");
            if (extras.containsKey("param0")) {
                int i = 0;
                while (true) {
                    if (extras.getStringArray("param" + i) != null) {
                        param.add(new ParamModel(extras.getStringArray("param" + i)[0], extras.getStringArray("param" + i)[1]));
                        i++;
                    } else {
                        break;
                    }
                }
            }
            this.loader = extras.getBoolean("loader");
            this.modal = extras.getBoolean("modal");
            this.isPrepare = extras.getBoolean("prepare");
            this.startAfter = extras.getLong("startAfter");
            this.repeatInterval = extras.getLong("repeatInterval");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        responseListener = (OnWebViewResponseListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());

        if (isPrepare) {
            if (this.startAfter > 0L) {

                Date date = new Date(now.getTimeInMillis() + startAfter);

                new NotifyMe.Builder(getActivity().getApplicationContext())
                        .title(this.domain)
                        .token(this.token)
                        .loader(loader ? 1 : 0)
                        .modal(modal ? 1 : 0)
                        .content("Survey need to provided")
                        .color(0, 0, 0, 255)
                        .led_color(255, 255, 255, 255)
                        .time(date)
                        .key("test")
                        .addAction(new Intent(), "Dismiss", true, false)
                        .large_icon(android.R.mipmap.sym_def_app_icon)
                        .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                        .build();

//                getActivity().finish();
            }
        } else {
            dbHelper = new DBHelper(getActivity().getApplicationContext());

            if (this.loader) {
                progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
                progressBar.setMax(100);
                progressBar.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6, Gravity.TOP));
            }

            qwWebView = new WebView(getActivity());
            qwWebView.getSettings().setJavaScriptEnabled(true);
            qwWebView.getSettings().setDomStorageEnabled(true);
            qwWebView.getSettings().setDefaultTextEncodingName("utf-8");
//        qwWebView.addJavascriptInterface(new JsObject(), "SsAndroidSdk");

        /*qwWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(QWSurveyView.QW_THANK_YOU_BASE_URL) || survey.getThankYouRedirect() == false) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                url = url + "?mode=app";
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
            public void onPageFinished(WebView view, String url) {
                Log.i("", "");

            }
        });*/

            qwWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if (consoleMessage.message().contains("{")) {
                        if (isJSONValid(consoleMessage.message().split(":: ")[1])) {
                            responseListener.onResponseEvent(toJSON(consoleMessage.message().split(":: ")[1]));
                            if (!dbHelper.surveyExist(domain, token)) {
                                dbHelper.insertSurvey(new SurveyModel(domain, token, loader, modal, isAlreadyTaken, startAfter, repeatInterval));
                            }
                        }
                    }
                    return super.onConsoleMessage(consoleMessage);

                }

            /*@Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                Log.d("alert", message);
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
            }*/

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (loader) {
                        if (newProgress == 100) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        progressBarAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), newProgress);
                        progressBarAnimator.setDuration(300);
                        progressBarAnimator.start();
                    }
                }
            });

            if (!param.isEmpty()) {
                StringBuffer buffer = new StringBuffer(this.domain + this.token + "?");
                for (int i = 0; i < param.size(); i++) {
                    if (i == 0) {
                        buffer.append(param.get(i).getKey() + "=" + URLEncoder.encode(param.get(i).getValue()));
                    } else {
                        buffer.append("&" + param.get(i).getKey() + "=" + URLEncoder.encode(param.get(i).getValue()));
                    }
                }
                qwWebView.loadUrl(buffer.toString());
            } else {
                qwWebView.loadUrl(this.domain + this.token);
            }
//        qwWebView.loadUrl("javascript:alert('hi')");


            frameLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (modal) {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, frameLayout.getHeight() / 2));
                        lp.gravity = Gravity.BOTTOM;
                        frameLayout.getForegroundGravity();
                        frameLayout.setLayoutParams(lp);
                    }
                }
            });

            frameLayout.addView(qwWebView);
            if (loader) {
                frameLayout.addView(progressBar);
            }
        }

        return frameLayout;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private JSONObject toJSON(String text) {
        JSONObject json;
        try {
            json = new JSONObject(text);
        } catch (JSONException e) {
            Log.e(DEBUG_LOG, e.getStackTrace().toString());
            return null;
        }
        return json;
    }

    /*private class JsObject {
        @JavascriptInterface
        public void shareData(String data) {
            responseListener.onResponseEvent(toJSON(data));
        }
    }*/
}
