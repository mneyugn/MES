public class IntegrationPoint {
    private double ksi;
    private double eta;
    private double weight1;
    private double weight2;

    public IntegrationPoint(double ksi, double eta, double weight1, double weight2) {
        this.ksi = ksi;
        this.eta = eta;
        this.weight1 = weight1;
        this.weight2 = weight2;
    }

    public IntegrationPoint(double ksi, double eta) {
        this(ksi, eta, 1., 1.);
    }

    public double getKsi() {
        return ksi;
    }

    public double getEta() {
        return eta;
    }

    public double getWeight1() {
        return weight1;
    }

    public double getWeight2() {
        return weight2;
    }
}
