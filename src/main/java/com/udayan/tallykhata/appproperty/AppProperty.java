package com.udayan.tallykhata.appproperty;

import com.udayan.tallykhata.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class AppProperty extends BaseEntity {
    private String appKey;
    private String appValue;
    private String profile;
}
