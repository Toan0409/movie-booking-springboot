# Booking Seat Logic — Bug Fix TODO

## Bugs Found & Fix Plan

### CRITICAL
- [ ] BUG-1: `cancelBooking()` checks `"CONFIRMED"` (không tồn tại trong enum) → không bảo vệ được booking PAID
- [ ] BUG-2: `cancelBooking()` không gọi `releaseSeats()` / `cancelTickets()` → ghế bị kẹt
- [ ] BUG-3: `occupySeats()` set `isAvailable=false` global (không per-showtime) → ghế bị block toàn bộ suất chiếu

### HIGH
- [ ] BUG-4: `releaseSeats()` set `isAvailable=true` vô điều kiện → re-enable ghế bị admin disable
- [ ] BUG-5: `occupySeats()` chỉ gọi khi payment chưa tồn tại → ghế không được lock khi payment đã có
- [ ] BUG-6: `autoExpireBookings()` load toàn bộ PENDING vào memory + race condition + 1 transaction cho tất cả
- [ ] BUG-7: `findActiveBookingsByShowtimeId()` dùng `'CONFIRMED'` không tồn tại → luôn trả về rỗng

### MEDIUM
- [ ] BUG-8: `checkAndAutoExpire()` không throw exception rõ ràng → caller nhận lỗi gây nhầm lẫn
- [ ] BUG-9: `seatStatusMap.putIfAbsent()` không ưu tiên OCCUPIED > RESERVED → hiển thị sai status
- [ ] BUG-10: `createBooking()` không check `seat.isAvailable` → có thể book ghế bị admin disable

## Files To Edit

- [ ] `BookingRepository.java` — fix query CONFIRMED, thêm `findExpiredPendingBookings()`
- [ ] `BookingServiceImpl.java` — fix `cancelBooking()`, fix `createBooking()`
- [ ] `BookingStatusServiceImpl.java` — fix `occupySeats()`, `releaseSeats()`, `handlePaidTransition()`, `autoExpireBookings()`, `checkAndAutoExpire()`
- [ ] `SeatServiceImpl.java` — fix priority map trong `getSeatAvailability()`

## Progress

- [ ] Step 1: Fix `BookingRepository.java`
- [ ] Step 2: Fix `BookingServiceImpl.java`
- [ ] Step 3: Fix `BookingStatusServiceImpl.java`
- [ ] Step 4: Fix `SeatServiceImpl.java`
- [ ] Step 5: Verify & test
