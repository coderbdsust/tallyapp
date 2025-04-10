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
            "WHERE p.ownerOrganization.id = :organizationId")
    Page<Product> findProductsByOrganization(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN p.madeBy e " +
            "WHERE p.ownerOrganization.id = :organizationId " +
            "AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ")")
    Page<Product> searchProductsByOrganizationAndSearchKey(@Param("organizationId") UUID organizationId, @Param("search") String search, Pageable pageable);

    @Query("SELECT count(p) FROM Product p " +
            "WHERE p.ownerOrganization.id = :organizationId")
    Long totalProductByOrganizationId(@Param("organizationId") UUID organizationId);


}
