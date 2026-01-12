package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaTransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    @Transactional
    public void receiveTransaction(Transaction transaction) {
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null) {
            return; // invalid user
        }

        if (sender.getBalance() < amount) {
            return; // insufficient funds
        }

        // Call the Incentives API
        String url = "http://localhost:8080/incentive";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

        Incentive incentiveResponse = restTemplate.postForObject(url, request, Incentive.class);
        float incentive = (incentiveResponse != null) ? incentiveResponse.getAmount() : 0.0f;

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Save record with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentive);
        transactionRecordRepository.save(record);

        // Print wilbur's balance after each transaction (last one is the final)
                // ALWAYS print wilbur's current balance after every transaction (last one is final)
        UserRecord wilbur = userRepository.findByName("wilbur");
        if (wilbur != null) {
            int floored = (int) Math.floor(wilbur.getBalance());
            System.out.println(">>> WILBUR CURRENT BALANCE AFTER TRANSACTION (rounded down): " + floored);
        }

        // Optional debug print
        System.out.println("Processed transaction of " + amount + " from " + sender.getName() + " to " + recipient.getName());
    }
}