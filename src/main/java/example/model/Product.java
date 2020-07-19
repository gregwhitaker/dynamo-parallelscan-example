package example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = Product.TABLE_NAME)
public class Product {
    public static final String TABLE_NAME = "catalog.products";

    private String id;
    private String name;
    private String status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProductPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ProductPrice> prices) {
        this.prices = prices;
    }
}
