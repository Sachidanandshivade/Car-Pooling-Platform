package com.CarPooling.CarPoolingPlatform.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    // ── Geocode: location string → [lat, lng] biased to Bangalore ────────────
    public double[] getCoordinates(String location) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "CarPoolingPlatform/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // First try: bounded strictly inside Bangalore
        String encodedLocation = location.replace(" ", "+");
        String url = "https://nominatim.openstreetmap.org/search?q="
                + encodedLocation + ",+Bangalore,+Karnataka,+India"
                + "&format=json&limit=1"
                + "&countrycodes=in"
                + "&viewbox=77.4,12.8,77.8,13.2"
                + "&bounded=1";

        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class);
        List results = response.getBody();

        // Second try: unbounded but still India only
        if (results == null || results.isEmpty()) {
            url = "https://nominatim.openstreetmap.org/search?q="
                    + encodedLocation + ",+Bangalore,+Karnataka,+India"
                    + "&format=json&limit=1"
                    + "&countrycodes=in";
            response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            results = response.getBody();
        }

        if (results == null || results.isEmpty()) {
            throw new RuntimeException(
                    "Location not found: \"" + location + "\". " +
                    "Try a specific name like 'BTM Layout' or 'JP Nagar 2nd Phase'.");
        }

        Map place = (Map) results.get(0);
        double lat = Double.parseDouble((String) place.get("lat"));
        double lng = Double.parseDouble((String) place.get("lon"));

        // Sanity check: must be within Karnataka bounds
        if (lat < 11.5 || lat > 18.5 || lng < 74.0 || lng > 78.6) {
            throw new RuntimeException(
                    "Location \"" + location + "\" resolved outside Karnataka. " +
                    "Please enter a Bangalore locality name.");
        }

        return new double[]{lat, lng};
    }

    // ── Road route via OSRM → list of [lat, lng] points ──────────────────────
    public List<double[]> getRoute(double srcLat, double srcLng,
                                   double destLat, double destLng) {
        RestTemplate restTemplate = new RestTemplate();

        // OSRM expects lng,lat order
        String url = "http://router.project-osrm.org/route/v1/driving/"
                + srcLng + "," + srcLat + ";"
                + destLng + "," + destLat
                + "?overview=full&geometries=geojson";

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            List routes = (List) response.get("routes");
            if (routes == null || routes.isEmpty()) {
                return fallbackLine(srcLat, srcLng, destLat, destLng);
            }

            Map route = (Map) routes.get(0);
            Map geometry = (Map) route.get("geometry");
            List<List<Double>> coordinates =
                    (List<List<Double>>) geometry.get("coordinates");

            List<double[]> points = new ArrayList<>();
            for (List<Double> coord : coordinates) {
                points.add(new double[]{coord.get(1), coord.get(0)}); // flip lng,lat → lat,lng
            }
            return points;

        } catch (Exception e) {
            return fallbackLine(srcLat, srcLng, destLat, destLng);
        }
    }

    // ── Road distance in km by summing route segments ─────────────────────────
    public double roadDistance(List<double[]> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += haversine(
                    route.get(i)[0], route.get(i)[1],
                    route.get(i + 1)[0], route.get(i + 1)[1]
            );
        }
        return total;
    }

    // ── Haversine straight-line distance in km ────────────────────────────────
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        return haversine(lat1, lon1, lat2, lon2);
    }

    // ── Check if a point is within 5 km of any route point ───────────────────
    public boolean isNearRoute(double lat, double lng, List<double[]> route) {
        for (double[] point : route) {
            if (haversine(lat, lng, point[0], point[1]) < 5) return true;
        }
        return false;
    }

    // ── Internal Haversine ────────────────────────────────────────────────────
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ── Fallback straight line if OSRM fails ──────────────────────────────────
    private List<double[]> fallbackLine(double srcLat, double srcLng,
                                        double destLat, double destLng) {
        List<double[]> line = new ArrayList<>();
        line.add(new double[]{srcLat, srcLng});
        line.add(new double[]{destLat, destLng});
        return line;
    }
}
