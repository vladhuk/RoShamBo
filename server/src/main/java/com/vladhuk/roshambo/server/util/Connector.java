package com.vladhuk.roshambo.server.util;

public interface Connector {
    void sendObject(Object object) throws DisconnectException;
    Object receiveObject() throws DisconnectException;
    void sendInteger(int i) throws DisconnectException;
    int receiveInteger() throws DisconnectException;
    void sendAnswer(boolean answer) throws DisconnectException;
    boolean receiveAnswer() throws DisconnectException;
}
