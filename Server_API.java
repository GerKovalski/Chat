package ru.geekbrains;

public interface Server_API {
    String SYSTEM_SYMBOL = "/";
    String CLOSE_CONNECTION = "/end";
    String AUTH = "/auth";
    String AUTH_SUCCESSFUL = "/authok";
    String PRIVATE_MESSAGE = "/w";
    String USERS_LIST = "/userslist";
}
