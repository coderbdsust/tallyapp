package com.udayan.tallyapp.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UserOrganizationId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserOrganization uo WHERE uo.id.organizationsId = :organizationId")
    void deleteUserOrganizationById(@Param("organizationId") UUID organizationId);

    @Query("SELECT uo FROM UserOrganization uo WHERE uo.id.organizationsId = :organizationId")
    List<UserOrganization> findAllUserOrganizationById(@Param("organizationId") UUID organizationId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserOrganization uo WHERE uo.id.userId = :userId AND uo.id.organizationsId = :organizationId")
    void deleteByUserIdAndOrganizationId(@Param("userId") UUID userId, @Param("organizationId") UUID organizationId);


}
