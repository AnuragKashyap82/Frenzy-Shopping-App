package kashyap.anurag.frenzystore.Models;

public class RewardModel {
    String validDate;

    public RewardModel() {
    }

    public RewardModel(String validDate) {
        this.validDate = validDate;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }
}
