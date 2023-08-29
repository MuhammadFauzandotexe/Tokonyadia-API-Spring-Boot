package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.Role;

public interface RoleService {
    Role getOrSave(String roleNumber);
}
