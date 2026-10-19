package com.example.aula4.Controller;

import com.example.aula4.Model.Medico;
import com.example.aula4.Repository.MedicoRepository;
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
@RequestMapping("medicos")
public class MedicoController {

    private final MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("medico/list");
        mv.addObject("medicos", repository.medicoList());
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
        if (result.hasErrors()) {
            return new ModelAndView("medico/form");
        }

        repository.save(medico);
        attributes.addFlashAttribute("mensagem", "Médico cadastrado com sucesso.");
        return new ModelAndView("redirect:/medicos/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("medico") Medico medico,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return new ModelAndView("medico/form");
        }

        repository.update(medico);
        attributes.addFlashAttribute("mensagem", "Médico atualizado com sucesso.");
        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("medico/form");
        mv.addObject("medico", repository.medico(id));
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        repository.remove(id);
        attributes.addFlashAttribute("mensagem", "Médico removido com sucesso.");
        return new ModelAndView("redirect:/medicos/list");
    }

    @GetMapping("/historico/{id}")
    public ModelAndView historico(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("medico/historico");
        Medico medico = repository.medico(id);
        mv.addObject("medico", medico);
        mv.addObject("consultas", medico != null ? medico.getConsultas() : null);
        return mv;
    }
}
