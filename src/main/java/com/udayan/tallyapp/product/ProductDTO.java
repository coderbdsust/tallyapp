package com.udayan.tallyapp.product;

import com.udayan.tallyapp.employee.EmployeeDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductDTO {

    @Builder
    @Data
    public static class ProductRequest{
        @NotNull(message = "Product name can't be empty")
        private String name;
        @NotNull(message = "Description can't be empty")
        private String description;
        @NotNull(message = "Employee cost can't be empty")
        private Double employeeCost;
        @NotNull(message = "Production cost can't be empty")
        private Double productionCost;
        @NotNull(message = "Selling price can't be empty")
        private Double sellingPrice;
        private Double soldPrice;
        private String imageUrl;
        private Boolean sold;
        private LocalDate soldDate;
    }


    @Builder
    @Data
    public static class ProductResponse{
        private UUID id;
        private String name;
        private String description;
        private Double employeeCost;
        private Double productionCost;
        private Double sellingPrice;
        private Double soldPrice;
        private String imageUrl;
        private Boolean sold;
        private LocalDate soldDate;
        private LocalDateTime createdDate;
        private EmployeeDTO.EmployeeResponse madeBy;
    }
}
