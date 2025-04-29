package model;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("name")
    private String name;

    @SerializedName("country")
    private String country;

    @SerializedName("state")
    private String state;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    public City() {}

    public City(String name, String country, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFullName() {
        StringBuilder builder = new StringBuilder(name);
        if (state != null && !state.isEmpty()) {
            builder.append(", ").append(state);
        }
        builder.append(", ").append(country);
        return builder.toString();
    }

    public boolean equals(City other) {
        if (other == null) return false;
        return this.name.equalsIgnoreCase(other.name)
                && this.country.equalsIgnoreCase(other.country);
    }

    @Override
    public String toString() {
        return String.format("City{name='%s', country='%s', lat=%.4f, lon=%.4f}",
                name, country, latitude, longitude);
    }

    public String getCoordinatesString() {
        return String.format("%.4f, %.4f", latitude, longitude);
    }
}