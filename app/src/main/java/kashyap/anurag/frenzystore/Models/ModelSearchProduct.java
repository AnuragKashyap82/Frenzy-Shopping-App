package kashyap.anurag.frenzystore.Models;

public class ModelSearchProduct {

    private String productId, productTitle, category, productImage;

    public ModelSearchProduct() {
    }

    public ModelSearchProduct(String productId, String productTitle, String category, String productImage) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.category = category;
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
