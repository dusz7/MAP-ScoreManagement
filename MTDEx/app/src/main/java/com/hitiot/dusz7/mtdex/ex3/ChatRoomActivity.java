package com.hitiot.dusz7.mtdex.ex3;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hitiot.dusz7.mtdex.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final int UPDATE_SETTING = 0;
    private static final int RECEIVE_MESSAGE = 1;
    private static final int SEND_MESSAGE = 2;

    // 输入框和发送按钮
    private EditText etMessageSending;
    private Button btSendMessage;

    // 展示消息用的ListView
    private ListView lvMsg;
    // 相应的适配器
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();

    // 展示设置信息用的TextView
    private TextView tvSettings;

    // 本机
    private static String thisIP;
    private int thisPort;
    // 通信对方
    private String thatIP;
    private int thatProt;

    // 两个线程类的实例：分别是Server和Client作用
    private ServerListener listener;
    private ClientSend clientSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        // 获取网络信息：局域网内的信息
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 得到本机IP
        int ipAddress = wifiInfo.getIpAddress();
        thisIP = intToIp(ipAddress);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("  本机IP："+thisIP);

        lvMsg = (ListView)findViewById(R.id.msg_list_view);
        adapter = new MsgAdapter(ChatRoomActivity.this, R.layout.msg_item, msgList);
        lvMsg.setAdapter(adapter);

        etMessageSending = (EditText)findViewById(R.id.edit_sending_message);
        tvSettings = (TextView)findViewById(R.id.text_settings);

        btSendMessage = (Button)findViewById(R.id.button_send_message);
        // 未设置信息时，不可点击
        btSendMessage.setClickable(false);
        btSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etMessageSending.getText().toString();
                if(!"".equals(content)) {

                    // 新建client发送的线程，socket通信，发送输入框输入的内容
                    clientSend = new ClientSend(thatIP, thatProt, content);
                    clientSend.start();
                }
            }
        });

    }

    // handler 处理在子线程内改变UI的情况
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message meg) {
            switch (meg.what) {
                case ChatRoomActivity.UPDATE_SETTING:
                    // 显示设置信息，包括本机IP、端口，对方IP、端口
                    getSupportActionBar().setTitle("  本机： "+thisIP+" : "+thisPort);
                    tvSettings.setText("ToDevice:  "+thatIP+" : "+thatProt);
                    break;
                case ChatRoomActivity.RECEIVE_MESSAGE:
                    // 添加一个"接收"类型的消息
                    Msg msg = new Msg((String)meg.obj, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    break;
                case ChatRoomActivity.SEND_MESSAGE:
                    Msg msg1 = new Msg((String)meg.obj, Msg.TYPE_SENT);
                    msgList.add(msg1);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    etMessageSending.setText("");
                    break;
            }
        }
    };

    /**
     * 菜单相关
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_chat_settings:
                showChatSettingsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 打开一个Dialog，来设置通信设置
    private void showChatSettingsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_chat_setting, (ViewGroup) findViewById(R.id.dialog_chat_setting));
        final EditText etThisPort = (EditText)layout.findViewById(R.id.edit_this_port);
        etThisPort.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        final EditText etThatIP = (EditText)layout.findViewById(R.id.edit_that_ip);
        etThatIP.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        final EditText etThatPort = (EditText)layout.findViewById(R.id.edit_that_port);
        etThatPort.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
        builder.setTitle("输入设置")
                .setView(layout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        thisPort = Integer.valueOf(etThisPort.getText().toString());
                        thatIP = (etThatIP.getText().toString());
                        thatProt = Integer.valueOf(etThatPort.getText().toString());


                        if(thisPort != 0.0 && thatProt != 0.0 && !"".equals(thatIP) && thatIP!=null) {
                            // 设置可点击
                            btSendMessage.setClickable(true);

                            msgList.removeAll(msgList);
                            adapter.notifyDataSetChanged();

                            // 启动一个Server线程，监听端口
                            listener = new ServerListener(thisPort);
                            listener.start();

                            // 添加设置信息展示
                            Message message = new Message();
                            message.what = ChatRoomActivity.UPDATE_SETTING;
                            handler.sendMessage(message);
                        }

                        Log.d("chatSetting",""+thisIP+":"+thisPort+"to"+thatIP+":"+thatProt);
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }


    /**
     * 客户端通过Socket发送消息的线程
     */
    class ClientSend extends Thread {
        private String serverIP;
        private int serverPort;
        private String content;
        private Socket clientSocket;

        public ClientSend(String ip, int port, String content) {
            super();
            this.serverIP = ip;
            this.serverPort = port;
            this.content = content;
        }

        @Override
        public void run() {
            try{

                // 实例化一个Socket
                clientSocket = new Socket(this.serverIP,this.serverPort);
                // 打开OutputStream
                OutputStream outputStream = clientSocket.getOutputStream();
                // 写数据，完成发送
                outputStream.write(this.content.getBytes());

                // 添加一个"发送"类型消息的展示（如果Socket发送成功的话，不然会报错）
                Message message = new Message();
                message.what = ChatRoomActivity.SEND_MESSAGE;
                message.obj = content;
                handler.sendMessage(message);

            }catch (IOException e) {
                Looper.prepare();
                // 发送失败，显示原因
                Toast.makeText(ChatRoomActivity.this,"发送失败: \n"+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }
    }

    /**
     * Socket通信服务端监听线程
     */
    class ServerListener extends Thread {
        private int serverPort;

        public ServerListener(int port) {
            super();
            this.serverPort = port;
        }

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                // 实例化一个ServerSocket，监听特定端口
                serverSocket = new ServerSocket(serverPort);

                while(true) {
                    // ServerSocket监听阻塞
                    Socket socket = serverSocket.accept();

                    // 接收到通信体后，赋给socket，读出其中的数据
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] bytes = new byte[1024];
                    dataInputStream.read(bytes);
                    String receive = "";
                    receive = new String(bytes, StandardCharsets.UTF_8);

                    dataInputStream.close();

                    // receive中存储着接收到的消息
                    if (!"".equals(receive)) {
                        // 添加一个"接收"类型的消息
                        Message message = new Message();
                        message.what = ChatRoomActivity.RECEIVE_MESSAGE;
                        message.obj = receive;
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(ChatRoomActivity.this,"绑定失败: \n"+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(this.listener != null) {
            listener.interrupt();
            listener = null;
        }
        if(this.clientSend != null) {
            clientSend.interrupt();
            clientSend = null;
        }
        super.onDestroy();
    }

    /**
     * 格式化IP
     * @param i
     * @return
     */
    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }


}
