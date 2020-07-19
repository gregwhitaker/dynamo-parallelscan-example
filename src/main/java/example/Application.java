package example;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

        createTable(dynamoDB);
        populateTable(dynamoDB);
    }

    private void createTable(DynamoDB dynamoDB) {
        LOG.info("Creating Table: {}", TABLE_NAME);

        final List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement("id", KeyType.HASH));

        final List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition("id", ScalarAttributeType.S));

        final CreateTableRequest createTableRequest = new CreateTableRequest();
        createTableRequest.setTableName(TABLE_NAME);
        createTableRequest.setKeySchema(keySchema);
        createTableRequest.setAttributeDefinitions(attributeDefinitions);
        createTableRequest.setProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));

        Table table = dynamoDB.createTable(createTableRequest);

        LOG.info("Created Table: {}", TABLE_NAME);
    }

    private void populateTable(DynamoDB dynamoDB) {

    }
}
