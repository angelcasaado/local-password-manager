package com.localpasswordmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "security_tracker")
public class SecurityTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String ipAddress;

    @Column
    private String username;

    @Column
    private String action;

}
