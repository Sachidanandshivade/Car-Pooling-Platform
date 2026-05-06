package com.CarPooling.CarPoolingPlatform.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LocationService {

    private final String API_KEY = "YOUR_GOOGLE_API_KEY";

    // 🔹 1. Get coordinates
    public double[] getCoordinates(String location) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + location + "&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        List results = (List) response.get("results");
        Map geometry = (Map) ((Map) results.get(0)).get("geometry");
        Map loc = (Map) geometry.get("location");

        double lat = (double) loc.get("lat");
        double lng = (double) loc.get("lng");

        return new double[]{lat, lng};
    }

    // 🔹 2. Get route polyline points
    public List<double[]> getRoute(double srcLat, double srcLng,
                                   double destLat, double destLng) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + srcLat + "," + srcLng
                + "&destination=" + destLat + "," + destLng
                + "&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        List routes = (List) response.get("routes");
        Map route = (Map) routes.get(0);
        String encoded =
                (String) ((Map) route.get("overview_polyline")).get("points");

        return decodePolyline(encoded);
    }

    // 🔹 3. Decode polyline
    public List<double[]> decodePolyline(String encoded) {
        List<double[]> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            poly.add(new double[]{lat / 1E5, lng / 1E5});
        }
        return poly;
    }

    // 🔹 4. Distance (Haversine)
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    // 🔹 5. Check if point is near route
    public boolean isNearRoute(double lat, double lng, List<double[]> route) {
        for (double[] p : route) {
            if (distance(lat, lng, p[0], p[1]) < 5) { // 5 km
                return true;
            }
        }
        return false;
    }
}