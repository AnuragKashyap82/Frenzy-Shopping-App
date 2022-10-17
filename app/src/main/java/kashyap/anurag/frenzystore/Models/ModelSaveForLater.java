package kashyap.anurag.frenzystore.Models;

public class ModelSaveForLater {

    private String productId, timestamp;

    public ModelSaveForLater() {
    }

    public ModelSaveForLater(String productId, String timestamp) {
        this.productId = productId;
        this.timestamp = timestamp;
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
}
