public class Service {
    private int serviceCode;
    private String serviceName;
    private double serviceCost;

    public Service(int serviceCode, String serviceName, double serviceCost) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.serviceCost = serviceCost;
    }

    public int getServiceCode() { return serviceCode; }
    public String getServiceName() { return serviceName; }
    public double getServiceCost() { return serviceCost; }

    public void displayServiceInfo() {
        System.out.printf("Service [%d] %s - $%.2f%n", serviceCode, serviceName, serviceCost);
    }

    @Override
    public String toString() {
        return "[" + serviceCode + "] " + serviceName + " - $" + String.format("%.2f", serviceCost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Service)) return false;
        Service s = (Service) obj;
        return this.serviceCode == s.serviceCode;
    }

    @Override
    public int hashCode() { return Integer.hashCode(serviceCode); }
}
