package com.udayan.tallykhata.appproperty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppPropertyRepository extends JpaRepository<AppProperty, UUID> {
    String findAppValueByAppKeyAndProfile(String appKey, String profile);
}
