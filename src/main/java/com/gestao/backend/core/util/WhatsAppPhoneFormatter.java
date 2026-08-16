package com.gestao.backend.core.util;

public class WhatsAppPhoneFormatter {

    /**
     * Limpa o número de telefone e o formata para o padrão internacional E.164.
     * Necessário para a API do WhatsApp (ex: +5511999998888).
     */
    public static String formatToE164(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }

        // 1. Remove tudo que não for dígito numérico (ex: (11) 99999-8888 -> 11999998888)
        String digitsOnly = phone.replaceAll("\\D", "");

        // 2. Se a pessoa passou com 10 dígitos (Fixo com DDD) ou 11 dígitos (Celular com DDD e o "9")
        // Assumimos que é número brasileiro e injetamos o DDI do Brasil (+55).
        if (digitsOnly.length() == 10 || digitsOnly.length() == 11) {
            return "+55" + digitsOnly;
        }

        // 3. Se a pessoa já passou o 55 mas esqueceu o +, adicionamos.
        if (digitsOnly.length() >= 12 && digitsOnly.startsWith("55")) {
            return "+" + digitsOnly;
        }

        // 4. Retorna com o "+" caso seja DDI de outro país.
        return "+" + digitsOnly;
    }
}
