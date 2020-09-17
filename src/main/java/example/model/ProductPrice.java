package example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.math.BigDecimal;

/**
 * Product pricing domain object.
 */
@DynamoDBDocument
public class ProductPrice {

    private String type;
    private BigDecimal price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
