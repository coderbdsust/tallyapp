package com.udayan.tallyapp.product;


import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.common.PageResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.employee.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                ()->new InvalidDataException("Couldn't find any employee")
        );
        Product product = productMapper.requestToEntity(productRequest);
        product.setMadeBy(employee);
        product = productRepository.save(product);
        return productMapper.entityToResponse(product);
    }

    public ProductDTO.ProductResponse editProduct(UUID productId, ProductDTO.ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(
                ()->new InvalidDataException("No product found")
        );
        product = productMapper.mergeRequestToEntity(productRequest, product);
        product.setUpdatedDate(LocalDateTime.now());
        product = productRepository.save(product);
        return productMapper.entityToResponse(product);
    }

    public ApiResponse deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                ()->new InvalidDataException("No product found")
        );

        productRepository.delete(product);

        return ApiResponse.builder()
                .sucs(true)
                .message("Product deleted successfully")
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .build();
    }

    public PageResponse<ProductDTO.ProductResponse> getProducts(UUID organizationId, String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Product> products = productRepository.findProductsByOrganization(organizationId, pageable);

        List<ProductDTO.ProductResponse> productResponseList =  products
                .stream()
                .map(p->productMapper.entityToResponse(p))
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
}
