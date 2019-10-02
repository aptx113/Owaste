package com.epoch.owaste.maps


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.epoch.owaste.BuildConfig
import com.epoch.owaste.R
import com.epoch.owaste.data.OwasteRepository
import com.epoch.owaste.data.Restaurant
import com.epoch.owaste.data.User
import com.epoch.owaste.databinding.FragmentMapsBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
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
        val TAG = "Eltin_MapsFragment"
        const val RC_SIGN_IN: Int = 101
        const val LOCATION_UPDATE_MIN_TIME = 500L
        const val LOCATION_UPDATE_MIN_DISTANCE = 10F
        const val EXP = "exp"
    }

    private lateinit var map: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener
    private lateinit var quickPermissionsOptions: QuickPermissionsOptions
    private lateinit var mapFragment: SupportMapFragment
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var mapView: View? = null
    private var userData: User? = null
    private val markersList = ArrayList<Marker>()

    /**
     * 定義「AuthUI.IdpConfig」清單，將App支援的身份提供商組態（identity provider config）加入List。
     * 此處加入Google組態。
     */
    private lateinit var authProvider: List<AuthUI.IdpConfig>

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        map.isMyLocationEnabled = true

        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {

            binding.fabCurrentLocation.setImageResource(R.drawable.ic_current_location)

            if (hasGps) {
                i(TAG, "hasGps")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            map.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(LatLng(location.latitude, location.longitude), 17F))
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
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE,
                    object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            map.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(LatLng(location.latitude, location.longitude), 17F))
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
            binding.fabCurrentLocation.setImageResource(R.drawable.ic_location_service_off)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

            i(TAG, "MapsFragment onAttach")
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

        quickPermissionsOptions = QuickPermissionsOptions(
        permanentlyDeniedMessage = getString(R.string.permanently_denied_message),
        rationaleMethod = { rationaleCallback(it) },
        permanentDeniedMethod = { permissionPermanentlyDenied(it) },
        permissionsDeniedMethod = { whenPermissionsAreDenied(it)}
    )
        onCheckedChangeListener = viewModel.onCheckedChangeListener()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.fl_map) as SupportMapFragment
//
//        mapFragment.getMapAsync(this)
//
//        mapView = mapFragment.view

        viewModel.getRestaurantsFromFirestore()
        i(TAG, "LiveData<List<Restaurant>> = ${viewModel.restaurants.value}")

        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.let {
            it.lifecycleOwner = this
            it.viewModel = this@MapsFragment.viewModel
            it.rvPlacePhoto.adapter = PlaceDetailsPhotoAdapter()
            it.rvPlaceReviews.adapter = PlaceDetailsReviewsAdapter()
        }

        onFabCurrentLocationClicked()
        navigateToRewardCards()
        navigateToQrCodeScanner()
        navigateToAddRestaurant()
        navigateToLevelInfo()
        displaySearchResultByTitle()
        initOnCheckedChangeListener()
        firebaseAuthStateListener()
        userSignOut()
        initPlaceApiCLient()
        clearSearchBarText()
        showClearSymbolOnSearchBarClicked()
        handlePlaceCommentVisibility()
        clearFilter()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        i(TAG, "MapsFragment onResume")
        if (FirebaseAuth.getInstance().currentUser != null) {

            OwasteRepository.initUserLevelWhenBackToMap()
            binding.progressbarUserExp.max = userData?.level?.times(100) ?: 0
        }
        binding.let {

            it.cbLv1.isChecked = false
            it.cbLv2.isChecked = false
            it.cbLv3.isChecked = false
            it.cbLv4.isChecked = false
            it.cbLv5.isChecked = false
        }
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {

        // this will be called when permission is denied once or more time.
        i(TAG, "rationaleCallback : this will be called when permission is denied once or more time")
        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(R.string.rationale_callback_title))
            .setMessage(getString(R.string.rationale_callback_message))
            .setPositiveButton(getString(R.string.rationale_callback_positive_button)) { _, _ -> req.proceed() }
            .setNegativeButton(getString(R.string.rationale_callback_negative_button)) { _, _ -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    fun permissionPermanentlyDenied(req: QuickPermissionsRequest) {

        // this will be called when some/all permissions required by the method are permanently
        // denied.
        i(TAG, "permissionPermanentlyDenied : this will be called when some/all permissions required by the method are permanently denied")
        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(R.string.permission_permanently_denied_title))
            .setMessage(getString(R.string.permission_permanently_denied_message))
            .setPositiveButton(getString(R.string.permission_permanently_denied_positive_button)) { _, _ -> req.openAppSettings() }
            .setNegativeButton(getString(R.string.permission_permanently_denied_negative_button)) { _, _ -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    fun whenPermissionsAreDenied(req: QuickPermissionsRequest) {

        // handle something when permissions are not granted and the request method cannot be called.
        i(TAG, "whenPermissionsAreDenied : handle something when permissions are not granted and the request method cannot be called")
        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(R.string.when_permissions_are_denied_title))
            .setMessage(getString(R.string.when_permissions_are_denied_message))
            .setPositiveButton(getString(R.string.when_permissions_are_denied_positive_button)) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    private fun dismissPlaceDetailOnMapClicked() {

        map.setOnMapClickListener {

            i(TAG, "map clicked !")
            binding.clInCvPlaceDetails.visibility = View.GONE
            binding.autoCompleteTvSearchBar.isCursorVisible = false
        }
    }

    private fun onFabCurrentLocationClicked() {

        binding.fabCurrentLocation.setOnClickListener {

            i(TAG, "fab_current_location clicked")

            runWithPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, options = quickPermissionsOptions
            ) {
                if ( !hasGps && !hasNetwork) {

                    showDialogIfLocationServiceOff()
                    getLocation()
                } else {
                    getLocation()
                }
            }
        }
    }

    private fun displaySearchResultByTitle() {

        binding.imgSearchIcon.setOnClickListener {

            showMarkerSearchedByTitle(binding.autoCompleteTvSearchBar.text.toString())
            binding.autoCompleteTvSearchBar.isCursorVisible = false
        }
    }

    private fun navigateToRewardCards() {

        binding.fabCard.setOnClickListener {

            if (binding.clProfile.isClickable) {

                Toast.makeText(this.context, getString(R.string.login_hint_on_fab_clicked), Toast.LENGTH_SHORT)
                    .show()
            } else {
                this.findNavController().navigate(R.id.action_global_rewardCardFragment)
            }
        }
    }

    private fun navigateToQrCodeScanner() {

        binding.fabQrcode.setOnClickListener {

            if (binding.clProfile.isClickable) {

                Toast.makeText(this.context, getString(R.string.login_hint_on_fab_clicked), Toast.LENGTH_SHORT)
                    .show()
            } else {
                this.findNavController().navigate(R.id.action_global_QRCodeScannerFragment)
            }
        }
    }

    private fun navigateToAddRestaurant() {

        val restaurantDialog = Dialog(this.requireContext())
        restaurantDialog.setCancelable(true)
        restaurantDialog.setContentView(R.layout.fragment_new_restaurant_dialog)

        binding.fabAddRestaurant.setOnClickListener {

            if (binding.clProfile.isClickable) {

                Toast.makeText(this.context, getString(R.string.login_hint_on_fab_clicked), Toast.LENGTH_SHORT)
                    .show()
            } else {
                restaurantDialog.show()
            }
        }
    }

    private fun navigateToLevelInfo() {

        val levelInfoDialog = Dialog(this.requireContext())
        levelInfoDialog.setCancelable(true)
        levelInfoDialog.setContentView(R.layout.fragment_restaurant_level_info)

        binding.fabLevelInfo.setOnClickListener {
            levelInfoDialog.show()
        }
    }
    private fun showDialogIfLocationServiceOff() {

        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(R.string.if_location_service_off))
            .setPositiveButton(getString(R.string.if_location_service_off_positive_button)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                getLocation()
            }
            .setNegativeButton(getString(R.string.if_location_service_off_negative_button)) { _, _ ->
                Toast.makeText(this.context, getString(R.string.if_location_service_off_negative_button_clicked), Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun showMarkerSearchedByTitle (title: String) {

        i(TAG, "search " + binding.autoCompleteTvSearchBar.text.toString())
        if ( !binding.autoCompleteTvSearchBar.text.isNullOrEmpty() ) {

            for (marker in markersList) {

                marker.isVisible = marker.title.equals(title, true)
//                if (marker.title.equals(title, true)) {
//
//                    marker.isVisible = marker.title.equals(title, true)
//                    i(TAG, "marker = ${marker.title}")
//                } else {
//                    Toast.makeText(this.requireContext(), "尚未收錄此店家，歡迎使用新增功能！", Toast.LENGTH_LONG).show()
//                }
    //            if (binding.autoCompleteTvSearchBar.text.toString().isEmpty()) {
    //                marker.isVisible = true
    //            }
            }
        } else {
            Toast.makeText(this.requireContext(), getString(R.string.empty_search), Toast.LENGTH_SHORT).show()
        }
    }
    private fun firebaseAuthStateListener() {

        authProvider = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user?.displayName.isNullOrEmpty()) {
                    i(TAG, "User = $user")

                    binding.let {

                        it.imgProfile.setImageResource(R.drawable.common_google_signin_btn_icon_light_normal)
                        it.txtProfileName.text = this.context?.getString(R.string.click_to_login_in)
                        it.imgProfile.isLongClickable = false
                        it.txtUserLevel.visibility = View.GONE
                        it.progressbarUserExp.visibility = View.GONE
                        it.txtUserExpGoal.visibility = View.GONE
                        it.txtUserExpSlash.visibility = View.GONE
                        it.txtUserCurrentExp.visibility = View.GONE
                        it.clProfile.setOnClickListener {
                            val intent = AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(authProvider)
                                .setAlwaysShowSignInMethodScreen(false)
                                .setIsSmartLockEnabled(false, true)
                                .build()
                            startActivityForResult(intent,
                                RC_SIGN_IN
                            )
                        }
                    }

//                    Toast.makeText(this.context, "登入成功 ! ", Toast.LENGTH_SHORT).show()
                } else {

                    OwasteRepository.initCurrentUserIfFirstTime {  }

                    i(TAG, "current user = ${user?.email}")

                    binding.let {

                        it.clProfile.isClickable = false
                        it.imgProfile.isClickable = false
                        it.imgProfile.isLongClickable = true
                        it.txtProfileName.text = user?.displayName
                        it.txtUserLevel.visibility = View.VISIBLE
                    }
                    Glide.with(this).load(user?.photoUrl).into(img_profile)

                    viewModel.getCurrentUserExpToUpdateProgressBar(OnSuccessListener { document ->
                        userData = document.toObject(User::class.java)
                        i(TAG, "userData = $userData")
                        userData?.let {

                            val totalExp = Integer.parseInt(document.get(EXP).toString())
                            val displayExp = totalExp - 100 * ( (1 + (userData?.level?.minus(1))!!) * (userData!!.level - 1) / 2 )

                            binding.let {

                                it.progressbarUserExp.progress = displayExp
                                it.progressbarUserExp.max = userData!!.level * 100
                                it.progressbarUserExp.visibility = View.VISIBLE
                                it.txtUserExpGoal.text = (userData!!.level * 100).toString()
                                it.txtUserExpGoal.visibility = View.VISIBLE
                                it.txtUserCurrentExp.text = displayExp.toString()
                                it.txtUserCurrentExp.visibility = View.VISIBLE
                                it.txtUserExpSlash.visibility = View.VISIBLE
                                i(TAG, "totalExp = $totalExp, displayExp = $displayExp")

                                when (userData!!.level) {
                                    1 -> it.txtUserLevel.text = getString(R.string.user_level_1)
                                    2 -> it.txtUserLevel.text = getString(R.string.user_level_2)
                                    3 -> it.txtUserLevel.text = getString(R.string.user_level_3)
                                    4 -> it.txtUserLevel.text = getString(R.string.user_level_4)
                                    5 -> it.txtUserLevel.text = getString(R.string.user_level_5)
                                    6 -> it.txtUserLevel.text = getString(R.string.user_level_6)
                                    7 -> it.txtUserLevel.text = getString(R.string.user_level_7)
                                    8 -> it.txtUserLevel.text = getString(R.string.user_level_8)
                                    9 -> it.txtUserLevel.text = getString(R.string.user_level_9)
                                    10 -> it.txtUserLevel.text = getString(R.string.user_level_10)
                                }
                                it.txtUserLevel.visibility = View.VISIBLE
                            }
                        }
                    })
                }
            }

        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    private fun userSignOut() {
        binding.imgProfile.setOnLongClickListener {

            firebaseSignOut()
            binding.let {

                it.imgProfile.setImageResource(R.drawable.common_google_signin_btn_icon_light_normal)
                it.txtProfileName.text = getString(R.string.click_to_login_in)
                it.txtUserLevel.text = ""
                it.imgProfile.isLongClickable = false
            }
            true
        }
    }
    private fun firebaseSignOut() {

        AuthUI.getInstance()
            .signOut(this.requireContext())
            .addOnSuccessListener {
                Toast.makeText(this.context, getString(R.string.log_out), Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(this.context, response?.error?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        map.animateCamera(CameraUpdateFactory
            .newLatLngZoom(marker?.position, map.cameraPosition.zoom), 500, null)

        marker?.let {

            viewModel.resetRestaurantDetailsToNull()
            binding.let {

                it.txtPlaceName.text = marker.title
                it.txtPlaceName.visibility = View.VISIBLE
                it.ratingbarPlaceRating.visibility = View.GONE
                it.txtRating.visibility = View.GONE
                it.txtRatingTotal.visibility = View.GONE
                it.txtRatingTotalRight.visibility = View.GONE
                it.txtRatingTotalLeft.visibility = View.GONE
                it.txtPriceLevel.visibility = View.GONE
                it.imgRestaurantLevel.visibility = View.GONE
                it.txtIsPlaceOpen.visibility = View.GONE
                it.rvPlacePhoto.visibility = View.GONE
                it.txtDotBetweenTypePriceLevel.visibility = View.GONE

                it.cvPlaceDetails.visibility = View.VISIBLE
                it.progressbarPlaceDetails.visibility = View.VISIBLE
                it.clInCvPlaceDetails.visibility = View.VISIBLE
            }

            viewModel.getClickedRestaurantFromFirestoreByName(marker.title, OnSuccessListener { querySnapshot ->

                if (!querySnapshot.isEmpty) {

                    i(TAG, "QuerySnapshot = ${querySnapshot.toObjects(Restaurant::class.java)}")

                    val placeIdOfClickedRestaurant = querySnapshot.toObjects(Restaurant::class.java)[0].placeId
                    viewModel.getPlaceDetails(placeIdOfClickedRestaurant)

                    viewModel.placeDetails.observe(this, Observer { place ->

                        place?.let { details ->
                            i(TAG, "viewModel.placeDetails = ${viewModel.placeDetails.value}")

                            binding.let {

                                it.progressbarPlaceDetails.visibility = View.GONE
                                it.txtPlaceDetiailShowReviews.visibility = View.VISIBLE
                                it.imgExpandReviewsArrow.visibility = View.VISIBLE
    //                            binding.imgRestaurantLevel.visibility = View.VISIBLE
                            }

                            if (details.photos != null) {
                                binding.rvPlacePhoto.visibility = View.VISIBLE
                            }
                            if (details.rating != null) {

                                binding.let {

                                    it.ratingbarPlaceRating.rating = details.rating
                                    it.ratingbarPlaceRating.visibility = View.VISIBLE
                                    it.txtRating.text = details.rating.toString()
                                    it.txtRating.visibility = View.VISIBLE
                                }
                            }
                            if (details.user_ratings_total != null) {

                                binding.let {

                                    it.txtRatingTotal.text = details.user_ratings_total.toString()
                                    it.txtRatingTotal.visibility = View.VISIBLE
                                    it.txtRatingTotalRight.visibility = View.VISIBLE
                                    it.txtRatingTotalLeft.visibility = View.VISIBLE
                                }
                            }
                            if (details.price_level != null && details.price_level != 0 && details.price_level != 1) {

                                binding.let {

                                    when (details.price_level) {
                                        2 -> it.txtPriceLevel.text = getString(R.string.price_level_2)
                                        3 -> it.txtPriceLevel.text = getString(R.string.price_level_3)
                                        4 -> it.txtPriceLevel.text = getString(R.string.price_level_4)
                                    }
                                    it.txtPriceLevel.visibility = View.VISIBLE
                                    it.txtDotBetweenTypePriceLevel.visibility = View.VISIBLE
                                }
                            }
                            if (details.opening_hours != null) {

                                binding.txtIsPlaceOpen.visibility = View.VISIBLE
                                if (details.opening_hours.open_now) {

                                    binding.let {

                                        it.txtIsPlaceOpen.text = getString(R.string.place_open)
                                        it.txtIsPlaceOpen.setTextColor(resources.getColor(R.color.darker_green_FF658540))
                                    }
                                } else {

                                    binding.let {

                                        it.txtIsPlaceOpen.text = getString(R.string.place_close)
                                        it.txtIsPlaceOpen.setTextColor(resources.getColor(R.color.quantum_vanillared400))
                                    }
                                }
                            } else {
                                binding.txtIsPlaceOpen.visibility = View.INVISIBLE
                            }
                        }
                    })
                    dismissPlaceDetailOnMapClicked()
                } else {
                    i(TAG, "QuerySnapshot = null")
                }
            })
        }

        return true
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

        //取得準備好的 Map
        map = googleMap
        mapView = mapFragment.view
        i("Eltin", "mapView=$mapView")

        //Customize Map style
        customizeMapStyle(googleMap)

        // include all places we have markers for on the map
        if (viewModel.restaurants.value != null) {

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()

            i(TAG, "viewModel apply : ${viewModel.restaurants.apply { this.value?.size }}")
            for (i in 0 until viewModel.restaurants.value!!.size) {

                val latLng =
                    LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
                i(TAG, "LatLng = $latLng")
                boundsBuilder.include(latLng)
            }
        // Add several markers and move the camera
            val bounds = boundsBuilder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 10f), 3000, null)
        }

        setUpMap()
        getLocationPermission()
    }

    private fun customizeMapStyle(googleMap: GoogleMap) {

        try {
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this.context,
                    R.raw.style_maps
                )
            )
            if (!isSuccess)
                Toast.makeText(this.context, getString(R.string.map_style_failed), Toast.LENGTH_SHORT).show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

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
        val markerLv1 =
            Bitmap.createScaledBitmap(bitmapDrawLv1.toBitmap(), width, height, false)
        val markerLv2 =
            Bitmap.createScaledBitmap(bitmapDrawLv2.toBitmap(), width, height, false)
        val markerLv3 =
            Bitmap.createScaledBitmap(bitmapDrawLv3.toBitmap(), width, height, false)
        val markerLv4 =
            Bitmap.createScaledBitmap(bitmapDrawLv4.toBitmap(), width, height, false)
        val markerLv5 =
            Bitmap.createScaledBitmap(bitmapDrawLv5.toBitmap(), width, height, false)

        for (i in 0 until viewModel.restaurants.value!!.size) {
            val latLng =
                LatLng(viewModel.restaurants.value!![i].lat, viewModel.restaurants.value!![i].lng)
            var bitmapDescriptor: BitmapDescriptor
            when (viewModel.restaurants.value!![i].level) {
                1 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerLv1)
                }
                2 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerLv2)
                }
                3 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerLv3)
                }
                4 -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerLv4)
                }
                else -> {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerLv5)
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
    }

    private fun getLocationPermission() = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        options = quickPermissionsOptions){

        //Google Map 中顯示裝置位置，且裝置移動會跟著移動的那個藍點
        map.isMyLocationEnabled = true
        getLocation()
    }

    private fun setUpMap() {

        map.let {

            it.uiSettings.isZoomGesturesEnabled = true
            it.uiSettings.isMyLocationButtonEnabled = false
            it.uiSettings.isMapToolbarEnabled = false
            it.uiSettings.isCompassEnabled = false
            it.setOnMarkerClickListener(this)
        }

        viewModel.restaurants.observe(this, Observer {
            addMarkersToMap()
        })

        // Tried to restrict panning of map
//        val TAIWAN = LatLngBounds(LatLng(22.0, 120.0), LatLng(25.0, 122.0))
//        map.setLatLngBoundsForCameraTarget(TAIWAN)
    }

    private fun clearSearchBarText() {

        binding.imgClearSearchText.setOnClickListener {

            binding.autoCompleteTvSearchBar.setText("")
            addMarkersToMap()
        }
    }

    private fun showClearSymbolOnSearchBarClicked() {

        binding.autoCompleteTvSearchBar.setOnClickListener {

            binding.autoCompleteTvSearchBar.isCursorVisible = true
        }
    }

    private fun handlePlaceCommentVisibility() {

        binding.txtPlaceDetiailShowReviews.setOnClickListener {

            i(TAG, "rvPlaceComment isVisible = ${binding.rvPlaceReviews.isVisible}")
            if (!binding.rvPlaceReviews.isVisible) {

                binding.let {

                    it.rvPlaceReviews.visibility = View.VISIBLE
                    it.imgSeparation.visibility = View.VISIBLE
                    it.imgExpandReviewsArrow.setImageResource(R.drawable.ic_expand_up_arrow)
                }
            } else {

                binding.let {

                    it.rvPlaceReviews.visibility = View.GONE
                    it.imgSeparation.visibility = View.GONE
                    it.imgExpandReviewsArrow.setImageResource(R.drawable.ic_expand_down_arrow)
                }
            }
        }

        binding.imgExpandReviewsArrow.setOnClickListener {

            i(TAG, "rvPlaceComment isVisible = ${binding.rvPlaceReviews.isVisible}")
            if (!binding.rvPlaceReviews.isVisible) {

                binding.let {

                    it.rvPlaceReviews.visibility = View.VISIBLE
                    it.imgSeparation.visibility = View.VISIBLE
                    it.imgExpandReviewsArrow.setImageResource(R.drawable.ic_expand_up_arrow)
                }
            } else {

                binding.let {

                    it.rvPlaceReviews.visibility = View.GONE
                    it.imgSeparation.visibility = View.GONE
                    it.imgExpandReviewsArrow.setImageResource(R.drawable.ic_expand_down_arrow)
                }
            }
        }

        i(TAG, "rvPlaceComment isVisible = ${binding.rvPlaceReviews.isVisible}")
    }

    private fun clearFilter() {

        binding.txtClearFilter.setOnClickListener {

            addMarkersToMap()
            i(TAG, "map clears !")
            i(TAG, "marker list = $markersList")
            binding.let {

                it.cbLv1.isChecked = false
                it.cbLv2.isChecked = false
                it.cbLv3.isChecked = false
                it.cbLv4.isChecked = false
                it.cbLv5.isChecked = false
            }
        }
    }
}