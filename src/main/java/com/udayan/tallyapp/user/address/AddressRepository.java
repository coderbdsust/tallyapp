package com.udayan.tallyapp.user.address;

import com.udayan.tallyapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    ArrayList<Address> findByUser(User user);
}
