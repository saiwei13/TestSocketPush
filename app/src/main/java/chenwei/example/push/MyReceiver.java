package chenwei.example.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * 监听网络变化的广播
 */
public class MyReceiver extends BroadcastReceiver {

    private final String TAG = "chenwei.MyReceiver";

    private NetworkInfo.State wifiState = null;
    private NetworkInfo.State mobileState = null;
    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        if (ACTION.equals(intent.getAction())) {
            //获取手机的连接服务管理器，这里是连接管理器类
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
//                Toast.makeText(context, "手机网络连接成功！", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"onReceive() 手机网络连接成功");
                connectSocket(context);
            } else if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED == wifiState && NetworkInfo.State.CONNECTED != mobileState) {
//                Toast.makeText(context, "无线网络连接成功！", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"onReceive() 无线网络连接成功");
                connectSocket(context);
            } else if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState) {
//                Toast.makeText(context, "手机没有任何网络...", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"onReceive() 手机没有任何网络....");
            }
        }
    }

    private void connectSocket(Context context){
        Log.i(TAG, "connectSocket()");
        Intent i = new Intent(PushService.ACTION_CONNECT);
        if(context != null){
            context.startService(i);
        }
    }
}
