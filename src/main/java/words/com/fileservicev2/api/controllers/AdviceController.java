package words.com.fileservicev2.api.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import words.com.fileservicev2.api.responds.ErrorDetailsRespond;
import words.com.fileservicev2.api.responds.ErrorRespond;


import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@ResponseBody
public class AdviceController {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ErrorRespond handleValidationException(BindException ex) {
        BindingResult result = ex.getBindingResult();
        var message = result.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        if (message.isBlank()){
            message = ex.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));
        }

        return new ErrorRespond(
               new ErrorDetailsRespond(
                          message,
                          ex.getClass().getSimpleName()
               )
        );
    }



    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {Exception.class})
    public ErrorRespond handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage(), e);
        return new ErrorRespond(
                new ErrorDetailsRespond(
                        e.getMessage(),
                        e.getClass().getSimpleName()
                )
        );
    }

}
