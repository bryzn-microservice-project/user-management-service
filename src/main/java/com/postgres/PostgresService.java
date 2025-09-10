package com.postgres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.postgres.models.Account;
import java.util.List;
import java.util.Optional;

@Service
public class PostgresService {

    @Autowired
    private AccountRepository paymentRepository;

    public List<Account> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return paymentRepository.findById(id);
    }

    // save includes creating and updating
    public Account save(Account payment) {
        return paymentRepository.save(payment);
    }

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Account> findByName(String name) {
        return paymentRepository.findByName(name);
    }

    public Account findByUsername(String username) {
        return paymentRepository.findByUsername(username);
    }

    public Account findByEmail(String email) {
        return paymentRepository.findByEmail(email);
    }
}
