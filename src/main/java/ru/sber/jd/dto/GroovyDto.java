package ru.sber.jd.dto;

import lombok.Data;
import java.util.Date;

@Data
public class GroovyDto {
    private Integer id;
    private String scriptName;
    private String scriptText;
    private String user;
    private Date date;
}
