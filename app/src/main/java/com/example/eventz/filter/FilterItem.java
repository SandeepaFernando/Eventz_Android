package com.example.eventz.filter;

public class FilterItem {
    private String fName;
    private String uEmail;
    private String rateCategory;
    private String location;

    public FilterItem(String fName, String uEmail, String rateCategory, String location) {
        this.fName = fName;
        this.uEmail = uEmail;
        this.rateCategory = rateCategory;
        this.location = location;
    }

    public String getfName() {
        return fName;
    }


    public String getRateCategory() {
        return rateCategory;
    }


    public String getLocation() {
        return location;
    }


    public String getuEmail() {
        return uEmail;
    }

}
