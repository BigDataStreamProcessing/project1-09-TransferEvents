package com.example.bigdata;

import net.datafaker.Faker;

public class TransactionPersonFaker extends Faker {
    public TransactionPersonProvider person() {
        return getProvider(TransactionPersonProvider.class, TransactionPersonProvider::new, this);
    }
}
