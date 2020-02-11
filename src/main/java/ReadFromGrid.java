import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

final class ReadFromGrid {
    static Double t0;  // temperatura początkowa
    static Double simulationTime;  // czas symulacji
    static Double stepTime;    // czas kroku czasowego
    static Double ambientTemperature;  // tempratura otoczenia
    static Double alfa;
    static Double H;
    static Double B;
    static Double N_H;
    static Double N_B;
    static Double cw;
    static Double k;
    static Double ro;

    ReadFromGrid(String path) throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(path));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        t0 = ((Long)jo.get("initial temperature")).doubleValue();
        simulationTime = ((Long)jo.get("simulation time")).doubleValue();
        stepTime = ((Long)jo.get("simulation step time")).doubleValue();
        ambientTemperature = ((Long)jo.get("ambient temperature")).doubleValue();
        alfa = ((Long)jo.get("alfa")).doubleValue();
        H = (Double)jo.get("H");
        B = (Double)jo.get("B");
        N_H = ((Long)jo.get("N_H")).doubleValue();
        N_B = ((Long)jo.get("N_B")).doubleValue();
        cw = ((Long)jo.get("specific heat")).doubleValue(); // ciepło właściwe
        k = ((Long)jo.get("conductivity")).doubleValue(); // przewodność
        ro = ((Long)jo.get("density")).doubleValue(); // gęstość

    }

}
