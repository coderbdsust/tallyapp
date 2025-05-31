package com.udayan.tallyapp.organization;

import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

    public OrganizationDTO.OrganizationResponse entityToResponse(Organization org) {
        return OrganizationDTO.OrganizationResponse.builder()
                .id(org.getId())
                .orgName(org.getOrgName())
                .orgRegNumber(org.getOrgRegNumber())
                .orgVatNumber(org.getOrgVatNumber())
                .orgTinNumber(org.getOrgTinNumber())
                .orgMobileNo(org.getOrgMobileNo())
                .orgEmail(org.getOrgEmail())
                .orgOpenAt(org.getOrgOpenAt())
                .orgOpenInWeek(org.getOrgOpenInWeek())
                .owner(org.getOwner())
                .orgOpeningTitle(org.getOrgOpeningTitle())
                .since(org.getSince())
                .orgAddressLine(org.getOrgAddressLine())
                .orgAddressCity(org.getOrgAddressCity())
                .orgAddressPostcode(org.getOrgAddressPostcode())
                .orgAddressCountry(org.getOrgAddressCountry())
                .image(org.getImage())
                .avatar(org.getAvatar())
                .status(org.getStatus())
                .build();
    }

    public Organization requestToEntity(Organization org, OrganizationDTO.OrganizationRequest orgRequest) {
        org.setId(orgRequest.getId());
        org.setOrgName(orgRequest.getOrgName());
        org.setOrgRegNumber(orgRequest.getOrgRegNumber());
        org.setOrgVatNumber(orgRequest.getOrgVatNumber());
        org.setOrgTinNumber(orgRequest.getOrgTinNumber());
        org.setOrgMobileNo(orgRequest.getOrgMobileNo());
        org.setOrgEmail(orgRequest.getOrgEmail());
        org.setOrgOpenAt(orgRequest.getOrgOpenAt());
        org.setOrgOpenInWeek(orgRequest.getOrgOpenInWeek());
        org.setOwner(orgRequest.getOwner());
        org.setOrgOpeningTitle(orgRequest.getOrgOpeningTitle());
        org.setSince(orgRequest.getSince());
        org.setOrgAddressLine(orgRequest.getOrgAddressLine());
        org.setOrgAddressCity(orgRequest.getOrgAddressCity());
        org.setOrgAddressPostcode(orgRequest.getOrgAddressPostcode());
        org.setOrgAddressCountry(orgRequest.getOrgAddressCountry());
        org.setImage(orgRequest.getImage());
        org.setAvatar(orgRequest.getAvatar());
        org.setStatus(orgRequest.getStatus());
        return org;
    }
}
