package com.example.bigdata;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class TransactionTypeProvider extends AbstractProvider<BaseProviders> {
    private static final String[] TYPE_NAMES = new String[]{"Entertainment", "Salary", "Health", "Food"};

    public TransactionTypeProvider(BaseProviders faker) {
        super(faker);
    }

    public String nextTypeName() {
        return TYPE_NAMES[faker.random().nextInt(TYPE_NAMES.length)];
    }
}
