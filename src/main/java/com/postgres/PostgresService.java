package com.postgres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.postgres.models.Account;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PostgresService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    // save includes creating and updating
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public List<Account> findByName(String name) {
        return accountRepository.findByName(name);
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Transactional
    public void updateRewardPoints(Long id, int rewardPoints) {
        accountRepository.updateRewardPoints(id, rewardPoints);
    }
}
