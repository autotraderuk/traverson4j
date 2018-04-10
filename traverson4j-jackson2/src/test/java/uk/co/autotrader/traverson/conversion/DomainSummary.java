package uk.co.autotrader.traverson.conversion;

public class DomainSummary extends HalResource<Void> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
