package example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

/**
 * Product pricing domain object.
 */
@DynamoDBDocument
public class ProductPrice {

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
