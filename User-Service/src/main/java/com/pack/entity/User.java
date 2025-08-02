package com.pack.entity;

import com.pack.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 13)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isLocked = false;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_deleted")
    private boolean isDeleted=false;

    @Version
    private int version;

    @Column(name = "is_logged",nullable = false)
    private boolean isLogged=false;

    private Role role= Role.USER;

}
