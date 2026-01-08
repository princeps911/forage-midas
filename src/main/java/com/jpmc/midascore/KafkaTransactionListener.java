package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionListener.class);

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void receiveTransaction(Transaction transaction) {
        System.out.println(">>> RECEIVED AMOUNT: " + transaction.getAmount());
    }
}