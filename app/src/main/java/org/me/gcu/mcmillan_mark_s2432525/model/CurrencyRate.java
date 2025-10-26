package org.me.gcu.mcmillan_mark_s2432525.model;

public class CurrencyRate {
    private String countryCode;
    private String name;
    private Double rateToGbp;
    private String lastUpdated;

    public CurrencyRate() {};

    public CurrencyRate(String countryCode, String name, Double rateToGbp, String lastUpdated) {
        this.countryCode = countryCode;
        this.name = name;
        this.rateToGbp = rateToGbp;
        this.lastUpdated = lastUpdated;
    }

    public String getCountryCode() { return countryCode; }
    public String getName() { return name; }
    public Double getRateToGbp() { return rateToGbp; }
    public String getLastUpdated() { return lastUpdated; }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRateToGbp(Double rateToGbp) {
        this.rateToGbp = rateToGbp;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
