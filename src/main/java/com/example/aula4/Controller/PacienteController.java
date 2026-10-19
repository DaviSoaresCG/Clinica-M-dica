package com.example.aula4.Controller;

import com.example.aula4.Model.Paciente;
import com.example.aula4.Repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("paciente/list");
        mv.addObject("pacientes", repository.pacienteList());
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
        if (result.hasErrors()) {
            return new ModelAndView("paciente/form");
        }

        repository.save(paciente);
        attributes.addFlashAttribute("mensagem", "Paciente cadastrado com sucesso.");
        return new ModelAndView("redirect:/pacientes/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("paciente") Paciente paciente,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return new ModelAndView("paciente/form");
        }

        repository.update(paciente);
        attributes.addFlashAttribute("mensagem", "Paciente atualizado com sucesso.");
        return new ModelAndView("redirect:/pacientes/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("paciente/form");
        mv.addObject("paciente", repository.paciente(id));
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        repository.remove(id);
        attributes.addFlashAttribute("mensagem", "Paciente removido com sucesso.");
        return new ModelAndView("redirect:/pacientes/list");
    }

    @GetMapping("/historico/{id}")
    public ModelAndView historico(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("paciente/historico");
        Paciente paciente = repository.paciente(id);
        mv.addObject("paciente", paciente);
        mv.addObject("consultas", paciente != null ? paciente.getConsultas() : null);
        return mv;
    }
}
