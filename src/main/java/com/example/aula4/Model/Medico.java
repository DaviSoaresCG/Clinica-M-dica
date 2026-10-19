package com.example.aula4.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends PessoaFisica {

    @NotBlank(message = "Informe o CRM.")
    @Pattern(regexp = "^[0-9A-Za-z/-]{4,20}$", message = "Informe um CRM válido (entre 4 e 20 caracteres).")
    private String crm;

    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas;

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
