package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import guru.qa.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTest {
    ClassLoader classLoader = FilesTest.class.getClassLoader();
    String zipFileName = "archive.zip";
    String zipPath = "src/test/resources/";
    String xlsxFileName = "Template_Check-list.xlsx";
    String pdfFileName = "SQL.pdf";
    String csvFileName = "username.csv";

    @DisplayName("Проверка CSV  в zip-архиве")
    @Test
    void testCsvInZip() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipFileName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zipfile = new ZipFile(new File(zipPath + zipFileName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if(entry.getName().equals(csvFileName)) {
                try (InputStream stream = zipfile.getInputStream(entry);
                     CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    List<String[]> csv = reader.readAll();
                    assertThat(csv).contains(
                            new String[]{"Username","Identifier","First name","Last name"},
                            new String[]{"booker12","9012","Rachel","Booker"}
                    );
                }
            }
        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @DisplayName("Проверка PDF  в zip-архиве")
    @Test
    void testPdfInZip() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipFileName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zipfile = new ZipFile(new File(zipPath + zipFileName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if(entry.getName().equals(pdfFileName)){
                try (InputStream stream = zipfile.getInputStream(entry)) {
                    PDF pdf = new PDF(stream);
                    assertThat(pdf.text).contains("Common Commands");
                }
            }

        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @DisplayName("Проверка XLSX  в zip-архиве")
    @Test
    void testXlsxInZip() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipFileName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zipfile = new ZipFile(new File(zipPath + zipFileName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if(entry.getName().equals(xlsxFileName)){
                try (InputStream stream = zipfile.getInputStream(entry)) {
                    XLS xls = new XLS(stream);
                    assertThat(
                            xls.excel.getSheetAt(0)
                                    .getRow(1)
                                    .getCell(4)
                                    .getStringCellValue()
                    ).contains("Passed");
                }
            }

        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @DisplayName("Парсинг json файла")
    @Test
    void testJsonFileEmployee() throws IOException {
        InputStream is = classLoader.getResourceAsStream("employee.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Employee employee = objectMapper.readValue(is, Employee.class);
        assertThat(employee.getRole()).isEqualTo("Developer");
    }
}
