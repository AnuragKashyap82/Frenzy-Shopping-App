package kashyap.anurag.frenzystore.Models;

import android.widget.LinearLayout;
import android.widget.TextView;

public class CartItemModel {

    private String productId, timestamp;

    public CartItemModel() {
    }

    public CartItemModel(String productId, String timestamp) {
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
