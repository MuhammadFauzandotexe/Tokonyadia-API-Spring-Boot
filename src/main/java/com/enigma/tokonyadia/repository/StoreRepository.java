package com.enigma.tokonyadia.repository;

import com.enigma.tokonyadia.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {
    Optional<Store> findFirstBySeller_Id(String sellerId);
    Optional<Store> findFirstBySeller_UserCredential_Id(String userId);
}
