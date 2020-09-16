package example;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import example.model.Product;
import example.model.ProductPrice;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runs the example application.
 */
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final int NUM_SEGMENT_SCANNER_THREADS = 4;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public class AppRunner implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            final AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                    .build();

            final DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
            final DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

            scanTable(dynamoDB);
        }

        private void scanTable(DynamoDB dynamoDB) {
            final ExecutorService executor = Executors.newFixedThreadPool(NUM_SEGMENT_SCANNER_THREADS);

            int totalSegments = NUM_SEGMENT_SCANNER_THREADS;
            for (int segment = 0; segment < totalSegments; segment++) {
                ScanSegmentTask task = new ScanSegmentTask(dynamoDB, Product.TABLE_NAME, 100, totalSegments, segment);
                executor.execute(task);
            }
        }
    }

    /**
     *
     */
    private static class ScanSegmentTask implements Runnable {

        private final DynamoDB dynamoDB;
        private final String tableName;
        private final int limit;
        private final int totalSegments;
        private final int segment;

        public ScanSegmentTask(DynamoDB dynamoDB, String tableName, int limit, int totalSegments, int segment) {
            this.dynamoDB = dynamoDB;
            this.tableName = tableName;
            this.limit = limit;
            this.totalSegments = totalSegments;
            this.segment = segment;
        }

        @Override
        public void run() {
            System.out.println("Scanning " + tableName + " segment " + segment + " out of " + totalSegments
                    + " segments " + limit + " items at a time...");
            int totalScannedItemCount = 0;

            Table table = dynamoDB.getTable(tableName);

            try {
                ScanSpec spec = new ScanSpec()
                        .withFilterExpression("productStatus = ACTIVE")
                        .withTotalSegments(totalSegments)
                        .withSegment(segment);

                ItemCollection<ScanOutcome> items = table.scan(spec);
                Iterator<Item> iterator = items.iterator();

                Item currentItem = null;
                while (iterator.hasNext()) {
                    totalScannedItemCount++;
                    currentItem = iterator.next();
                    System.out.println(currentItem.toString());
                }

            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            finally {
                System.out.println("Scanned " + totalScannedItemCount + " items from segment " + segment + " out of "
                        + totalSegments + " of " + tableName);
            }
        }
    }
}
