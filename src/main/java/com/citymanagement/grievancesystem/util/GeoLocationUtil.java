package com.citymanagement.grievancesystem.util;

import org.springframework.stereotype.Component;

@Component
public class GeoLocationUtil {

    // Earth radius in kilometers
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * Calculate distance between two coordinates in kilometers using Haversine formula
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Find nearby complaints within a specified radius in kilometers
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param otherLat Another latitude
     * @param otherLon Another longitude
     * @param radiusInKm Radius in kilometers
     * @return True if the other location is within the radius
     */
    public boolean isLocationWithinRadius(double targetLat, double targetLon,
                                          double otherLat, double otherLon,
                                          double radiusInKm) {
        double distance = calculateDistance(targetLat, targetLon, otherLat, otherLon);
        return distance <= radiusInKm;
    }
}