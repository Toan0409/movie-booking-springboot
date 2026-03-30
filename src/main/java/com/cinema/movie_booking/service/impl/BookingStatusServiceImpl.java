package com.cinema.movie_booking.service.impl;

import com.cinema.movie_booking.dto.booking.BookingResponseDTO;
import com.cinema.movie_booking.dto.booking.UpdateBookingStatusRequestDTO;
import com.cinema.movie_booking.entity.*;
import com.cinema.movie_booking.enums.BookingStatus;
import com.cinema.movie_booking.exception.InvalidStatusTransitionException;
import com.cinema.movie_booking.exception.ResourceNotFoundException;
import com.cinema.movie_booking.mapper.BookingMapper;
import com.cinema.movie_booking.repository.*;
import com.cinema.movie_booking.service.BookingStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation xu ly logic cap nhat trang thai Booking.
 *
 * Luong xu ly chinh:
 * 1. Tim Booking theo ID, throw ResourceNotFoundException neu khong ton tai
 * 2. Kiem tra booking co het han khong (auto-expire)
 * 3. Validate chuyen trang thai theo rule
 * 4. Thuc hien side effects (giai phong ghe, tao Ticket, luu Payment)
 * 5. Luu trang thai moi
 * 6. Ghi log lich su thay doi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingStatusServiceImpl implements BookingStatusService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long bookingId,
            UpdateBookingStatusRequestDTO request,
            String changedBy) {

        log.info("[BookingStatus] Admin '{}' dang cap nhat booking #{} sang trang thai {}",
                changedBy, bookingId, request.getStatus());

        // 1. Tim booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay booking voi ID: " + bookingId));

        // 2. Kiem tra va xu ly auto-expire truoc khi validate
        checkAndAutoExpire(booking, changedBy);

        // 3. Lay trang thai hien tai
        BookingStatus currentStatus = parseStatus(booking.getStatus(), bookingId);
        BookingStatus newStatus = request.getStatus();

        // 4. Validate chuyen trang thai
        if (!currentStatus.canTransitionTo(newStatus)) {
            log.warn("[BookingStatus] Chuyen trang thai khong hop le: booking #{} tu {} sang {}",
                    bookingId, currentStatus, newStatus);
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        String oldStatus = booking.getStatus();

        // 5. Thuc hien side effects theo trang thai moi
        switch (newStatus) {
            case PAID -> handlePaidTransition(booking, request);
            case CANCELLED -> handleCancelledTransition(booking);
            case FAILED -> handleFailedTransition(booking);
            default -> {
                /* PENDING: khong co side effect */ }
        }

        // 6. Cap nhat trang thai booking
        booking.setStatus(newStatus.name());
        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDTO(booking);
    }

    /**
     * Scheduled task: tu dong huy cac booking PENDING da het han.
     * Chay moi 1 phut de kiem tra.
     */
    @Override
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoExpireBookings() {
        List<Booking> expiredBookings = bookingRepository.findByStatus(BookingStatus.PENDING.name())
                .stream()
                .filter(b -> b.getExpiryDate() != null
                        && LocalDateTime.now().isAfter(b.getExpiryDate()))
                .toList();

        if (expiredBookings.isEmpty()) {
            return;
        }

        log.info("[AutoExpire] Tim thay {} booking PENDING da het han, dang huy...",
                expiredBookings.size());

        for (Booking booking : expiredBookings) {
            try {
                String oldStatus = booking.getStatus();
                releaseSeats(booking);
                booking.setStatus(BookingStatus.CANCELLED.name());
                bookingRepository.save(booking);
                log.info("[AutoExpire] Da huy booking #{} (het han luc {})",
                        booking.getBookingId(), booking.getExpiryDate());
            } catch (Exception e) {
                log.error("[AutoExpire] Loi khi huy booking #{}: {}",
                        booking.getBookingId(), e.getMessage(), e);
            }
        }
    }

    // =========================================================================
    // PRIVATE HELPERS — Side Effects
    // =========================================================================

    /**
     * Xu ly khi chuyen sang PAID:
     * - Tao Ticket cho moi BookingDetail (neu chua co)
     * - Luu Payment record
     */
    private void handlePaidTransition(Booking booking, UpdateBookingStatusRequestDTO request) {
        log.debug("[BookingStatus] Xu ly PAID cho booking #{}", booking.getBookingId());

        // Tao tickets
        createTicketsForBooking(booking);

        // Luu payment (neu chua co)
        if (!paymentRepository.existsByBooking_BookingId(booking.getBookingId())) {
            String method = (request.getPaymentMethod() != null
                    && !request.getPaymentMethod().isBlank())
                            ? request.getPaymentMethod()
                            : "MANUAL";

            Payment payment = Payment.builder()
                    .paymentCode(generatePaymentCode())
                    .booking(booking)
                    .user(booking.getUser())
                    .amount(booking.getFinalAmount())
                    .paymentMethod(method)
                    .status("COMPLETED")
                    .transactionId(UUID.randomUUID().toString())
                    .paymentDate(LocalDateTime.now())
                    .paymentNote(request.getNote())
                    .build();

            paymentRepository.save(payment);
            log.debug("[BookingStatus] Da luu Payment cho booking #{}", booking.getBookingId());
            occupySeats(booking);
        } else {
            log.warn("[BookingStatus] Payment da ton tai cho booking #{}, bo qua tao moi",
                    booking.getBookingId());
        }
    }

    /**
     * Xu ly khi chuyen sang CANCELLED:
     * - Giai phong ghe (set isAvailable = true)
     * - Huy cac Ticket lien quan (neu co)
     */
    private void handleCancelledTransition(Booking booking) {
        log.debug("[BookingStatus] Xu ly CANCELLED cho booking #{}", booking.getBookingId());
        releaseSeats(booking);
        cancelTickets(booking);
    }

    /**
     * Xu ly khi chuyen sang FAILED:
     * - Giai phong ghe (set isAvailable = true)
     */
    private void handleFailedTransition(Booking booking) {
        log.debug("[BookingStatus] Xu ly FAILED cho booking #{}", booking.getBookingId());
        releaseSeats(booking);
    }

    /**
     * Giai phong ghe: set isAvailable = true cho tat ca ghe trong booking.
     */
    private void releaseSeats(Booking booking) {
        List<BookingDetail> details = bookingDetailRepository.findByBooking_BookingId(booking.getBookingId());

        if (details.isEmpty()) {
            log.warn("[BookingStatus] Booking #{} khong co booking detail nao de giai phong ghe",
                    booking.getBookingId());
            return;
        }

        List<Seat> seatsToRelease = details.stream()
                .map(BookingDetail::getSeat)
                .toList();

        seatsToRelease.forEach(seat -> {
            seat.setIsAvailable(true);
            log.debug("[BookingStatus] Giai phong ghe {} (ID: {})",
                    seat.getSeatCode(), seat.getSeatId());
        });

        seatRepository.saveAll(seatsToRelease);
        log.info("[BookingStatus] Da giai phong {} ghe cho booking #{}",
                seatsToRelease.size(), booking.getBookingId());
    }

    // Khi chuyển sang trạng thái PAID thì cần lock ghế
    private void occupySeats(Booking booking) {
        List<BookingDetail> details = bookingDetailRepository.findByBooking_BookingId(booking.getBookingId());

        List<Seat> seats = details.stream()
                .map(BookingDetail::getSeat)
                .toList();

        seats.forEach(seat -> {
            seat.setIsAvailable(false);
        });

        seatRepository.saveAll(seats);

        log.info("[BookingStatus] Da danh dau {} ghe la OCCUPIED cho booking #{}",
                seats.size(), booking.getBookingId());
    }

    /**
     * Tao Ticket cho moi BookingDetail chua co ticket.
     */
    private void createTicketsForBooking(Booking booking) {
        List<BookingDetail> details = bookingDetailRepository.findByBooking_BookingId(booking.getBookingId());

        int created = 0;
        for (BookingDetail detail : details) {
            if (!ticketRepository.existsByBookingDetail_BookingDetailId(detail.getBookingDetailId())) {
                Ticket ticket = Ticket.builder()
                        .ticketCode(generateTicketCode())
                        .qrCode(generateQrCode(booking.getBookingCode(), detail.getBookingDetailId()))
                        .status("VALID")
                        .bookingDetail(detail)
                        .build();
                ticketRepository.save(ticket);
                created++;
                log.debug("[BookingStatus] Da tao Ticket {} cho BookingDetail #{}",
                        ticket.getTicketCode(), detail.getBookingDetailId());
            }
        }
        log.info("[BookingStatus] Da tao {} ticket cho booking #{}", created, booking.getBookingId());
    }

    /**
     * Huy cac Ticket lien quan den booking (set status = CANCELLED).
     */
    private void cancelTickets(Booking booking) {
        List<Ticket> tickets = ticketRepository.findByBookingDetail_Booking_BookingId(booking.getBookingId());

        if (!tickets.isEmpty()) {
            tickets.forEach(t -> t.setStatus("CANCELLED"));
            ticketRepository.saveAll(tickets);
            log.info("[BookingStatus] Da huy {} ticket cua booking #{}",
                    tickets.size(), booking.getBookingId());
        }
    }

    // =========================================================================
    // PRIVATE HELPERS — Auto Expire
    // =========================================================================

    /**
     * Kiem tra neu booking PENDING da het han thi tu dong chuyen sang CANCELLED.
     * Nem BadRequestException de thong bao cho caller biet booking da bi huy.
     */
    private void checkAndAutoExpire(Booking booking, String changedBy) {
        if (BookingStatus.PENDING.name().equals(booking.getStatus())
                && booking.getExpiryDate() != null
                && LocalDateTime.now().isAfter(booking.getExpiryDate())) {

            log.info("[BookingStatus] Booking #{} da het han (expiryDate: {}), tu dong huy",
                    booking.getBookingId(), booking.getExpiryDate());

            String oldStatus = booking.getStatus();
            releaseSeats(booking);
            booking.setStatus(BookingStatus.CANCELLED.name());
            bookingRepository.save(booking);
        }
    }

    // =========================================================================
    // PRIVATE HELPERS — Logging & Code Generation
    // =========================================================================

    private BookingStatus parseStatus(String statusStr, Long bookingId) {
        try {
            return BookingStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            log.error("[BookingStatus] Booking #{} co trang thai khong hop le trong DB: '{}'",
                    bookingId, statusStr);
            throw new com.cinema.movie_booking.exception.BadRequestException(
                    "Trang thai hien tai cua booking khong hop le: " + statusStr);
        }
    }

    private String generateTicketCode() {
        return "TK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generatePaymentCode() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generateQrCode(String bookingCode, Long bookingDetailId) {
        return "QR_" + bookingCode + "_" + bookingDetailId;
    }
}
