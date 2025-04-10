package com.udayan.tallyapp.product;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product/v1")
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/{organizationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProducts(@PathVariable("organizationId") UUID organizationId,
                                         @RequestParam(name = "search", defaultValue = "", required = false) String search,
                                         @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                         @RequestParam(name = "size", defaultValue = "10", required = false) int size){
        return ResponseEntity.ok(productService.getProducts(organizationId, search, page, size));
    }

    @GetMapping("/{organizationId}/total-products")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> totalProducts(@PathVariable("organizationId") UUID organizationId){
        return ResponseEntity.ok(productService.totalProducts(organizationId));
    }

    @PostMapping("/{employeeId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(@PathVariable("employeeId") UUID employeeId, @Valid @RequestBody ProductDTO.ProductRequest productRequest){
        return new ResponseEntity<>(productService.createProduct(employeeId, productRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editProduct(@PathVariable("productId") UUID productId, @Valid @RequestBody ProductDTO.ProductRequest productRequest){
        return ResponseEntity.ok(productService.editProduct(productId, productRequest));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") UUID productId){
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }
}
