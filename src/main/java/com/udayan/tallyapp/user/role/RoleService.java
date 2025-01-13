package com.udayan.tallykhata.user.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public ArrayList<String> getAvailableRoles(){
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new));
    }

}
