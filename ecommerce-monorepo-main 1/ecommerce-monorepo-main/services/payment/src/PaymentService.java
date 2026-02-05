public class PaymentService {
    private RetryPolicy retryPolicy;

    public PaymentResult processPayment(PaymentRequest request) {
        int attempt = 0;
        Exception lastException = null;

        while (retryPolicy.shouldRetry(attempt, lastException)) {
            try {
                return gateway.charge(request);
            } catch (Exception e) {
                lastException = e;
                try { Thread.sleep(retryPolicy.getBackoffDelay(attempt)); } catch(Exception ignored) {}
                attempt++;
            }
        }
        throw new PaymentFailedException("Retries exhausted");
    }
}
