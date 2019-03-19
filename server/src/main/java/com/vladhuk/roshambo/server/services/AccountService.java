package com.vladhuk.roshambo.server.services;

import com.vladhuk.roshambo.server.dao.AccountDAO;
import com.vladhuk.roshambo.server.models.Account;


public class AccountService {

    private AccountDAO accountDAO = new AccountDAO();

    public void save(Account account) {
        accountDAO.save(account);
    }

    public void update(Account account) {
        accountDAO.update(account);
    }

    public void delete(Account account) {
        accountDAO.delete(account);
    }

    public Account find(int id) {
        return accountDAO.findById(id);
    }

    public Account find(String username, String password) {
        return accountDAO.findByUsernameAndPassword(username, password);
    }

    public Account find(Account account) {
        return find(account.getUsername(), account.getPassword());
    }

    public boolean isUsernameExist(String nickname) {
        return accountDAO.findUsername(nickname) != null;
    }

    public boolean isUsernameExist(Account account) {
        if (account == null) {
            return false;
        }

        return isUsernameExist(account.getUsername());
    }

}
