package com.gestao.backend.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "address_cep", length = 9)
    private String cep;

    @Column(name = "address_logradouro", length = 150)
    private String logradouro;

    @Column(name = "address_numero", length = 20)
    private String numero;

    @Column(name = "address_complemento", length = 100)
    private String complemento;

    @Column(name = "address_bairro", length = 100)
    private String bairro;

    @Column(name = "address_localidade", length = 100)
    private String localidade;

    @Column(name = "address_uf", length = 2)
    private String uf;

    // Método a ser invocado pelos @PrePersist e @PreUpdate das entidades hospedeiras
    public void formatToUpperCase() {
        if (this.logradouro != null) this.logradouro = this.logradouro.toUpperCase().trim();
        if (this.complemento != null) this.complemento = this.complemento.toUpperCase().trim();
        if (this.bairro != null) this.bairro = this.bairro.toUpperCase().trim();
        if (this.localidade != null) this.localidade = this.localidade.toUpperCase().trim();
        if (this.uf != null) this.uf = this.uf.toUpperCase().trim();
    }
}
