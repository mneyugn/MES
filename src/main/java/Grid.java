public class Grid {
    Node[] nodes;
    Element[] elements;

    double H;
    double W;
    // ilość węzłów na wysokość i na szerokość
    int nH;
    int nW;

    // ilość węzłów, liczba elementów
    private int nN, nE;

    private double dH;
    private double dW;
    double t0;

    private UniversalElement universalElement;

    Grid(GlobalData globalData) {
        this.nodes = new Node[GlobalData.nN];
        this.elements = new Element[globalData.nE];
        this.dH = globalData.dH;
        this.dW = globalData.dW;
        universalElement = new UniversalElement();

        universalElement.print();

        System.out.println(universalElement.getElement(1, 2));
        createGrid(GlobalData.H, GlobalData.W, GlobalData.nH, GlobalData.nW, GlobalData.nN, GlobalData.nE, GlobalData.t0);
    }

    void createGrid(double h, double w, int nH, int nW, int nN, int nE, double t0) {
        H = h;
        W = w;
        this.nH = nH;
        this.nW = nW;
        this.nN = nN;
        this.nE = nE;
        this.t0 = t0;
        completeNodes();
        completeElements();
        agregate();
    }


    private void completeNodes() {
        int index = 0;
        boolean bc;

        for (double x = 0; x <= W; x += dW)
            for (double y = 0; y <= H; y += dH) {
                bc = isBoundary(x, y);
                nodes[index] = new Node(x, y, t0, bc);
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

    void agregate() {
        double[][] globalH = new double[GlobalData.nN][GlobalData.nN];
        double[][] globalC = new double[GlobalData.nN][GlobalData.nN];
        double[] globalP = new double[GlobalData.nN];

        //petla for po wszystkich węzłach siatki
        for (int i = 0; i < GlobalData.nE; i++) {
            //pobieram id wszystkich wezłów siatki
            int[] indexTab = elements[i].getID();

            //dodaje poszczegolne elementy do macierzy globalnej H wykorzystując id węzłów
            for (int y = 0; y < 4; y++) {
                //petla for po kolumnach macierzy H danego elementu
                globalP[indexTab[y]] += elements[i].getLocalPVector()[y];
                for (int x = 0; x < 4; x++) {
                    //dodajemy element do H zgodnie z id węzłów
                    int index_row = indexTab[y];
                    int index_column = indexTab[x];
                    globalH[index_row][index_column] += elements[i].getLocalHMatrix()[y][x];
                    globalC[index_row][index_column] += elements[i].getLocalCMatrix()[y][x];
                }
            }
        }
        System.out.println(globalH.length + "\t\t" + globalH[1].length + "\t\t P " + globalP.length);
        for (double doubles : globalP) {
            System.out.print(doubles + "\t\t");
//            for (double aDouble : doubles) {
//                System.out.print(aDouble + "\t\t");
//            }
            System.out.println();
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

