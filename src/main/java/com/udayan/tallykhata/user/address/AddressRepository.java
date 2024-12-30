package com.udayan.tallykhata.user.address;

import com.udayan.tallykhata.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    ArrayList<Address> findByUser(User user);
}
