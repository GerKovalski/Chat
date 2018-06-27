package ru.geekbrains.server;

import ru.geekbrains.Server_API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

public class ClientHandler implements Server_API{
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(Server server, Socket socket){
        try{
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            nick = "undefined";
        }catch(IOException e){
            e.printStackTrace();
        }
        new Thread(() -> {
                try{
                    //Authorization
                    socket.setSoTimeout(20000); //Тайм-аут сокета
                    try {
                        while(true){
                            String msg = in.readUTF();
                            if(msg.startsWith(AUTH)){
                                String[] elements = msg.split(" ");
                                String nick = server.getAuthService().getNickByLoginPass(elements[1],elements[2]);
                                if(nick != null){
                                    if(!server.isNickBusy(nick)){
                                        sendMessage(AUTH_SUCCESSFUL + " " + nick);
                                        this.nick = nick;
                                        server.broadcastUsersList();
                                        server.broadcast(this.nick + " has entered the chat room!");
                                        socket.setSoTimeout(0);
                                        break;
                                    }else sendMessage("This account is already in use!");
                                }else sendMessage("Wrong login/password!");
                            }else sendMessage("You should authorize first!");
                            if(msg.equalsIgnoreCase(CLOSE_CONNECTION)) disconnect();
                        }
                    } catch (InterruptedIOException e) {
                        disconnectTimeOut();
                    }
                    //отвечает за прием обычных сообщений
                    while(true){
                        String msg = in.readUTF();
                        if(msg.startsWith(SYSTEM_SYMBOL)){
                            if(msg.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                            else if(msg.startsWith(PRIVATE_MESSAGE)){ //w nick message
                                String nameTo = msg.split(" ")[1]; //нулевой элемент это системная команда /w
                                String message = msg.substring(nameTo.length() + 4); //4 = / + w + 2 пробела (до ника и после)
                                server.sendPrivateMessage(this, nameTo, message);
                            }else {
                                sendMessage("Command doesn't exist!");
                            }
                        }else{
                            System.out.println("client: " + msg);
                            server.broadcast(this.nick + " " + msg);
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    disconnect();
                    try{
                        socket.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
        }).start();

    }
    public void sendMessage(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public String getNick(){
        return nick;
    }
    public void disconnectTimeOut(){
        sendMessage("You have been disconnected in case of timeout!");
        server.unSubscribeMe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void disconnect(){
        sendMessage("You have been disconnected!");
        server.unSubscribeMe(this);
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
