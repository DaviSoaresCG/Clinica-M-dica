package com.example.aula4.Controller;

import com.example.aula4.Model.Medico;
import com.example.aula4.Repository.MedicoRepository;
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
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "busca", required = false) String busca) {
        ModelAndView mv = new ModelAndView("medico/list");
        List<Medico> medicos;

        if (busca != null && !busca.trim().isEmpty()) {
            String termo = busca.trim();
            if (termo.length() > 50) {
                mv.addObject("mensagemErro", "O termo de pesquisa deve ter no máximo 50 caracteres.");
                medicos = repository.medicoList();
            } else {
                String termoDigitos = termo.replaceAll("\\D", "");
                medicos = repository.medicoList().stream()
                        .filter(m -> (m.getNome() != null && m.getNome().toLowerCase().contains(termo.toLowerCase())) ||
                                (m.getCrm() != null && m.getCrm().toLowerCase().contains(termo.toLowerCase())) ||
                                (!termoDigitos.isEmpty() && m.getCpf() != null && m.getCpf().replaceAll("\\D", "").contains(termoDigitos)) ||
                                (m.getEmail() != null && m.getEmail().toLowerCase().contains(termo.toLowerCase())))
                        .toList();
            }
            mv.addObject("busca", termo);
        } else {
            medicos = repository.medicoList();
        }

        mv.addObject("medicos", medicos);
        return mv;
    }

    @GetMapping("/form")
    public ModelAndView form(@ModelAttribute("medico") Medico medico) {
        ModelAndView mv = new ModelAndView("medico/form");
        mv.addObject("medico", medico);
        return mv;
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute("medico") Medico medico,
                             BindingResult result,
                             RedirectAttributes attributes) {
        // Validação de unicidade de CRM
        if (medico.getCrm() != null && !medico.getCrm().isBlank()) {
            boolean crmJaExiste = repository.medicoList().stream()
                    .anyMatch(m -> m.getCrm() != null &&
                            m.getCrm().equalsIgnoreCase(medico.getCrm().trim()));
            if (crmJaExiste) {
                result.rejectValue("crm", "error.crm", "Já existe um médico cadastrado com este CRM.");
            }
        }

        // Validação de unicidade de CPF
        if (medico.getCpf() != null && !medico.getCpf().isBlank()) {
            String cpfLimpo = medico.getCpf().replaceAll("\\D", "");
            boolean cpfJaExiste = repository.medicoList().stream()
                    .anyMatch(m -> m.getCpf() != null &&
                            m.getCpf().replaceAll("\\D", "").equals(cpfLimpo));
            if (cpfJaExiste) {
                result.rejectValue("cpf", "error.cpf", "Já existe um médico cadastrado com este CPF.");
            }
        }

        if (result.hasErrors()) {
            return new ModelAndView("medico/form");
        }

        repository.save(medico);
        attributes.addFlashAttribute("mensagem", "Médico cadastrado com sucesso!");
        return new ModelAndView("redirect:/medicos/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("medico") Medico medico,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (medico.getId() == null) {
            result.rejectValue("id", "error.id", "Identificador do médico não informado.");
        }

        // Validação de unicidade de CRM na alteração
        if (medico.getCrm() != null && !medico.getCrm().isBlank() && medico.getId() != null) {
            boolean crmJaExiste = repository.medicoList().stream()
                    .anyMatch(m -> m.getId() != null &&
                            !m.getId().equals(medico.getId()) &&
                            m.getCrm() != null &&
                            m.getCrm().equalsIgnoreCase(medico.getCrm().trim()));
            if (crmJaExiste) {
                result.rejectValue("crm", "error.crm", "Já existe outro médico cadastrado com este CRM.");
            }
        }

        // Validação de unicidade de CPF na alteração
        if (medico.getCpf() != null && !medico.getCpf().isBlank() && medico.getId() != null) {
            String cpfLimpo = medico.getCpf().replaceAll("\\D", "");
            boolean cpfJaExiste = repository.medicoList().stream()
                    .anyMatch(m -> m.getId() != null &&
                            !m.getId().equals(medico.getId()) &&
                            m.getCpf() != null &&
                            m.getCpf().replaceAll("\\D", "").equals(cpfLimpo));
            if (cpfJaExiste) {
                result.rejectValue("cpf", "error.cpf", "Já existe outro médico cadastrado com este CPF.");
            }
        }

        if (result.hasErrors()) {
            return new ModelAndView("medico/form");
        }

        repository.update(medico);
        attributes.addFlashAttribute("mensagem", "Médico atualizado com sucesso!");
        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Medico medico = repository.medico(id);
        if (medico == null) {
            attributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return new ModelAndView("redirect:/medicos/list");
        }
        ModelAndView mv = new ModelAndView("medico/form");
        mv.addObject("medico", medico);
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Medico medico = repository.medico(id);
        if (medico == null) {
            attributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return new ModelAndView("redirect:/medicos/list");
        }

        // Validação de integridade: impedir exclusão se possuir consultas
        if (medico.getConsultas() != null && !medico.getConsultas().isEmpty()) {
            attributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o médico " + medico.getNome() +
                            " pois ele possui " + medico.getConsultas().size() + " consulta(s) vinculada(s) no histórico.");
            return new ModelAndView("redirect:/medicos/list");
        }

        try {
            repository.remove(id);
            attributes.addFlashAttribute("mensagem", "Médico excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Não foi possível excluir o médico devido a restrições de integridade.");
        }

        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/historico/{id}")
    public ModelAndView historico(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Medico medico = repository.medico(id);
        if (medico == null) {
            attributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return new ModelAndView("redirect:/medicos/list");
        }
        ModelAndView mv = new ModelAndView("medico/historico");
        mv.addObject("medico", medico);
        mv.addObject("consultas", medico.getConsultas());
        return mv;
    }
}
