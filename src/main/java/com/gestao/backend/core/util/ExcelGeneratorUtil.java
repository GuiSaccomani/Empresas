package com.gestao.backend.core.util;

import com.gestao.backend.financial.dto.FinancialTransactionResponseDTO;
import com.gestao.backend.financial.entity.TransactionType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ExcelGeneratorUtil {

    public static ByteArrayInputStream transactionsToExcel(List<FinancialTransactionResponseDTO> transactions) {
        String[] columns = {"Data", "Tipo", "Descrição", "Valor"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Extrato Financeiro");

            // Estilo do Cabeçalho (Fundo azul escuro, texto branco e negrito)
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Cria a Linha do Cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Estilos para Moeda Real (R$)
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            int rowIdx = 1;
            BigDecimal totalBalance = BigDecimal.ZERO;

            // Preenchimento das Linhas de Dados
            for (FinancialTransactionResponseDTO tx : transactions) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(tx.transactionDate().toString());
                
                String typeStr = tx.type() == TransactionType.INCOME ? "RECEITA" : "DESPESA";
                row.createCell(1).setCellValue(typeStr);
                
                row.createCell(2).setCellValue(tx.description());

                Cell amountCell = row.createCell(3);
                double amount = tx.amount().doubleValue();
                
                if (tx.type() == TransactionType.INCOME) {
                    totalBalance = totalBalance.add(tx.amount());
                    amountCell.setCellValue(amount);
                } else {
                    totalBalance = totalBalance.subtract(tx.amount());
                    amountCell.setCellValue(amount * -1); // Mostrar como negativo visualmente
                }
                amountCell.setCellStyle(currencyStyle);
            }

            // Linha Final: Saldo Consolidado
            Row totalRow = sheet.createRow(rowIdx + 1); // Pula uma linha
            Cell totalLabelCell = totalRow.createCell(2);
            totalLabelCell.setCellValue("SALDO CONSOLIDADO:");
            
            // Estilo especial para o saldo final
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
                totalFont.setColor(IndexedColors.RED.getIndex()); // Vermelho se devedor
            } else {
                totalFont.setColor(IndexedColors.GREEN.getIndex()); // Verde se positivo
            }
            totalStyle.setFont(totalFont);
            totalStyle.setDataFormat(format.getFormat("R$ #,##0.00"));
            
            totalLabelCell.setCellStyle(totalStyle);

            Cell totalValueCell = totalRow.createCell(3);
            totalValueCell.setCellValue(totalBalance.doubleValue());
            totalValueCell.setCellStyle(totalStyle);

            // Auto-ajuste da largura das colunas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Falha ao gerar arquivo Excel: " + e.getMessage());
        }
    }
}
