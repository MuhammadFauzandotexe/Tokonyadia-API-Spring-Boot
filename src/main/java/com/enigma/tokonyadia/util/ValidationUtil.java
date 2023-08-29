package com.enigma.tokonyadia.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public <T> void validate(T obj) {
        Set<ConstraintViolation<Object>> validate = validator.validate(obj);
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }
    }

    public static Map<String, Object> mapConstraintViolationException(Set<ConstraintViolation<?>> constraintViolations) {
        Map<String, Object> result = new HashMap<>();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            result.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        return result;
    }

}
