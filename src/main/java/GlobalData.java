import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class GlobalData {

    static Double t0;  // temperatura początkowa
    static Double simulationTime;  // czas symulacji
    static Double stepTime;    // czas kroku czasowego
    static Double ambientTemperature;  // tempratura otoczenia
    static Double alfa;
    static Double H;// wysokość siatki
    static Double W;// szerokość siatki
    static Integer nH;// ilość węzłów - wysokość
    static Integer nW;// ilość węzłów - szerokość
    static Double specificHeat;
    public static Double k; //
    static Double ro;

    static int nN; // ilość węzłów
    static int nE; // ilość elementów
    double dH;
    double dW;
    static final int NUM_OF_INTEGRATION_POINTS_2D = 4;
    static final int NUM_OF_SHAPE_FUNCTIONS = 4;

    public GlobalData(String path) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(path));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        t0 = ((Long)jo.get("initial temperature")).doubleValue();
        simulationTime = ((Long)jo.get("simulation time")).doubleValue();
        stepTime = ((Long)jo.get("simulation step time")).doubleValue();
        ambientTemperature = ((Long)jo.get("ambient temperature")).doubleValue();
        alfa = ((Long)jo.get("alfa")).doubleValue();
        H = (Double)jo.get("H");
        W = (Double)jo.get("W");
        nH = ((Long)jo.get("nH")).intValue();
        nW = ((Long)jo.get("nW")).intValue();
        specificHeat = ((Long)jo.get("specific heat")).doubleValue(); // ciepło właściwe
        k = ((Long)jo.get("conductivity")).doubleValue(); // przewodność
        ro = ((Long)jo.get("density")).doubleValue(); // gęstość

        this.nN = nH * nW;
        this.nE = (nH - 1) * (nW - 1);
        dH = H / (nH - 1);
        dW = W / (nW - 1);

    }


}

