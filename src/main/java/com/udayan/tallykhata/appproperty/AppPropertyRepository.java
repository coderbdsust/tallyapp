package com.udayan.tallykhata.appproperty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppPropertyRepository extends JpaRepository<AppProperty, Long> {
    String findAppValueByAppKeyAndProfile(String appKey, String profile);
}
