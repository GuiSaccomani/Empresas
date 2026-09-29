package com.gestao.backend.core.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
@SuppressWarnings("null")
public class GlobalExceptionHandler {

    // Erros 404 - Recursos não encontrados
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/not-found"));
        return problemDetail;
    }

    // Erros 400 - Parâmetros incorretos ou regras de negócio quebradas
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Requisição inválida");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/bad-request"));
        return problemDetail;
    }

    // Erros 400 - Falhas de Validação (Anotações nos DTOs como @NotBlank, @CnpjAlfanumerico)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Agrupa todas as mensagens de erro em uma string limpa para o front-end
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/validation-error"));
        return problemDetail;
    }

    
    // Erros 409 - Conflito de recursos duplicados
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/conflict"));
        return problemDetail;
    }

    // Erros 500 - Captura de segurança genérica
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");
        problemDetail.setTitle("Erro Interno");
        problemDetail.setType(URI.create("https://api.nosso-app.com/errors/internal-server-error"));
        return problemDetail;
    }
}
