// services/order/src/OrderService.java

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Minimal OrderService:
 * - creates orders
 * - marks payment success/failure
 *
 * Uses an in-memory store for demo purposes.
 */
public class OrderService {

    private final OrderStateMachine stateMachine;
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public OrderService(OrderStateMachine stateMachine) {
        this.stateMachine = Objects.requireNonNull(stateMachine, "stateMachine must not be null");
    }

    public String createOrder(String cartId, String userId, long totalCents) {
        requireNonBlank(cartId, "cartId must not be blank");
        requireNonBlank(userId, "userId must not be blank");
        if (totalCents < 0) throw new IllegalArgumentException("totalCents must be >= 0");

        String orderId = "ord_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Order o = new Order(orderId, cartId, userId, totalCents, stateMachine.initial());
        // Common approach: once created, mark as PAYMENT_PENDING
        o.status = stateMachine.markPaymentPending(o.status);

        orders.put(orderId, o);
        return orderId;
    }

    public void markPaymentSucceeded(String orderId, String paymentId) {
        requireNonBlank(paymentId, "paymentId must not be blank");
        Order o = get(orderId);

        o.paymentId = paymentId;
        o.status = stateMachine.markPaid(o.status);
    }

    public void markPaymentFailed(String orderId, String reason) {
        requireNonBlank(reason, "reason must not be blank");
        Order o = get(orderId);

        o.failureReason = reason;
        o.status = stateMachine.markPaymentFailed(o.status);
    }

    public Order getOrder(String orderId) {
        return get(orderId);
    }

    // ---- internal helpers ----

    private Order get(String orderId) {
        requireNonBlank(orderId, "orderId must not be blank");
        Order o = orders.get(orderId);
        if (o == null) throw new OrderNotFoundException("Order not found: " + orderId);
        return o;
    }

    private void requireNonBlank(String v, String msg) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(msg);
    }

    // ---- Domain object ----

    public static class Order {
        private final String orderId;
        private final String cartId;
        private final String userId;
        private final long totalCents;

        private OrderStateMachine.OrderStatus status;

        private String paymentId;
        private String failureReason;

        private Order(String orderId, String cartId, String userId, long totalCents, OrderStateMachine.OrderStatus status) {
            this.orderId = orderId;
            this.cartId = cartId;
            this.userId = userId;
            this.totalCents = totalCents;
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public String getCartId() { return cartId; }
        public String getUserId() { return userId; }
        public long getTotalCents() { return totalCents; }
        public OrderStateMachine.OrderStatus getStatus() { return status; }
        public String getPaymentId() { return paymentId; }
        public String getFailureReason() { return failureReason; }
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }
}
