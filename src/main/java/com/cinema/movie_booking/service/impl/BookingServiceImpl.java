package com.cinema.movie_booking.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.movie_booking.dto.booking.BookingRequestDTO;
import com.cinema.movie_booking.dto.booking.BookingResponseDTO;
import com.cinema.movie_booking.entity.*;
import com.cinema.movie_booking.exception.ResourceNotFoundException;
import com.cinema.movie_booking.exception.SeatAlreadyBookedException;
import com.cinema.movie_booking.mapper.BookingMapper;
import com.cinema.movie_booking.repository.*;
import com.cinema.movie_booking.service.BookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final SeatRepository seatRepository;
    private final BookingDetailRepository bookingDetailRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(Long userId, BookingRequestDTO request) {

        // ===== 1. Validate =====
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new IllegalArgumentException("Seat list is empty");
        }

        // ===== 2. Lock seats =====
        List<Seat> seats = seatRepository.findAllByIdForUpdate(request.getSeatIds());

        if (seats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("Some seats not found");
        }

        // ===== 3. Check already booked =====
        for (Seat seat : seats) {
            boolean isBooked = bookingDetailRepository
                    .existsByShowtimeIdAndSeatId(showtime.getShowtimeId(), seat.getSeatId());

            if (isBooked) {
                throw new SeatAlreadyBookedException("Seat already booked: " + seat.getSeatId());
            }
        }

        // ===== 4. Calculate price =====
        double totalAmount = seats.stream()
                .mapToDouble(seat -> showtime.getPrice() * seat.getSeatType().getPriceMultiplier())
                .sum();

        double discountAmount = 0;
        PromoCode promo = null;

        // ===== 5. Apply promo =====
        if (request.getPromoCodeId() != null) {

            promo = promoCodeRepository.findById(request.getPromoCodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

            discountAmount = promo.calculateDiscount(totalAmount);

            promo.setUsedCount(promo.getUsedCount() + 1);
            promoCodeRepository.save(promo);
        }

        double finalAmount = Math.max(totalAmount - discountAmount, 0);

        // ===== 6. Create booking =====
        Booking booking = Booking.builder()
                .bookingCode(generateBookingCode())
                .user(user)
                .showtime(showtime)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status("PENDING")
                .quantity(seats.size())
                .bookingDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .promoCode(promo)
                .notes(request.getNotes())
                .build();

        bookingRepository.save(booking);

        // ===== 7. Create booking details =====
        List<BookingDetail> details = new ArrayList<>();

        for (Seat seat : seats) {

            double price = showtime.getPrice() * seat.getSeatType().getPriceMultiplier();

            BookingDetail detail = BookingDetail.builder()
                    .booking(booking)
                    .seat(seat)
                    .unitPrice(price)
                    .quantity(1)
                    .subtotal(price)
                    .build();

            details.add(detail);
        }

        bookingDetailRepository.saveAll(details);
        booking.setBookingDetails(details);

        return BookingMapper.toBookingResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO getBookingByCode(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        return BookingMapper.toBookingResponseDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getUserBookings(Long userId) {

        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void cancelBooking(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalStateException("Cannot cancel confirmed booking");
        }

        booking.setStatus("CANCELLED");
    }

    private String generateBookingCode() {
        String code;
        do {
            code = "BK" + System.currentTimeMillis();
        } while (bookingRepository.existsByBookingCode(code));
        return code;
    }
}