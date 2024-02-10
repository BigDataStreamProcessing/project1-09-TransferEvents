package com.example.bigdata;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class TransactionStatusProvider extends AbstractProvider<BaseProviders> {
    private static final String[] STATUS_NAMES = new String[]{"Accepted", "Rejected"};

    public TransactionStatusProvider(BaseProviders faker) {
        super(faker);
    }

    public String nextStatusName() {
        return STATUS_NAMES[faker.random().nextInt(STATUS_NAMES.length)];
    }
}

