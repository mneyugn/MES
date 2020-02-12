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

    UniversalElement universalElement;

    Grid(GlobalData globalData) {
        this.nodes = new Node[globalData.nN];
        this.elements = new Element[globalData.nE];
        this.dH = globalData.dH;
        this.dW = globalData.dW;
        universalElement = new UniversalElement();
//        System.out.println(universalElement.toString());
        universalElement.print();
        createGrid(globalData.H, globalData.W, globalData.nH, globalData.nW, globalData.nN, globalData.nE, globalData.t0);
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
    }


    private void completeNodes() {
        int index = 0;
        boolean bc;

        for (double x = 0; x <= W; x += dW)
            for (double y = 0; y <= H; y += dH) {
                bc = isBoundary(x, y);
                nodes[index++] = new Node(x, y, t0, bc);
            }
    }


    private void completeElements() {
        int index = 0;

        int noElementsW = nW - 1;
        int noElementsH = nH - 1;
        for (int i = 0; i < noElementsW; i++) {
            for (int j = 0; j < noElementsH; j++) {
                int n1 = i * nH + j;            // bottom-left
                int n2 = (i + 1) * nH + j;      // bottom-right
                int n3 = (i + 1) * nH + j + 1;  // top-right
                int n4 = i * nH + j + 1;        // top-right

                elements[index++] = new Element(n1, n2, n3, n4);
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

}

