package com.vladhuk.roshambo.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "account")
public class Account implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    private String username = "";
    private String password = "";

    public Account() {}

    public Account(String username) {
        this.username = username;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String nickname) {
        this.username = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username;
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

        return object.username.equals(username) && object.password.equals(password);
    }

    @Override
    public int hashCode() {
        int result = 0;

        result += 37 * result + username.hashCode();
        result += 37 * result + password.hashCode();

        return result;
    }
}
