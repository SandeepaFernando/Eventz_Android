package com.example.eventz.home;

public class VendorInfo {
    private String title;
    private String venue;
    private String date;
    private String num_people;

    public VendorInfo(String title, String venue, String date, String num_people) {
        this.title = title;
        this.venue = venue;
        this.date = date;
        this.num_people = num_people;
    }

    public String getTitle() {
        return title;
    }

    public String getVenue() {
        return venue;
    }

    public String getDate() {
        return date;
    }

    public String getNum_people() {
        return num_people;
    }
}
