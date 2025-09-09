# E‑commerce Domain: Recommended Entities to Add

This document outlines typical entities for a transactional e‑commerce system, how they relate to existing models (User, Product, Category, Role), and suggested fields/relationships. It’s meant as a practical roadmap for incremental implementation in this Spring Boot + JPA project.

Key goals:
- Keep controllers/services cohesive and focused.
- Normalize data to support checkout, fulfillment, and analytics.
- Allow future growth (multi‑address, multiple payment attempts, refunds, returns, promotions, etc.).

Note: This is a planning document. It does not change runtime behavior. When you begin implementing, add entities gradually and wire repositories/services/controllers with tests.

---

## 1) Customer & Identity Layer

Leverage current `User` (with `Role`) and add profile/address details separated from auth.

- CustomerProfile
  - Why: Separate PII and preferences from auth fields and allow additional customer metadata.
  - Fields: id (PK), user (1:1 User), phone, marketingOptIn, dateOfBirth (optional), createdAt, updatedAt
  - Rels: OneToOne(User)

- Address
  - Why: Customers typically have multiple addresses (shipping, billing). Store independently.
  - Fields: id, user (FK), firstName, lastName, line1, line2, city, state, postalCode, country, phone, type (BILLING/SHIPPING), isDefault, createdAt, updatedAt
  - Rels: ManyToOne(User), used by Cart, Order (snapshotted), Shipment

- UserRole or Authority (optional, only if you need finer RBAC than existing enum Role)
  - Why: Finer‑grained permissions for admin tooling.

Implementation note: Keep using `Role` enum for now; expand later if needed.

---

## 2) Catalog Extensions

Existing: `Product`, `Category`.

- ProductVariant (SKU)
  - Why: Support size/color/material variations, unique stock and price by variant.
  - Fields: id, product (FK), sku, title (e.g., "T‑Shirt - Medium - Blue"), price, compareAtPrice, weight, barcode, attributes (JSON or Option/Value refs), isActive, createdAt, updatedAt
  - Rels: ManyToOne(Product), OneToMany(ProductImage) optionally, OneToMany(Inventory)

- Option & OptionValue (optional)
  - Why: Structured options (Size, Color) and values (M, Blue) to compose variants.
  - Fields: Option(id, name, displayName, position); OptionValue(id, option, value, displayValue, position)
  - Rels: OneToMany(OptionValue), ManyToMany between Variant and OptionValue (join table)

- ProductImage
  - Why: Multiple images per product/variant.
  - Fields: id, product(FK), variant(FK, nullable), url, altText, position, isPrimary

- Review (ProductReview)
  - Why: Social proof and SEO; moderation may be needed.
  - Fields: id, product(FK), user(FK), rating (1‑5), title, body, status (PENDING/APPROVED/REJECTED), createdAt, updatedAt

- Tag (optional)
  - Why: Flexible product grouping beyond categories.
  - Rels: ManyToMany(Product)

---

## 3) Cart & Checkout

- Cart
  - Why: Persist shoppers’ selections prior to order; guest carts may be supported with a token.
  - Fields: id, user(FK, nullable for guest), sessionToken (for guests), currency, subtotal, discountTotal, taxTotal, shippingTotal, grandTotal, createdAt, updatedAt, status (ACTIVE/CONVERTED/ABANDONED)
  - Rels: OneToMany(CartItem), ManyToOne(Address billing/shipping snapshot optional), Coupon redemptions

- CartItem
  - Why: Line items for the cart.
  - Fields: id, cart(FK), product(FK nullable if using variant strictly), variant(FK), quantity, unitPrice, lineSubtotal, lineDiscount, lineTax, lineTotal, createdAt, updatedAt

- Coupon / Promotion
  - Why: Discounts and promotional campaigns.
  - Fields (Coupon): id, code, type (PERCENT/FIXED/SHIPPING), value, usageLimit, usageCount, minOrderAmount, startsAt, endsAt, isActive, createdAt, updatedAt
  - Rels: ManyToMany(Product/Category) inclusion/exclusion (optional)

- TaxRate / TaxRule (optional initially)
  - Why: Handle tax calculation by region.
  - Fields: id, country, state, postalCodePattern, percentage, isActive

---

## 4) Orders & Fulfillment

- Order
  - Why: Immutable record of a purchase, including snapshots of pricing and addresses at the time of checkout.
  - Fields: id, orderNumber (unique human‑friendly), user(FK, nullable for guest), status (NEW/PAID/SHIPPED/DELIVERED/CANCELLED/REFUNDED), currency, subtotal, discountTotal, taxTotal, shippingTotal, grandTotal, placedAt, updatedAt
  - Snapshots: billingAddress*, shippingAddress*, customerEmail/name (in case Account changes)
  - Rels: OneToMany(OrderItem), OneToMany(Payment), OneToMany(Shipment)

- OrderItem
  - Why: Snapshot of what was purchased.
  - Fields: id, order(FK), productId, variantId, name, sku, unitPrice, quantity, lineSubtotal, lineDiscount, lineTax, lineTotal

- Payment
  - Why: Record attempts and outcomes from the PSP (payment service provider).
  - Fields: id, order(FK), provider (STRIPE/PAYPAL/etc.), amount, currency, status (PENDING/SUCCEEDED/FAILED/REFUNDED/PARTIALLY_REFUNDED), transactionId, failureReason, createdAt, updatedAt
  - Rels: OneToMany(PaymentTransaction) if you want granular events

- PaymentTransaction (optional granular log)
  - Why: Track auth, capture, refund events separately.
  - Fields: id, payment(FK), type (AUTH/CAPTURE/REFUND), amount, currency, occurredAt, rawPayload (JSON)

- Shipment
  - Why: Track fulfillment of order items.
  - Fields: id, order(FK), carrier, serviceLevel, trackingNumber, shippedAt, deliveredAt, status (PENDING/SHIPPED/DELIVERED/RETURNED), shippingCost
  - Rels: OneToMany(ShipmentItem), ManyToOne(Address origin warehouse optional)

- ShipmentItem (optional if partial shipments supported)
  - Fields: id, shipment(FK), orderItem(FK), quantity

- Return & Refund (optional to start)
  - ReturnRequest: id, order(FK), reason, status (REQUESTED/AUTHORIZED/RECEIVED/REJECTED), createdAt
  - Refund: id, order(FK), payment(FK), amount, currency, reason, status (PENDING/COMPLETED/FAILED), createdAt

---

## 5) Inventory & Warehousing

- InventoryItem (Stock)
  - Why: Track on‑hand stock per variant, possibly per warehouse.
  - Fields: id, variant(FK), warehouse(FK nullable if single location), onHand, reserved, available (computed), reorderLevel, updatedAt

- StockMovement
  - Why: Audit of inventory changes (purchase, sale, return, adjustment).
  - Fields: id, variant(FK), type (IN/OUT/ADJUSTMENT/RESERVATION), quantity, referenceType (ORDER/RETURN/PO/ADJUSTMENT), referenceId, occurredAt, note

- Warehouse (optional for multi‑location)
  - Fields: id, name, address(FK), isActive

Implementation note: If you start simple, keep a single InventoryItem per Variant.

---

## 6) Content, Engagement, and Support

- Wishlist
  - Fields: id, user(FK), createdAt; Rels: OneToMany(WishlistItem)
  - WishlistItem: id, wishlist(FK), product(FK), variant(FK nullable), addedAt

- Review (mentioned above under Catalog)

- Notification / EmailLog
  - Why: Record of transactional emails (order confirmations, shipping notices) for support.
  - Fields: id, toEmail, type (ORDER_CONFIRMATION, SHIPPING_UPDATE, PASSWORD_RESET, etc.), status (QUEUED/SENT/FAILED), providerMessageId, sentAt, payload (JSON)

- AuditLog
  - Why: Admin actions trail (price change, order status changes) for compliance and debugging.
  - Fields: id, actorUserId, action, entityType, entityId, before(JSON), after(JSON), occurredAt

---

## 7) Config & Settings

- StoreConfig / FeatureFlag (optional)
  - Fields: key, value, type, isActive; Enable/disable features or keep secrets out of code (non‑secret operational flags only).

- ShippingMethod / RateTable (optional)
  - Define carrier/service options, base cost and logic for shipping total calculation.

---

## 8) Relationship Overview (High Level)

- User 1—1 CustomerProfile; User 1—N Address; User 1—N Review; User 1—N Wishlist
- Product 1—N ProductVariant; Product 1—N ProductImage; Product N—M Tag; Product 1—N Review
- Variant 1—N InventoryItem; Variant N—M OptionValue
- Cart 1—N CartItem; Cart may relate to Coupon (N—M via redemptions)
- Order 1—N OrderItem; Order 1—N Payment; Order 1—N Shipment; Order N—N Coupon (applied)
- Shipment 1—N ShipmentItem; ShipmentItem N—1 OrderItem

---

## 9) Minimal JPA Sketches (for reference only)

Package suggestion: `com.lordbyronsenterprises.model` (consistent with existing models). These are sketches to guide future implementation.

```java
// Order.java
@Entity
public class Order {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true) private String orderNumber;
  @ManyToOne private User user; // nullable for guest
  @Enumerated(EnumType.STRING) private OrderStatus status;
  private String currency;
  private BigDecimal subtotal; private BigDecimal discountTotal; private BigDecimal taxTotal; private BigDecimal shippingTotal; private BigDecimal grandTotal;
  // Snapshotted addresses
  @Embedded private AddressSnapshot billingAddress;
  @Embedded private AddressSnapshot shippingAddress;
  private Instant placedAt; private Instant updatedAt;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) private List<OrderItem> items = new ArrayList<>();
}

// OrderItem.java
@Entity
public class OrderItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private Order order;
  private Long productId; private Long variantId; private String name; private String sku;
  private BigDecimal unitPrice; private Integer quantity; private BigDecimal lineSubtotal; private BigDecimal lineDiscount; private BigDecimal lineTax; private BigDecimal lineTotal;
}

// Cart.java
@Entity
public class Cart {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private User user; // nullable for guest
  @Column(unique = true) private String sessionToken; // for guests
  private String currency; private BigDecimal subtotal; private BigDecimal discountTotal; private BigDecimal taxTotal; private BigDecimal shippingTotal; private BigDecimal grandTotal;
  @Enumerated(EnumType.STRING) private CartStatus status;
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) private List<CartItem> items = new ArrayList<>();
}

// CartItem.java
@Entity
public class CartItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private Cart cart;
  @ManyToOne private Product product; // or ProductVariant if variants
  private Integer quantity; private BigDecimal unitPrice; private BigDecimal lineTotal;
}

// Address.java
@Entity
public class Address {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private User user;
  private String firstName, lastName, line1, line2, city, state, postalCode, country, phone;
  @Enumerated(EnumType.STRING) private AddressType type; // BILLING, SHIPPING
  private boolean isDefault;
}

// Payment.java
@Entity
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private Order order;
  private String provider; private BigDecimal amount; private String currency;
  @Enumerated(EnumType.STRING) private PaymentStatus status;
  private String transactionId; private String failureReason;
}

// Shipment.java
@Entity
public class Shipment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private Order order;
  private String carrier; private String serviceLevel; private String trackingNumber;
  @Enumerated(EnumType.STRING) private ShipmentStatus status;
}

// InventoryItem.java
@Entity
public class InventoryItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne private Product product; // or ProductVariant if you adopt variants
  private Integer onHand; private Integer reserved;
}
```

Enums you might add: OrderStatus, CartStatus, AddressType, PaymentStatus, ShipmentStatus.

---

## 10) Phased Implementation Plan

1) Checkout backbone
- Cart + CartItem; Coupon (basic percent/fixed); Address; minimal TaxRate. 
- Wire services and endpoints to support add to cart, update quantities, apply coupon, estimate totals.

2) Order capture and payment
- Order + OrderItem; Payment recording (without PSP integration initially); basic inventory decrement.

3) Fulfillment
- Shipment + ShipmentItem; shipping label/tracking fields; order status transitions.

4) Catalog variants and inventory
- ProductVariant; InventoryItem; StockMovement for audit.

5) Engagement & reviews
- Review, Wishlist; moderation endpoints.

6) Returns and refunds
- ReturnRequest + Refund; align with Payment/PSP flows.

---

## 11) Notes for This Repository

- Package placement: `src/main/java/com/lordbyronsenterprises/server/model` for entities; match existing structure for repositories, services, controllers, and mappers.
- Mapping: Use DTOs and mappers (as with Product/User) to keep controllers thin.
- Validation: Prefer DTO validation (`jakarta.validation`) at controller boundaries.
- Security: Keep BCrypt password hashing already set up; later, add authentication/authorization for checkout and admin routes.
- Database migrations: If you adopt Flyway/Liquibase later, codify schema changes incrementally.

This guide should help you choose and prioritize the next entities to implement according to your business needs.