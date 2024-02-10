package com.example.bigdata;

import net.datafaker.Faker;

public class TransactionTypeFaker extends Faker {
    public TransactionTypeProvider type() {
        return getProvider(TransactionTypeProvider.class, TransactionTypeProvider::new, this);
    }
}
