package com.udayan.tallyapp.appproperty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppPropertyRepository extends JpaRepository<AppProperty, UUID> {
    String findAppValueByAppKeyAndProfile(String appKey, String profile);

    @Query("SELECT a FROM AppProperty a WHERE " +
            "LOWER(a.appKey) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.profile) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<AppProperty> searchAppProperties(String keyword, Pageable pageable);
}
