package com.rh.folhaPagamento.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class RelatorioFolhaService {

    public byte[] gerarPdf(String nomeFuncionario, String login, List<FolhaDePagamento> folhas) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
            Paragraph titulo = new Paragraph("Histórico de Folhas de Pagamento", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
            document.add(new Paragraph("Funcionário: " + (nomeFuncionario != null ? nomeFuncionario : "-"), infoFont));
            document.add(new Paragraph("Login: " + (login != null ? login : "-"), infoFont));
            document.add(Chunk.NEWLINE);

            if (folhas == null || folhas.isEmpty()) {
                document.add(new Paragraph("Nenhuma folha de pagamento encontrada.", infoFont));
                document.close();
                return baos.toByteArray();
            }

            folhas.sort(Comparator
                    .comparing(FolhaDePagamento::getAnoReferencia)
                    .thenComparing(FolhaDePagamento::getMesReferencia));

            PdfPTable tabela = new PdfPTable(6);
            tabela.setWidthPercentage(100f);
            tabela.setWidths(new float[]{2f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f});

            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Color headerBg = new Color(55, 65, 81);

            addHeaderCell(tabela, "Mês/Ano", headerFont, headerBg);
            addHeaderCell(tabela, "Bruto", headerFont, headerBg);
            addHeaderCell(tabela, "Adicionais", headerFont, headerBg);
            addHeaderCell(tabela, "Benefícios", headerFont, headerBg);
            addHeaderCell(tabela, "Descontos", headerFont, headerBg);
            addHeaderCell(tabela, "Líquido", headerFont, headerBg);

            Font cellFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

            for (FolhaDePagamento f : folhas) {
                String ref = String.format("%02d/%d", f.getMesReferencia(), f.getAnoReferencia());
                addCell(tabela, ref, cellFont);
                addCell(tabela, formatBRL(f.getSalarioBruto()), cellFont);
                addCell(tabela, formatBRL(f.getTotalAdicionais()), cellFont);
                addCell(tabela, formatBRL(f.getTotalBeneficios()), cellFont);
                addCell(tabela, formatBRL(f.getTotalDescontos()), cellFont);
                addCell(tabela, formatBRL(f.getSalarioLiquido()), cellFont);
            }

            document.add(tabela);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de folhas de pagamento", e);
        }
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(bg);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static String formatBRL(BigDecimal value) {
        if (value == null) return "R$ 0,00";
        return "R$ " + value.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }
}
