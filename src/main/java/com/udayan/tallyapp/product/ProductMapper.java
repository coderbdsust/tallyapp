package com.udayan.tallyapp.product;

import com.udayan.tallyapp.employee.EmployeeDTO;
import com.udayan.tallyapp.employee.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {

    @Autowired
    EmployeeMapper employeeMapper;

    public ProductDTO.ProductResponse entityToResponse(Product product){

        EmployeeDTO.EmployeeResponse employeeResponse = employeeMapper.entityToResponse(product.getMadeBy());

        return ProductDTO.ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .employeeCost(product.getEmployeeCost())
                .productionCost(product.getProductionCost())
                .sellingPrice(product.getSellingPrice())
                .soldPrice(product.getSoldPrice())
                .imageUrl(product.getImageUrl())
                .sold(product.getSold())
                .soldDate(product.getSoldDate())
                .madeBy(employeeResponse)
                .createdDate(product.getCreatedDate())
                .build();
    }

    public Product requestToEntity(ProductDTO.ProductRequest prodReq){
        Product product = new Product();
        product.setName(prodReq.getName());
        product.setDescription(prodReq.getDescription());
        product.setEmployeeCost(prodReq.getEmployeeCost());
        product.setProductionCost(prodReq.getProductionCost());
        product.setSellingPrice(prodReq.getSellingPrice());
        product.setSoldPrice(prodReq.getSoldPrice());
        product.setSold(prodReq.getSold());
        product.setSoldDate(prodReq.getSoldDate());
        product.setImageUrl(prodReq.getImageUrl());
        return product;
    }

    public Product mergeRequestToEntity(ProductDTO.ProductRequest prodReq, Product product) {
        product.setName(prodReq.getName());
        product.setDescription(prodReq.getDescription());
        product.setEmployeeCost(prodReq.getEmployeeCost());
        product.setProductionCost(prodReq.getProductionCost());
        product.setSellingPrice(prodReq.getSellingPrice());
        product.setSoldPrice(prodReq.getSoldPrice());
        product.setSold(prodReq.getSold());
        product.setSoldDate(prodReq.getSoldDate());
        product.setImageUrl(prodReq.getImageUrl());
        return product;
    }
}
