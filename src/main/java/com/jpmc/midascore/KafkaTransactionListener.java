package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class KafkaTransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

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

        // Valid transaction
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        transactionRecordRepository.save(record);
        // === ADD THESE LINES TO VIEW WALDORF BALANCE ===
        UserRecord waldorf = userRepository.findByName("waldorf");
        if (waldorf != null) {
            int flooredBalance = (int) Math.floor(waldorf.getBalance());
            System.out.println(">>> WALDORF FINAL BALANCE (rounded down): " + flooredBalance);
        }
        // =========================
        // Optional: debug print
        System.out.println("Processed transaction of " + amount + " from " + sender.getName() + " to " + recipient.getName());
    }
}