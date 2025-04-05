package org.example.homework111;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HelloApplication extends Application {

    private Map<Integer, Map<String, Double>> yearlySales = new HashMap<>();
    private List<SaleRecord> salesRecords = new ArrayList<>();


    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);

        Button loadButton = new Button("Загрузить Excel файл");
        ChoiceBox<Integer> yearChoice = new ChoiceBox<>();
        LineChart<String, Number> lineChart = createEmptyChart();

        root.getChildren().addAll(loadButton, yearChoice, lineChart);

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    readExcelData(file);
                    updateYearChoice(yearChoice);
                    if (!yearChoice.getItems().isEmpty()) {
                        yearChoice.setValue(yearChoice.getItems().get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        yearChoice.setOnAction(e -> {
            Integer selectedYear = yearChoice.getValue();
            if (selectedYear != null) {
                updateChart(lineChart, selectedYear);
            }
        });

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Анализ продаж sll");
        stage.setScene(scene);
        stage.show();
    }

    private void readExcelData(File file) throws IOException {
        salesRecords.clear();
        yearlySales.clear();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Пропускаем заголовок
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                double price = row.getCell(2).getNumericCellValue();
                int quantity = (int) row.getCell(3).getNumericCellValue();
                double totalSale = row.getCell(4).getNumericCellValue();
                Date saleDate = row.getCell(5).getDateCellValue();

                SaleRecord record = new SaleRecord(id, name, price, quantity, totalSale, saleDate);
                salesRecords.add(record);

                Calendar cal = Calendar.getInstance();
                cal.setTime(saleDate);
                int year = cal.get(Calendar.YEAR);
                String month = new SimpleDateFormat("MMMM").format(saleDate);

                yearlySales
                        .computeIfAbsent(year, k -> new LinkedHashMap<>())
                        .merge(month, totalSale, Double::sum);
            }
        }
    }

    private LineChart<String, Number> createEmptyChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Месяц");
        yAxis.setLabel("Прибыль");
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Месячная прибыль");
        return chart;
    }

    private void updateChart(LineChart<String, Number> chart, int year) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Прибыль " + year);

        Map<String, Double> monthlySales = yearlySales.getOrDefault(year, new HashMap<>());
        for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
        chart.setTitle("Месячная прибыль за " + year + " год");
    }

    private void updateYearChoice(ChoiceBox<Integer> yearChoice) {
        yearChoice.getItems().clear();
        yearChoice.getItems().addAll(yearlySales.keySet());
    }

    public static void main(String[] args) {
        launch(args);
    }
}