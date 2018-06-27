package ru.geekbrains.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWindow extends JFrame{
    private JTextArea jTextArea;
    private JTextArea usersList;
    private JScrollPane jspUsers;
    private JTextField jTextField;
    private JTextField jtfLogin;
    private JPasswordField jtfPassword;
    private JPanel top;
    private JPanel jPanelBottom;
    private ClientConnection client;

    public ChatWindow(){
        client = new ClientConnection();
        client.init(this);
        setTitle("Chat Window");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        jTextArea = new JTextArea();
        usersList = new JTextArea();
        jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(200, 20));
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        usersList.setEditable(false);
        usersList.setPreferredSize(new Dimension(100, 1));
        jspUsers = new JScrollPane(usersList);
        JButton jButtonSend = new JButton("Send");

        jPanelBottom = new JPanel();
        jPanelBottom.add(jTextField, BorderLayout.CENTER);
        jPanelBottom.add(jButtonSend, BorderLayout.EAST);

        jtfLogin = new JTextField();
        jtfPassword = new JPasswordField();
        JButton jbAuth = new JButton("Login");
        top = new JPanel(new GridLayout(1,3));
        top.add(jtfLogin);
        top.add(jtfPassword);
        top.add(jbAuth);

        add(jScrollPane, BorderLayout.CENTER);
        add(jPanelBottom, BorderLayout.SOUTH);
        add(jspUsers, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        jButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
        });
        jTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
        });
        jbAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                auth();
            }
        });

        usersList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                System.out.println("Clicked");
                System.out.println(e.getComponent());
            }
            @Override
            public void mousePressed(MouseEvent e){
                System.out.println("Pressed");
            }
            @Override
            public void mouseReleased(MouseEvent e){
                System.out.println("Released");
                System.out.println(e.getComponent());
            }
            @Override
            public void mouseEntered(MouseEvent e){
            }
            @Override
            public void mouseExited(MouseEvent e){
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                try{
                    client.disconnect();
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        switchWindows();
        setVisible(true);
    }
    public void sendMessage(){
        String msg = jTextField.getText();
        jTextField.setText("");
        client.sendMessage(msg);
    }
    public void showMessage(String msg){
        jTextArea.append(msg + "\n");
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
    public void auth(){
        client.auth(jtfLogin.getText(), new String(jtfPassword.getPassword()));
        jtfLogin.setText("");
        jtfPassword.setText("");
    }
    public void switchWindows(){
        top.setVisible(!client.isAuthorized());
        jPanelBottom.setVisible(client.isAuthorized());
        jspUsers.setVisible(client.isAuthorized());
    }
    public void showUsersList(String[] users){
        usersList.setText("");
        for(int i = 1; i < users.length; i++){
            usersList.append(users[i] + "\n");
        }
    }

}
