import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Test
{
    public static void main(String[] args)
    {
        new Test().run();
    }

    private void run()
    {
        CsvParserSettings settings = new CsvParserSettings();
        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");

        // creates a CSV parser
        CsvParser parser = new CsvParser(settings);

        InputStream stream = getClass().getResourceAsStream("/data/vi-shortcuts.csv");
        try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
            // parses all rows in one go.
            List<String[]> allRows = parser.parseAll(reader);
            allRows.forEach(row -> System.out.println("row = " + Arrays.toString(row)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
