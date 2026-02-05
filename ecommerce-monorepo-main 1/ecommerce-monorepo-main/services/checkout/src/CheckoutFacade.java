// services/checkout/src/CheckoutFacade.java

import java.util.Objects;

/**
 * CheckoutFacade is the API-facing entry point for checkout flows.
 * In a real app, a REST controller would call this facade.
 */
public class CheckoutFacade {

    private final CheckoutOrchestrator orchestrator;

    public CheckoutFacade(CheckoutOrchestrator orchestrator) {
        this.orchestrator = Objects.requireNonNull(orchestrator, "orchestrator must not be null");
    }

    /**
     * Initiates checkout for a given cart + user.
     */
    public CheckoutReceipt checkout(String cartId, String userId) {
        return orchestrator.placeOrder(cartId, userId);
    }

    /**
     * Minimal DTO returned to caller.
     */
    public static class CheckoutReceipt {
        private final String orderId;
        private final String paymentId;
        private final long totalCents;

        public CheckoutReceipt(String orderId, String paymentId, long totalCents) {
            this.orderId = orderId;
            this.paymentId = paymentId;
            this.totalCents = totalCents;
        }

        public String getOrderId() { return orderId; }
        public String getPaymentId() { return paymentId; }
        public long getTotalCents() { return totalCents; }
    }
}
