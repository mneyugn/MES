public class GlobalData {

    private double H; // wysokość siatki
    private double W; // szerokość siatki
    private double nH; // ilość węzłów - wysokość
    private double nW; // ilość węzłów - szerokość
    int nN; // ilość węzłów
    int nE; // ilość elementów
    double dH;
    double dW;
    double t0;


    public GlobalData(double h, double w, double nH, double nW, double nN, double nE, double t0) {
        H = h;
        W = w;
        this.nH = nH;
        this.nW = nW;
        this.nN = (int)(nH * nW);
        this.nE = (int)((nH - 1) * (nW - 1));
        this.t0 = t0;
        double dH = H / (nH - 1);
        double dW = W / (nW - 1);

        }


}

