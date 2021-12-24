import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class ThirdLessonTest {

    private final static String iotVegaUrl = "https://iotvega.com/";
    private final static String browserVersion = "93.0.4577.82";
    private final static String buttonList = "//*[@id=\"header\"]/div[2]/div/div[2]/ul/li[2]/a";
    private final static String sheetName = "products";
    private final static List<String> optionsParams = List.of(
            "start-maximized",
            "enable-automation",
            "--no-sandbox",
            "--disable-infobars",
            "--disable-dev-shm-usage",
            "--disable-browser-side-navigation",
            "--disable-gpu"
    );

    /**
     * Шаг 1. Реализовать альтернативный вариант работы № 2 с использованием системы Github Copilot.
     * Рекомендация – написать парсинг сайта с помощью Copilot-технологии и библиотеки Selenium
     * на удобном для себя языке программирования.
     * Шаг 2. Провести модульное тестирование для полученного кода и определить,
     * насколько покрытие кода отличается от работы №2.
     * <p>
     * Парсинг сайта IOT VEGA сервер с выгрузкой всех доступных устройств в excel c помощью Selenium.
     */
    @Test
    public void testIotVegaParser() throws IOException {
        chromedriver().browserVersion(browserVersion).setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(optionsParams);
        WebDriver driver = new ChromeDriver(options);

        driver.get(iotVegaUrl);
        driver.findElement(By.xpath(buttonList)).click();
        List<IotVegaEntity> entities = getEntities(driver);
        createExcelFile(entities);
    }

    private static List<IotVegaEntity> getEntities(WebDriver driver) {
        List<IotVegaEntity> entities = new LinkedList<>();
        List<WebElement> webElements3 = driver.findElements(By.cssSelector("a.main-container"));
        webElements3.forEach(webElement -> {
            String name = webElement.findElement(By.cssSelector("h2")).getText();
            List<String> features = webElement.findElement(By.cssSelector("div.product-features"))
                    .findElement(By.tagName("ul"))
                    .findElements(By.tagName("li"))
                    .stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
            String price = webElement.findElement(By.cssSelector("div.product-cost"))
                    .findElement(By.cssSelector(".price_item")).getText();
            entities.add(new IotVegaEntity(name, price, features));
        });
        return entities;
    }

    private static void createExcelFile(List<IotVegaEntity> entities) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Название");
        headerCell = header.createCell(1);
        headerCell.setCellValue("Цена");
        headerCell = header.createCell(2);
        headerCell.setCellValue("Опции");
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        for (int i = 1; i < entities.size(); i++) {
            IotVegaEntity entity = entities.get(i);
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(entity.getName());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(entity.getPrice());

            cell = row.createCell(2);
            cell.setCellValue(String.join(", ", entity.getFeature()));
        }
        FileOutputStream outputStream = new FileOutputStream("excel/result.xlsx");
        workbook.write(outputStream);
    }

    @AllArgsConstructor
    @Getter
    private static class IotVegaEntity {
        private final String name;
        private final String price;
        private final List<String> feature;
    }
}
