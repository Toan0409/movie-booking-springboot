package com.cinema.movie_booking.dto.bookingdetail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDetailResponseDTO {
    private Long bookingDetailId;

    private Long seatId;

    private Integer seatNumber;

    private Double unitPrice;

    private Integer quantity;

    private Double subtotal;
}
