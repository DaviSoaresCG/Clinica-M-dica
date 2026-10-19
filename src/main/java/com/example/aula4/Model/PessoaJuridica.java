package com.example.aula4.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("PJ")
public class PessoaJuridica extends Pessoa {

    @NotBlank(message = "Informe a razão social.")
    @Size(min = 2, max = 150, message = "A razão social deve ter entre {min} e {max} caracteres.")
    private String razaoSocial;

    @NotBlank(message = "Informe o CNPJ.")
    @Pattern(regexp = "^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$", message = "Informe um CNPJ válido (14 dígitos ou formato 00.000.000/0000-00).")
    private String cnpj;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
