import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParseException {

        try {
            GlobalData globalData = new GlobalData("src/grid.json");
            Grid grid = new Grid(globalData);
            grid.getNodeAt(1);
            grid.getElementAt(0);
            grid.getElement(0).print();
            grid.getElement(0).printJacobian();


//          print H matrix ::
//            for (int i = 0; i < 4; i++) {
//                for (int j = 0; j < 4; j++) {
//                    System.out.print(grid.getElement(0).getLocalHMatrix()[i][j] + "\t\t");
//                }
//                System.out.println();
//            }

        } catch (IOException e) {
            System.out.println("File not found!");
        }

//
//        double[][] matrixData2 = { {1d,2d}, {2d,5d}};
//        RealMatrix n = new Array2DRowRealMatrix(matrixData2);
//        new LUDecomposition(n);
//        System.out.println(n.getRow(1));
//        System.out.println(new LUDecomposition(n).getSolver().toString());
    }
}
