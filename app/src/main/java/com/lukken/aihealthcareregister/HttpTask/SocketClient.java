package com.lukken.aihealthcareregister.HttpTask;

import android.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lukken.aihealthcareregister.JoinActivity;
import com.lukken.aihealthcareregister.MainActivity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SocketClient {
    private static SocketClient _instance;
    public static SocketClient getInstance(MainActivity activity){
        if(_instance == null)
            _instance = new SocketClient(activity);
        return _instance;
    }
    MainActivity mActivity;
    private SocketClient(MainActivity activity){
        mActivity = activity;
    }

    /**
     * 키오스크로 회원가입 완료 전송
     * @param ucode 가입자 코드
     */
    public void sendJoinComplete(final String ucode){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpTask.URL_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create()).build();
        HttpTask httpTask = retrofit.create(HttpTask.class);
        httpTask.getSocketInfo().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.body().get("code").getAsInt();
                if(code == 200) {
                    String ip = response.body().get("address").getAsString();
                    int port = response.body().get("port").getAsInt();
                    SocketThread thread = new SocketThread(ip, port, ucode);
                    thread.start();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    class SocketThread extends Thread{
        String ipaddress;
        int port;
        String data;
        public SocketThread(String address, int port, String data){
            this.ipaddress = address;
            this.port = port;
            this.data = data;

//            this.ipaddress = address;
//            this.port = port;

        }

        @Override
        public void run() {
            try{
                //전송
                Socket socket = new Socket(ipaddress, port);
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(data);
                outstream.flush();

                //응답
                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                String response = (String) instream.readObject();
                if(!response.equals("SUCCESS"))
                    ShowFaileSend(data);

                socket.close();
            }catch(Exception e){
                ShowFaileSend(data);
            }
        }
    }

    void ShowFaileSend(final String data){
        mActivity.runOnUiThread(() -> {
            new AlertDialog.Builder(mActivity).setMessage("키오스크에 회원가입 결과 전송이 실패 하였습니다.\n다시 전송 하시겠습니까?").setPositiveButton("예", (dialog, which) -> {
                sendJoinComplete(data);
            }).setNegativeButton("아니오", null).setCancelable(false).show();
        });
    }
}
