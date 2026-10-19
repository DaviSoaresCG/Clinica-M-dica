package com.example.aula4.Controller;

import com.example.aula4.Model.Paciente;
import com.example.aula4.Repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Transactional
@Controller
@RequestMapping("pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository repository;

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "busca", required = false) String busca) {
        ModelAndView mv = new ModelAndView("paciente/list");
        List<Paciente> pacientes;

        if (busca != null && !busca.trim().isEmpty()) {
            String termo = busca.trim();
            if (termo.length() > 50) {
                mv.addObject("mensagemErro", "O termo de pesquisa deve ter no máximo 50 caracteres.");
                pacientes = repository.pacienteList();
            } else {
                String termoDigitos = termo.replaceAll("\\D", "");
                pacientes = repository.pacienteList().stream()
                        .filter(p -> (p.getNome() != null && p.getNome().toLowerCase().contains(termo.toLowerCase())) ||
                                (!termoDigitos.isEmpty() && p.getCpf() != null && p.getCpf().replaceAll("\\D", "").contains(termoDigitos)) ||
                                (p.getEmail() != null && p.getEmail().toLowerCase().contains(termo.toLowerCase())) ||
                                (p.getConvenio() != null && p.getConvenio().toLowerCase().contains(termo.toLowerCase())))
                        .toList();
            }
            mv.addObject("busca", termo);
        } else {
            pacientes = repository.pacienteList();
        }

        mv.addObject("pacientes", pacientes);
        return mv;
    }

    @GetMapping("/form")
    public ModelAndView form(@ModelAttribute("paciente") Paciente paciente) {
        ModelAndView mv = new ModelAndView("paciente/form");
        mv.addObject("paciente", paciente);
        return mv;
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute("paciente") Paciente paciente,
                             BindingResult result,
                             RedirectAttributes attributes) {
        // Validação de unicidade de CPF
        if (paciente.getCpf() != null && !paciente.getCpf().isBlank()) {
            String cpfLimpo = paciente.getCpf().replaceAll("\\D", "");
            boolean cpfJaExiste = repository.pacienteList().stream()
                    .anyMatch(p -> p.getCpf() != null &&
                            p.getCpf().replaceAll("\\D", "").equals(cpfLimpo));
            if (cpfJaExiste) {
                result.rejectValue("cpf", "error.cpf", "Já existe um paciente cadastrado com este CPF.");
            }
        }

        if (result.hasErrors()) {
            return new ModelAndView("paciente/form");
        }

        repository.save(paciente);
        attributes.addFlashAttribute("mensagem", "Paciente cadastrado com sucesso!");
        return new ModelAndView("redirect:/pacientes/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("paciente") Paciente paciente,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (paciente.getId() == null) {
            result.rejectValue("id", "error.id", "Identificador do paciente não informado.");
        }

        // Validação de unicidade de CPF na atualização (desconsiderando o próprio paciente)
        if (paciente.getCpf() != null && !paciente.getCpf().isBlank() && paciente.getId() != null) {
            String cpfLimpo = paciente.getCpf().replaceAll("\\D", "");
            boolean cpfJaExiste = repository.pacienteList().stream()
                    .anyMatch(p -> p.getId() != null &&
                            !p.getId().equals(paciente.getId()) &&
                            p.getCpf() != null &&
                            p.getCpf().replaceAll("\\D", "").equals(cpfLimpo));
            if (cpfJaExiste) {
                result.rejectValue("cpf", "error.cpf", "Já existe outro paciente cadastrado com este CPF.");
            }
        }

        if (result.hasErrors()) {
            return new ModelAndView("paciente/form");
        }

        repository.update(paciente);
        attributes.addFlashAttribute("mensagem", "Paciente atualizado com sucesso!");
        return new ModelAndView("redirect:/pacientes/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Paciente paciente = repository.paciente(id);
        if (paciente == null) {
            attributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return new ModelAndView("redirect:/pacientes/list");
        }
        ModelAndView mv = new ModelAndView("paciente/form");
        mv.addObject("paciente", paciente);
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Paciente paciente = repository.paciente(id);
        if (paciente == null) {
            attributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return new ModelAndView("redirect:/pacientes/list");
        }

        // Validação de integridade: impedir exclusão se possuir consultas
        if (paciente.getConsultas() != null && !paciente.getConsultas().isEmpty()) {
            attributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o paciente " + paciente.getNome() +
                            " pois ele possui " + paciente.getConsultas().size() + " consulta(s) vinculada(s) no histórico.");
            return new ModelAndView("redirect:/pacientes/list");
        }

        try {
            repository.remove(id);
            attributes.addFlashAttribute("mensagem", "Paciente excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Não foi possível excluir o paciente devido a restrições de integridade.");
        }

        return new ModelAndView("redirect:/pacientes/list");
    }

    @GetMapping("/historico/{id}")
    public ModelAndView historico(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Paciente paciente = repository.paciente(id);
        if (paciente == null) {
            attributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return new ModelAndView("redirect:/pacientes/list");
        }
        ModelAndView mv = new ModelAndView("paciente/historico");
        mv.addObject("paciente", paciente);
        mv.addObject("consultas", paciente.getConsultas());
        return mv;
    }
}
