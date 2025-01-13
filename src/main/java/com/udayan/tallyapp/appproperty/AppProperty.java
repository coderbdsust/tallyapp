package com.udayan.tallyapp.appproperty;

import com.udayan.tallyapp.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"profile", "appKey"}))
@Data
public class AppProperty extends BaseEntity {
    private String appKey;
    private String appValue;
    private String profile;
}
