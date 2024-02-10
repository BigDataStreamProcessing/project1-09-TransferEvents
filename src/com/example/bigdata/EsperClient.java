package com.example.bigdata;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import net.datafaker.Faker;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static net.datafaker.transformations.Field.field;

public class EsperClient {
    public static void main(String[] args) throws InterruptedException {
        int noOfRecordsPerSec;
        int howLongInSec;
        if (args.length < 2) {
            noOfRecordsPerSec = 2;
            howLongInSec = 5;
        } else {
            noOfRecordsPerSec = Integer.parseInt(args[0]);
            howLongInSec = Integer.parseInt(args[1]);
        }

        Configuration config = new Configuration();
        EPCompiled epCompiled = getEPCompiled(config);

        // Connect to the EPRuntime server and deploy the statement
        EPRuntime runtime = EPRuntimeProvider.getRuntime("http://localhost:port", config);
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(epCompiled);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPStatement resultStatement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "answer");

        // Add a listener to the statement to handle incoming events
        resultStatement.addListener( (newData, oldData, stmt, runTime) -> {
            for (EventBean eventBean : newData) {
                System.out.printf("R: %s%n", eventBean.getUnderlying());
            }
        });

        Faker faker = new Faker();
        TransactionStatusFaker statusFaker = new TransactionStatusFaker();
        TransactionTypeFaker typeFaker = new TransactionTypeFaker();
        TransactionPersonFaker personFaker = new TransactionPersonFaker();

        String record;

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + (1000L * howLongInSec)) {
            for (int i = 0; i < noOfRecordsPerSec; i++) {
                Timestamp eTimestamp = faker.date().past(30, TimeUnit.SECONDS);
                eTimestamp.setNanos(0);
                Timestamp iTimestamp = Timestamp.valueOf(LocalDateTime.now().withNano(0));

                Schema<Object, ?> schema = Schema.of(
                        field("category", () -> typeFaker.type().nextTypeName()),
                        field("status", () -> statusFaker.status().nextStatusName()),
                        field("sender", () -> personFaker.person().nextPersonName()),
                        field("recipient", () -> personFaker.person().nextPersonName()),
                        field("amount", () -> String.valueOf(faker.number().numberBetween(0, 10000))),
                        field("ets", eTimestamp::toString),
                        field("its", iTimestamp::toString)
                        );

                JsonTransformer<Object> transformer = JsonTransformer.builder().build();
                record = transformer.generate(schema, 1);
                runtime.getEventService().sendEventJson(record, "TransferEvent");
            }
            waitToEpoch();
        }
    }

    private static EPCompiled getEPCompiled(Configuration config) {
        CompilerArguments compilerArgs = new CompilerArguments(config);
        // Compile the EPL statement
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled;
        try {
            epCompiled = compiler.compile("""
                    @public @buseventtype create json schema TransferEvent(category string, status string, sender string, recipient string, amount int, ets string, its string);
                    @name('answer') SELECT category, status, sender, recipient, amount, ets, its
                    FROM TransferEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec);
                    """, compilerArgs);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }
        return epCompiled;
    }

    static void waitToEpoch() throws InterruptedException {
        long millis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(millis) ;
        Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
        long millis2 = instantTrunc.toEpochMilli() ;
        TimeUnit.MILLISECONDS.sleep(millis2+1000-millis);
    }
}

