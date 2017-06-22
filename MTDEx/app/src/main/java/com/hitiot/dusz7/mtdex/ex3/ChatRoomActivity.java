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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private ListView lvMsg;
    private EditText etMessageSending;
    private Button btSendMessage;
    private MsgAdapter adapter;
    private TextView tvSettings;

    private List<Msg> msgList = new ArrayList<Msg>();

    private String thisIP;
    private int thisPort;
    private String thatIP;
    private int thatProt;

    private ServerListener listener;
    private ClientSend clientSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        thisIP = intToIp(ipAddress);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("本机IP："+thisIP);

        adapter = new MsgAdapter(ChatRoomActivity.this, R.layout.msg_item, msgList);
        etMessageSending = (EditText)findViewById(R.id.edit_sending_message);

//        initMsgs();
        lvMsg = (ListView)findViewById(R.id.msg_list_view);
        lvMsg.setAdapter(adapter);

        btSendMessage = (Button)findViewById(R.id.button_send_message);
        btSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etMessageSending.getText().toString();
                if(!"".equals(content)) {

                    clientSend = new ClientSend(thatIP,thatProt,content);
                    clientSend.start();
                }
            }
        });
        btSendMessage.setClickable(false);

        tvSettings = (TextView)findViewById(R.id.text_settings);

    }

    private void initMsgs () {
        Msg msg1 = new Msg("hello guy", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("hee",Msg.TYPE_SENT);
        msgList.add(msg2);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message meg) {
            switch (meg.what) {
                case 0:
//                    Log.d("recHandler","ss"+(String)meg.obj);
                    Msg msg = new Msg((String)meg.obj, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    break;
                case 1:
                    getSupportActionBar().setTitle("本机："+thisIP+":"+thisPort);
                    tvSettings.setText("To: "+thatIP+":"+thatProt);
                    break;
                case 2:
//                    Log.d("recHandler","ss"+(String)meg.obj);
                    Msg msg1 = new Msg((String)meg.obj, Msg.TYPE_SENT);
                    msgList.add(msg1);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    etMessageSending.setText("");
            }
        }
    };


    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }@Override
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
                            btSendMessage.setClickable(true);
//                            msgList = new ArrayList<Msg>();
                            msgList.removeAll(msgList);
                            adapter.notifyDataSetChanged();
                            listener = new ServerListener(thisPort);
                            listener.start();
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }

                        Log.d("chatSetting",""+thisIP+":"+thisPort+"to"+thatIP+":"+thatProt);
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }


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
                clientSocket = new Socket(this.serverIP,this.serverPort);
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(this.content.getBytes());

                Message message = new Message();
                message.what = 2;
                message.obj = content;

                handler.sendMessage(message);

            }catch (IOException e) {
                Looper.prepare();
                Toast.makeText(ChatRoomActivity.this,"发送失败:"+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
                e.printStackTrace();
            }
        }
    }

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
                serverSocket = new ServerSocket(this.serverPort);
                while(true) {
                    Socket socket = serverSocket.accept();

                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] bytes = new byte[1024];
                    dataInputStream.read(bytes);
                    String receive = "";
                    receive = new String(bytes, StandardCharsets.UTF_8);

                    dataInputStream.close();
                    if (!"".equals(receive)) {
                        Log.d("socketrec",receive);
                        Message message = new Message();
                        message.what = 0;
                        message.obj = receive;
                        handler.sendMessage(message);
                    }

                }
            } catch (IOException e) {
                Looper.prepare();
                Toast.makeText(ChatRoomActivity.this,"绑定失败:"+e.toString(),Toast.LENGTH_LONG).show();
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


}
