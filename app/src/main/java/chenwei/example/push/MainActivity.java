package chenwei.example.push;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 测试 websocket 功能
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private final String TAG = "chenwei.TestSocketPush";

    private Button mBtConnect,mBtDisConn,mBtSendMsg;

//    private Handler mHandler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            int what = -1;
//            what = msg.what;
//            if(what == WebSocketLogic.MSG_CONNECT_OK){
//
//                Toast.makeText(MainActivity.this,"连接服务器成功，正在进入聊天室....",Toast.LENGTH_SHORT).show();
//
//                startActivity(new Intent(MainActivity.this,ChatRoomActivity.class));
//
//            } else if(what == WebSocketLogic.MSG_CONNECT_FAIL){
//
//                String tmp = (String) msg.obj;
//                Toast.makeText(MainActivity.this,"连接服务器失败，请重新尝试！["+tmp+"]",Toast.LENGTH_SHORT).show();
//
//            } else if(what == WebSocketLogic.MSG_DISCONNECT){
//            } else if(what == WebSocketLogic.MSG_SYSTEM_NOTIFY){
//                String tmp = (String) msg.obj;
//                Toast.makeText(MainActivity.this,tmp,Toast.LENGTH_SHORT).show();
//            }
////            super.handleMessage(msg);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtConnect = (Button) this.findViewById(R.id.bt_websocket_open);
        mBtConnect.setOnClickListener(this);
        mBtConnect.setEnabled(false);

        mBtDisConn = (Button) this.findViewById(R.id.bt_websocket_close);
        mBtDisConn.setOnClickListener(this);
        mBtDisConn.setEnabled(false);

        startService(new Intent(PushService.ACTION_CONNECT));


    }

    @Override
    protected void onResume() {
        super.onResume();
//        WebSocketLogic.getInstance().addHandler(mHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        WebSocketLogic.getInstance().removeHandler(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        WebSocketLogic.getInstance().disconnect();
    }

    @Override
    public void onClick(View v) {
        if(mBtConnect == v){
//            if(WebSocketLogic.getInstance().isConnected()){
//                Toast.makeText(MainActivity.this,"回到聊天室....",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this,ChatRoomActivity.class));
//            }else {
//                WebSocketLogic.getInstance().connect();
//            }
        } else if(mBtDisConn == v){
            WebSocketLogic.getInstance().disconnect();
        }
    }

    private void writeStream(OutputStream out,byte[] stream) {

        BufferedOutputStream bos = null ;

        Log.i(TAG,"writeStream()");

        try {
            bos = new BufferedOutputStream(out);
            bos.write(stream);
            bos.flush();
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readStream(InputStream in) {

        Log.i(TAG,"readStream()");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer sb = new StringBuffer();


            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }

            Log.i(TAG,"sb = "+sb.toString());
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


