package ru.sber.jd.services;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.sber.jd.dto.GroovyDto;
import ru.sber.jd.dto.GrrovyScriptResultDto;
import ru.sber.jd.entity.GroovyEntity;
import ru.sber.jd.repository.GroovyRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static java.util.Collections.singletonMap;


@Service
@RequiredArgsConstructor
public class GroovyService  {

    @PersistenceContext
    private EntityManager em;

    private final GroovyRepository groovyRepository;
    private static final long SCRIPT_TIMEOUT_IN_SECONDS = 15;
    private static final List<String> CLASS_BLACK_LIST = Stream.of(System.class, Thread.class)
            .map(Class::getName)
            .collect(Collectors.toList());



    public GrrovyScriptResultDto runGroovy(GroovyDto groovyDto, String currentUser){
        //TODO add run in other thread
        String script = groovyDto.getScriptText();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompilerConfiguration configuration = createCompilerConfiguration();
        Binding binding = new Binding();
        binding.setProperty("out", new PrintStream(outputStream, true));
        GroovyShell groovyShell = new GroovyShell(binding, configuration);
        try{
            Object result = groovyShell.evaluate(script);
            GrrovyScriptResultDto grrovyScriptResultDto = createResult(result, outputStream.toString());
            save(groovyDto, currentUser);
            return grrovyScriptResultDto;
        }
        catch (Throwable throwable) {
            String message = throwable.getMessage() == null ? throwable.getClass().getName() : throwable.getMessage();
            return createResult(null, message);
        }
    }


    private  GrrovyScriptResultDto createResult(Object result, String output) {
        GrrovyScriptResultDto scriptletResult = new GrrovyScriptResultDto();
        scriptletResult.setResult(result);
        if (StringUtils.hasLength(output)) {
            scriptletResult.setOutput(output);
        }
        return scriptletResult;
    }


    private CompilerConfiguration createCompilerConfiguration() {
        ASTTransformationCustomizer timedCustomizer = new ASTTransformationCustomizer(singletonMap("value", SCRIPT_TIMEOUT_IN_SECONDS), TimedInterrupt.class);
        SecureASTCustomizer secureCustomizer = new SecureASTCustomizer();
        secureCustomizer.setReceiversBlackList(CLASS_BLACK_LIST);
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(secureCustomizer, timedCustomizer);
        return configuration;
    }


    private GroovyDto save(GroovyDto dto, String currentUser)  {
        GroovyEntity groovyEntity = mapToEntity(dto, currentUser);
        GroovyEntity savedEntity = groovyRepository.save(groovyEntity);
        return mapToDto(savedEntity);
    }


    public List<GroovyDto> getAll(){
        return StreamSupport.stream(groovyRepository.findAll().spliterator(), false)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<GroovyEntity> getUserScripts(String user){
        return em.createQuery("SELECT g  FROM GroovyEntity g WHERE user = :param", GroovyEntity.class)
                .setParameter("param", user).getResultList();
    }

    private GroovyEntity mapToEntity(GroovyDto dto, String currentUser){
        GroovyEntity entity = new GroovyEntity();
        entity.setId(dto.getId());
        entity.setScriptName(dto.getScriptName());
        entity.setScriptText(dto.getScriptText());
        entity.setDate(new Date());
        entity.setUser(currentUser);
        return entity;
    }

    private GroovyDto mapToDto(GroovyEntity groovyEntity){
        GroovyDto dto = new GroovyDto();
        dto.setId(groovyEntity.getId());
        dto.setScriptName(groovyEntity.getScriptName());
        dto.setScriptText(groovyEntity.getScriptText());
        dto.setUser(groovyEntity.getUser());
        dto.setDate(groovyEntity.getDate());
        return dto;
    }



}
