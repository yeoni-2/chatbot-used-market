package com.example.chatbot_used_market.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {
  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

  public static Point getPoint(double latitude, double longitude){
    return geometryFactory.createPoint(new Coordinate(longitude, latitude));
  }

  // https://www.baeldung.com/java-find-distance-between-points

  private static double haversine(double value){
    return Math.pow(Math.sin(value/2), 2);
  }

  public static double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
    final int EARTH_RADIUS = 6371;
    double dLat = Math.toRadians((endLat - startLat));
    double dLong = Math.toRadians((endLong - startLong));

    startLat = Math.toRadians(startLat);
    endLat = Math.toRadians(endLat);

    double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS * c;
  }

  public static boolean isInDistance(double lat1, double lng1, double lat2, double lng2, int distance){
    return calculateDistance(lat1, lng1, lat2, lng2) <= distance;
  }
}
