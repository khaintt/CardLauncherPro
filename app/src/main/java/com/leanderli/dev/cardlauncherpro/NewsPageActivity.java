package com.leanderli.dev.cardlauncherpro;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewsPageActivity extends AppCompatActivity {

    private static final int MY_REQUEST_APPWIDGET = 1;
    private static final int MY_CREATE_APPWIDGET = 2;
    private static final int HOST_ID = 1024;
    private static String TAG = "AddAppWidget";
    AppWidgetManager appWidgetManager = null;
    private Button btAddShortCut;
    // 装载Appwidget的父视图
    private LinearLayout linearLayout;
    private AppWidgetHost mAppWidgetHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        linearLayout = findViewById(R.id.linl_app_news_page_main);
        btAddShortCut = findViewById(R.id.btn_add_widget);

        //其参数hostid大意是指定该AppWidgetHost 即本Activity的标记Id， 直接设置为一个整数值吧 。
        mAppWidgetHost = new AppWidgetHost(NewsPageActivity.this, HOST_ID);

        //为了保证AppWidget的及时更新 ， 必须在Activity的onCreate/onStar方法调用该方法
        // 当然可以在onStop方法中，调用mAppWidgetHost.stopListenering() 停止AppWidget更新
        mAppWidgetHost.startListening();

        //获得AppWidgetManager对象
        appWidgetManager = AppWidgetManager.getInstance(NewsPageActivity.this);

        btAddShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示所有能创建AppWidget的列表 发送此 ACTION_APPWIDGET_PICK 的Action
                Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);

                //向系统申请一个新的appWidgetId ，该appWidgetId与我们发送Action为ACTION_APPWIDGET_PICK
                //  后所选择的AppWidget绑定 。 因此，我们可以通过这个appWidgetId获取该AppWidget的信息了

                //为当前所在进程申请一个新的appWidgetId
                int newAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                Log.i(TAG, "The new allocate appWidgetId is ----> " + newAppWidgetId);

                //作为Intent附加值 ， 该appWidgetId将会与选定的AppWidget绑定
                pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);

                //选择某项AppWidget后，立即返回，即回调onActivityResult()方法
                startActivityForResult(pickIntent, MY_REQUEST_APPWIDGET);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //直接返回，没有选择任何一项 ，例如按Back键
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case MY_REQUEST_APPWIDGET:
                Log.i(TAG, "MY_REQUEST_APPWIDGET intent info is -----> " + data);
                int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                Log.i(TAG, "MY_REQUEST_APPWIDGET : appWidgetId is ----> " + appWidgetId);

                //得到的为有效的id
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    //查询指定appWidgetId的 AppWidgetProviderInfo对象 ， 即在xml文件配置的<appwidget-provider />节点信息
                    AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

                    //如果配置了configure属性 ， 即android:configure = "" ，需要再次启动该configure指定的类文件,通常为一个Activity
                    if (appWidgetProviderInfo.configure != null) {

                        Log.i(TAG, "The AppWidgetProviderInfo configure info -----> " + appWidgetProviderInfo.configure);

                        //配置此Action
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                        intent.setComponent(appWidgetProviderInfo.configure);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        startActivityForResult(intent, MY_CREATE_APPWIDGET);
                    } else {
                        onActivityResult(MY_CREATE_APPWIDGET, RESULT_OK, data);  //参数不同，简单回调而已
                    }
                }
                break;
            case MY_CREATE_APPWIDGET:
                completeAddAppWidget(data);
                break;
            default:
                break;
        }
    }

    //向当前视图添加一个用户选择的
    private void completeAddAppWidget(Intent data) {
        Bundle extra = data.getExtras();
        int appWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        //等同于上面的获取方式
        //int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , AppWidgetManager.INVALID_APPWIDGET_ID) ;

        Log.i(TAG, "completeAddAppWidget : appWidgetId is ----> " + appWidgetId);

        if (appWidgetId == -1) {
            Toast.makeText(NewsPageActivity.this, "添加窗口小部件有误", Toast.LENGTH_SHORT);
            return;
        }

        AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(NewsPageActivity.this, appWidgetId, appWidgetProviderInfo);

        //linearLayout.addView(hostView) ;

        int widget_minWidht = appWidgetProviderInfo.minWidth;
        int widget_minHeight = appWidgetProviderInfo.minHeight;
        //设置长宽  appWidgetProviderInfo 对象的 minWidth 和  minHeight 属性
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(widget_minWidht, widget_minHeight);
        //添加至LinearLayout父视图中
        linearLayout.addView(hostView, linearLayoutParams);
    }
}
