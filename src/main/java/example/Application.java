package example;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runs the example application.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final Long NUM_TABLE_ENTRIES = 1_000L;

    public static void main(String... args) {
        Application example = new Application();
        example.run();
    }

    public void run() {
        final AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                .build();

        final DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
        final DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        createTable(dynamoDB);
        populateTable(mapper);
    }

    private void createTable(DynamoDB dynamoDB) {
        LOG.info("Creating Table: {}", Product.TABLE_NAME);

        final List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement("id", KeyType.HASH));

        final List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition("id", ScalarAttributeType.S));

        final CreateTableRequest createTableRequest = new CreateTableRequest();
        createTableRequest.setTableName(Product.TABLE_NAME);
        createTableRequest.setKeySchema(keySchema);
        createTableRequest.setAttributeDefinitions(attributeDefinitions);
        createTableRequest.setProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));

        Table table = dynamoDB.createTable(createTableRequest);

        LOG.info("Created Table: {}", Product.TABLE_NAME);
    }

    private void populateTable(DynamoDBMapper mapper) {
        for (int i = 1; i <= NUM_TABLE_ENTRIES; i++) {
            final ProductPrice msrpPrice = new ProductPrice();
            msrpPrice.setType("msrp");
            msrpPrice.setPrice(ThreadLocalRandom.current().nextDouble());

            final ProductPrice listPrice = new ProductPrice();
            listPrice.setType("list");
            listPrice.setPrice(ThreadLocalRandom.current().nextDouble());

            final Product product = new Product();
            product.setId(UUID.randomUUID().toString());
            product.setStatus("ACTIVE");
            product.setName(RandomStringUtils.randomAlphabetic(10));
            product.setPrices(Arrays.asList(msrpPrice, listPrice));

            mapper.save(product);
        }
    }
}
