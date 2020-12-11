package ru.sber.jd.exception;


import ru.sber.jd.dto.GroovyDto;


public class EmptyScriptException extends Exception{
    private GroovyDto dto;

    public String getDto() {
        return "Bad request - " + dto.toString();
    }

    public EmptyScriptException(GroovyDto dto) {
        this.dto = dto;
    }
}
