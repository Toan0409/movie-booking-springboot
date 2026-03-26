# Fix: POST /api/admin/users trả về 403 Forbidden (content-length: 0)

## Nguyên nhân
- Request thiếu JWT Bearer Token → Spring Security chặn với 403
- SecurityConfig không restrict `/api/admin/**` chỉ cho ADMIN role
- UserAdminController thiếu `@PreAuthorize("hasRole('ADMIN')")`

## Các bước sửa

- [x] Phân tích lỗi
- [x] Sửa `SecurityConfig.java` — Thêm rule `hasRole("ADMIN")` cho `/api/admin/**`
- [x] Sửa `UserAdminController.java` — Thêm `@PreAuthorize("hasRole('ADMIN')")` ở class level
- [ ] Kiểm tra lại luồng: Login → lấy token → gọi API với Bearer token
