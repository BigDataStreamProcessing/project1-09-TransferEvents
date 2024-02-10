package com.example.bigdata;

import net.datafaker.Faker;

public class TransactionStatusFaker extends Faker {
    public TransactionStatusProvider status() {
        return getProvider(TransactionStatusProvider.class, TransactionStatusProvider::new, this);
    }
}
