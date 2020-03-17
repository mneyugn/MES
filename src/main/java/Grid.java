import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Grid {
    private Node[] nodes;
    private Element[] elements;

    private double H;
    private double W;

    private int nH;
    private int nW;

    private double dH;
    private double dW;

    private double initialTemp;

    private double[][] globalH;
    private double[][] globalC;
    private double[] globalP;

    Grid() {
        this.nodes = new Node[GlobalData.nN];
        this.elements = new Element[GlobalData.nE];
        this.dH = GlobalData.dH;
        this.dW = GlobalData.dW;
        this.H = GlobalData.H;
        this.W = GlobalData.W;
        this.nH = GlobalData.nH;
        this.nW = GlobalData.nW;
        this.initialTemp = GlobalData.initialTemp;
        new UniversalElement();
        this.globalH = new double[GlobalData.nN][GlobalData.nN];
        this.globalC = new double[GlobalData.nN][GlobalData.nN];
        this.globalP = new double[GlobalData.nN];
        createGrid();
        startSimulation();
    }


    private void createGrid() {
        completeNodes();
        completeElements();
        agregate();
    }


    private void completeNodes() {
        int index = 0;
        boolean isBc;

        for (double x = 0; x <= W; x += dW)
            for (double y = 0; y <= H; y += dH) {
                isBc = isBoundary(x, y);
                nodes[index] = new Node(x, y, initialTemp, isBc);
                index++;
            }
    }


    private void completeElements() {
        int index = 0;
        boolean[] edgesWithBc;

        int noElementsW = nW - 1;
        int noElementsH = nH - 1;
        Node[] nodesOfElement;
        for (int i = 0; i < noElementsW; i++) {
            for (int j = 0; j < noElementsH; j++) {
                int n1 = i * nH + j;            // bottom-left
                int n2 = (i + 1) * nH + j;      // bottom-right
                int n3 = (i + 1) * nH + j + 1;  // top-right
                int n4 = i * nH + j + 1;        // top-right

                nodesOfElement = new Node[]{nodes[n1], nodes[n2], nodes[n3], nodes[n4]};
                edgesWithBc = new boolean[]{nodes[n1].isBC() && nodes[n2].isBC(), //bottom
                        nodes[n2].isBC() && nodes[n3].isBC(), //right
                        nodes[n3].isBC() && nodes[n4].isBC(), //top
                        nodes[n4].isBC() && nodes[n1].isBC()};//left
                elements[index++] = new Element(n1, n2, n3, n4, nodesOfElement, edgesWithBc);
            }
        }
    }

    private void agregate() {
        //petla for po wszystkich węzłach siatki
        for (int i = 0; i < GlobalData.nE; i++) {
            //pobieram id wszystkich wezłów siatki
            int[] indexTab = elements[i].getID();
            Element currentElement = elements[i];
            //dodaje poszczegolne elementy do macierzy globalnej H wykorzystując id węzłów
            for (int y = 0; y < 4; y++) {
                //petla for po kolumnach macierzy H danego elementu
                globalP[indexTab[y]] += elements[i].getLocalPVector()[y];
                for (int x = 0; x < 4; x++) {
                    //dodajemy element do H zgodnie z id węzłów
                    int index_row = indexTab[y];
                    int index_column = indexTab[x];
                    globalH[index_row][index_column] += currentElement.getLocalHMatrix()[y][x] + currentElement.getLocalHbcMatrix()[y][x];
                    globalC[index_row][index_column] += currentElement.getLocalCMatrix()[y][x];
                }
            }
        }
    }

    void startSimulation() {

        double[] t0Vector = new double[nodes.length];

        double dTau = GlobalData.stepTime;
        int iterationCount = new Double(GlobalData.simulationTime / dTau).intValue();
        for (int i = 0; i < iterationCount; i++) {
            for (int j = 0; j < t0Vector.length; j++) {
                t0Vector[j] = nodes[j].getT();
            }
            //[H] = [H] + [C]/dTau
            RealMatrix globalHtmp = new Array2DRowRealMatrix(globalH);
            RealMatrix globalCtmp = new Array2DRowRealMatrix(globalC);
            RealMatrix globalMatrix = globalHtmp.add(globalCtmp.scalarMultiply(1 / dTau));

            //{p} = {p} + {[C]/dTau} * {t_0}
            RealMatrix t0VectorTmp = new Array2DRowRealMatrix(t0Vector);
            RealMatrix vectorPTmp = new Array2DRowRealMatrix(globalP);
            RealMatrix globalVector = vectorPTmp.add(globalCtmp.scalarMultiply(1 / dTau).multiply(t0VectorTmp));

            // {t1} = [H]-1  *  {t0}
            double[] t1Vector = MatrixUtils.inverse(globalMatrix).multiply(globalVector).getColumn(0);

            double minTemp = t1Vector[0];
            double maxTemp = t1Vector[0];

            for (double v : t1Vector) {
                if (v < minTemp) minTemp = v;
                if (v > maxTemp) maxTemp = v;
            }

            System.out.println("Iteration:\t" + (i+1) +"\tMin:\t" + minTemp + "\t\tMax: \t" + maxTemp);

            for (int j = 0; j < t0Vector.length; j++) {
                nodes[j].setT(t1Vector[j]);
            }
        }
    }


    private boolean isBoundary(double x, double y) {
        return (x == 0 || y == 0 || x == W || y == H);
    }


    void getNodeAt(int n) {
        System.out.println(nodes[n].toString());
    }

    void getElementAt(int n) {
        System.out.println(elements[n]);
    }

    public double getXOfNodeID(int id) {
        return nodes[id].getX();
    }

    public double getYOfNodeID(int id) {
        return nodes[id].getY();
    }

    Element getElement(int id) {
        return elements[id];
    }

}

