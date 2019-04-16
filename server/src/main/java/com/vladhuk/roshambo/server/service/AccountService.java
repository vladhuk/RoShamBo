package com.vladhuk.roshambo.server.service;

import com.vladhuk.roshambo.server.dao.AccountDao;
import com.vladhuk.roshambo.server.model.Account;


public class AccountService {

    private AccountDao accountDao = new AccountDao();

    public void save(Account account) {
        accountDao.save(account);
    }

    public void update(Account account) {
        accountDao.update(account);
    }

    public void delete(Account account) {
        accountDao.delete(account);
    }

    public Account find(int id) {
        return accountDao.findById(id);
    }

    public Account find(String username, String password) {
        return accountDao.findByUsernameAndPassword(username, password);
    }

    public Account find(Account account) {
        return find(account.getUsername(), account.getPassword());
    }

    public boolean isUsernameExist(String nickname) {
        return accountDao.findUsername(nickname) != null;
    }

    public boolean isUsernameExist(Account account) {
        if (account == null) {
            return false;
        }

        return isUsernameExist(account.getUsername());
    }

}
