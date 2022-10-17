package kashyap.anurag.frenzystore.Models;

public class ModelMostSoldProduct {

    private String productTitle, trendingCount, productId, sellingCount;
    private boolean active;

    public ModelMostSoldProduct() {
    }

    public ModelMostSoldProduct(String productTitle, String trendingCount, String productId, String sellingCount, boolean active) {
        this.productTitle = productTitle;
        this.trendingCount = trendingCount;
        this.productId = productId;
        this.sellingCount = sellingCount;
        this.active = active;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getTrendingCount() {
        return trendingCount;
    }

    public void setTrendingCount(String trendingCount) {
        this.trendingCount = trendingCount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellingCount() {
        return sellingCount;
    }

    public void setSellingCount(String sellingCount) {
        this.sellingCount = sellingCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
