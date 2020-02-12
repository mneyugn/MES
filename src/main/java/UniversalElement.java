import java.util.Arrays;

import static java.lang.Math.sqrt;

public class UniversalElement {

    double[][] dNdKsiMatrix;
    double[][] dNdEtaMatrix;
    double[][] NiMatrix;
    IntegrationPoint[] integrationPoints;

    public UniversalElement() {
        this.dNdKsiMatrix = new double[4][4];
        this.dNdEtaMatrix = new double[4][4];
        this.NiMatrix = new double[4][4];

        double iPoint1 = -1 / sqrt(3);
        double iPoint2 =  1 / sqrt(3);

        integrationPoints = new IntegrationPoint[]{
                new IntegrationPoint(iPoint1, iPoint1),
                new IntegrationPoint(iPoint2, iPoint1),
                new IntegrationPoint(iPoint2, iPoint2),
                new IntegrationPoint(iPoint1, iPoint2)
        };
        CalculateUniversalElement();
    }

    private void CalculateUniversalElement() {
        for (int i = 0; i < integrationPoints.length; i++) {
            double ksi = integrationPoints[i].getKsi();
            double eta = integrationPoints[i].getEta();

            dNdKsiMatrix[i][0] = -0.25 * (1 - eta);
            dNdKsiMatrix[i][1] = 0.25 * (1 - eta);
            dNdKsiMatrix[i][2] = 0.25 * (1 + eta);
            dNdKsiMatrix[i][3] = -0.25 * (1 + eta);

            dNdEtaMatrix[i][0] = -0.25 * (1 - ksi);
            dNdEtaMatrix[i][1] = -0.25 * (1 + ksi);
            dNdEtaMatrix[i][2] = 0.25 * (1 + ksi);
            dNdEtaMatrix[i][3] = 0.25 * (1 - ksi);

            NiMatrix[i][0] = localN1(integrationPoints[i]);
            NiMatrix[i][1] = localN2(integrationPoints[i]);
            NiMatrix[i][2] = localN3(integrationPoints[i]);
            NiMatrix[i][3] = localN4(integrationPoints[i]);
        }

    }

    private double localN1(IntegrationPoint integrationPoint) {
        return 0.25 * (1 - integrationPoint.getKsi()) * (1 - integrationPoint.getEta());
    }

    private double localN2(IntegrationPoint integrationPoint) {
        return 0.25 * (1 + integrationPoint.getKsi()) * (1 - integrationPoint.getEta());
    }

    private double localN3(IntegrationPoint integrationPoint) {
        return 0.25 * (1 + integrationPoint.getKsi()) * (1 + integrationPoint.getEta());
    }

    private double localN4(IntegrationPoint integrationPoint) {
        return 0.25 * (1 - integrationPoint.getKsi()) * (1 + integrationPoint.getEta());
    }

    @Override
    public String toString() {
        return "UniversalElement{" +
                "dNdKsiMatrix=" + Arrays.toString(dNdKsiMatrix[0]) +
//                ", dNdEtaMatrix=" + dNdEtaMatrix.toString() +
//                ", NiMatrix=" + Arrays.toString(NiMatrix) +
//                ", integrationPoints=" + Arrays.toString(integrationPoints) +
                '}';
    }

    void print() {
        System.out.println("dN po dKsi");
        for (double[] ndKsiMatrix : dNdKsiMatrix) {
            for (double ksiMatrix : ndKsiMatrix) {
                System.out.print(ksiMatrix + "\t \t");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("dN po dKsi");
        for (double[] ndEtaMatrix : dNdEtaMatrix) {
            for (double etaMatrix : ndEtaMatrix) {
                System.out.print(etaMatrix + "\t \t");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Ni matrix");
        for (double[] niMatrix : NiMatrix) {
            for (double nimatrix : niMatrix) {
                System.out.print(nimatrix + "\t \t");
            }
            System.out.println();
        }


    }
}
