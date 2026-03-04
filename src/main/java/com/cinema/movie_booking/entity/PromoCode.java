package com.cinema.movie_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_code_id")
    private Long promoCodeId;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "min_order_amount")
    private Double minOrderAmount;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "promoCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
