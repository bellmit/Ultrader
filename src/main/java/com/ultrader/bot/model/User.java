package com.ultrader.bot.model;


import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

@EntityScan
@Entity
@Table(name = "users")
@Data
public class User {
    @Id

    @Column(name="ID", unique=true, updatable=false, nullable=false)
    @GeneratedValue
    private long id;

    @Column(name="username", unique=true, updatable=false, nullable=false)
    private String username;

    @Column(name="password_hash", unique=true, updatable=false, nullable=false)
    private String passwordHash;

    @Column(name="roleId", unique=true, updatable=false, nullable=false)
    private String roleId;
}
