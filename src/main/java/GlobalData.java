public class GlobalData {

    double H; // wysokość siatki
    double W; // szerokość siatki
    int nH; // ilość węzłów - wysokość
    int nW; // ilość węzłów - szerokość
    int nN; // ilość węzłów
    int nE; // ilość elementów
    double dH;
    double dW;
    double t0;
    public static final int NUM_OF_INTEGRATION_POINTS_2D = 4;
    public static final int NUM_OF_SHAPE_FUNCTIONS = 4;

    public GlobalData(double h, double w, int nH, int nW, double t0) {
        H = h;
        W = w;
        this.nH = nH;
        this.nW = nW;
        this.nN = nH * nW;
        this.nE = (nH - 1) * (nW - 1);
        this.t0 = t0;
        dH = H / (nH - 1);
        dW = W / (nW - 1);

    }


}

