package de.othr.crusher.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service for fetching weather information for gym locations. Uses Open-Meteo API to get current
 * weather conditions.
 */
@Service
public class WeatherService {

  private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

  private final WebClient webClient;

  @Value("${weather.api.geocoding.url}")
  private String geocodingUrl;

  @Value("${weather.api.forecast.url}")
  private String forecastUrl;

  @Value("${weather.api.timeout:5000}")
  private int timeout;

  public WeatherService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  /**
   * Gets the current weather condition for a given city.
   *
   * @param city the city name
   * @return WeatherInfo with text and icon, or null if unavailable
   */
  public WeatherInfo getWeatherForCity(String city) {
    if (city == null || city.isBlank()) {
      logger.warn("City name is null or empty");
      return null;
    }

    try {
      // Step 1: Geocode city to coordinates
      GeocodingResponse geocoding = geocodeCity(city);
      if (geocoding == null || geocoding.results == null || geocoding.results.length == 0) {
        logger.warn("No geocoding results found for city: {}", city);
        return null;
      }

      double latitude = geocoding.results[0].latitude;
      double longitude = geocoding.results[0].longitude;

      // Step 2: Get weather for coordinates
      WeatherResponse weather = getWeather(latitude, longitude);
      if (weather == null || weather.current == null) {
        logger.warn("No weather data found for coordinates: {}, {}", latitude, longitude);
        return null;
      }

      // Step 3: Map weather code to weather info
      int weatherCode = weather.current.weatherCode;
      WeatherInfo weatherInfo = mapWeatherCodeToWeatherInfo(weatherCode);
      logger.info(
          "Weather for {} (lat={}, lon={}): code={} -> {} ({})",
          city,
          latitude,
          longitude,
          weatherCode,
          weatherInfo.text(),
          weatherInfo.icon());
      return weatherInfo;

    } catch (WebClientResponseException e) {
      logger.error(
          "HTTP error fetching weather for city {}: {} {}",
          city,
          e.getStatusCode(),
          e.getMessage());
      return null;
    } catch (Exception e) {
      logger.error("Error fetching weather for city {}: {}", city, e.getMessage());
      return null;
    }
  }

  /** Geocodes a city name to coordinates using Open-Meteo Geocoding API. */
  private GeocodingResponse geocodeCity(String city) {
    return webClient
        .get()
        .uri(geocodingUrl + "?name={city}&count=1&language=de&format=json", city)
        .retrieve()
        .bodyToMono(GeocodingResponse.class)
        .timeout(Duration.ofMillis(timeout))
        .block();
  }

  /** Gets current weather for given coordinates using Open-Meteo Weather API. */
  private WeatherResponse getWeather(double latitude, double longitude) {
    return webClient
        .get()
        .uri(
            forecastUrl + "?latitude={lat}&longitude={lon}&current=weather_code&timezone=auto",
            latitude,
            longitude)
        .retrieve()
        .bodyToMono(WeatherResponse.class)
        .timeout(Duration.ofMillis(timeout))
        .block();
  }

  /**
   * Maps WMO Weather Code to WeatherInfo with text and icon. Based on WMO Code table:
   * https://open-meteo.com/en/docs
   */
  private WeatherInfo mapWeatherCodeToWeatherInfo(int weatherCode) {
    return switch (weatherCode) {
      case 0, 1 -> new WeatherInfo("Sunny", "sun"); // Clear sky, mainly clear
      case 2 -> new WeatherInfo("Cloudy", "cloudy"); // Partly cloudy
      case 3 -> new WeatherInfo("Cloudy", "cloudy"); // Overcast
      case 45, 48 -> new WeatherInfo("Foggy", "cloud-fog"); // Fog and depositing rime fog
      case 51, 53, 55 ->
          new WeatherInfo("Rainy", "cloud-rain"); // Drizzle: Light, moderate, and dense
      case 56, 57 -> new WeatherInfo("Rainy", "cloud-rain"); // Freezing Drizzle: Light and dense
      case 61, 63, 65 -> new WeatherInfo("Rainy", "cloud-rain"); // Rain: Slight, moderate and heavy
      case 66, 67 -> new WeatherInfo("Rainy", "cloud-rain"); // Freezing Rain: Light and heavy
      case 71, 73, 75 ->
          new WeatherInfo("Snowy", "cloud-snow"); // Snow fall: Slight, moderate, and heavy
      case 77 -> new WeatherInfo("Snowy", "cloud-snow"); // Snow grains
      case 80, 81, 82 ->
          new WeatherInfo("Rainy", "cloud-rain"); // Rain showers: Slight, moderate, and violent
      case 85, 86 -> new WeatherInfo("Snowy", "cloud-snow"); // Snow showers slight and heavy
      case 95 -> new WeatherInfo("Rainy", "cloud-rain"); // Thunderstorm: Slight or moderate
      case 96, 99 ->
          new WeatherInfo("Rainy", "cloud-rain"); // Thunderstorm with slight and heavy hail
      default -> new WeatherInfo("Cloudy", "cloudy"); // Unknown/default to cloudy
    };
  }

  /** Weather information record containing text and icon name. */
  public record WeatherInfo(String text, String icon) {}

  // DTOs for API responses
  private static class GeocodingResponse {
    public GeocodingResult[] results;
  }

  private static class GeocodingResult {
    public double latitude;
    public double longitude;
    public String name;
  }

  private static class WeatherResponse {
    public CurrentWeather current;
  }

  private static class CurrentWeather {
    @JsonProperty("weather_code")
    public int weatherCode;
  }
}
