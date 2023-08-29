package com.enigma.tokonyadia.constant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum ERole {
    ROLE_CUSTOMER,
    ROLE_ADMIN,
    ROLE_SELLER;

    public static ERole get(String value) {
        for (ERole eRole : ERole.values()) {
            if (eRole.name().equalsIgnoreCase(value)) return eRole;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found");
    }

    /**
     * get ERole by index start with 1
     * <ul>
     *     <li>1. ROLE_CUSTOMER</li>
     *     <li>2. ROLE_ADMIN</li>
     *     <li>3. ROLE_SELLER</li>
     * </ul>
     *
     * @param index start with 1
     */
    public static ERole getByIndex(String index) {
        try {
            int idx = Integer.parseInt(index);
            for (ERole value : values()) {
                if (value.ordinal() == (idx - 1)) {
                    return value;
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role tidak ditemukan");
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot parse: index must be number");
        }
    }
}
