package kashyap.anurag.frenzystore.Models;

public class WishListModel {

    private  String productId, trendingCount, productTitle, timestamp;

    public WishListModel() {
    }

    public WishListModel(String productId, String trendingCount, String productTitle, String timestamp) {
        this.productId = productId;
        this.trendingCount = trendingCount;
        this.productTitle = productTitle;
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTrendingCount() {
        return trendingCount;
    }

    public void setTrendingCount(String trendingCount) {
        this.trendingCount = trendingCount;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
