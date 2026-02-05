public class CheckoutService {
    public void checkout(String orderId) {
        try {
            paymentService.processPayment(request);
        } catch (PaymentFailedException e) {
            orderService.markPaymentFailed(orderId);
            throw new CheckoutException("Payment failed after retries");
        }
    }
}
