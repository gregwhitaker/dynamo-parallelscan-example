package example;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import example.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

@SpringBootApplication
public class Application {
    private static final int NUM_SEGMENT_SCANNER_THREADS = 4;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Runs the application.
     */
    @Component
    public class AppRunner implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            final AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                    .build();

            scanTable(new DynamoDB(dynamoDBClient));

            System.exit(0);
        }

        /**
         * Starts table scanning.
         *
         * @param dynamoDB dynamodb client
         * @throws Exception
         */
        private void scanTable(DynamoDB dynamoDB) throws Exception {
            final ExecutorService executor = Executors.newFixedThreadPool(NUM_SEGMENT_SCANNER_THREADS);
            final List<ScanSegmentTask> tasksToExecute = new ArrayList<>();

            int totalSegments = NUM_SEGMENT_SCANNER_THREADS;
            for (int segment = 0; segment < totalSegments; segment++) {
                tasksToExecute.add(new ScanSegmentTask(dynamoDB, Product.TABLE_NAME, totalSegments, segment));
            }

            // Execute all table scanning tasks in parallel and wait for completion
            executor.invokeAll(tasksToExecute);

            System.out.println("Scan completed!");
        }
    }

    /**
     * Task that scans the catalog.products table for a particular table segment.
     */
    private static class ScanSegmentTask implements Callable<Void> {

        private final DynamoDB dynamoDB;
        private final String tableName;
        private final int totalSegments;
        private final int segment;

        public ScanSegmentTask(DynamoDB dynamoDB, String tableName, int totalSegments, int segment) {
            this.dynamoDB = dynamoDB;
            this.tableName = tableName;
            this.totalSegments = totalSegments;
            this.segment = segment;
        }

        @Override
        public Void call() throws Exception {
            System.out.printf("Scanning %s segment  %s out of %s segments...\n", tableName, segment, totalSegments);

            final Table table = dynamoDB.getTable(tableName);
            final LongAdder recordCnt = new LongAdder();

            Map<String, AttributeValue> lastEvaluatedKey = null;
            do {
                final ScanSpec spec = new ScanSpec()
                        .withConsistentRead(false)
                        .withTotalSegments(totalSegments)
                        .withSegment(segment);

                if (lastEvaluatedKey != null) {
                    spec.withExclusiveStartKey("key", lastEvaluatedKey.get("key"));
                }

                final ItemCollection<ScanOutcome> items = table.scan(spec);
                final Iterator<Item> iterator = items.iterator();

                if (iterator.hasNext()) {
                    while (iterator.hasNext()) {
                        Product product = Product.from(iterator.next());

                        System.out.printf("[segment %s] Found product %s\n", segment, product.getId());
                        recordCnt.increment();
                    }
                } else {
                    System.out.printf("No records found in %s\n", tableName);
                }

                lastEvaluatedKey = items.getLastLowLevelResult().getScanResult().getLastEvaluatedKey();
            } while (lastEvaluatedKey != null);

            System.out.printf("Processed %s records on segment %s\n", recordCnt.longValue(), segment);

            return null;
        }
    }
}
