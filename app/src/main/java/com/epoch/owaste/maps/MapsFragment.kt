package com.epoch.owaste.maps


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.epoch.owaste.BuildConfig
import com.epoch.owaste.R
import com.epoch.owaste.data.OwasteRepository
import com.epoch.owaste.databinding.FragmentMapsBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val RC_SIGN_IN: Int = 101
        const val TAG = "Eltin_MapsFragment"
        const val LOCATION_UPDATE_MIN_TIME = 5000L
        const val LOCATION_UPDATE_MIN_DISTANCE = 10F
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var lastLocation: Location
    var location: Location? = null
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? =null
//    private lateinit var locationListener: LocationListener
    private var mapView: View? = null
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener

    lateinit var mapFragment: SupportMapFragment
    lateinit var locationButton: View
    val markersList = ArrayList<Marker>()
    /**
     * 定義「AuthUI.IdpConfig」清單，將App支援的身份提供商組態（identity provider config）加入List。
     * 此處加入Google組態。
     */
    lateinit var authProvider: List<AuthUI.IdpConfig>

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {

            if (hasGps) {
                i(TAG, "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 17F))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }
                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    locationGps = localGpsLocation
                }
            }
            if (hasNetwork) {
                i(TAG, "hasNetwork")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 17F))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }
                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null) {
                    locationNetwork = localNetworkLocation
                }

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps!!.accuracy > localNetworkLocation!!.accuracy) {
                        i(TAG, "Network Latitude :" + locationNetwork!!.latitude)
                        i(TAG, "Network Longitude :" + locationNetwork!!.longitude)
                    } else {
                        i(TAG, "Gps Latitude :" + locationGps!!.latitude)
                        i(TAG, "Gps Longitude :" + locationGps!!.longitude)
                    }
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this)
            .get(MapsViewModel::class.java)

        mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.fl_map, mapFragment).commitNow()

        mapFragment.getMapAsync(this) // return OnMapReadyCallback
        mapView = mapFragment.view
        i("Eltin", "mapView=$mapView")

        onCheckedChangeListener = viewModel.onCheckedChangeListener()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.fl_map) as SupportMapFragment
//
//        mapFragment.getMapAsync(this)
//
//        mapView = mapFragment.view

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        viewModel.getRestaurantsFromFirestore()
        i(TAG, "LiveData<List<Restaurant>> = ${viewModel.restaurants.value}")

        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.let {
            it.lifecycleOwner = this
        }

        binding.fabCurrentLocation.setOnClickListener {

            i(TAG, "fab_current_location clicked")

            runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) {
                getLocation()
            }
//            getCurrentLocation()
            //            getLocationPermission()
//            i(TAG, "locationButton = $locationButton")
//            locationButton.callOnClick()
        }
//            if (ActivityCompat.checkSelfPermission(this.requireContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this.requireActivity(),
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE)
//                return@setOnClickListener
//            }

//            map.isMyLocationEnabled = true
//            fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
//
//                if (location != null) {
//                    lastLocation = location
//                    val currentLatLng = LatLng(location.latitude, location.longitude)
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
//                }
//            }

        binding.imgSearchIcon.setOnClickListener {
            binding.let {
                it.cbLv1.visibility = View.VISIBLE
                it.cbLv2.visibility = View.VISIBLE
                it.cbLv3.visibility = View.VISIBLE
                it.cbLv4.visibility = View.VISIBLE
                it.cbLv5.visibility = View.VISIBLE
                it.imgFilterLv1.visibility = View.VISIBLE
                it.imgFilterLv2.visibility = View.VISIBLE
                it.imgFilterLv3.visibility = View.VISIBLE
                it.imgFilterLv4.visibility = View.VISIBLE
                it.imgFilterLv5.visibility = View.VISIBLE
            }
        }
        binding.fabCard.setOnClickListener {
            this.findNavController().navigate(R.id.action_global_loyaltyCardFragment)
        }

        binding.fabQrcode.setOnClickListener {
            this.findNavController().navigate(R.id.action_global_QRCodeScannerFragment)
        }

        // create data of restaurants on Firestore
//        for (i in 0 until restaurantsList.size) {
//            viewModel.addRestaurant(restaurantsList[i])
//            i(TAG, "restaurants added on Firestore : ${restaurantsList[i].name}")
//        }

//        searchRestaurants()
//        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//        i(TAG, "context = $context")

        initOnCheckedChangeListener()

        firebaseAuthStateListener()

//        googleSignIn()
        userSignOut()
        initPlaceApiCLient()
//        getCurrentLocation()

        // Inflate the layout for this fragment
        return binding.root
    }

//    override fun onPause() {
//        super.onPause()
//        locationManager.removeUpdates(locationListener)
//    }

    private fun firebaseAuthStateListener() {
        authProvider = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    i(TAG, "User = $user")
                    img_profile.setImageResource(R.drawable.common_google_signin_btn_icon_light_normal)
                    txt_profile_name.text = getString(R.string.click_to_login_in)
                    img_profile.isLongClickable = false
                    binding.imgProfile.setOnClickListener {
                        val intent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(authProvider)
                            .setAlwaysShowSignInMethodScreen(false)
                            .setIsSmartLockEnabled(false)
                            .build()
                        startActivityForResult(intent,
                            RC_SIGN_IN
                        )
//                        checkIfUserInFirestore()
                    }
                } else {

//                    checkIfUserInFirestore()
                    OwasteRepository.initCurrentUserIfFirstTime {  }

                    img_profile.isClickable = false
                    img_profile.isLongClickable = true
                    txt_profile_name.text = user.displayName
                    txt_user_level.text = getString(R.string.user_level)
                    Glide.with(this).load(user.photoUrl).into(img_profile)

                    if (user.displayName != null) {

                        txt_user_level.visibility = View.VISIBLE
                    }
                }
            }

        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

//    private fun checkIfUserInFirestore() {
//        viewModel.firestoreDb.collection("User")
//            .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid)
//            .get()
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//
//                    if (it.result?.size() != 0) {
//                        i(TAG, "QuerySnapshot = ${it.result?.size()}")
//                    } else {
//                        val newUser = User(
//                            exp = 0,
//                            uid = FirebaseAuth.getInstance().currentUser!!.uid
//                        )
//                        viewModel.firestoreDb.collection("User")
//                            .add(newUser)
//                            .addOnSuccessListener { document ->
//                                i(TAG, "Add new user, document UID = ${document.id}")
//                            }
//                    }
//                }
//            }
//    }
    private fun googleSignIn() {

        binding.imgProfile.setOnClickListener {
            i("EltinMapsF", "Sign In clicked")
            showSignInOptions()
        }
    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(authProvider)
                .setTheme(R.style.SingIn)
                .build(), RC_SIGN_IN
        )
    }

    private fun userSignOut() {
        binding.imgProfile.setOnLongClickListener {

            firebaseSignOut()
            binding.imgProfile.setImageResource(R.drawable.common_google_signin_btn_icon_light_normal)
            txt_profile_name.text = getString(R.string.click_to_login_in)
            txt_user_level.text = ""
            binding.imgProfile.isLongClickable = false
            true
        }
    }
    private fun firebaseSignOut() {

        AuthUI.getInstance()
            .signOut(this.requireContext())
            .addOnSuccessListener {
                Toast.makeText(this.context, "怎麼登出得這麼突然...", Toast.LENGTH_SHORT).show()
            }
    }
//    private fun getLocationPermission() {
//        runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
//            Toast.makeText(this.requireContext(), "開啟位置權限 ya", Toast.LENGTH_SHORT).show()
////            locationManager()
//        }
//    }

    private fun initPlaceApiCLient() {
        // Initialize the SDK
        Places.initialize(this.requireContext(), BuildConfig.API_KEY)
        //Create a new Places client instance
        val placesClient = Places.createClient(this.requireContext())
    }

    private fun getPlaceDetails() {
        // Define a Place ID
        val placeId = ""

        // Specify the fields to return
        val placeFields = listOf(Place.Field.RATING)
    }

    // use for loop to initialize multiple checkboxes.setOnCheckedChangeListener
    private fun initOnCheckedChangeListener() {
        val ids = intArrayOf(
            R.id.cb_lv1,
            R.id.cb_lv2,
            R.id.cb_lv3,
            R.id.cb_lv4,
            R.id.cb_lv5
        )

        for (i in ids.indices) {
            binding.root.findViewById<CheckBox>(ids[i])
                ?.setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {

                val response = IdpResponse.fromResultIntent(data)
//                val user = FirebaseAuth.getInstance().currentUser // get current User
//
//                Glide.with(this).load(user?.photoUrl).into(img_profile)
//                txt_profile_name.text = user?.displayName
//
//                if (user?.displayName != null) {
//                    txt_user_level.visibility = View.VISIBLE
//
//                }
//                i("EltinMapsF", "user UID = ${user?.uid}")
                Toast.makeText(this.context, response?.error?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

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
//        getLocationPermission()

        //取得準備好的 Map
        map = googleMap

        //Customize Map style
        customizeMapStyle(googleMap)

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()
        // include all places we have markers for on the map
        for (i in 0 until viewModel.restaurants.value!!.size) {

            val latLng =
                LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
            i(TAG, "LatLng = $latLng")
            boundsBuilder.include(latLng)
        }
        val bounds = boundsBuilder.build()

        // Add several markers and move the camera

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen

//        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 19f), 5000, null)
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f), 5000, null)
//        with(map){
//            moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, w))*
//        }

        mapView = mapFragment.view
        i("Eltin", "mapView=$mapView")

//        getCurrentLocation()
        setUpMap()
        getLocationPermission()
//        getDefaultLocationButtonGone()
    }

    private fun customizeMapStyle(googleMap: GoogleMap) {
        try {
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this.context,
                    R.raw.style_maps
                )
            )
            if (!isSuccess)
                Toast.makeText(this.context, "Map style loads failed", Toast.LENGTH_SHORT).show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

//    private fun getDefaultLocationButtonGone() {
//        locationButton = (
//                mapView?.findViewById<View>(Integer.parseInt("1"))
//                    ?.parent as View).findViewById<View>(Integer.parseInt("2"))
//        val layoutParams = locationButton.layoutParams as (RelativeLayout.LayoutParams)
//        // position on right bottom
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
//        layoutParams.setMargins(0, 0, 30, 30)
//        locationButton.visibility = View.VISIBLE
//    }

    /**
     * Show all the specified markers on the map
     */
    private fun addMarkersToMap() {

        map.clear()

        val height = 160
        val width = 160
        val bitmapDrawLv1 = resources.getDrawable(R.drawable.ic_marker_lv1)
        val bitmapDrawLv2 = resources.getDrawable(R.drawable.ic_marker_lv2)
        val bitmapDrawLv3 = resources.getDrawable(R.drawable.ic_marker_lv3)
        val bitmapDrawLv4 = resources.getDrawable(R.drawable.ic_marker_lv4)
        val bitmapDrawLv5 = resources.getDrawable(R.drawable.ic_marker_lv5)
        val smallMarkerLv1 =
            Bitmap.createScaledBitmap(bitmapDrawLv1.toBitmap(), width, height, false)
        val smallMarkerLv2 =
            Bitmap.createScaledBitmap(bitmapDrawLv2.toBitmap(), width, height, false)
        val smallMarkerLv3 =
            Bitmap.createScaledBitmap(bitmapDrawLv3.toBitmap(), width, height, false)
        val smallMarkerLv4 =
            Bitmap.createScaledBitmap(bitmapDrawLv4.toBitmap(), width, height, false)
        val smallMarkerLv5 =
            Bitmap.createScaledBitmap(bitmapDrawLv5.toBitmap(), width, height, false)

        for (i in 0 until viewModel.restaurants.value!!.size) {
            val latLng =
                LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
            var bitmapDescriptor: BitmapDescriptor
            when (viewModel.restaurants.value!![i].level) {
                1 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarkerLv1)
                }
                2 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarkerLv2)
                }
                3 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarkerLv3)
                }
                4 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarkerLv4)
                }
                else -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarkerLv5)
                }
            }

            markersList.add(
                map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptor)
                        .title(viewModel.restaurants.value!![i].name)
                )
            )

            i(TAG, "markerList = ${markersList[i]}")
        }
//        if (viewModel.restaurants.value != null) {
//
//        }
    }

//    private fun searchRestaurants() {
//
//        searchView = binding.svLocationWidget
//        searchView.setOnQueryTextListener(object :
//            androidx.appcompat.widget.SearchView.OnQueryTextListener {
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                val location = searchView.query?.toString()
//                val addressList: List<Address>?
//
//                if (location != null || location != "") {
//                    val geocoder = Geocoder(this@MapsFragment.context)
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1)
//                        val address = addressList?.get(0)
//                        val latLng = LatLng(address!!.latitude, address.longitude)
//                        map.addMarker(MarkerOptions().position(latLng).title(location))
//                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
//    }

    private fun getLocationPermission() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION){

        //Google Map 中顯示裝置位置，且裝置移動會跟著移動的那個藍點
        map.isMyLocationEnabled = true
        getLocation()
    }
    private fun setUpMap() {

        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.setOnMarkerClickListener(this)

        viewModel.restaurants.observe(this, Observer {
            addMarkersToMap()
        })


//        if (ActivityCompat.checkSelfPermission(this.requireContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this.requireActivity(),
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE)
//            return
//        }
//
//        map.isMyLocationEnabled = true
//        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
//
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
//            }
//        }
    }

//    private fun getCurrentLocation() {
//
//        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//        if (!(isGPSEnabled || isNetworkEnabled)) {
//            Snackbar.make(mapView!!, "位置服務要打開唷", Snackbar.LENGTH_INDEFINITE).show()
//        } else {
//            try {
//                if (isNetworkEnabled) {
//                    locationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener
//                    )
//                        location =
//                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                }
//
//                if (isGPSEnabled) {
//                    locationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER,
//                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener
//                    )
//                    location =
//                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                }
//            } catch (ex: SecurityException) {
//                e(TAG, "Security Exception, no location available")
//            }
//            if (location != null) {
//                map.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        LatLng(
//                            location!!.latitude,
//                            location!!.longitude
//                        ), 10f
//                    )
//                )
//                i(TAG, "current latlng = ${location!!.latitude}, ${location!!.longitude}")
//            }
//        }
//    }

//        private fun addRestaurant(viewModel: MapsViewModel) {
//        binding.fabAddRestaurant.setOnClickListener {
//            //add restaurants to Firestore
//            viewModel.addRestaurant()
//        }
//    }
}