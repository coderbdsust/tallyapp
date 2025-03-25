package com.udayan.tallyapp.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationEmployeeRepository extends JpaRepository<OrganizationEmployee, OrganizationEmployeeId> {

    @Modifying
    @Query("DELETE FROM OrganizationEmployee u WHERE u.employeesId = :employeeId")
    void deleteEmployeeByEmployeeId(Employee employeeId);
}
