# Spring-Boot-Excel-Reader-
read excel with fast speed

This library is available from from Maven Central, and you can optionally install it yourself.

<!-- POI for parsing Excel files-->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>4.1.2</version>
    </dependency>

    <!-- POI-ooxml -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>4.1.2</version>
    </dependency>

    <!-- For reading very large Excel file -->
    <dependency>
        <groupId>com.monitorjbl</groupId>
        <artifactId>xlsx-streamer</artifactId>
        <version>2.1.0</version>
    </dependency>
To use it, add this to your POM: Just paste the code with the class name ReadLargeFile.java, and see the magic,

import code.axis.properties.ConfigReader;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

public class ReadLargeFile {
     public static void main(String[] args) {
       try (InputStream inputStream = new FileInputStream(new File("C:/Users/Nischal/Desktop/Qualtiy Assurance of Data Clener/Extra Large Files/update_fileName01-26-2021-6-34-49.XLSX"))) { //FilePath from your device
        Workbook workbook = StreamingReader.builder().rowCacheSize(200).bufferSize(4096).open(inputStream);
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getStringCellValue(cell);
                    System.out.println(cellValue);
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

private static String getStringCellValue(Cell cell) {
    try {
        switch (cell.getCellType()) {
            case FORMULA:
                try {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                } catch (NumberFormatException e) {
                    return cell.getStringCellValue();
                }
            case NUMERIC:
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case STRING:
                String cellValue = cell.getStringCellValue().trim();
                String pattern = "\\^\\$?-?([1-9][0-9]{0,2}(,\\d{3})*(\\.\\d{0,2})?|[1-9]\\d*(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))$|^-?\\$?([1-9]\\d{0,2}(,\\d{3})*(\\.\\d{0,2})?|[1-9]\\d*(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))$|^\\(\\$?([1-9]\\d{0,2}(,\\d{3})*(\\.\\d{0,2})?|[1-9]\\d*(\\.\\d{0,2})?|0(\\.\\d{0,2})?|(\\.\\d{1,2}))\\)$";
                if (((Pattern.compile(pattern)).matcher(cellValue)).find()) {
                    return cellValue.replaceAll("[^\\d.]", "");
                }
                return cellValue.trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case ERROR:
                return null;
            default:
                return cell.getStringCellValue();
        }
    } catch (Exception e) {
        if (e.getLocalizedMessage() != null && ConfigReader.isDisplayWarnLog())
            return "";
    }
    return "";
}

