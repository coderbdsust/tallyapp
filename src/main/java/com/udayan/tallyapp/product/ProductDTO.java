package com.udayan.tallyapp.product;

import com.udayan.tallyapp.employee.EmployeeDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ProductDTO {

    @Builder
    @Data
    public static class ProductRequest{
        @NotNull(message = "Product name can't be empty")
        private String name;
        @NotNull(message = "Product code can't be empty")
        private String code;
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
        private UUID madeBy;
    }


    @Builder
    @Data
    public static class ProductResponse{
        private UUID id;
        private String name;
        private String code;
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

    @Data
    @Builder
    public static class TotalProductResponse{
        private Long totalProducts;
        private ArrayList<Axis> graph=new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Axis{
        private Long x;
        private String y;
    }
}
