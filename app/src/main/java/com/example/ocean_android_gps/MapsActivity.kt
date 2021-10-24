package com.example.ocean_android_gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
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
import com.google.android.libraries.places.api.Places

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

        // Não alterar google_maps_api.xml, e sim AndroidManifest.xml
        // Chave está em local.properties
        // Mostra chave google maps...
        val apiKey = BuildConfig.GMP_KEY
        if (apiKey.isNullOrEmpty())
            Toast.makeText(this, "Chave google maps vazia", Toast.LENGTH_SHORT).show()

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

        // Needs phone set to allow Google Position for GPS
        //val ultimaLocalizacao = locationManager.getLastKnownLocation(locationProvider)

        // Lista de provedores GPS
        val providers: List<String> = locationManager.getProviders(true)
        var ultimaLocalizacao: Location? = null
        for (provider in providers) {
            val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (ultimaLocalizacao == null || l.getAccuracy() < ultimaLocalizacao.getAccuracy()) {
            // Found best last known location: %s", l);
                ultimaLocalizacao = l
            }
        }

        ultimaLocalizacao?.let {

            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

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
        } ?: run {
            Toast.makeText(this, "Última localização conhecida não encontrada", Toast.LENGTH_SHORT).show()
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