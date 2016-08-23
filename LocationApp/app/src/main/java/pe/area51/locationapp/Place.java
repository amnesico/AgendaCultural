package pe.area51.locationapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by USER on 20/08/2016.
 */
public class Place {

    private final long id;
    private final String name_place;
    private final String district;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final LatLng latLong;


    public Place(long id, String name_place, String district, String address, double latitude, double longitude) {
        this.id = id;

        this.name_place = name_place;
        this.district = district;
        this.address = address;

        this.latitude = latitude;
        this.longitude = longitude;
        this.latLong = new LatLng(latitude,longitude);
    }

    public long getId() {
        return id;
    }

    public String getName_place() {
        return name_place;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLatLong() {
        return latLong;
    }
}
