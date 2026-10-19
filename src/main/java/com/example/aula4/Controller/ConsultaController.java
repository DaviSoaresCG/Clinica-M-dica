package com.example.aula4.Controller;

import com.example.aula4.Model.Consulta;
import com.example.aula4.Repository.ConsultaRepository;
import com.example.aula4.Repository.MedicoRepository;
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
@RequestMapping("consultas")
public class ConsultaController {

    private final ConsultaRepository repository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaController(ConsultaRepository repository,
                              MedicoRepository medicoRepository,
                              PacienteRepository pacienteRepository) {
        this.repository = repository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("consulta/list");
        mv.addObject("consultas", repository.consultaList());
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
        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("consulta/form");
            mv.addObject("medicos", medicoRepository.medicoList());
            mv.addObject("pacientes", pacienteRepository.pacienteList());
            return mv;
        }

        repository.save(consulta);
        attributes.addFlashAttribute("mensagem", "Consulta cadastrada com sucesso.");
        return new ModelAndView("redirect:/consultas/list");
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @ModelAttribute("consulta") Consulta consulta,
                               BindingResult result,
                               RedirectAttributes attributes) {
        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("consulta/form");
            mv.addObject("medicos", medicoRepository.medicoList());
            mv.addObject("pacientes", pacienteRepository.pacienteList());
            return mv;
        }

        repository.update(consulta);
        attributes.addFlashAttribute("mensagem", "Consulta atualizada com sucesso.");
        return new ModelAndView("redirect:/consultas/list");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("consulta/form");
        mv.addObject("consulta", repository.consulta(id));
        mv.addObject("medicos", medicoRepository.medicoList());
        mv.addObject("pacientes", pacienteRepository.pacienteList());
        return mv;
    }

    @GetMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id, RedirectAttributes attributes) {
        repository.remove(id);
        attributes.addFlashAttribute("mensagem", "Consulta removida com sucesso.");
        return new ModelAndView("redirect:/consultas/list");
    }
}
