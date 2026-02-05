// services/checkout/src/CheckoutOrchestrator.java

import java.util.Objects;

/**
 * CheckoutOrchestrator coordinates dependent services:
 * - Cart totals
 * - Pricing (discount/tax/shipping)
 * - Payment processing
 * - Order state updates
 *
 * This is where cross-service impact becomes obvious for Agent 2.
 */
public class CheckoutOrchestrator {

    private final CartPort cartPort;
    private final PricingPort pricingPort;
    private final CheckoutService checkoutService;

    public CheckoutOrchestrator(CartPort cartPort, PricingPort pricingPort, CheckoutService checkoutService) {
        this.cartPort = Objects.requireNonNull(cartPort, "cartPort must not be null");
        this.pricingPort = Objects.requireNonNull(pricingPort, "pricingPort must not be null");
        this.checkoutService = Objects.requireNonNull(checkoutService, "checkoutService must not be null");
    }

    public CheckoutFacade.CheckoutReceipt placeOrder(String cartId, String userId) {
        // Pull cart subtotal from cart domain
        long subtotalCents = cartPort.getSubtotalCents(cartId);

        // Pricing step: apply discount/tax/shipping (kept simple and deterministic)
        PricingPort.PricingBreakdown breakdown = pricingPort.calculateTotals(subtotalCents, userId);

        // Delegate to CheckoutService for payment + order state changes
        CheckoutService.CheckoutResult result = checkoutService.checkout(
                cartId,
                userId,
                breakdown.getTotalCents()
        );

        return new CheckoutFacade.CheckoutReceipt(
                result.getOrderId(),
                result.getPaymentId(),
                breakdown.getTotalCents()
        );
    }

    // ---- Ports (interfaces) so this monorepo stays modular & testable ----

    public interface CartPort {
        long getSubtotalCents(String cartId);
    }

    public interface PricingPort {
        PricingBreakdown calculateTotals(long subtotalCents, String userId);

        class PricingBreakdown {
            private final long subtotalCents;
            private final long discountCents;
            private final long taxCents;
            private final long shippingCents;

            public PricingBreakdown(long subtotalCents, long discountCents, long taxCents, long shippingCents) {
                this.subtotalCents = subtotalCents;
                this.discountCents = discountCents;
                this.taxCents = taxCents;
                this.shippingCents = shippingCents;
            }

            public long getSubtotalCents() { return subtotalCents; }
            public long getDiscountCents() { return discountCents; }
            public long getTaxCents() { return taxCents; }
            public long getShippingCents() { return shippingCents; }

            public long getTotalCents() {
                long discounted = Math.max(0, subtotalCents - discountCents);
                return discounted + taxCents + shippingCents;
            }
        }
    }
}
