package com.example.aula4.Controller;

import com.example.aula4.Model.Consulta;
import com.example.aula4.Model.Medico;
import com.example.aula4.Model.Paciente;
import com.example.aula4.Repository.ConsultaRepository;
import com.example.aula4.Repository.MedicoRepository;
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
@RequestMapping("consultas")
public class ConsultaController {

    @Autowired
    private ConsultaRepository repository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "busca", required = false) String busca) {
        ModelAndView mv = new ModelAndView("consulta/list");
        List<Consulta> consultas;

        if (busca != null && !busca.trim().isEmpty()) {
            String termo = busca.trim();
            if (termo.length() > 50) {
                mv.addObject("mensagemErro", "O termo de pesquisa deve ter no máximo 50 caracteres.");
                consultas = repository.consultaList();
            } else {
                consultas = repository.consultaList().stream()
                        .filter(c -> (c.getMedico() != null && c.getMedico().getNome() != null && c.getMedico().getNome().toLowerCase().contains(termo.toLowerCase())) ||
                                (c.getPaciente() != null && c.getPaciente().getNome() != null && c.getPaciente().getNome().toLowerCase().contains(termo.toLowerCase())) ||
                                (c.getObservation() != null && c.getObservation().toLowerCase().contains(termo.toLowerCase())))
                        .toList();
            }
            mv.addObject("busca", termo);
        } else {
            consultas = repository.consultaList();
        }

        mv.addObject("consultas", consultas);
        return mv;
    }

    @GetMapping("/form")
    public ModelAndView form(@ModelAttribute("consulta") Consulta consulta) {
        ModelAndView mv = new ModelAndView("consulta/form");
        mv.addObject("consulta", consulta);
        mv.addObject("medicos", medicoRepository.medicoList());
        mv.addObject("pacientes", pacienteRepository.pacienteList());
        return mv;
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute("consulta") Consulta consulta,
                             BindingResult result,
                             RedirectAttributes attributes) {
        validarIntegridadeConsulta(consulta, result);

        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("consulta/form");
            mv.addObject("medicos", medicoRepository.medicoList());
            mv.addObject("pacientes", pacienteRepository.pacienteList());
            return mv;
        }

        repository.save(consulta);
        attributes.addFlashAttribute("mensagem", "Consulta agendada com sucesso!");
        return new ModelAndView("redirect:/consultas/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("consulta") Consulta consulta,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (consulta.getId() == null) {
            result.rejectValue("id", "error.id", "Identificador da consulta não informado.");
        }

        validarIntegridadeConsulta(consulta, result);

        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("consulta/form");
            mv.addObject("medicos", medicoRepository.medicoList());
            mv.addObject("pacientes", pacienteRepository.pacienteList());
            return mv;
        }

        repository.update(consulta);
        attributes.addFlashAttribute("mensagem", "Consulta atualizada com sucesso!");
        return new ModelAndView("redirect:/consultas/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Consulta consulta = repository.consulta(id);
        if (consulta == null) {
            attributes.addFlashAttribute("mensagemErro", "Consulta não encontrada.");
            return new ModelAndView("redirect:/consultas/list");
        }
        ModelAndView mv = new ModelAndView("consulta/form");
        mv.addObject("consulta", consulta);
        mv.addObject("medicos", medicoRepository.medicoList());
        mv.addObject("pacientes", pacienteRepository.pacienteList());
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Consulta consulta = repository.consulta(id);
        if (consulta == null) {
            attributes.addFlashAttribute("mensagemErro", "Consulta não encontrada.");
            return new ModelAndView("redirect:/consultas/list");
        }

        try {
            repository.remove(id);
            attributes.addFlashAttribute("mensagem", "Consulta cancelada/excluída com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Não foi possível cancelar a consulta.");
        }

        return new ModelAndView("redirect:/consultas/list");
    }

    private void validarIntegridadeConsulta(Consulta consulta, BindingResult result) {
        // Validação e carregamento do Médico
        if (consulta.getMedico() == null || consulta.getMedico().getId() == null) {
            result.rejectValue("medico", "error.medico", "Selecione o médico responsável.");
        } else {
            Medico medicoCarregado = medicoRepository.medico(consulta.getMedico().getId());
            if (medicoCarregado == null) {
                result.rejectValue("medico", "error.medico", "Médico selecionado não foi encontrado no cadastro.");
            } else {
                consulta.setMedico(medicoCarregado);
            }
        }

        // Validação e carregamento do Paciente
        if (consulta.getPaciente() == null || consulta.getPaciente().getId() == null) {
            result.rejectValue("paciente", "error.paciente", "Selecione o paciente.");
        } else {
            Paciente pacienteCarregado = pacienteRepository.paciente(consulta.getPaciente().getId());
            if (pacienteCarregado == null) {
                result.rejectValue("paciente", "error.paciente", "Paciente selecionado não foi encontrado no cadastro.");
            } else {
                consulta.setPaciente(pacienteCarregado);
            }
        }

        // Regra de consistência: mesmo médico não pode ter duas consultas exatamente no mesmo horário
        if (consulta.getDate() != null && consulta.getMedico() != null && consulta.getMedico().getId() != null) {
            boolean conflitoAgenda = repository.consultaList().stream()
                    .anyMatch(c -> c.getMedico() != null &&
                            c.getMedico().getId().equals(consulta.getMedico().getId()) &&
                            c.getDate() != null &&
                            c.getDate().equals(consulta.getDate()) &&
                            (consulta.getId() == null || !c.getId().equals(consulta.getId())));
            if (conflitoAgenda) {
                result.rejectValue("date", "error.date", "O médico já possui uma consulta marcada para esta data e horário.");
            }
        }
    }
}
