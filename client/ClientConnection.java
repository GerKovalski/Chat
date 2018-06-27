package ru.geekbrains.client;

import ru.geekbrains.ServerConst;
import ru.geekbrains.Server_API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection implements ServerConst, Server_API {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    private boolean isAuthorized = false;

    public boolean isAuthorized(){
        return isAuthorized;
    }
    public void setAuthorized(boolean authorized){
        isAuthorized = authorized;
    }
    public ClientConnection(){
    }
    public void init(ChatWindow view){
        try{
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new Thread(new Runnable() {
                @Override
                public void run(){
                    try{
                        while(true){
                            String msg = in.readUTF();
                            if(msg.startsWith(AUTH_SUCCESSFUL)){
                                setAuthorized(true);
                                view.switchWindows();
                                break;
                            }
                            view.showMessage(msg);
                        }
                        while(true){
                            String msg = in.readUTF();
                            if(msg.startsWith(SYSTEM_SYMBOL)){
                                if(msg.startsWith(USERS_LIST)){
                                    String[] users = msg.split(" ");
                                    Arrays.sort(users);
                                    view.showUsersList(users);
                                }
                            }else
                                view.showMessage(msg);
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendMessage(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void auth(String login, String password){
        try{
            out.writeUTF( AUTH + " " + login + " " + password);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try{
            out.writeUTF(CLOSE_CONNECTION);
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
