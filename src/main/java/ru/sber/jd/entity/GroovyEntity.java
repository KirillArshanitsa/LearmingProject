package ru.sber.jd.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class GroovyEntity {
    @Id
    @Column
    @GenericGenerator(name="generator", strategy="increment")
    @GeneratedValue(generator="generator")
    private Integer id;
    @Column
    private String scriptName;
    @Column
    private String scriptText;
    @Column
    private String user;
    @Column
    private Date date;
}
