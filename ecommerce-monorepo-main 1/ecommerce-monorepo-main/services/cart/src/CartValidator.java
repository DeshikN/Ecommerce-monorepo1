// services/cart/src/CartValidator.java

import java.util.Objects;

public class CartValidator {

    public void validateAddItem(CartItem item) {
        Objects.requireNonNull(item, "item must not be null");
        requireNonBlank(item.getSku(), "sku must not be blank");
        requirePositive(item.getQuantity(), "quantity must be > 0");
        requireNonNegative(item.getUnitPriceCents(), "unitPriceCents must be >= 0");
    }

    public void validateUpdateQuantity(String sku, int newQuantity) {
        requireNonBlank(sku, "sku must not be blank");
        if (newQuantity <= 0) {
            throw new ValidationException("newQuantity must be > 0");
        }
    }

    public void validateRemoveItem(String sku) {
        requireNonBlank(sku, "sku must not be blank");
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    private void requirePositive(int value, String message) {
        if (value <= 0) {
            throw new ValidationException(message);
        }
    }

    private void requireNonNegative(long value, String message) {
        if (value < 0) {
            throw new ValidationException(message);
        }
    }

    // ---- Minimal supporting types ----
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class CartItem {
        private final String sku;
        private final int quantity;
        private final long unitPriceCents;

        public CartItem(String sku, int quantity, long unitPriceCents) {
            this.sku = sku;
            this.quantity = quantity;
            this.unitPriceCents = unitPriceCents;
        }

        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public long getUnitPriceCents() { return unitPriceCents; }
    }
}
