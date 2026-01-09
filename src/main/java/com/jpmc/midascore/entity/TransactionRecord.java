package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    private float amount;

    // Default constructor required by JPA
    public TransactionRecord() {}

    // This is the one we need!
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    // Optional: getters if you want
    public long getId() { return id; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public float getAmount() { return amount; }
}