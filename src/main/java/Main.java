import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParseException {

        try {
            new GlobalData("src/test_case_2.json");
            new Grid();


        } catch (IOException e) {
            System.out.println("File not found!");
        }
    }
}
