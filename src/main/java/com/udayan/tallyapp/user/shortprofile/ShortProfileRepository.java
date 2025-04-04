package com.udayan.tallyapp.user.shortprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShortProfileRepository extends JpaRepository<ShortProfile, UUID> {
}
