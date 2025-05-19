package com.udayan.tallyapp.product;


import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.common.PageResponse;
import com.udayan.tallyapp.customexp.DuplicateKeyException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.employee.EmployeeRepository;
import com.udayan.tallyapp.organization.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProductMapper productMapper;

    public ProductDTO.ProductResponse createProduct(UUID employeeId, ProductDTO.ProductRequest productRequest) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new InvalidDataException("Couldn't find any employee")
        );
        Organization ownerOrganization = employee.getOrganization().get(0);
        Product product = productMapper.requestToEntity(productRequest);
        product.setMadeBy(employee);
        product.setOwnerOrganization(ownerOrganization);
        try {
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Couldn't save product using same code");
        }
        return productMapper.entityToResponse(product);
    }

    public ProductDTO.ProductResponse editProduct(UUID productId, ProductDTO.ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new InvalidDataException("No product found")
        );

        Employee employee = employeeRepository.findById(productRequest.getMadeBy()).orElseThrow(
                () -> new InvalidDataException("Employee not found")
        );

        Organization ownerOrganization = employee.getOrganization().get(0);

        product.setMadeBy(employee);

        product.setOwnerOrganization(ownerOrganization);

        product = productMapper.mergeRequestToEntity(productRequest, product);
      //  product.setUpdatedDate(LocalDateTime.now());
        try {
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Couldn't save product using same code");
        }
        return productMapper.entityToResponse(product);
    }

    public ApiResponse deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new InvalidDataException("No product found")
        );

        productRepository.delete(product);

        return ApiResponse.builder()
                .sucs(true)
                .message("Product deleted successfully")
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .build();
    }

    public PageResponse<ProductDTO.ProductResponse> getProducts(UUID organizationId, String search, String searchCriteria, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products;

        if (search != null && search.length() > 2) {
            products = switch (searchCriteria) {
                case "name" ->
                        productRepository.searchProductsByOrganizationAndProductName(organizationId, search, pageable);
                case "code" ->
                        productRepository.searchProductsByOrganizationAndProductCode(organizationId, search, pageable);
                case "madeBy" ->
                        productRepository.searchProductsByOrganizationAndEmployeeName(organizationId, search, pageable);
                default -> productRepository.searchProductsByOrganizationAndSearchKey(organizationId, search, pageable);
            };
        } else
            products = productRepository.findProductsByOrganization(organizationId, pageable);

        List<ProductDTO.ProductResponse> productResponseList = products
                .stream()
                .map(p -> productMapper.entityToResponse(p))
                .toList();

        return new PageResponse<>(
                productResponseList,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public ProductDTO.TotalProductResponse totalProducts(UUID organizationId) {

        Long totalProduct = productRepository.totalProductByOrganizationId(organizationId);

        return ProductDTO.TotalProductResponse.builder()
                .totalProducts(totalProduct)
                .graph(new ArrayList<>())
                .build();
    }
}
