package com.shuvojitkar.tourist.Map_Helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nuhel on 7/30/2017.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private GoogleMap mMap;
    private String url;
    private PlaceDetails placeDetails;
    private ArrayList<PlaceDetails> placeDetailsArrayList;
    private Context context;
    private OnNearByFound onNearByFound;

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        context = (Context) objects[2];
        onNearByFound= (OnNearByFound) objects[3];

        DownloadNearbyPlaceUrl downloadURL = new DownloadNearbyPlaceUrl();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){


        NearByPlaceDataParser parser = new NearByPlaceDataParser();
        placeDetailsArrayList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(placeDetailsArrayList);
    }

    private void showNearbyPlaces(ArrayList<PlaceDetails> placeDetailsArrayList)
    {



        if(placeDetailsArrayList.size()==0){
            Toast.makeText(context,"Sorry we find nothing for you",Toast.LENGTH_SHORT).show();
            return;

        }

        onNearByFound.placeFound(placeDetailsArrayList);

        for(int i = 0; i < placeDetailsArrayList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            placeDetails = placeDetailsArrayList.get(i);

            String placeName = placeDetails.getPlaceName();
            String vicinity = placeDetails.getVicinity();
            String ref = placeDetails.getReference();
            double lat = placeDetails.getLat();
            double lng = placeDetails.getLan();

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ ref);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}