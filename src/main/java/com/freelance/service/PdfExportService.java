package com.freelance.service;

import com.freelance.model.Project;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Сервис для экспорта данных проектов в PDF формат.
 * <p>
 * Генерирует отчеты с информацией о проектах, рентабельности
 * и предупреждениями о переработках в формате PDF.
 * </p>
 *
 */
public class PdfExportService {

    private final ProjectService projectService;

    /**
     * Конструктор по умолчанию с созданием нового экземпляра ProjectService.
     */
    public PdfExportService() {
        this.projectService = new ProjectService();
    }

    /**
     * Конструктор с инъекцией зависимости ProjectService.
     *
     * @param projectService сервис для работы с проектами
     */
    public PdfExportService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Генерирует PDF отчет по всем проектам.
     * <p>
     * Отчет включает:
     * - Общую рентабельность
     * - Список всех проектов с детальной информацией
     * - Предупреждения о переработках
     * </p>
     *
     * @return массив байт с содержимым PDF документа
     * @throws Exception если произошла ошибка при создании PDF
     */
    public byte[] generateProjectReport() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("ОТЧЕТ ПО ФРИЛАНС-ПРОЕКТАМ")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Дата: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Map<String, Object> profitability = projectService.calculateOverallProfitability();
        document.add(new Paragraph("ОБЩАЯ РЕНТАБЕЛЬНОСТЬ")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20));

        document.add(new Paragraph(String.format("Общий доход: %.2f ₽", profitability.get("totalRevenue"))));
        document.add(new Paragraph(String.format("Всего часов: %.1f ч", profitability.get("totalHours"))));
        document.add(new Paragraph(String.format("Средняя ставка: %.2f ₽/ч", profitability.get("averageHourlyRate"))));
        document.add(new Paragraph(String.format("Количество проектов: %d", profitability.get("projectCount")))
                .setMarginBottom(20));

        List<Project> projects = projectService.getAllProjects();
        if (!projects.isEmpty()) {
            document.add(new Paragraph("СПИСОК ПРОЕКТОВ")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20));

            float[] columnWidths = {4, 3, 2, 2, 2, 2};
            Table table = new Table(columnWidths);
            table.setWidth(550);

            table.addHeaderCell(new Cell().add(new Paragraph("Название").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Клиент").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ставка").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Часы").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Доход").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Статус").setBold()));

            for (Project project : projects) {
                table.addCell(project.getName());
                table.addCell(project.getClient());
                table.addCell(String.format("%.2f ₽/ч", project.getHourlyRate()));
                table.addCell(String.format("%.1f ч", project.getHoursWorked()));
                table.addCell(String.format("%.2f ₽", project.calculateRevenue()));
                
                String status = "";
                switch (project.getStatus()) {
                    case "active": status = "Активен"; break;
                    case "completed": status = "Завершен"; break;
                    case "paused": status = "Приостановлен"; break;
                    default: status = project.getStatus();
                }
                table.addCell(status);
            }

            document.add(table);
        }

        List<String> warnings = projectService.checkForOverwork();
        if (!warnings.isEmpty()) {
            document.add(new Paragraph("ПРЕДУПРЕЖДЕНИЯ О ПЕРЕРАБОТКАХ")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20));

            for (String warning : warnings) {
                document.add(new Paragraph("• " + warning));
            }
        }

        document.close();
        return baos.toByteArray();
    }
}
