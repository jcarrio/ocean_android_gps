package com.example.ocean_android_gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ocean_android_gps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CircleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        val cristo = LatLng(-22.9519, -43.2105)

        mMap.addMarker(MarkerOptions().position(cristo).title("Cristo Redentor"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(cristo))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cristo, 16.25f))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cristo, 16.25f))

        iniciarLocalizacao()
    }

    private fun iniciarLocalizacao() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationProvider = LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Caso não tenha as permissões, solitica
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )

            // Encerra a execução do método
            return
        }

//        val ultimaLocalizacao = locationManager.getLastKnownLocation(locationProvider)
        val ultimaLocalizacao = locationManager.getLastKnownLocation(locationProvider)

        Toast.makeText(this, ultimaLocalizacao.toString(), Toast.LENGTH_LONG).show()

        ultimaLocalizacao?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.25f))

            mMap.addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(50.0)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(3f)
                    .fillColor(Color.parseColor("#537CDBE7"))
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Se o RequestCode for 1, significa que foi a chamada de localização que fizemos
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarLocalizacao()
        }
    }
}