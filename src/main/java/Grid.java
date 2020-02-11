public class Grid {
    Node[] nodes;
    Element[] elements;

    Double H, W;

    // ilość węzłów na wysokość i na szerokość
    int nH, nW;

    // ilość węzłów, liczba elementów
    private Double nN, nE;

    private Double dH;
    private Double dW;
    Double t;

    Grid(GlobalData globalData) {
        this.nodes = new Node[globalData.nN];
        this.elements = new Element[globalData.nE];
        this.dH = globalData.dH;
        this.dW = globalData.dW;
    }

    void createGrid(Double h, Double w, int nH, int nW, Double nN, Double nE) {
        H = h;
        W = w;
        this.nH = nH;
        this.nW = nW;
        this.nN = nN;
        this.nE = nE;

        completeNodes();
        completeElements();
    }


    private void completeNodes() {
        int index = 0;
        boolean bc;
        for (int x = 0; x < W; x += dW) {
            for (int y = 0; y < H; y += dH) {

                bc = isBoundary(x, y);
                nodes[index++] = new Node(x, y, t, bc);
            }
        }
    }


    private void completeElements() {

        for (int i = 0; i < nW; i++) {
            for (int j = 0; j < nH; j++) {
                Node n1 = nodes[i * nH + j]; // down-left
                Node n2 = nodes[i]
             }


        }
    }


    private boolean isBoundary(double x, double y) {
        return (x == 0 || y == 0 || x == H || y == W);
    }


    void print() {
        for (Node node : nodes) {
            System.out.println(node);
        }

    }

}
