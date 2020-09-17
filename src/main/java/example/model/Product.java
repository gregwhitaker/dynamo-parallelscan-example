package example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product domain object.
 */
@DynamoDBTable(tableName = Product.TABLE_NAME)
public class Product {
    public static final String TABLE_NAME = "catalog.products";

    /**
     * Converts DynamoDB item to an instance of {@link Product}.
     *
     * @param item item to convert
     * @return product
     * @throws JsonProcessingException
     */
    public static Product from(Item item) throws JsonProcessingException {
        final Product product = new Product();
        product.setId(item.getString("id"));
        product.setName(item.getString("name"));
        product.setProductStatus(item.getString("productStatus"));
        product.setCreatedAt(item.getLong("createdAt"));
        product.setPrices(item.getList("prices").stream()
                .map(o -> {
                    final ProductPrice price = new ProductPrice();
                    price.setType(((LinkedHashMap<String, Object>)o).get("type").toString());
                    price.setPrice((BigDecimal) ((LinkedHashMap<String, Object>)o).get("price"));

                    return price;
                })
                .collect(Collectors.toList()));

        return product;
    }

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
