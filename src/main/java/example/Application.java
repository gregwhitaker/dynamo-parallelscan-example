package example;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs the example application.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final String TABLE_NAME = "catalog.products";

    public static void main(String... args) {
        Application example = new Application();
        example.run();
    }

    public void run() {
        final AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                .build();

        final DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
    }

    private void createTable() {

    }

    private void populateTable() {

    }
}
