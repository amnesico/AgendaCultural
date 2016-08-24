package pe.area51.locationapp;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private LocationManager locationManager;
    private TextView locationInfoTextView;
    private MapFragment mapFragment;

    static final LatLng LIMA = new LatLng(-12.0515135, -77.0402321);
    static final LatLng MIRAFLORES = new LatLng(-12.1242787,-77.0214352);
    static final LatLng BARRANCO = new LatLng(-12.141667,  -77.016667);
    static final LatLng LINCE = new LatLng(-12.082418, -77.0379035);

    private GoogleMap map;
    private SQLiteManager sqLiteManager;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationInfoTextView = (TextView) findViewById(R.id.textview_location_info);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        sqLiteManager = SQLiteManager.getInstance(this);

        populateBD();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final ArrayList<Place> places;
        Place place;
        Location location;

        final Iterator<Place> placeIterator;

        clearMarkers(markers);

        switch (item.getItemId() ) {
            case R.id.barranco:

                places = sqLiteManager.getPlaces("Barranco");

                map.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(BARRANCO,
                                13, 30f, 112.5f))); // zoom, tilt, bearing

                placeIterator = places.iterator();


                while(placeIterator.hasNext()){
                    place = placeIterator.next();
                    markers.add(map.addMarker(new MarkerOptions()
                            .position(place.getLatLong())
                            .title(place.getName_place())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .snippet(place.getAddress())) );


                }

                return true;

            case R.id.miraflores:

                places = sqLiteManager.getPlaces("Miraflores");

                map.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(MIRAFLORES,
                                13, 30f, 112.5f))); // zoom, tilt, bearing


                placeIterator = places.iterator();

                while(placeIterator.hasNext()){
                    place = placeIterator.next();
                    markers.add(map.addMarker(new MarkerOptions()
                            .position(place.getLatLong())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            .title(place.getName_place())
                            .snippet(place.getAddress())) );


                }
                return true;


            case R.id.cercado:
                places = sqLiteManager.getPlaces("Cercado");

                map.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(LIMA,
                                13, 30f, 112.5f))); // zoom, tilt, bearing


                placeIterator = places.iterator();

                while(placeIterator.hasNext()){
                    place = placeIterator.next();
                    markers.add(map.addMarker(new MarkerOptions()
                            .position(place.getLatLong())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title(place.getName_place())
                            .snippet(place.getAddress())) );


                }

                return true;

            case R.id.nearby:
                loadPlacesNear(currentLocation);
                return true;

            default:
                return false;

        }

    }

    /** Carga en el mapa todos los Places cercanos al currentLocation **/
    private void loadPlacesNear(Location currentLocation){
        final ArrayList<Place> places;
        final Iterator<Place> placeIterator;
        Place place;
        int distanceInKm = 0;
        final LatLng currentLatLng;

        /** Obtengo todos los Places de la BD **/
        places = sqLiteManager.getPlaces("%%");

        placeIterator = places.iterator();

        currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(currentLatLng, 13, 0, 0)));

        /** Agrego un marcador al arreglo de marcadores, con mi posición actual **/
        markers.add(map.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Aquí me encuentro")));

        /** Recorro todos los Places **/
        while(placeIterator.hasNext()){
            place = placeIterator.next();

            /** Calculo la distancia entre mi posición actual y la del Place**/
            distanceInKm = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), place.getLatitude(), place.getLongitude());

            /** Muestro solo los Places ubicados a 5 kilometros a la redonda **/
            if (distanceInKm <= 5 ) {
                markers.add(map.addMarker(new MarkerOptions()
                        .position(place.getLatLong())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(place.getName_place())
                        .snippet(place.getAddress() + " (A "+ String.valueOf(distanceInKm)+ " Km.)" )));

            }

        }

    }

    /** Oculta todos los marcadores del mapa **/
    private void clearMarkers(ArrayList<Marker> markers) {
        Iterator<Marker> markerIterator = markers.iterator();

        while(markerIterator.hasNext()){
            markerIterator.next().setVisible(false);
        }

    }

    /** Llena la base de datos **/
    private void populateBD() {

        sqLiteManager.onUpgrade(sqLiteManager.getWritableDatabase(), 2, 3);
        sqLiteManager.insertPlace(new Place(-1, "Lugar de la Memoria, la Tolerancia y la Inclusión Social", "Miraflores", "Av. San Martin 151", -12.110138, -77.053658 ));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural Ricardo Palma", "Miraflores", "Av. José Larco 770", -12.125461, -77.029708));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural Ccori Wasi", "Miraflores", "Calle 2 de Mayo 142", -12.117685, -77.029504));
        sqLiteManager.insertPlace(new Place(-1, "Centro de la Imagen", "Miraflores", "Av. 28 de Julio 815", -12.128207, -77.025561));
        sqLiteManager.insertPlace(new Place(-1, "Instituto Raúl Porras Barrenechea", "Miraflores", "Narciso De La Colina 398",  -12.118399, -77.026803));

        sqLiteManager.insertPlace(new Place(-1, "Sargento Pimienta", "Barranco", "Av. Francisco Bolognesi 757", -12.143953, -77.018721));
        sqLiteManager.insertPlace(new Place(-1, "Museo de Arte Contemporáneo", "Barranco", "Av. Almte. Miguel Grau 1511", -12.136194, -77.023226));
        sqLiteManager.insertPlace(new Place(-1, "La Noche", "Barranco", "Sánchez Carrión 199", -12.148027, -77.020224 ));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural Juan Parra del Riego", "Barranco", "Pedro de Osma 135", -12.150839, -77.021978));
        sqLiteManager.insertPlace(new Place(-1, "Casa Cultural Mocha Graña", "Barranco", "Saenz Peña 107", -12.143434, -77.022409));


        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural de la Escuela de Bellas Artes", "Cercado", "Jirón Huallaga 402 - 426", -12.048189, -77.028375));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural Inca Garcilaso", "Cercado", "Jirón Ucayali 391", -12.048701, -77.029104 ));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural San Marcos", "Cercado", "Av. Nicolás de Piérola 1222", -12.054655, -77.032075));
        sqLiteManager.insertPlace(new Place(-1, "Espacio Fundación Telefónica Lima", "Cercado", "Av. Arequipa 1155", -12.076287, -77.035498));
        sqLiteManager.insertPlace(new Place(-1, "Centro Cultural de España", "Cercado", "Jirón Natalio Sanchez 181", -12.070488, -77.037397));


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(LIMA, 11, 0, 0)));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String namePlace = marker.getTitle();
                final String addressPlace = marker.getSnippet();

                marker.showInfoWindow();

                //Toast.makeText(MainActivity.this, namePlace + "\n" + addressPlace, Toast.LENGTH_SHORT).show();
                return false; //Devuelvo false para que aparezca el toolbar de Google Maps.
            }
        });

    }


    private void startLocationUpdates() {
        final List<String> providers = locationManager.getAllProviders();
        for (final String provider : providers) {
            locationManager.requestLocationUpdates(provider, 0, 0, this);
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void showLocation(Place place) {
        final String namePlace = place.getName_place();
        final String districtPlace = place.getDistrict();
        final String addressPlace = place.getAddress();

        locationInfoTextView.setText(namePlace + "\n" + districtPlace + "\n" + addressPlace);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    public int calculateDistance(double userLat, double userLng,
                                 double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
    }

}
