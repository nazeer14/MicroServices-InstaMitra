package com.pack.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "services",uniqueConstraints = @UniqueConstraint(columnNames = "service_name"))
public class ServiceCatalog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name",unique = true)
    private String name;

    private String serviceCode;

    @Column(name = "service_category")
    private String serviceCategory;

    private String about;


    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SubService> subServices;

    private boolean enabled=false;
}
