import java.util.Arrays;

public class Element {
    private int[] ID;
    private double[][] jacobian;
    private Node[] nodes;
    private double[] detJ2D;


    public Element(int n1, int n2, int n3, int n4, Node[] nodes) {
        this.ID = new int[]{n1, n2, n3, n4};

        this.nodes = nodes;
        detJ2D = new double[GlobalData.NUM_OF_INTEGRATION_POINTS_2D];
        jacobian = new double[2][2];
        CalculateJacobians2D();
    }

    private void CalculateJacobians2D() {
        int numOfIntegrationPoints = GlobalData.NUM_OF_INTEGRATION_POINTS_2D;
        int numOfShapeFunctions = GlobalData.NUM_OF_SHAPE_FUNCTIONS;

        for (int i = 0; i < numOfIntegrationPoints; i++) {

            //  dx/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[0][0] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getX();
            }

            //  dx/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[1][0] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getX();
            }

            //  dy/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[0][1] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getY();
            }

            //  dy/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[1][1] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getY();
            }

            // wyznacznik Jakobianu 2D
            detJ2D[i] = jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0];
        }
    }

    @Override
    public String toString() {
        return "Element{" +
                "ID=" + Arrays.toString(ID) +
                '}';
    }
}
