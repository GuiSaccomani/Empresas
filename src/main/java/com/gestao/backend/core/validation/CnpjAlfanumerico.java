package com.gestao.backend.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CnpjAlfanumericoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CnpjAlfanumerico {
    String message() default "Documento inválido. Deve ser um CPF ou CNPJ (incluindo o novo padrão alfanumérico).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
