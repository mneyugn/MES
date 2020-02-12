import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParseException {

        try {
            new ReadFromGrid("src/grid.json");
        } catch (IOException e) {
            System.out.println("File not found!");
        }



        GlobalData gD = new GlobalData(ReadFromGrid.H, ReadFromGrid.W, ReadFromGrid.nH, ReadFromGrid.nW, ReadFromGrid.t0);
        Grid grid = new Grid(gD);
        grid.getNodeAt(16);
        grid.getElementAt(14);

//
//        double[][] matrixData2 = { {1d,2d}, {2d,5d}};
//        RealMatrix n = new Array2DRowRealMatrix(matrixData2);
//        System.out.println(new LUDecomposition(n).getDeterminant());
//        System.out.println(new LUDecomposition(n).getSolver());





    }
}
