package chenwei.example.push;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 *
 * websocket 单件
 *
 * Created by chenwei on 4/22/15.
 */
public class WebSocketLogic {

    private final String TAG = "chenwei.WebSocketLogic";
    /** 服务器地址 */
//    private static final String SREVER_URL="10.61.137.26:8888";//192.168.1.104
    private static final String SREVER_URL="192.168.1.104:8888";
    public static final int MSG_CONNECT_OK = 1;
    public static final int MSG_CONNECT_FAIL = 2;
    public static final int MSG_DISCONNECT = 3;
    /** 服务器通知*/
    public static final int MSG_SYSTEM_NOTIFY = 4;
    /** 显示聊天内容 */
    public static final int MSG_SHOW_CHAT_CONTENT = 5;

    /** 再次连接 */
    public static final int MSG_RE_CONNECT = 6;


    private static WebSocketLogic instance = null;

    public static WebSocketLogic getInstance(){
        if(instance == null){
            instance = new WebSocketLogic();
        }
        return instance;
    }

    /**
     * 构造方法
     */
    private WebSocketLogic(){

    }

    private String peopeleId ;
    private String roomName;
    private int count ;

    private Handler mHandler = new Handler();
//    private Handler mHandler  = null;

    public void setHandler(Handler handler){
        mHandler = handler;
    }

    private WebSocketClient client = null;

    private String userid = "3c0253e1297d62bdf1ed5ccd4b80d16c";

    /**
     * websocket 连接
     */
    public void connect(){

        Log.i(TAG, "connect()");

        if(client != null && client.isConnected()){
            return;
        }

        List<BasicNameValuePair> extraHeaders = Arrays.asList(
//                new BasicNameValuePair("Cookie", "session=abcd"),
//                new BasicNameValuePair("username","chenwei"),
//                new BasicNameValuePair("pwd",Tool.md5("123456"))
                new BasicNameValuePair("userid",userid)
        );

        client = new WebSocketClient(URI.create("ws://" + SREVER_URL + "/push"),new WebSocketClient.Listener(){

            @Override
            public void onConnect() {
                Log.i(TAG, "Connected!");
                notifyHandler(MSG_CONNECT_OK, "");
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "onMessage() message = "+message);
                try {
                    JSONObject json = new JSONObject(message);
                    Log.i(TAG,"message  json = "+json.toString());

                    String tmp_type = json.getString("type");
                    if(tmp_type.equals("2")){    //系统通知
                        String tmp = json.getString("count");
                        setCount(Integer.parseInt(tmp));
                        tmp = json.getString("msg");
                        notifyHandler(MSG_SYSTEM_NOTIFY, tmp);
                    }
                } catch (JSONException e) {
                    Log.e(TAG,e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(byte[] data) {
                Log.i(TAG, "onMessage(byte[])  "+new String(data).toString());
            }

            @Override
            public void onDisconnect(int code, String reason) {

                notifyHandler(MSG_DISCONNECT, "code=[" + code + "], reason=" + reason);
                disconnect();

                Log.i(TAG,"onDisconnect() code = "+code + " , reason="+reason);
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "onError  = "+error.toString()+" ["+error+"]");

//                java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)

//                if(error instanceof NullPointerException){
//                    Log.i(TAG,"null");
//                }

                if(error instanceof SocketException){
                    if(error.toString().contains("Connection reset by peer")){
                        notifyHandler(MSG_CONNECT_FAIL, error.toString());
                    }
                }

                disconnect();
            }
        },extraHeaders);

        client.connect();
    }

    /**
     * 判断是否连接
     * @return
     */
    public boolean isConnected(){
        if(client!=null && client.isConnected()){
            return true;
        }
        return false;
    }

    Message msg;



    /**
     *
     */
    private void notifyHandler(int what, String str){
        if(mHandler != null){
            msg = mHandler.obtainMessage(what,str);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * websocket 断开
     */
    public void disconnect(){
        Log.i(TAG,"disconnect()");
        if(client != null && client.isConnected()){
            client.disconnect();
            Log.i(TAG,"已断开");
        }

        client = null;

        if(mHandler!=null){
            mHandler.sendEmptyMessageDelayed(MSG_RE_CONNECT,10000);
        }
    }

    /**
     * 向服务器发送msg
     * @param str
     */
    public void sendMsg(String str){
        Log.i(TAG,"sendMsg()");
        if(client != null && client.isConnected() && !TextUtils.isEmpty(str)){
            Log.i(TAG,"连接状态");
//            client.send("hello!");
//            client.send(new String("hello world by wei.chen").getBytes());
              client.send(str);
        }
    }

    /**
     * TODO
     */
    public void ping(){

    }

    //------------------get/set------------------------------------------------
    public String getPeopeleId() {
        return peopeleId;
    }

    public void setPeopeleId(String peopeleId) {
        this.peopeleId = peopeleId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 单件销毁
     */
    public static void onDestory(){
        if(instance != null){

            instance.disconnect();
            instance.client = null;

            instance = null;
        }
    }
}
