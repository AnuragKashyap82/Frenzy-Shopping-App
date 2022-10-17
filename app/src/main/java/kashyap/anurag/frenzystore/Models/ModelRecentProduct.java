package kashyap.anurag.frenzystore.Models;

public class ModelRecentProduct {

    private String productId, timestamp;
    private boolean active;

    public ModelRecentProduct() {
    }

    public ModelRecentProduct(String productId, String timestamp, boolean active) {
        this.productId = productId;
        this.timestamp = timestamp;
        this.active = active;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
