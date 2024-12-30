package com.udayan.tallykhata.appproperty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AppPropertyService {

    @Autowired
    private AppPropertyRepository appPropertyRepository;

    public List<AppProperty> getAllProperties(){
        List<AppProperty> propertyList = appPropertyRepository.findAll();
        return propertyList;
    }
}
