package com.example.bigdata;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class TransactionPersonProvider extends AbstractProvider<BaseProviders>{
    private static final String[] PERSON_NAMES = new String[]{"Patrick", "Bob", "Tamika",
            "Julio", "Theodore", "Janusz", "Gerwant"};

    public TransactionPersonProvider(BaseProviders faker) {
        super(faker);
    }

    public String nextPersonName() {
        return PERSON_NAMES[faker.random().nextInt(PERSON_NAMES.length)];
    }
}
