# Booking Status Update Feature - TODO

## Steps

- [x] Read and understand existing codebase
- [ ] Create `enums/BookingStatus.java`
- [ ] Create `entity/BookingStatusHistory.java`
- [ ] Create `repository/PaymentRepository.java`
- [ ] Create `repository/TicketRepository.java`
- [ ] Create `repository/BookingStatusHistoryRepository.java`
- [ ] Create `dto/booking/UpdateBookingStatusRequestDTO.java`
- [ ] Create `exception/InvalidStatusTransitionException.java`
- [ ] Create `service/BookingStatusService.java`
- [ ] Create `service/impl/BookingStatusServiceImpl.java`
- [ ] Edit `controller/admin/BookingAdminController.java` — add PATCH endpoint
- [ ] Edit `exception/GlobalExceptionHandler.java` — add handler for InvalidStatusTransitionException
- [ ] Edit `repository/BookingDetailRepository.java` — fix status IN query (CONFIRMED → PAID)
- [ ] Verify build compiles
