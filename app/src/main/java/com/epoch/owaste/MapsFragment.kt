package com.epoch.owaste


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import kotlinx.android.synthetic.main.fragment_maps.*
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener
{

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val RC_SIGN_IN: Int = 101
        const val TAG = "Eltin_MapsFragment"
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var lastLocation: Location
    private var mapView: View? = null
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel

    /**
     * 定義「AuthUI.IdpConfig」清單，將App支援的身份提供商組態（identity provider config）加入List。
     * 此處加入Google組態。
     */
    lateinit var authProvider: List<AuthUI.IdpConfig>

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this)
                .get(MapsViewModel::class.java)
//        viewModel.getSavedRestaurants().observe(this, Observer {
//
//        })

        val mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.fl_map, mapFragment).commit()

        mapFragment.getMapAsync(this)
        mapView = mapFragment.view

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.fl_map) as SupportMapFragment
//
//        mapFragment.getMapAsync(this)
//
//        mapView = mapFragment.view

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.let {
            it.lifecycleOwner = this
        }

        binding.fabCurrentLocation.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this.requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
                return@setOnClickListener
            }

            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->

                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                }
            }
        }
        binding.fabCard.setOnClickListener {
            this.findNavController().navigate(R.id.action_global_loyaltyCardFragment)
        }


//        addRestaurant(viewModel)

        searchRestaurants()

        initOnCheckedChangeListener()

        authProvider = listOf (
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

//        (view as? MotionLayout)?.getTransition(R.id.ml_above_map)?.setEnable(false)

        googleSignIn()

        initPlaceApiCLient()

        // Inflate the layout for this fragment
        return binding.root
    }

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
//    override fun onResume() {
//        super.onResume()
//
//        setUpMapIfNeeded()
//    }
//
//    private fun setUpMapIfNeeded() {
//
//        if (map == null) {
//
//            getMapAsync(this)
//        }
//    }

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
            binding.root.findViewById<CheckBox>(ids[i])?.setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }

    val onCheckedChangeListener =
        OnCheckedChangeListener { checkBox, isChecked ->
            when (checkBox?.id) {
                R.id.cb_lv1 -> if (isChecked) {
                    i(TAG, "show lv1 !")
                val level1 = restaurantsList.filter { it.level == 1 }
                    viewModel._restaurants.value = level1
                    i(TAG, "level 1 = ${viewModel.restaurants.value}")
                } else {
                    i(TAG, "hide lv1 !")
                }
                R.id.cb_lv2 -> if (isChecked) {
                    i(TAG, "show lv2 !")
                    val level2 = restaurantsList.filter { it.level == 2 }
                    viewModel._restaurants.value = level2
                    i(TAG, "level 2 = ${viewModel.restaurants.value}")
                } else {
                    i(TAG, "hide lv2 !")
                }
                R.id.cb_lv3 -> if (isChecked) {
                    i(TAG, "show lv3 !")
                    val level3 = restaurantsList.filter { it.level == 3 }
                    viewModel._restaurants.value = level3
                    i(TAG, "level 3 = ${viewModel.restaurants.value}")
                } else {
                    i(TAG, "hide lv3 !")
                }
                R.id.cb_lv4 -> if (isChecked) {
                    i(TAG, "show lv4 !")
                    val level4 = restaurantsList.filter { it.level == 4 }
                    viewModel._restaurants.value = level4
                    i(TAG, "level 4 = ${viewModel.restaurants.value}")
                } else {
                    i(TAG, "hide lv4 !")
                }
                R.id.cb_lv5 -> if (isChecked) {
                    i(TAG, "show lv5 !")
                    val level5 = restaurantsList.filter { it.level == 5 }
                    viewModel._restaurants.value = level5
                    i(TAG, "level 5 = ${viewModel.restaurants.value}")
                } else {
                    i(TAG, "hide lv5 !")
                }
            }
        }

//    fun onClick(v: View) {
//        when (v.id) {
//            R.id.cb_lv1 -> i(TAG, "show lv1 !")
//            R.id.cb_lv2 -> i(TAG, "show lv2 !")
//            R.id.cb_lv3 -> i(TAG, "show lv3 !")
//            R.id.cb_lv4 -> i(TAG, "show lv4 !")
//            R.id.cb_lv5 -> i(TAG, "show lv5 !")
//        }
//    }
    private fun googleSignIn() {
//        binding.fabGoogleSignIn.setOnClickListener {
//            i("EltinMapsF", "Sign In clicked")
//            showSignInOptions()
//        }
        binding.imgProfile.setOnClickListener {
            i("EltinMapsF", "Sign In clicked")
            showSignInOptions()
    }
    }

    private fun addRestaurant(viewModel: MapsViewModel) {
        binding.fabAddRestaurant.setOnClickListener {
            //add restaurant to Firestore
            val restaurant = Restaurants(
                id = 2,
                cardId = 5555,
                level = 3,
                lat = 25.042044,
                lng = 121.564699,
                placeId = "BBB",
                name = "AWESOME BURGER"
            )
            viewModel.addRestaurant(restaurant)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser // get current User
                i("EltinMapsF", "" + user?.photoUrl)
                Glide.with(this).load(user?.photoUrl).into(img_profile)
//                txt_profile_name.text = user?.displayName
//                cl_profile.visibility = View.VISIBLE
//                img_profile.visibility = View.VISIBLE
//                img_profile_frame.visibility = View.VISIBLE
                txt_profile_name.text = user?.displayName
//                ml_above_map.visibility = View.VISIBLE

                if (user?.displayName != null) {
                    txt_user_level.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(this.context, "" + response?.error?.message, Toast.LENGTH_SHORT)
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

        map = googleMap

        //Customize Map style
        try{
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this.context, R.raw.style_maps)
            )
            if (!isSuccess)
                Toast.makeText(this.context, "Map style loads failed", Toast.LENGTH_SHORT).show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()
        // include all places we have markers for on the map
        for (i in 0 until viewModel.restaurants.value!!.size) {

            val latLng = LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
            i(TAG, "LatLng = $latLng")
            boundsBuilder.include(latLng)
        }
//        places.keys.map { place -> boundsBuilder.include(places.getValue(place)) }
        val bounds = boundsBuilder.build()

        // Add several markers and move the camera
//        val weNMeCafe = LatLng(25.042044, 121.564699)
//        map.addMarker(MarkerOptions().position(weNMeCafe).title("好好文化創意 We & Me Cafe"))

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen

        val latLng = LatLng(25.042336, 121.564289)
//        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 19f), 5000, null)
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f), 5000, null)
//        with(map){
//            moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, w))*
//        }

        setUpMap()


        viewModel.restaurants.observe(this, Observer {
            addMarkersToMap()
        })
    }

    /**
     * Show all the specified markers on the map
     */
    private fun addMarkersToMap() {

        map.clear()
        val markersList = ArrayList<Marker>()
        val height = 160
        val width = 160
        val bitmapDrawLv1 = resources.getDrawable(R.drawable.ic_marker_lv1)
        val bitmapDrawLv2 = resources.getDrawable(R.drawable.ic_marker_lv2)
        val bitmapDrawLv3 = resources.getDrawable(R.drawable.ic_marker_lv3)
        val bitmapDrawLv4 = resources.getDrawable(R.drawable.ic_marker_lv4)
        val bitmapDrawLv5 = resources.getDrawable(R.drawable.ic_marker_lv5)
        val smallMarkerLv1 = Bitmap.createScaledBitmap(bitmapDrawLv1.toBitmap(), width, height, false)
        val smallMarkerLv2 = Bitmap.createScaledBitmap(bitmapDrawLv2.toBitmap(), width, height, false)
        val smallMarkerLv3 = Bitmap.createScaledBitmap(bitmapDrawLv3.toBitmap(), width, height, false)
        val smallMarkerLv4 = Bitmap.createScaledBitmap(bitmapDrawLv4.toBitmap(), width, height, false)
        val smallMarkerLv5 = Bitmap.createScaledBitmap(bitmapDrawLv5.toBitmap(), width, height, false)

        for (i in 0 until viewModel.restaurants.value!!.size) {
            val latLng = LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
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
                map.addMarker(MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptor)
                    .title(viewModel.restaurants.value!![i].name))
            )

//            for (marker in markersList) {
//                if (marker.isVisible) {
//                    marker.isVisible = false
//                } else {
//                    markersList.add(map.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptor)))
//                }
//            }
//            map.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptor))
            i(TAG, "Restaurant ${viewModel.restaurants.value!![i].name} was added, level = ${viewModel.restaurants.value!![i].level}")
        }
//        val placeDetailsMap = mutableMapOf(
//
//            // Uses a custom icon
//            "WE_ME_CAFE" to PlacesDetails(
//                position = places.getValue("WE_ME_CAFE"),
//                title = "好好文化創意 We & Me Cafe",
//                snippet = "02 2763 8767",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
//            ),
//
//            "AWESOME_BURGER" to PlacesDetails(
//                position = places.getValue("AWESOME_BURGER"),
//                title = "AWESOME BURGER",
//                snippet = "02 2764 2906",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv3)
//            ),
//
//            "MR_BAI_MU" to PlacesDetails(
//                position = places.getValue("MR_BAI_MU"),
//                title = "白暮蛋餅先生2號店松菸",
//                snippet = "0979 949 848",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
//            ),
//
//            "KOREA_HOUSE" to PlacesDetails(
//                position = places.getValue("KOREA_HOUSE"),
//                title = "韓明屋",
//                snippet = "02 2746 8317",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
//            ),
//
//            "GOOD_COFFEE" to PlacesDetails(
//                position = places.getValue("GOOD_COFFEE"),
//                title = "好咖啡拿鐵專賣店",
//                snippet = "02 2749 5567",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv1)
//            ),
//
//            "BELGIUM_COFFEE" to PlacesDetails(
//                position = places.getValue("BELGIUM_COFFEE"),
//                title = "比利時咖啡",
//                snippet = "02 2761 3600",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv1)
//            ),
//
//            "ONE_DUMPLING" to PlacesDetails(
//                position = places.getValue("ONE_DUMPLING"),
//                title = "一記水餃牛肉麵店",
//                snippet = "02 2747 1433",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
//            ),
//
//            "JI_MAN_WU" to PlacesDetails(
//                position = places.getValue("JI_MAN_WU"),
//                title = "吉滿屋食坊",
//                snippet = "02 2768 3251",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv3)
//            ),
//
//            "DU_LAO_DA" to PlacesDetails(
//                position = places.getValue("DU_LAO_DA"),
//                title = "杜佬大手作弁當",
//                snippet = "02 2765 1127",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv2)
//            ),
//
//            "TAIWAN_A_CHENG" to PlacesDetails(
//                position = places.getValue("TAIWAN_A_CHENG"),
//                title = "台灣阿誠現炒菜",
//                snippet = "02 2745 8198",
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lv4)
//            )
//        )
//
//        // place markers for each of the defined locations
//        placeDetailsMap.keys.map {
//            with(placeDetailsMap.getValue(it)) {
//                map.addMarker(
//                    MarkerOptions()
//                        .position(position)
//                        .title(title)
//                        .snippet(snippet)
//                        .icon(icon)
//                        .infoWindowAnchor(infoWindowAnchorX, infoWindowAnchorY)
//                        .draggable(draggable)
//                        .zIndex(zIndex))
//            }
//        }
    }

    fun resizeMarkerIcon() {

//        val markerList = listOf(
//            R.drawable.ic_marker_lv1,
//            R.drawable.ic_marker_lv2,
//            R.drawable.ic_marker_lv3,
//            R.drawable.ic_marker_lv4,
//            R.drawable.ic_marker_lv5
//        )
//
//        for (i in 0 until markerList.size) {
//
//            val height = 32
//            val width = 32
//            val bitmapdraw = resources.getDrawable(markerList[i])
//            val b = bitmapdraw.toBitmap()
//            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
//        }
//

    }
    private fun searchRestaurants() {

        searchView = binding.svLocationWidget
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query?.toString()
                val addressList: List<Address>?

                if (location != null || location != "") {
                    val geocoder = Geocoder(this@MapsFragment.context)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                        val address = addressList?.get(0)
                        val latLng = LatLng(address!!.latitude, address.longitude)
                        map.addMarker(MarkerOptions().position(latLng).title(location))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
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

    private fun setUpMap() {

//        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.setOnMarkerClickListener(this)

        // Add lots of markers to the GoogleMap.
        addMarkersToMap()
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

//    /**
//     * 定義「AuthUI.IdpConfig」清單，將App支援的身份提供商組態（identity provider config）加入List。
//     * 此處加入Google組態。
//     */
//    val authProvider: List<AuthUI.IdpConfig> = listOf(
//        AuthUI.IdpConfig.GoogleBuilder().build()
//    )

    val authListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user: FirebaseUser? = auth.currentUser
            if (user == null) {
                val intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(authProvider)
                    .setAlwaysShowSignInMethodScreen(true)
                    .setIsSmartLockEnabled(false)
                    .build()
                startActivityForResult(intent, RC_SIGN_IN)
            } else {

            }
        }

    private fun showSignInOptions () {
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(authProvider)
            .setTheme(R.style.SingIn)
            .build(), RC_SIGN_IN)
    }

    /**
     * Uses the Glide library to load an image by URL into an [ImageView]
     */
//    fun bindImage(imgView: ImageView, imgUrl: String?) {
//        imgUrl?.let {
//            val imgUri = it.toUri().buildUpon().build()
//            GlideApp.with(imgView.context)
//                .load(imgUri)
//                .apply(
//                    RequestOptions()
//                        .placeholder(R.drawable.ic_placeholder)
//                        .error(R.drawable.ic_placeholder))
//                .into(imgView)
//        }
//    }
}
