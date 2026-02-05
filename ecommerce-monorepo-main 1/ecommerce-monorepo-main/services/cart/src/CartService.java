// services/cart/src/CartService.java

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CartService {

    private final CartValidator validator;

    // In-memory cart store: cartId -> cart
    private final Map<String, Cart> carts = new LinkedHashMap<>();

    public CartService(CartValidator validator) {
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
    }

    public Cart getOrCreateCart(String cartId) {
        requireNonBlank(cartId, "cartId must not be blank");
        return carts.computeIfAbsent(cartId, Cart::new);
    }

    public Cart addItem(String cartId, CartValidator.CartItem item) {
        validator.validateAddItem(item);

        Cart cart = getOrCreateCart(cartId);
        cart.addOrIncrement(item.getSku(), item.getQuantity(), item.getUnitPriceCents());
        return cart;
    }

    public Cart updateQuantity(String cartId, String sku, int newQuantity) {
        validator.validateUpdateQuantity(sku, newQuantity);

        Cart cart = getOrCreateCart(cartId);
        cart.setQuantity(sku, newQuantity);
        return cart;
    }

    public Cart removeItem(String cartId, String sku) {
        validator.validateRemoveItem(sku);

        Cart cart = getOrCreateCart(cartId);
        cart.remove(sku);
        return cart;
    }

    public long calculateSubtotalCents(String cartId) {
        Cart cart = getOrCreateCart(cartId);
        return cart.subtotalCents();
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    // ---- Minimal domain model (kept in-file for easy copy/paste) ----

    public static class Cart {
        private final String cartId;
        private final Map<String, LineItem> items = new LinkedHashMap<>();

        public Cart(String cartId) {
            this.cartId = cartId;
        }

        public String getCartId() { return cartId; }

        public Map<String, LineItem> getItems() {
            return Collections.unmodifiableMap(items);
        }

        public void addOrIncrement(String sku, int qtyToAdd, long unitPriceCents) {
            LineItem existing = items.get(sku);
            if (existing == null) {
                items.put(sku, new LineItem(sku, qtyToAdd, unitPriceCents));
            } else {
                // Keep last known unit price (simple)
                existing.quantity += qtyToAdd;
                existing.unitPriceCents = unitPriceCents;
            }
        }

        public void setQuantity(String sku, int newQty) {
            LineItem existing = items.get(sku);
            if (existing == null) {
                throw new CartNotFoundException("SKU not found in cart: " + sku);
            }
            existing.quantity = newQty;
        }

        public void remove(String sku) {
            items.remove(sku);
        }

        public long subtotalCents() {
            long sum = 0;
            for (LineItem li : items.values()) {
                sum += li.unitPriceCents * (long) li.quantity;
            }
            return sum;
        }
    }

    public static class LineItem {
        private final String sku;
        private int quantity;
        private long unitPriceCents;

        public LineItem(String sku, int quantity, long unitPriceCents) {
            this.sku = sku;
            this.quantity = quantity;
            this.unitPriceCents = unitPriceCents;
        }

        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public long getUnitPriceCents() { return unitPriceCents; }
    }

    public static class CartNotFoundException extends RuntimeException {
        public CartNotFoundException(String message) {
            super(message);
        }
    }
}
