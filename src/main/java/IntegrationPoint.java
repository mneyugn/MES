public class IntegrationPoint {
    private double ksi;
    private double eta;
    private double ksiWeight;
    private double etaWeight;

    public IntegrationPoint(double ksi, double eta, double ksiWeight, double etaWeight) {
        this.ksi = ksi;
        this.eta = eta;
        this.ksiWeight = ksiWeight;
        this.etaWeight = etaWeight;
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

    public double getKsiWeight() {
        return ksiWeight;
    }

    public double getEtaWeight() {
        return etaWeight;
    }
}
