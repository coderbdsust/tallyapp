package com.udayan.tallyapp.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p " +
            "JOIN p.madeBy e " +
            "JOIN OrganizationEmployee oe ON oe.employeesId.id = e.id " +
            "WHERE oe.organizationId.id = :organizationId")
    Page<Product> findProductsByOrganization(@Param("organizationId") UUID organizationId, Pageable pageable);

}
