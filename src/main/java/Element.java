import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Element {
    private static final int NUM_OF_INTEGRATION_POINTS_2_D = GlobalData.NUM_OF_INTEGRATION_POINTS_2D;
    private static final int NUM_OF_SHAPE_FUNCTIONS = GlobalData.NUM_OF_SHAPE_FUNCTIONS;
    private static final int NUM_OF_SIDES_IN_ELEMENT = 4;
    private int[] ID;
    private double[][][] jacobian; // [integration point num][][] - Jacobi matrix for each integration point
    private Node[] nodes;
    boolean[] edgesWithBc;
    private double[] detJ2D;
    private double[][] localHMatrix;
    private double[][] localCMatrix;

    public double[] getLocalPVector() {
        return localPVector;
    }

    private double[] localPVector;

    public double[][] getLocalCMatrix() {
        return localCMatrix;
    }

    public double[][] getLocalHMatrix() {
        return localHMatrix;
    }

    public Node[] getNodes() {
        return nodes;
    }
    public int[] getID() {
        return ID;
    }

    Element(int n1, int n2, int n3, int n4, Node[] nodes, boolean[] edgesWithBc) {
        this.ID = new int[]{n1, n2, n3, n4};

        this.nodes = nodes;
        this.edgesWithBc = edgesWithBc;
        detJ2D = new double[NUM_OF_INTEGRATION_POINTS_2_D];
        jacobian = new double[4][2][2];
        localHMatrix = new double[NUM_OF_SHAPE_FUNCTIONS][NUM_OF_SHAPE_FUNCTIONS];
        localCMatrix = new double[NUM_OF_SHAPE_FUNCTIONS][NUM_OF_SHAPE_FUNCTIONS];
        calculateAll();
    }

    private void calculateAll() {
        calculateJacobians2D();
        RealMatrix HWithoutHbc = calculateLocalHMatrixWithoutHbc();
        calculateHbcMatrixAndPVector(HWithoutHbc);
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

    private RealMatrix calculateLocalHMatrixWithoutHbc() {
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

        double weight1, weight2;
        RealMatrix subLocalHMatrixSum = new Array2DRowRealMatrix(new double[4][4]);
        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            weight1 = UniversalElement.integrationPoints[i].getWeight1();
            weight2 = UniversalElement.integrationPoints[i].getWeight2();

            subLocalHMatrixSum = subLocalHMatrixSum.add(subLocalHMatrixTmp[i].scalarMultiply(weight1 * weight2 * detJ2D[i]));
        }

        //multiplication by k
        return subLocalHMatrixSum.scalarMultiply(GlobalData.k);
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
        RealMatrix subLocalCMatrixSumTmp = new Array2DRowRealMatrix(new double[4][4]);
        for (int i = 0; i < NUM_OF_INTEGRATION_POINTS_2_D; i++) {
            // {N}{N}T
            RealMatrix niVector = new Array2DRowRealMatrix(UniversalElement.NiMatrix[i]);
            RealMatrix niResultMatrix = niVector.multiply(niVector.transpose());

            //{N}{N}T * detJ
            subLocalCMatrixSumTmp = subLocalCMatrixSumTmp.add(niResultMatrix.scalarMultiply(detJ2D[i]));
        }
        // sum of sub-local C matrices multiplied by specific heat and ro
        localCMatrix = subLocalCMatrixSumTmp.scalarMultiply(GlobalData.specificHeat * GlobalData.ro).getData();
    }

    void calculateHbcMatrixAndPVector(RealMatrix localHWithoutHbc) {
        RealMatrix localHbcMatrix = new Array2DRowRealMatrix(new double[4][4]);
        RealMatrix localPVectorTmp = new Array2DRowRealMatrix(new double[4]);

        IntegrationPoint[][] bcpc = new IntegrationPoint[4][2];
        for (int i = 0; i < 4; i++) {
            bcpc[i] = new IntegrationPoint[2];
        }
        bcpc[0][0] = new IntegrationPoint(-1 / sqrt(3), -1);
        bcpc[0][1] = new IntegrationPoint(1 / sqrt(3), -1);

        bcpc[1][0] = new IntegrationPoint(1, -1 / sqrt(3));
        bcpc[1][1] = new IntegrationPoint(1, 1 / sqrt(3));

        bcpc[2][0] = new IntegrationPoint(1 / sqrt(3), 1);
        bcpc[2][1] = new IntegrationPoint(-1 / sqrt(3), 1);

        bcpc[3][0] = new IntegrationPoint(-1, 1 / sqrt(3));
        bcpc[3][1] = new IntegrationPoint(-1, -1 / sqrt(3));

        RealMatrix subHbcMatrix1tmp;
        RealMatrix subHbcMatrix2tmp;

        for (int i = 0; i < NUM_OF_SIDES_IN_ELEMENT; i++) {
            if (edgesWithBc[i]) {
                Node bc1 = nodes[i];
                Node bc2 = nodes[(i + 1) % 4];

                double deltaX = sqrt(pow((bc1.getX() - bc2.getX()), 2) + pow((bc1.getY() - bc2.getY()), 2)) / 2;
                double detJ1D = deltaX / 2;

                // HBC = alfa {N}{N}T * w
                // Hbc1       {N}{N}T * w
                subHbcMatrix1tmp = new Array2DRowRealMatrix(UniversalElement.niValuesVector(bcpc[i][0]));
                RealMatrix subHbcResultMatrix1Tmp = subHbcMatrix1tmp.multiply(subHbcMatrix1tmp.transpose()).scalarMultiply(bcpc[i][0].getWeight1());

                // Hbc2     {N}{N}T * w
                subHbcMatrix2tmp = new Array2DRowRealMatrix(UniversalElement.niValuesVector(bcpc[i][1]));
                RealMatrix subHbcResultMatrix2Tmp = subHbcMatrix2tmp.multiply(subHbcMatrix2tmp.transpose()).scalarMultiply(bcpc[i][1].getWeight2());

                // alfa*(Hbc1 + Hbc2)*detJ1D
                RealMatrix hbcForCurrenSide = subHbcResultMatrix1Tmp.add(subHbcResultMatrix2Tmp);
                hbcForCurrenSide = hbcForCurrenSide.scalarMultiply(GlobalData.alfa * detJ1D);

                // localHbcMatrix - sum of sub-local Hbc matrices
                localHbcMatrix = localHbcMatrix.add(hbcForCurrenSide);


                // P VECTOR
                // Pbc1             {N}* w1
                RealMatrix subPVector1tmp = new Array2DRowRealMatrix(UniversalElement.niValuesVector(bcpc[i][0]));
                subPVector1tmp = subPVector1tmp.scalarMultiply(bcpc[i][0].getWeight1());

                // Pbc2             {N}* w2
                RealMatrix subPVector2tmp = new Array2DRowRealMatrix(UniversalElement.niValuesVector(bcpc[i][1]));
                subPVector2tmp = subPVector2tmp.scalarMultiply(bcpc[i][0].getWeight1());

                // Pbc1 + Pbc2
                RealMatrix pForCurrenSide = subPVector1tmp.add(subPVector2tmp);
                // (Pbc1 + Pbc2) * alfa * Too * detJ1D
                pForCurrenSide = pForCurrenSide.scalarMultiply(GlobalData.alfa * GlobalData.ambientTemperature * detJ1D);
                localPVectorTmp = localPVectorTmp.add(pForCurrenSide);

            }
        }
        localHMatrix = localHWithoutHbc.add(localHbcMatrix).getData();
        localPVector = localPVectorTmp.scalarMultiply(-1).getColumn(0);
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

    @Override
    public String toString() {
        return "Element{" +
                "ID=" + Arrays.toString(ID) +
                ", edgesWithBc=" + Arrays.toString(edgesWithBc) +
                '}';
    }
}
