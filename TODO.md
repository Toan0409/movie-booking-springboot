# TODO - Fix Movie Management Bugs

## Danh sách lỗi cần sửa

- [x] Fix 1: `axiosClient.js` — đổi port `8000` → `8080` (đã đúng)
- [x] Fix 2: `movieApi.js` — sửa URL `/admin/movies` → `/movies`
- [x] Fix 3: `MoviesPage.jsx` — `handleSubmit`: chuyển `status` string → boolean flags (`isNowShowing`, `isComingSoon`, `isFeatured`)
- [x] Fix 4: `MoviesPage.jsx` — `openModal` (edit) + table display: đọc `isNowShowing/isComingSoon` thay vì `movie.status`
- [x] Fix 5: `AgeRatingValidator.java` + `ValidAgeRating.java` — thêm chuẩn phân loại Việt Nam (P, K, T13, T16, T18, C)
