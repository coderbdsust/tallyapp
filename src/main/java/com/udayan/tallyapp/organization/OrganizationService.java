package com.udayan.tallyapp.organization;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserOrganizationRepository userOrganizationRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public OrganizationDTO.OrganizationRequest createOrganization(OrganizationDTO.OrganizationRequest orgRequest, User currentUser) {
        Organization org;
        if (orgRequest.getId() != null) {
            log.debug("Looking for organization information using id: " + orgRequest.getId());
            org = organizationRepository.findById(orgRequest.getId()).orElseThrow(
                    () -> new InvalidDataException("Invalid organization id for update")
            );
            log.debug("Updating organization information using id: " + orgRequest.getId());
        } else {
            org = new Organization();
        }

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
        Organization saved = organizationRepository.save(org);
        orgRequest.setId(saved.getId());

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(()->new InvalidDataException("Invalid user for organization registry"));

        user.getOrganizations().add(saved);
        userRepository.save(user);

        return orgRequest;
    }

    public List<Organization> getOrganizations(User currentUser) {
        List<Organization> organizations = organizationRepository.findByUser(currentUser);
        return organizations;
    }

    public Organization getOrganization(UUID id, User currentUser) {
        Organization organization = organizationRepository.findById(id).orElseThrow(
                () -> new InvalidDataException("No organization found using this id: " + id)
        );
        return organization;
    }

    @Transactional
    public ApiResponse deleteOrganization(UUID id, User currentUser) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Organization not found"));

        userOrganizationRepository.deleteUserOrganizationById(id);

        organizationRepository.deleteById(id);

        return ApiResponse
                .builder()
                .sucs(true)
                .userDetail(currentUser.getEmail())
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .message("Organization deleted successfully")
                .build();
    }

    public ApiResponse addUsersToOrganization(UUID organizationId, List<UUID> assignUsers, User currentUser) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new InvalidDataException("Organization not found"));

        List<User> users = userRepository.findAllById(assignUsers);
        log.debug("Assignable user counts : {} ", users.size());

        List<UserOrganization> existingEntries = userOrganizationRepository.findAllUserOrganizationById(organizationId);

        List<UUID> existingUserIds = existingEntries.stream()
                .map(userOrg -> userOrg.getId().getUserId())
                .collect(Collectors.toList());

        log.debug("Existing organization users: {}", existingUserIds);

        List<UserOrganization> newEntries = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId())) // Skip existing users
                .map(user -> UserOrganization.builder()
                        .id(new UserOrganizationId(user.getId(), organization.getId()))
                        .user(user)
                        .organization(organization)
                        .build())
                .toList();

        if (newEntries.isEmpty()) {
            throw new InvalidDataException("Minimum one user doesn't found to assign in organization");
        }

        userOrganizationRepository.saveAll(newEntries);

        return ApiResponse.builder()
                .sucs(true)
                .userDetail(currentUser.getEmail())
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .message(newEntries.size() + " user(s) assigned to organization successfully")
                .build();
    }

    public Long getOrganizationTotalEmployee(UUID orgId, User currentUser) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow(
                ()->new InvalidDataException("Invalid organization id")
        );
        return (long) organization.getEmployees().size();
    }

    public OrganizationDTO.OrganizationTopEmployee getOrganizationTopEmployee(UUID orgId, User currentUser) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow(
                ()->new InvalidDataException("Invalid organization id")
        );

        return organization.getEmployees().stream()
                .max(Comparator.comparing(Employee::getBillingRate)) // Find employee with max billing rate
                .map(employee -> new OrganizationDTO.OrganizationTopEmployee(
                        employee.getFullName(),
                        employee.getDateOfBirth(),
                        employee.getMobileNo(),
                        employee.getProfileImage()
                )).orElse(null);
    }
}
