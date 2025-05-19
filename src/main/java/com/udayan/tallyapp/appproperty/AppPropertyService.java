package com.udayan.tallyapp.appproperty;

import com.udayan.tallyapp.common.PageResponse;
import com.udayan.tallyapp.customexp.DuplicateKeyException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppPropertyService {

    @Autowired
    private AppPropertyRepository appPropertyRepository;

    public PageResponse<AppProperty> getAllProperties(int page, int size, String search) {
        log.debug("page {}, size {}, search {} ", page, size, search);
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("createdDate"),
                Sort.Order.asc("profile"),
                Sort.Order.asc("appKey")
        ));

        Page<AppProperty> propertyList;

        if (search.length() > 3) {
            propertyList = appPropertyRepository.searchAppProperties(search, pageable);
        } else {
            propertyList = appPropertyRepository.findAll(pageable);
        }

        return new PageResponse<>(
                propertyList.getContent(),
                propertyList.getNumber(),
                propertyList.getSize(),
                propertyList.getTotalElements(),
                propertyList.getTotalPages(),
                propertyList.isFirst(),
                propertyList.isLast()
        );
    }

    public AppProperty editAppProperty(AppPropertyDTO.AppPropertyEditRequest request) {
        AppProperty appProperty = appPropertyRepository.findById(request.getId()).orElseThrow(
                () -> new InvalidDataException("Invalid app property id")
        );
        appProperty.setAppValue(request.getAppValue());
        appProperty.setAppKey(request.getAppKey());
        appProperty.setProfile(request.getProfile());
       // appProperty.setUpdatedDate(LocalDateTime.now());
        return appPropertyRepository.save(appProperty);
    }

    public AppProperty createAppProperty(AppPropertyDTO.AppPropertyCreateRequest request) {
        AppProperty appProperty = new AppProperty();
        appProperty.setAppValue(request.getAppValue());
        appProperty.setAppKey(request.getAppKey());
        appProperty.setProfile(request.getProfile());
       // appProperty.setUpdatedDate(LocalDateTime.now());

        try {
            return appPropertyRepository.save(appProperty);
        }catch(DataIntegrityViolationException ex){
            log.error("",ex);
            throw new DuplicateKeyException("Couldn't save information, app key exist using same profile");
        }
    }
}
