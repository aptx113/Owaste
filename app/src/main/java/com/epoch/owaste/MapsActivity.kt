package com.epoch.owaste

import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException

class MapsActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnInfoWindowCloseListener,
    GoogleMap.OnInfoWindowLongClickListener
{

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var lastLocation: Location
    private var mapView: View? = null

    /**
     * map to store place names and locations
     */
    private val places = mapOf(
        "WE_ME_CAFE" to LatLng(25.042044, 121.564699),
        "AWESOME_BURGER" to LatLng(25.042098, 121.564179),
        "MR_BAI_MU" to LatLng(25.042336, 121.564289),
        "KOREA_HOUSE" to LatLng(25.042178, 121.564455),
        "GOOD_COFFEE" to LatLng(25.042068, 121.563881),
        "BELGIUM_COFFEE" to LatLng(25.042302, 121.564160),
        "ONE_DUMPLING" to LatLng(25.042486, 121.564393),
        "JI_MAN_WU" to LatLng(25.042297, 121.564098),
        "DU_LAO_DA" to LatLng(25.042402, 121.564190),
        "TAIWAN_A_CHENG" to LatLng(25.042451, 121.564005)
    )
    override fun onMarkerClick(p0: Marker?) = false

    override fun onInfoWindowClick(p0: Marker?) {
        Toast.makeText(this,"Info Window Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowClose(p0: Marker?) {
        Toast.makeText(this,"Info Window Closed", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowLongClick(p0: Marker?) {
        Toast.makeText(this,"Info Window Long Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapView = mapFragment.view

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        searchRestaurants()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        //Customize Map style
        try{
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_maps)
            )
            if (!isSuccess)
                Toast.makeText(this, "Map style loads failed", Toast.LENGTH_SHORT).show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        map = googleMap

//        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.setOnMarkerClickListener(this)

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()
        // include all places we have markers for on the map
        places.keys.map { place -> boundsBuilder.include(places.getValue(place)) }
        val bounds = boundsBuilder.build()

        // Add several markers and move the camera
//        val weNMeCafe = LatLng(25.042044, 121.564699)
//        map.addMarker(MarkerOptions().position(weNMeCafe).title("好好文化創意 We & Me Cafe"))

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))

//        with(map){
//            moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, w))
//        }

        // Add lots of markers to the GoogleMap.
        addMarkerToMap()

        setUpMap()
    }

    /**
     * Show all the specified markers on the map
     */
    private fun addMarkerToMap() {

        val placeDetailsMap = mutableMapOf(

            // Uses a custom icon
            "WE_ME_CAFE" to PlaceDetails(
                position = places.getValue("WE_ME_CAFE"),
                title = "好好文化創意 We & Me Cafe",
                snippet = "02 2763 8767",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
            ),

            "AWESOME_BURGER" to PlaceDetails(
                position = places.getValue("AWESOME_BURGER"),
                title = "AWESOME BURGER",
                snippet = "02 2764 2906",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv3)
            ),

            "MR_BAI_MU" to PlaceDetails(
                position = places.getValue("MR_BAI_MU"),
                title = "白暮蛋餅先生2號店松菸",
                snippet = "0979 949 848",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
            ),

            "KOREA_HOUSE" to PlaceDetails(
                position = places.getValue("KOREA_HOUSE"),
                title = "韓明屋",
                snippet = "02 2746 8317",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
            ),

            "GOOD_COFFEE" to PlaceDetails(
                position = places.getValue("GOOD_COFFEE"),
                title = "好咖啡拿鐵專賣店",
                snippet = "02 2749 5567",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv1)
            ),

            "BELGIUM_COFFEE" to PlaceDetails(
                position = places.getValue("BELGIUM_COFFEE"),
                title = "比利時咖啡",
                snippet = "02 2761 3600",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv1)
            ),

            "ONE_DUMPLING" to PlaceDetails(
                position = places.getValue("ONE_DUMPLING"),
                title = "一記水餃牛肉麵店",
                snippet = "02 2747 1433",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
            ),

            "JI_MAN_WU" to PlaceDetails(
                position = places.getValue("JI_MAN_WU"),
                title = "吉滿屋食坊",
                snippet = "02 2768 3251",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv3)
            ),

            "DU_LAO_DA" to PlaceDetails(
                position = places.getValue("DU_LAO_DA"),
                title = "杜佬大手作弁當",
                snippet = "02 2765 1127",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
            ),

            "TAIWAN_A_CHENG" to PlaceDetails(
                position = places.getValue("TAIWAN_A_CHENG"),
                title = "台灣阿誠現炒菜",
                snippet = "02 2745 8198",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
            )
        )

        // place markers for each of the defined locations
        placeDetailsMap.keys.map {
            with(placeDetailsMap.getValue(it)) {
                map.addMarker(MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(icon)
                    .infoWindowAnchor(infoWindowAnchorX, infoWindowAnchorY)
                    .draggable(draggable)
                    .zIndex(zIndex))
            }
        }
    }

    private fun searchRestaurants() {

        searchView = findViewById(R.id.sv_location_widget)
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query?.toString()
                val addressList: List<Address>?

                if (location != null || location != "") {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                        val address = addressList?.get(0)
                        val latLng = LatLng(address!!.latitude, address.longitude)
                        map.addMarker(MarkerOptions().position(latLng).title(location))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }
}
