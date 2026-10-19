package com.example.aula4.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("PF")
public class PessoaFisica extends Pessoa {

    @NotBlank(message = "Informe o nome.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre {min} e {max} caracteres.")
    private String nome;

    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$", message = "Informe um CPF válido (11 dígitos ou formato 000.000.000-00).")
    private String cpf;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    // Alias para compatibilidade
    public String getName() {
        return nome;
    }

    public void setName(String name) {
        this.nome = name;
    }
}
