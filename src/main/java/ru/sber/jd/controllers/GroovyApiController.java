package ru.sber.jd.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sber.jd.dto.GroovyDto;
import ru.sber.jd.dto.GrrovyScriptResultDto;
import ru.sber.jd.entity.GroovyEntity;
import ru.sber.jd.exception.EmptyScriptException;
import ru.sber.jd.services.GroovyService;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class GroovyApiController {

    private final GroovyService groovyService;

    @PostMapping("/runGroovyJson")
    public ResponseEntity<GrrovyScriptResultDto> runScriptJson(@RequestBody GroovyDto groovyDto) throws EmptyScriptException {
        if((groovyDto.getScriptText().isEmpty()) || (groovyDto.getScriptText() == null)){
            System.out.println(groovyDto);
            throw new EmptyScriptException(groovyDto);
        }
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(groovyService.runGroovy(groovyDto, currentUser));
    }


    @GetMapping("/getAllScriptsJson")
    public List<GroovyDto> findAll(){
        return groovyService.getAll();
    }

    @GetMapping("/getUserScriptsJson")
    public List<GroovyEntity> findAllUser(){
        return groovyService.getUserScripts(SecurityContextHolder.getContext().getAuthentication().getName());
    }



    @ExceptionHandler(EmptyScriptException.class)
    public ResponseEntity<String> fail(EmptyScriptException ex) {
        return ResponseEntity.badRequest().body(ex.getDto());
    }
}
