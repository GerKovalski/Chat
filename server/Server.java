package ru.geekbrains.server;

import ru.geekbrains.ServerConst;
import ru.geekbrains.Server_API;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements ServerConst, Server_API{
    private Vector<ClientHandler> clients;
    private AuthService authService;
    public AuthService getAuthService(){
        return authService;
    }
    public Server(){
        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();
        try{
            server = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start(); //placeholder
            System.out.println("Server is up and running! Awaiting for connections");
            while(true){
                socket = server.accept();
                clients.add(new ClientHandler(this, socket));
                System.out.println("Client has connected!");
            }
        }catch(IOException e){
        }finally{
        }
    }
    public void broadcast(String msg){
        for(ClientHandler client : clients){
            client.sendMessage(msg);
        }
    }
    public void sendPrivateMessage(ClientHandler from, String to, String msg){
        boolean nickFound = false;
        for(ClientHandler client : clients){
            if(client.getNick().equals(to)){
                nickFound = true;
                client.sendMessage("from " + from.getNick() + ": " + msg);
                from.sendMessage("to " + to + " msg: " + msg);
                break;
            }
        }
        if(!nickFound)
            from.sendMessage("User not found");
    }
    public void broadcastUsersList(){
        StringBuffer sb = new StringBuffer(USERS_LIST);
        for(ClientHandler client : clients){
            sb.append(" " + client.getNick());
        }
        for(ClientHandler client : clients){
            client.sendMessage(sb.toString());
        }
    }
    public void unSubscribeMe(ClientHandler c){
        clients.remove(c);
        broadcastUsersList();
    }
    public boolean isNickBusy(String nick){
        for(ClientHandler client : clients){
            if(client.getNick().equals(nick)) return true;
        }
        return false;
    }
}
