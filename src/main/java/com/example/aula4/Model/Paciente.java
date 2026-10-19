package com.example.aula4.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@DiscriminatorValue("PACIENTE")
public class Paciente extends PessoaFisica {

    @Size(max = 50, message = "O convênio deve ter no máximo {max} caracteres.")
    private String convenio;

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas;

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }

    // Alias para compatibilidade
    public String getPhone() {
        return getTelefone();
    }

    public void setPhone(String phone) {
        setTelefone(phone);
    }
}
