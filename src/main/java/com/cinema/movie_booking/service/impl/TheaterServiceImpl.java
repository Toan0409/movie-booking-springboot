package com.cinema.movie_booking.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.movie_booking.dto.theater.TheaterRequestDTO;
import com.cinema.movie_booking.dto.theater.TheaterResponseDTO;
import com.cinema.movie_booking.entity.Cinema;
import com.cinema.movie_booking.entity.Seat;
import com.cinema.movie_booking.entity.SeatType;
import com.cinema.movie_booking.entity.Theater;
import com.cinema.movie_booking.mapper.TheaterMapper;
import com.cinema.movie_booking.repository.CinemaRepository;
import com.cinema.movie_booking.repository.SeatRepository;
import com.cinema.movie_booking.repository.SeatTypeRepository;
import com.cinema.movie_booking.repository.TheaterRepository;
import com.cinema.movie_booking.service.TheaterService;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;

    @Override
    @Transactional
    public TheaterResponseDTO create(TheaterRequestDTO request) {
        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new RuntimeException("Cinema not found"));

        Theater theater = TheaterMapper.toEntity(request, cinema);
        theater = theaterRepository.save(theater);

        generateSeats(theater, request.getRowsCount(), request.getSeatsPerRow());

        return TheaterMapper.toDTO(theater);
    }

    // Ví dụ:
    // rowsCount = 3
    // seatsPerRow = 4
    // → hệ thống tạo:
    // A1 A2 A3 A4
    // B1 B2 B3 B4
    // C1 C2 C3 C4
    private void generateSeats(Theater theater, Integer rowsCount, Integer seatsPerRow) {
        if (rowsCount == null || seatsPerRow == null || rowsCount <= 0 || seatsPerRow <= 0) {
            return;
        }

        SeatType defaultSeatType = seatTypeRepository.findByName("STANDARD")
                .orElseGet(() -> {
                    SeatType standardType = SeatType.builder()
                            .name("STANDARD")
                            .description("Standard seat")
                            .priceMultiplier(1.0)
                            .build();
                    return seatTypeRepository.save(standardType);
                });

        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < rowsCount; row++) {
            char rowChar = (char) ('A' + row); // A, B, C...

            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = Seat.builder()
                        .seatRow(String.valueOf(rowChar))
                        .seatNumber(seatNum)
                        .seatCode(rowChar + String.valueOf(seatNum)) // A1, A2, B1...
                        .theater(theater)
                        .seatType(defaultSeatType)
                        .isAvailable(true)
                        .isCoupleSeat(false)
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

    @Override
    public void delete(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater not found"));
        theater.setIsActive(false);
        theaterRepository.save(theater);
    }

    @Override
    public Page<TheaterResponseDTO> getAll(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findAll(pageable);
        return theaters.map(TheaterMapper::toDTO);
    }

    @Override
    public TheaterResponseDTO getById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public TheaterResponseDTO restore(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        theater.setIsActive(true);
        theaterRepository.save(theater);
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public TheaterResponseDTO update(Long id, TheaterRequestDTO request) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        theater.setName(request.getName());
        theater.setRowsCount(request.getRowsCount());
        theater.setTotalSeats(request.getRowsCount() * request.getSeatsPerRow());
        theater.setSeatsPerRow(request.getSeatsPerRow());
        theater.setTheaterType(request.getTheaterType());
        theater = theaterRepository.save(theater);
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public Page<TheaterResponseDTO> getByIsActiveTrue(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findByIsActiveTrue(pageable);
        return theaters.map(TheaterMapper::toDTO);
    }
}
