package com.udayan.tallyapp.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query("SELECT e FROM Employee e JOIN e.organization o WHERE o.id = :organizationId")
    Page<Employee> findAllByOrganizationId(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.organization o WHERE o.id = :organizationId and " +
            "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :searchKey, '%')) OR " +
            "LOWER(e.mobileNo) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<Employee> findAllByOrganizationAndSearchParam(@Param("organizationId") UUID organizationId,
                                                       @Param("searchKey") String searchKey,
                                                       Pageable pageable);
}
