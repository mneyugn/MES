public class Node {
    private double X,Y; // współrzędne węzła
    private double T;   // temperatura
    private boolean BC; // warunek brzegowy (0/1)

    public Node(double x, double y, double t, boolean BC) {
        X = x;
        Y = y;
        T = t;
        this.BC = BC;
    }

    @Override
    public String toString() {
        return "Node{" +
                "X=" + X +
                ", Y=" + Y +
                ", T=" + T +
                ", BC=" + BC +
                '}';
    }
}
