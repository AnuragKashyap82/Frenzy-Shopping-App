package kashyap.anurag.frenzystore.Models;

public class ModelQuestion {

    private String question, answer, questionId, productId, uid;

    public ModelQuestion() {
    }

    public ModelQuestion(String question, String answer, String questionId, String productId, String uid) {
        this.question = question;
        this.answer = answer;
        this.questionId = questionId;
        this.productId = productId;
        this.uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
