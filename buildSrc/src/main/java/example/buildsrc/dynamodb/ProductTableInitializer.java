package example.buildsrc.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Loads test products into the localstack dynamodb "catalog.products" table.
 */
public class ProductTableInitializer {

    private final DynamoDBMapper mapper;

    /**
     * Creates an instance of {@link ProductTableInitializer}.
     *
     * @param dynamoClient amazon dynamodb client
     */
    public ProductTableInitializer(AmazonDynamoDB dynamoClient) {
        this.mapper = new DynamoDBMapper(dynamoClient);
    }

    /**
     * Initializes the table.
     */
    public void run() {
        for (int i = 0; i < 1_000; i++) {
            final ProductPrice msrp = new ProductPrice();
            msrp.setType("msrp");
            msrp.setPrice(ThreadLocalRandom.current().nextDouble(10, 100));

            final ProductPrice list = new ProductPrice();
            list.setType("list");
            list.setPrice(msrp.getPrice() - 2);

            final Product product = new Product();
            product.setId(StringUtils.leftPad(Integer.toString(i), 5, "0"));
            product.setName(String.format("Widget-%s", i));
            product.setDescription(String.format("Description for Widget-%s", i));
            product.setCreatedAt(System.currentTimeMillis());
            product.setPrices(Arrays.asList(msrp, list));

            if (i % 2 == 0) {
                product.setProductStatus("ACTIVE");
            } else {
                product.setProductStatus("INACTIVE");
            }

            mapper.save(product);
        }
    }

    /**
     *
     */
    @DynamoDBTable(tableName = Product.TABLE_NAME)
    static class Product {
        public static final String TABLE_NAME = "catalog.products";

        @DynamoDBHashKey
        private String id;

        private String name;
        private String description;
        private String productStatus;
        private long createdAt;
        private List<ProductPrice> prices;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProductStatus() {
            return productStatus;
        }

        public void setProductStatus(String productStatus) {
            this.productStatus = productStatus;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public List<ProductPrice> getPrices() {
            return prices;
        }

        public void setPrices(List<ProductPrice> prices) {
            this.prices = prices;
        }
    }

    /**
     *
     */
    @DynamoDBDocument
    static class ProductPrice {

        private String type;
        private double price;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
