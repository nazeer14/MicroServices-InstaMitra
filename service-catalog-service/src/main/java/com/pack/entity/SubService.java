package com.pack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sub_services", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;



    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonBackReference
    private ServiceCatalog service;


    @Column(name = "it_is_available")
    private boolean itIsAvailable;

}

