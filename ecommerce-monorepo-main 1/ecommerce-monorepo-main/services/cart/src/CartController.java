// services/cart/src/CartController.java

import java.util.Objects;

/**
 * "Controller-style" wrapper for CartService.
 * This is framework-neutral so you can later wrap it with Spring annotations if you want.
 */
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = Objects.requireNonNull(cartService, "cartService must not be null");
    }

    public CartService.Cart getCart(String cartId) {
        return cartService.getOrCreateCart(cartId);
    }

    public CartService.Cart addItem(String cartId, String sku, int quantity, long unitPriceCents) {
        CartValidator.CartItem item = new CartValidator.CartItem(sku, quantity, unitPriceCents);
        return cartService.addItem(cartId, item);
    }

    public CartService.Cart updateQuantity(String cartId, String sku, int newQuantity) {
        return cartService.updateQuantity(cartId, sku, newQuantity);
    }

    public CartService.Cart removeItem(String cartId, String sku) {
        return cartService.removeItem(cartId, sku);
    }

    public long getSubtotalCents(String cartId) {
        return cartService.calculateSubtotalCents(cartId);
    }
}
