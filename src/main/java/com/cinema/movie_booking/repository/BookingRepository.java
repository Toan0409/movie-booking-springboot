package com.cinema.movie_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cinema.movie_booking.entity.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings by showtime ID
     */
    List<Booking> findByShowtime_ShowtimeId(Long showtimeId);

    /**
     * Find booking by booking code
     */
    Optional<Booking> findByBookingCode(String bookingCode);

    /**
     * Check if booking exists for a showtime
     */
    boolean existsByShowtime_ShowtimeId(Long showtimeId);

    /**
     * Find all bookings by user ID
     */
    List<Booking> findByUser_UserId(Long userId);

    /**
     * Find all bookings by status
     */
    List<Booking> findByStatus(String status);

    /**
     * Find active bookings for a showtime (CONFIRMED status)
     */
    @Query("SELECT b FROM Booking b WHERE b.showtime.showtimeId = :showtimeId AND b.status = 'CONFIRMED'")
    List<Booking> findActiveBookingsByShowtimeId(@Param("showtimeId") Long showtimeId);
}
