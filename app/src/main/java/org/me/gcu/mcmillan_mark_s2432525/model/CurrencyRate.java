package org.me.gcu.mcmillan_mark_s2432525.model;

public class CurrencyRate {
    private String countryCode;
    private String currencyName;
    private Double rateToGbp;
    private String lastUpdated;

    public CurrencyRate() {};

    public CurrencyRate(String countryCode, String currencyName, Double rateToGbp, String lastUpdated) {
        this.countryCode = countryCode;
        this.currencyName = currencyName;
        this.rateToGbp = rateToGbp;
        this.lastUpdated = lastUpdated;
    }

    public String getCountryCode() { return countryCode; }
    public String getCurrencyName() { return currencyName; }
    public Double getRateToGbp() { return rateToGbp; }
    public String getLastUpdated() { return lastUpdated; }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setRateToGbp(Double rateToGbp) {
        this.rateToGbp = rateToGbp;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
