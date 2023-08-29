package com.enigma.tokonyadia.repository;

import com.enigma.tokonyadia.constant.ERole;
import com.enigma.tokonyadia.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findFirstByRole(ERole role);
}
