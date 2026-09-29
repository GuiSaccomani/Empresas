package com.gestao.backend.company.entity;

import com.gestao.backend.core.entity.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies", indexes = {@Index(name = "idx_company_slug", columnList = "slug", unique = true)})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 18)
    private String document; // CNPJ ou CPF para registro no BD

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 150, unique = true)
    private String slug;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Embedded
    private Address address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "usa_agenda", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean usaAgenda = true;

    @Column(name = "usa_financeiro", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean usaFinanceiro = true;

    @Column(name = "usa_clientes", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean usaClientes = true;

    @PrePersist
    @PreUpdate
    protected void formatFields() {
        if (this.address != null) {
            this.address.formatToUpperCase();
        }
    }
}
