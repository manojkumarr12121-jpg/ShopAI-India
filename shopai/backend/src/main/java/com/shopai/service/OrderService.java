package com.shopai.service;

import com.shopai.dto.ApiResponse;
import com.shopai.model.*;
import com.shopai.repository.OrderRepository;
import com.shopai.repository.ProductRepository;
import com.shopai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    // ── Place a new order ─────────────────────────────────────────────────────
    @Transactional
    public Map<String, Object> placeOrder(Map<String, Object> body) {

        // 1. Extract user
        Long userId = Long.parseLong(body.get("userId").toString());
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Extract cart items  [{ productId, qty, finalPrice }]
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) body.get("cartItems");
        if (cartItems == null || cartItems.isEmpty())
            throw new RuntimeException("Cart is empty");

        // 3. Build OrderItems and calculate totals
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map<String, Object> ci : cartItems) {
            Long pid  = Long.parseLong(ci.get("productId").toString());
            int  qty  = Integer.parseInt(ci.get("qty").toString());
            BigDecimal unitPrice = new BigDecimal(ci.get("finalPrice").toString());

            Product product = productRepo.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + pid));

            // Check stock
            if (product.getStockQty() < qty)
                throw new RuntimeException("Insufficient stock for: " + product.getTitle());

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(lineTotal);

            items.add(OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .discountPercent(product.getDiscountPercent())
                    .totalPrice(lineTotal)
                    .build());

            // Deduct stock
            product.setStockQty(product.getStockQty() - qty);
            productRepo.save(product);
        }

        // 4. Shipping & promo
        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(499)) >= 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(49);

        String promoCode = body.getOrDefault("promoCode", "").toString().toUpperCase().trim();
        BigDecimal discount = applyPromo(promoCode, subtotal);

        BigDecimal finalAmount = subtotal.add(shipping).subtract(discount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

        // 5. Build and save Order
        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .totalAmount(subtotal)
                .discountAmount(discount)
                .shippingAmount(shipping)
                .finalAmount(finalAmount)
                .status(Order.OrderStatus.CONFIRMED)
                .paymentMethod(body.getOrDefault("paymentMethod", "COD").toString())
                .paymentStatus(Order.PaymentStatus.PAID)
                .shippingAddress(body.getOrDefault("shippingAddress", user.getAddress() != null ? user.getAddress() : user.getCity()).toString())
                .promoCode(promoCode.isEmpty() ? null : promoCode)
                .build();

        Order saved = orderRepo.save(order);

        // Attach order reference to items and save
        items.forEach(i -> i.setOrder(saved));
        saved.setItems(items);
        orderRepo.save(saved);

        log.info("Order placed: {} for user: {}", orderNumber, user.getEmail());

        // 6. Return response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNumber", orderNumber);
        result.put("orderId",     saved.getOrderId());
        result.put("status",      "CONFIRMED");
        result.put("subtotal",    subtotal);
        result.put("shipping",    shipping);
        result.put("discount",    discount);
        result.put("finalAmount", finalAmount);
        result.put("paymentMethod", order.getPaymentMethod());
        result.put("message",     "Order placed successfully! Order ID: " + orderNumber);
        return result;
    }

    // ── Get all orders for a user ─────────────────────────────────────────────
    public List<Map<String, Object>> getOrdersByUser(Long userId) {
        List<Order> orders = orderRepo.findByUserUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order o : orders) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("orderId",       o.getOrderId());
            map.put("orderNumber",   o.getOrderNumber());
            map.put("status",        o.getStatus());
            map.put("paymentStatus", o.getPaymentStatus());
            map.put("finalAmount",   o.getFinalAmount());
            map.put("itemCount",     o.getItems() != null ? o.getItems().size() : 0);
            map.put("createdAt",     o.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    // ── Get single order detail ───────────────────────────────────────────────
    public Map<String, Object> getOrderDetail(String orderNumber) {
        Order o = orderRepo.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderId",        o.getOrderId());
        map.put("orderNumber",    o.getOrderNumber());
        map.put("status",         o.getStatus());
        map.put("paymentStatus",  o.getPaymentStatus());
        map.put("paymentMethod",  o.getPaymentMethod());
        map.put("totalAmount",    o.getTotalAmount());
        map.put("discountAmount", o.getDiscountAmount());
        map.put("shippingAmount", o.getShippingAmount());
        map.put("finalAmount",    o.getFinalAmount());
        map.put("shippingAddress",o.getShippingAddress());
        map.put("promoCode",      o.getPromoCode());
        map.put("createdAt",      o.getCreatedAt());

        if (o.getItems() != null) {
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (OrderItem i : o.getItems()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productId",   i.getProduct().getProductId());
                item.put("productName", i.getProduct().getTitle());
                item.put("brand",       i.getProduct().getBrand());
                item.put("imageUrl",    i.getProduct().getImageUrl());
                item.put("quantity",    i.getQuantity());
                item.put("unitPrice",   i.getUnitPrice());
                item.put("totalPrice",  i.getTotalPrice());
                itemList.add(item);
            }
            map.put("items", itemList);
        }
        return map;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String generateOrderNumber() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = new Random().nextInt(9000) + 1000;
        return "SAI-" + ts + "-" + rand;
    }

    private BigDecimal applyPromo(String code, BigDecimal subtotal) {
        return switch (code) {
            case "AISHIP50"  -> subtotal.multiply(BigDecimal.valueOf(0.50));
            case "INDIA200"  -> BigDecimal.valueOf(200);
            case "NEWUSER30" -> subtotal.multiply(BigDecimal.valueOf(0.30));
            default          -> BigDecimal.ZERO;
        };
    }
}
