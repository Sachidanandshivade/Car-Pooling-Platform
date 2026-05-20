package com.CarPooling.CarPoolingPlatform.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    // ── Geocode: location string → [lat, lng] using Nominatim (FREE, no key) ──
    public double[] getCoordinates(String location) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "CarPoolingPlatform/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://nominatim.openstreetmap.org/search?q="
                + location.replace(" ", "+")
                + "&format=json&limit=1";

        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class);

        List results = response.getBody();
        if (results == null || results.isEmpty()) {
            throw new RuntimeException(
                    "Location not found: \"" + location + "\". Try a more specific address.");
        }

        Map place = (Map) results.get(0);
        double lat = Double.parseDouble((String) place.get("lat"));
        double lng = Double.parseDouble((String) place.get("lon")); // Nominatim uses "lon"

        return new double[]{lat, lng};
    }

    // ── Routing: driving route via OSRM (FREE, no key) ───────────────────────
    public List<double[]> getRoute(double srcLat, double srcLng,
                                   double destLat, double destLng) {
        RestTemplate restTemplate = new RestTemplate();

        // OSRM expects [lng, lat] order
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
            List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

            List<double[]> points = new ArrayList<>();
            for (List<Double> coord : coordinates) {
                points.add(new double[]{coord.get(1), coord.get(0)}); // flip lng,lat → lat,lng
            }
            return points;

        } catch (Exception e) {
            return fallbackLine(srcLat, srcLng, destLat, destLng);
        }
    }

    // ── Haversine distance in km ──────────────────────────────────────────────
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ── Check if point is within 5 km of any point on a route ────────────────
    public boolean isNearRoute(double lat, double lng, List<double[]> route) {
        for (double[] point : route) {
            if (distance(lat, lng, point[0], point[1]) < 5) return true;
        }
        return false;
    }

    // ── Fallback: straight line if OSRM fails ─────────────────────────────────
    private List<double[]> fallbackLine(double srcLat, double srcLng,
                                        double destLat, double destLng) {
        List<double[]> line = new ArrayList<>();
        line.add(new double[]{srcLat, srcLng});
        line.add(new double[]{destLat, destLng});
        return line;
    }
}