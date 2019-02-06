package com.vladhuk.roshambo.server;

import java.io.Serializable;

public class Account implements Serializable {

    private String nickname = "";
    private String password = "";

    public Account() {}

    public Account(String nickname) {
        this.nickname = nickname;
    }

    public Account(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return nickname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Account object = (Account) obj;

        return object.nickname.equals(nickname) && object.password.equals(password);
    }

    @Override
    public int hashCode() {
        int result = 0;

        result += 37 * result + nickname.hashCode();
        result += 37 * result + password.hashCode();

        return result;
    }
}
