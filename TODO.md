# TODO - Fix duplicate seat_code when generating seats

- [x] Update Seat entity unique constraints:
  - [x] Remove global unique on `seat_code`
  - [x] Add composite unique (`theater_id`, `seat_code`) named `UK_seat_theater_code`
  - [x] Keep composite unique (`theater_id`, `seat_row`, `seat_number`) named `UK_seat_theater_row_number`

- [x] Update SeatRepository for generation safety checks:
  - [x] Add `existsByTheater_TheaterId(Long theaterId)` to detect pre-existing seats in a theater

- [x] Refactor TheaterServiceImpl.generateSeats():
  - [x] Ensure method runs inside transaction flow of create() (via @Transactional + flush before generate)
  - [x] Add guard for invalid row/seat values
  - [x] Add idempotency guard: if theater already has seats then skip generation
  - [x] Generate seats deterministically and in-memory deduplicate by seatCode (HashSet<String>)
  - [x] Defensive DB duplicate check per seat via existsByTheater_TheaterIdAndSeatCode
  - [x] Save with `saveAll` only once after validation

- [x] Verify create() flow does not invoke generation more than once

- [x] Summarize root cause and DB design correction
