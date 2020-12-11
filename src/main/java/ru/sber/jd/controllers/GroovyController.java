package ru.sber.jd.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sber.jd.dto.GroovyDto;
import ru.sber.jd.exception.EmptyScriptException;
import ru.sber.jd.services.GroovyService;


@Controller
@RequiredArgsConstructor
public class GroovyController {

    private final GroovyService groovyService;
    Logger logger = LoggerFactory.getLogger(GroovyController.class);

    @PostMapping("/runGroovy")
    public String runScript(Model model, GroovyDto groovyDto) throws EmptyScriptException {
        if((groovyDto.getScriptText().isEmpty()) || (groovyDto.getScriptText() == null)){
            logger.error(groovyDto.toString());
            throw new EmptyScriptException(groovyDto);
        }
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("GrrovyScriptResultDto", groovyService.runGroovy(groovyDto, currentUser));
        return "result";
    }

    @GetMapping("/getAllScripts")
    public String findAll(Model model){
        model.addAttribute("GroovyDtos", groovyService.getAll());
        return "report";
    }


    @GetMapping("/getUserScripts")
    public String findAllUser(Model model){
        model.addAttribute("GroovyDtos", groovyService.getUserScripts(SecurityContextHolder.getContext().getAuthentication().getName()));
        return "report";
    }


    @ExceptionHandler(EmptyScriptException.class)
    public ResponseEntity<String> fail(EmptyScriptException ex) {
        return ResponseEntity.badRequest().body(ex.getDto());
    }
}
