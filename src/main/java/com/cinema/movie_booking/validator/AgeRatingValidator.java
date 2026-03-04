package com.cinema.movie_booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator kiểm tra độ tuổi phim
 */
public class AgeRatingValidator implements ConstraintValidator<ValidAgeRating, String> {

    private static final Set<String> VALID_RATINGS = new HashSet<>(
            Arrays.asList("G", "PG", "PG-13", "R", "NC-17"));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Cho phép null - sử dụng @NotBlank riêng nếu cần
        if (value == null || value.isEmpty()) {
            return true;
        }
        return VALID_RATINGS.contains(value);
    }
}
