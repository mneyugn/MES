import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

public class Element {
    private static final int NUM_OF_INTEGRATION_POINTS_2_D = GlobalData.NUM_OF_INTEGRATION_POINTS_2D;
    private static final int NUM_OF_SHAPE_FUNCTIONS = GlobalData.NUM_OF_SHAPE_FUNCTIONS;
    private int[] ID;
    private double[][][] jacobian; // [integration point num][][] - Jacobi matrix for each integration point
    private Node[] nodes;
    private double[] detJ2D;
    private double[][] localHMatrix;

    public double[][] getLocalHMatrix() {
        return localHMatrix;
    }



    Element(int n1, int n2, int n3, int n4, Node[] nodes) {
        this.ID = new int[]{n1, n2, n3, n4};

        this.nodes = nodes;
        detJ2D = new double[NUM_OF_INTEGRATION_POINTS_2_D];
        jacobian = new double[4][2][2];
        localHMatrix = new double[NUM_OF_SHAPE_FUNCTIONS][NUM_OF_SHAPE_FUNCTIONS];
        calculateJacobians2D();
        calculateLocalHMatrix();
        calculateLocalCMatrix();
    }

    private void calculateJacobians2D() {
        int numOfShapeFunctions = NUM_OF_SHAPE_FUNCTIONS;

        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {

            //  dx/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][0][0] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getX();
            }

            //  dy/dksi
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][0][1] += UniversalElement.dNdKsiMatrix[i][j] * nodes[j].getY();
            }

            //  dx/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][1][0] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getX();
            }


            //  dy/deta
            for (int j = 0; j < numOfShapeFunctions; j++) {
                jacobian[i][1][1] += UniversalElement.dNdEtaMatrix[i][j] * nodes[j].getY();
            }

            // Jacobian 2D
            detJ2D[i] = jacobian[i][0][0] * jacobian[i][1][1] - jacobian[i][0][1] * jacobian[i][1][0];
        }
    }

    void print() {
        for (double v : detJ2D) {
            System.out.println(v + "\t\t");
        }
    }

    void printJacobian() {
        for (double[][] doubles : jacobian) {
            for (double[] aDouble : doubles) {
                for (double v : aDouble) {
                    System.out.print(v + "\t\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    void calculateLocalHMatrix() {
        // k( {dN/dx}{dN/dx}T
        // dN/dx =
        RealMatrix[] subLocalHMatrixTmp = new RealMatrix[4];

        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            double[] dNdX = new double[]{
                    dNdX(i, 0), dNdX(i, 1), dNdX(i, 2), dNdX(i, 3)
            };
            double[] dNdY = new double[]{
                    dNdY(i, 0), dNdY(i, 1), dNdY(i, 2), dNdY(i, 3)
            };
            // {dN/dx}{dN/dx}T
            RealMatrix dNdxVector = new Array2DRowRealMatrix(dNdX);
            RealMatrix dNdxResultMatrix = dNdxVector.multiply(dNdxVector.transpose());

            // {dN/dy}{dN/dy}T
            RealMatrix dNdyVector = new Array2DRowRealMatrix(dNdY);
            RealMatrix dNdyResultMatrix = dNdyVector.multiply(dNdyVector.transpose());

            // {dN/dx}{dN/dx}T + {dN/dy}{dN/dy}T
            RealMatrix resultHMatrix = dNdxResultMatrix.add(dNdyResultMatrix);
            subLocalHMatrixTmp[i] = resultHMatrix;
        }

        double w1, w2;
        RealMatrix subLocalHMatrixSum = new Array2DRowRealMatrix(new double[4][4]);
        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            w1 = UniversalElement.integrationPoints[i].getKsiWeight();
            w2 = UniversalElement.integrationPoints[i].getEtaWeight();

            subLocalHMatrixSum = subLocalHMatrixSum.add(subLocalHMatrixTmp[i].scalarMultiply(w1 * w2 * detJ2D[i]));
        }

        //multiplication by k
        localHMatrix = subLocalHMatrixSum.scalarMultiply(GlobalData.k).getData();
    }

    private double dNdX(int pointIndex, int shapeFunctionIndex) {
        // dNi/dx = 1/detJ * (dy/dEta * dNi/dKsi - dy/dKsi * dNi/dEta)
        return 1 / detJ2D[pointIndex] *
                (jacobian[pointIndex][1][1] *
                        UniversalElement.dNdKsiMatrix[pointIndex][shapeFunctionIndex] +
                        (-1) * jacobian[pointIndex][0][1] *
                                UniversalElement.dNdEtaMatrix[pointIndex][shapeFunctionIndex]);
    }

    private double dNdY(int pointIndex, int shapeFunctionIndex) {
        // dNi/dy = 1/detJ * (- dx/dEta * dNi/dKsi + dx/dKsi * dNi/dEta)
        return 1 / detJ2D[pointIndex] *
                ((-1) * jacobian[pointIndex][1][0] *
                        UniversalElement.dNdKsiMatrix[pointIndex][shapeFunctionIndex] +
                        jacobian[pointIndex][0][0] *
                                UniversalElement.dNdEtaMatrix[pointIndex][shapeFunctionIndex]);
    }

    private void calculateLocalCMatrix() {
        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            RealMatrix niVector = new Array2DRowRealMatrix(UniversalElement.NiMatrix[i]);
            RealMatrix niResultMatrix = niVector.multiply(niVector.transpose());





        }


    }
    @Override
    public String toString() {
        return "Element{" +
                "ID=" + Arrays.toString(ID) +
                '}';
    }

}
