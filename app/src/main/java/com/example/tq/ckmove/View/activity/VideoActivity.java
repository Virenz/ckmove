package com.example.tq.ckmove.View.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tq.ckmove.R;
import com.example.tq.ckmove.base.VerticalProgressBar;
import com.example.tq.ckmove.base.WebViewMod;
import com.example.tq.ckmove.until.EscapeUtils;
import com.example.tq.ckmove.until.MyStringRequest;
import com.example.tq.ckmove.until.RequestQueueSingleton;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/7/11/011.
 */

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";

    private WebViewMod webView;
    private View title;
    private ImageButton imgBack;
    private String url;
    private Matcher mMatcher;
    private String mFirst_url;
    private Boolean touchCancel = false;


    /** 屏幕调节亮度和音量 */
    private AudioManager mAudioManager;
    /** 最大声音 */
    private int mMaxVolume;
    /** 当前声音 */
    private int mVolume = -1;
    /** 当前亮度 */
    private float mBrightness = -1f;
    private GestureDetector mGestureDetector;
    private VerticalProgressBar vpb_left,vpb_right;
    private int leftProgress = 0,rightProgress = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏

        setContentView(R.layout.activity_play);

        webView = new WebViewMod(getApplicationContext());
        FrameLayout view =  (FrameLayout) findViewById(R.id.videowebview);
        view.addView(webView);
        initWebView();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        vpb_left = (VerticalProgressBar)findViewById(R.id.vpb_left);
        vpb_right = (VerticalProgressBar)findViewById(R.id.vpb_right);

        // 设置title_bar功能：返回 标题 剧集
        title = findViewById(R.id.title_bar);
        String title = getIntent().getStringExtra("title");//传进来视频链接
        String episode = getIntent().getStringExtra("episode");//传进来视频链接;
        TextView title_tv = (TextView)findViewById(R.id.title);
        title_tv.setText(title);
        TextView episode_tv = (TextView)findViewById(R.id.setting);
        episode_tv.setText("剧集: " + episode);
        imgBack = (ImageButton)findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        webView.resumeTimers();
        webView.onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }

    private void initData() {

        RequestQueueSingleton queue = RequestQueueSingleton.getInstance(this);
        queue.requestAsyn(url, 0, null, new RequestQueueSingleton.ReqCallBack<String>() {
                    /**
                     * 响应成功
                     *
                     * @param response
                     */
                    @Override
                    public void onReqSuccess(String response) {
                        Log.d(TAG, response);
                        String regEx = "unescape\\(\"(.+?)\",0,0\\)";
                        Pattern pattern = Pattern.compile(regEx);
                        mMatcher = pattern.matcher(response);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (mMatcher.find()) {
                                    Log.e(TAG, mMatcher.group(1));
                                    mFirst_url = mMatcher.group(1);
                                    final String requestUrl = "javascript:getUrl('"+mFirst_url+"')";
                                    Log.e(TAG, requestUrl );
                                    String unescape = EscapeUtils.unescape(requestUrl);
                                    Log.e(TAG, "unescape: "+unescape);
                                    webView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            webView.loadUrl(requestUrl);
                                        }
                                    });

                                }
                            }
                        }).start();
                    }

                    /**
                     * 响应失败
                     *
                     * @param errorMsg
                     */
                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(VideoActivity.this, "网络不稳定!加载失败!请稍后重试!", Toast.LENGTH_SHORT).show();
                    }
                });
        /*RequestQueue queue = Volley.newRequestQueue(this);
        MyStringRequest stringRequest = new MyStringRequest(url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                String regEx = "unescape\\(\"(.+?)\",0,0\\)";
                Pattern pattern = Pattern.compile(regEx);
                mMatcher = pattern.matcher(response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMatcher.find()) {
                            Log.e(TAG, mMatcher.group(1));
                            mFirst_url = mMatcher.group(1);
                            final String requestUrl = "javascript:getUrl('"+mFirst_url+"')";
                            Log.e(TAG, requestUrl );
                            String unescape = EscapeUtils.unescape(requestUrl);
                            Log.e(TAG, "unescape: "+unescape);
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(requestUrl);
                                }
                            });

                        }
                    }
                }).start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);

                Toast.makeText(VideoActivity.this, "网络不稳定!加载失败!请稍后重试!", Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(stringRequest);*/

    }

    /** 展示网页界面 **/
    @JavascriptInterface
    public void initWebView() {
        WebChromeClient wvcc = new WebChromeClient();
        //webView.setHorizontalScrollBarEnabled(false);//水平不显示
        //webView.setVerticalScrollBarEnabled(false); //垂直不显示
        url = getIntent().getStringExtra("url");//传进来视频链接
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(this, "android");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // 让JavaScript可以自动打开windows
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setUseWideViewPort(true); // 关键点
        //webSettings.setAllowFileAccess(false); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 针对分辨率进行适配全屏
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }else if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }

        webView.setWebChromeClient(wvcc);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(url == null) return false;
                try {
                    if(url.startsWith("http://") || url.startsWith("https://")|| url.startsWith("file:///")
                        //其他自定义的scheme
                            ) {
                        webView.loadUrl(url);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String urllink) {

                String url = urllink.toLowerCase();
                if((url.endsWith(".png")||url.endsWith(".gif")||url.endsWith(".jpg")||url.endsWith(".jpeg"))&&
                        (!url.contains("play.png"))&&(!url.contains("load.gif"))&&(!url.contains("close"))){
                    System.out.println(url);
                    try {
                        InputStream localCopy = getResources().openRawResource(+R.drawable.line);
                        return new WebResourceResponse("image/*", "UTF-8", localCopy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                initData();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }

        };
        webView.setWebViewClient(wvc);

        webView.setWebChromeClient(new WebChromeClient() {

            //网络请求部分
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
            @Override
            public boolean onJsAlert(WebView mWebView, String url, String message, JsResult result) {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(mWebView.getContext());
                localBuilder.setMessage(message).setPositiveButton("确定", null);
                localBuilder.setCancelable(false);
                localBuilder.create().show();

                //注意:
                //必须要这一句代码:result.confirm()表示:
                //处理结果为确定状态同时唤醒WebCore线程
                //否则不能继续点击按钮
                result.confirm();
                return true;
            }

            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i("ansen", "网页标题:" + title);
            }

        });

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // 返回键退回
            finish();
            return true;
        }

        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.clearCache(true); //清空缓存
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ViewGroup webViewLayout = ((ViewGroup) webView.getParent());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (webViewLayout != null) {
                    webViewLayout.removeView(webView);
                }
                webView.removeAllViews();
                webView.destroy();
            }else {
                webView.removeAllViews();
                webView.destroy();
                if (webViewLayout != null) {
                    webViewLayout.removeView(webView);
                }
            }
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView = null;
        }
        super.onDestroy();
        System.exit(0);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /** 手势结束 */
    private void endGesture() {
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 显示
        rightProgress = (int)(mVolume * 100 / mMaxVolume);
        vpb_right.setProgress(rightProgress);

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    /** 显示标题 */
    private void showTitle() {
        if (!touchCancel) {
            title.setVisibility(View.VISIBLE);
            touchCancel = true;
        }
        else {
            title.setVisibility(View.GONE);
            touchCancel = false;
        }
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /** 双击 */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            showTitle();
            return super.onSingleTapConfirmed(e);
        }

        /** 滑动 */
        @SuppressWarnings("deprecation")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getRawX(), mOldY = e1.getRawY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (mOldX > windowWidth * 1.0 / 2)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth * 1.0 / 2)// 左边滑动
                onBrightnessSlide(distanceY / windowHeight);
                //onSystemBrightnessSlide(distanceY / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /** 定时隐藏 */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            vpb_left.setVisibility(View.GONE);
            vpb_right.setVisibility(View.GONE);

        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
            // 显示
            rightProgress = (int)(mVolume * 100 / mMaxVolume);
            vpb_right.setProgress(rightProgress);
        }
        vpb_right.setVisibility(View.VISIBLE);

        int index = (int)(percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        int progress = rightProgress + (int)(percent * 100);
        if(progress > 100){
            progress = 100;
        }else if(progress < 0){
            progress = 0;
        }
        vpb_right.setProgress(progress);
    }

    /**
     * 滑动改变屏幕亮度
     *
     * @param percent
     */
    public void onBrightnessSlide(float percent) {
        mBrightness = getWindow().getAttributes().screenBrightness;
        if (mBrightness <= 0.00f)
            mBrightness = 0.50f;
        if (mBrightness < 0.01f)
            mBrightness = 0.01f;

        // 显示
        vpb_left.setVisibility(View.VISIBLE);
        leftProgress = (int) (mBrightness * 100);
        vpb_left.setProgress(leftProgress);
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        leftProgress = leftProgress + (int) (percent * 100);
        if (leftProgress > 100) {
            leftProgress = 100;
        } else if (leftProgress < 0) {
            leftProgress = 0;
        }
        vpb_left.setProgress(leftProgress);
    }

    /**
     * 滑动改变系统亮度
     * @param percent
     */
    public void onSystemBrightnessSlide(float percent){
        try {
            mBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(mBrightness < 0){
            mBrightness = 0;
        }
        // 显示  系统屏幕亮度最大值为255
        vpb_left.setVisibility(View.VISIBLE);
        leftProgress = (int) (mBrightness * 100 / 255);
        vpb_left.setProgress(leftProgress);

        int brightness = (int)(mBrightness + percent * 255);
        if(brightness > 255){
            brightness = 255;
        }else if(brightness < 0){
            brightness = 0;
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brightness);

        leftProgress = leftProgress + (int) (percent * 100);
        if (leftProgress > 100) {
            leftProgress = 100;
        } else if (leftProgress < 0) {
            leftProgress = 0;
        }
        vpb_left.setProgress(leftProgress);
    }

    //获取系统亮度  Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
    //设置系统亮度  Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,systemBrightness);
    /** 开启自动亮度调节后，改不了系统亮度(可以改变屏幕亮度)，要先关闭自动亮度调节 ***/
    /** * 停止自动亮度调节 */

    public void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
    /**
     * * 开启亮度自动调节 *
     *
     * @param activity
     */

    public void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}

