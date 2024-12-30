package com.udayan.tallykhata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(name = "created_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedDate;
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid=UUID.randomUUID().toString();

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
