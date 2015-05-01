package chenwei.example.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * TODO: 心跳包
 * TODO: service 重启
 */
public class PushService extends Service {

    private final String TAG = "chenwei.PushService";

    public static final String ACTION_CONNECT = "com.chenwei.service.connect_socket";

    //通知管理器
    private NotificationManager nm;
    private Notification baseNF;
    //BASE Notification ID
    private int Notification_ID_BASE = 110;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);

            int what = msg.what;
            if(what == WebSocketLogic.MSG_RE_CONNECT){
                if(isConnectInternet()){
                    reConnect();
                }
            } else if(what == WebSocketLogic.MSG_SYSTEM_NOTIFY) {
                showNotification((String) msg.obj);
            }
        }
    };

    public PushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG,"onCreate()");

        initNotification();
        WebSocketLogic.getInstance().setHandler(mHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG,"onStartCommand ");
        if(intent != null){
            String action = intent.getAction();
            if(ACTION_CONNECT.equals(action)){
                connect();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        nm.cancel(Notification_ID_BASE);

    }

    /** 重连次数*/
    private int reCtime=0;
    /**
     * 重新连接
     */
    private void reConnect(){

        Log.i(TAG,"reConnect() time = "+System.currentTimeMillis());

        //pass
        if(isConnectInternet()){
            if(connect()){
                reCtime = 0;
            }else {
                reCtime += 1;

                if(reCtime == 3){
                    reCtime = 0;
                    mHandler.sendEmptyMessageDelayed(WebSocketLogic.MSG_RE_CONNECT,60000);
                } else {
                    mHandler.sendEmptyMessageDelayed(WebSocketLogic.MSG_RE_CONNECT,10000);
                }
            }
        }
    }

    private boolean connect(){
        boolean isConnectSocket = WebSocketLogic.getInstance().isConnected();
        if(isConnectSocket){

        } else {
            WebSocketLogic.getInstance().connect();
        }

        return WebSocketLogic.getInstance().isConnected();
    }

    /**
     * 判断是否有网络连接，wifi或者手机卡网络连接
     * @return true 有连接， false 没有
     */
    public boolean isConnectInternet(){
        ConnectivityManager cntmng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cntmng == null){
            return false;
        }
        NetworkInfo netInfo = cntmng.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    private NotificationCompat.Builder mBuilder;

    private void initNotification(){
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //新建状态栏通知
//        baseNF = new Notification();

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("hello world");
    }

    /**
     * 在通知栏显示信息
     */
    private void showNotification(String msg){

        Log.i(TAG,"showNotification() msg="+msg);

//        Notification notification = new Notification.Builder(
//                getApplicationContext())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("內容標題")
//                .setContentText(msg)
//                .build(); // 建立通知



        mBuilder.setContentText(msg);

        //发出状态栏通知
        //The first parameter is the unique ID for the Notification
        // and the second is the Notification object.
        nm.notify(Notification_ID_BASE, mBuilder.build());
    }

//    /**
//     * 检测网络变化，
//     */
//    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver(){
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            DataUpdateTool.LOG_I(DataUpdateTool.TAG, "[DataUpdate]BroadcastReceiver onReceive");
//            if(mBinder == null || mBinder.getIsInBackground()){
//                return ;
//            }
//            int netType = DataUpdateTool.getNetWorkType(context);
//            DataUpdateTool.LOG_I(DataUpdateTool.TAG, "[DataUpdate] BroadcastReceiver onReceive" + netType);
//            //网络真的发生了改变才处理
//            if(netType == mLastNetType){
//                return;
//            }
//            mLastNetType = netType;
//            switch (netType) {
//                case NetType.TYPE_WIFI:
//                    if(mIsNeedStartDownloadWhenHaveWifi == true)
//                    {
//                        mIsNeedStartDownloadWhenHaveWifi = false;
//                        mBinder.restoreTaskStatus();
//                        mBinder.startDownloadTask(false);
//                        refreshDownloadingScreen();
//                        removeDialog(DIALOG_ID_NET_NO_WIFI_BROADCAST);
//                        removeDialog(DIALOG_ID_NETWORK_ERROR_BROADCAST);
//                    }
//                    break;
//                case NetType.TYPE_MOBILE:
//                    if(mBinder.isAnyTaskRunning())
//                    {
//                        mIsNeedStartDownloadWhenHaveWifi = true;
//                        mBinder.saveTaskStatus();
//                        mBinder.stopAllDownloadTask();
//                        refreshDownloadingScreen();
//                        showDialog(DIALOG_ID_NET_NO_WIFI_BROADCAST);
//                    }
//                    break;
//                case NetType.TYPE_NONE:
//                    if(mBinder.isAnyTaskRunning())
//                    {
//                        mIsNeedStartDownloadWhenHaveWifi = true;
//                        mBinder.saveTaskStatus();
//                        mBinder.stopAllDownloadTask();
//                        refreshDownloadingScreen();
//                        showDialog(DIALOG_ID_NETWORK_ERROR_BROADCAST);
//                    }
//                    break;
//            }
//        }
//    };
//
//    IntentFilter nIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//    registerReceiver(mConnectivityReceiver, nIntentFilter);
}
