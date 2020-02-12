import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

public class Element {
    public static final int NUM_OF_INTEGRATION_POINTS_2_D = GlobalData.NUM_OF_INTEGRATION_POINTS_2D;
    public static final int NUM_OF_SHAPE_FUNCTIONS = GlobalData.NUM_OF_SHAPE_FUNCTIONS;
    private int[] ID;
    private double[][][] jacobian; // [integration point num][][] - Jacobi matrix for each integration point
    private Node[] nodes;
    private double[] detJ2D;
    double[][] localHMatrix;


    public Element(int n1, int n2, int n3, int n4, Node[] nodes) {
        this.ID = new int[]{n1, n2, n3, n4};

        this.nodes = nodes;
        detJ2D = new double[NUM_OF_INTEGRATION_POINTS_2_D];
        jacobian = new double[4][2][2];
        localHMatrix = new double[NUM_OF_SHAPE_FUNCTIONS][NUM_OF_SHAPE_FUNCTIONS];
        calculateJacobians2D();
        calculateLocalHMatrix();
    }

    private void calculateJacobians2D() {
        int numOfShapeFunctions = NUM_OF_SHAPE_FUNCTIONS;

        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {

            //  dx/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][0][0] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getX();
            }

            //  dx/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][1][0] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getX();
            }

            //  dy/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][0][1] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getY();
            }

            //  dy/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][1][1] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getY();
            }

            // Jacobian 2D
            detJ2D[i] = jacobian[i][0][0] * jacobian[i][1][1] - jacobian[i][0][1] * jacobian[i][1][0];
        }
    }


    void calculateLocalHMatrix() {
        // k( {dN/dx}{dN/dx}T
        // dN/dx =
        RealMatrix[] subLocalHMatrix = new RealMatrix[4];

        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            double[] dNdX = new double[]{
                    dNdX(i, 0), dNdX(i, 1), dNdX(i, 2), dNdX(i, 3)
            };
            double[] dNdY = new double[]{
                    dNdY(i, 0), dNdY(i, 1), dNdY(i, 2), dNdY(i, 3)
            };
            RealMatrix dNdxVector = new Array2DRowRealMatrix(dNdX);
            RealMatrix dNdxResultMatrix = dNdxVector.multiply(dNdxVector.transpose());

            RealMatrix dNdyVector = new Array2DRowRealMatrix(dNdY);
            RealMatrix dNdyResultMatrix = dNdyVector.multiply(dNdyVector.transpose());

            RealMatrix resultHMatrix = dNdxResultMatrix.add(dNdyResultMatrix);
            subLocalHMatrix[i] = resultHMatrix;

        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(subLocalHMatrix[0].getData()[i][j] + "\t\t");
            }
            System.out.println();
        }
        double w1,w2;
        RealMatrix subLocalHMatrixSum = new Array2DRowRealMatrix();
//        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
//            w1 = UniversalElement.integrationPoints[i].getKsiWeight();
//            w2 = UniversalElement.integrationPoints[i].getEtaWeight();
//
//            for (int j = 0; j < 4; j++) {
//                for (int k = 0; k < 4; k++) {
//                    System.out.print(subLocalHMatrix[i].getData()[j][k] + "\t\t");
//                }
//                System.out.println();
//            }

//            subLocalHMatrixSum.add(subLocalHMatrix[i].scalarMultiply(w1 * w2 * detJ2D[i]));
//        }

//        var sum = new double[4, 4];
//        for (int i = 0; i < UniversalElement.PointsCount; i++) {
//            var pc = UniversalElement.Points[i];
//            var factor = pc.WeightKsi * pc.WeightEta * JacobianDeterminants2D[i] * conductivity;
//            var matrix = MatrixUtils.MultiplyMatrix(HLocalMatrices[i], factor);
//            sum = MatrixUtils.AddMatrices(sum, matrix);
//        }
//        HLocalMatrix = sum;
    }

    private double dNdX(int pointIndex, int shapeFunctionIndex) {
        // dNi/dx = 1/detJ * (dy/dEta * dNi/dKsi - dy/dKsi * dNi/dEta)
        return 1 / detJ2D[pointIndex] *
                jacobian[pointIndex][1][1] *
                UniversalElement.dNdKsiMatrix[pointIndex][shapeFunctionIndex] +
                (-1) * jacobian[pointIndex][0][1] *
                        UniversalElement.dNdEtaMatrix[pointIndex][shapeFunctionIndex];
    }

    private double dNdY(int pointIndex, int shapeFunctionIndex) {
        // dNi/dy = 1/detJ * (- dx/dEta * dNi/dKsi + dx/dKsi * dNi/dEta)
        return 1 / detJ2D[pointIndex] *
                (-1) * jacobian[pointIndex][1][0] *
                UniversalElement.dNdKsiMatrix[pointIndex][shapeFunctionIndex] +
                jacobian[pointIndex][0][0] *
                        UniversalElement.dNdEtaMatrix[pointIndex][shapeFunctionIndex];
    }

    @Override
    public String toString() {
        return "Element{" +
                "ID=" + Arrays.toString(ID) +
                '}';
    }
}
