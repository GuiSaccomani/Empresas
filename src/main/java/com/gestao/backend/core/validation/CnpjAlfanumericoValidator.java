package com.gestao.backend.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjAlfanumericoValidator implements ConstraintValidator<CnpjAlfanumerico, String> {

    // Regex: Aceita com ou sem pontuação. 
    // Primeiros 12 caracteres (letras e números), os últimos 2 são apenas números.
    private static final String CNPJ_ALFANUMERICO_REGEX = "^[A-Za-z0-9]{2}\\.?[A-Za-z0-9]{3}\\.?[A-Za-z0-9]{3}\\/?[A-Za-z0-9]{4}\\-?[0-9]{2}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        // Remove pontuações caso existam para uniformizar
        String limpo = value.replaceAll("[^A-Za-z0-9]", "");

        // Validação genérica para CPF (11 dígitos numéricos)
        if (limpo.length() == 11 && limpo.matches("^[0-9]+$")) {
            return true; // É um CPF numérico
        }

        // Validação de CNPJ (antigo numérico e novo alfanumérico)
        if (limpo.length() == 14) {
            return value.matches(CNPJ_ALFANUMERICO_REGEX) || limpo.matches("^[A-Za-z0-9]{12}[0-9]{2}$");
        }

        return false;
    }
}
