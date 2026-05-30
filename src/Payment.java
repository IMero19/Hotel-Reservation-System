import java.time.LocalDate;

public class Payment {
    private int paymentID;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentDate;

    public Payment(int paymentID, double amount, String paymentMethod) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "Pending";
        this.paymentDate = LocalDate.now().toString();
    }

    public int getPaymentID() { return paymentID; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentDate() { return paymentDate; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    public void setPaymentStatus(String status) { this.paymentStatus = status; }
    public void setPaymentDate(String date) { this.paymentDate = date; }

    public void processPayment() {
        paymentStatus = "Completed";
        System.out.println("Payment processed successfully.");
    }

    public void refundPayment() {
        paymentStatus = "Refunded";
        System.out.println("Payment refunded.");
    }

    public void displayPaymentInfo() {
        System.out.printf("Payment ID: %d | Amount: $%.2f | Method: %s | Status: %s | Date: %s%n",
                paymentID, amount, paymentMethod, paymentStatus, paymentDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Payment)) return false;
        Payment p = (Payment) obj;
        return this.paymentID == p.paymentID;
    }

    @Override
    public int hashCode() { return Integer.hashCode(paymentID); }
}
