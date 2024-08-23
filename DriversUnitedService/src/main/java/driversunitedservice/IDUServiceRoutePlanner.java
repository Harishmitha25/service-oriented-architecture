package driversunitedservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class IDUServiceRoutePlanner {
    public static void main(String args[]) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/routePlanner", new RoutePlannerHandler());
        server.start();
        System.out.println("Server is running on port 8001");
    }

    static class RoutePlannerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	
        	 // Add CORS headers to all responses from this handler.
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            
            // Handle pre-flight CORS requests here
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // HTTP 204 No Content
                exchange.close();
                return;
            }
            
            
            String query = exchange.getRequestURI().getQuery();

            String origin = extractParameter(query, "startLocation");
            String destination = extractParameter(query, "targetLocation");
            String mode = extractParameter(query, "transportMeans");

            if (origin.isEmpty() || destination.isEmpty() || mode.isEmpty()) {
                sendErrorResponse(exchange, 400, "One or more Addresses are invalid or input parameters are empty.");
                return;
            }

            mode = convertModeToApiParameter(mode);
            if (mode == null) {
                sendErrorResponse(exchange, 400, "Invalid transportMeans specified.");
                return;
            }

            String apiKey = "";
            String apiUrl = constructApiUrl(origin, destination, mode, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String routeDetails = extractRouteDetails(response.body(), mode);
                sendResponse(exchange, 200, routeDetails);
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Failed to retrieve directions due to an invalid address exception: " + e.getMessage());
            }
        }

        private String convertModeToApiParameter(String mode) {
            switch (mode) {
                case "car":
                case "motorbike":
                    return "driving";
                case "bicycle":
                case "e-bike":
                    return "bicycling";
                default:
                    return null;
            }
        }

        private String constructApiUrl(String origin, String destination, String mode, String apiKey) {
            return "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                    "&destination=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                    "&mode=" + URLEncoder.encode(mode, StandardCharsets.UTF_8) +
                    "&key=" + apiKey;
        }

        private String convertDistance(long meters) {
            double miles = meters * 0.000621371;
            return String.format("%.2f miles", miles);
        }

        private String convertDuration(long seconds) {
            long minutes = seconds / 60;
            return String.format("%d minutes", minutes);
        }

        private String extractRouteDetails(String jsonResponse, String transportMeans) {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject route = json.getAsJsonArray("routes").get(0).getAsJsonObject();
            JsonObject leg = route.getAsJsonArray("legs").get(0).getAsJsonObject();

            // Extract distance and duration
            long distance = leg.getAsJsonObject("distance").get("value").getAsLong();
            long duration = leg.getAsJsonObject("duration").get("value").getAsLong();
            String convertedDistance = convertDistance(distance);
            String convertedDuration = convertDuration(duration);

            // Extract start and end addresses
            String startAddress = leg.get("start_address").getAsString();
            String endAddress = leg.get("end_address").getAsString();

            // Extract steps (if available)
            JsonArray stepsArray = leg.getAsJsonArray("steps");
            JsonArray stepsDetails = new JsonArray();
            for (JsonElement stepElement : stepsArray) {
                JsonObject step = stepElement.getAsJsonObject();
                JsonObject stepDetails = new JsonObject();
                stepDetails.addProperty("estimatedDistance", step.getAsJsonObject("distance").get("text").getAsString());
                stepDetails.addProperty("estimatedTime", step.getAsJsonObject("duration").get("text").getAsString());
                stepDetails.addProperty("instruction", step.get("html_instructions").getAsString());
                stepsDetails.add(stepDetails);
            }

            // Create the route details object
            JsonObject details = new JsonObject();
            details.addProperty("transportMeans", transportMeans);
            details.addProperty("estimatedDistance", convertedDistance);
            details.addProperty("estimatedTime", convertedDuration);
            details.addProperty("startLocation", startAddress);
            details.addProperty("targetLocation", endAddress);
            details.add("RouteInstructionSteps", stepsDetails);
            return details.toString();
        }

    }

    private static String extractParameter(String query, String parameter) {
        String[] parts = query.split("&");
        for (String part : parts) {
            if (part.startsWith(parameter + "=")) {
                return part.substring(parameter.length() + 1);
            }
        }
        return "";
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
//    	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBody.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream responseBodyStream = exchange.getResponseBody()) {
            responseBodyStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        String errorResponse = "{\"error\": \"" + errorMessage + "\"}";
        sendResponse(exchange, statusCode, errorResponse);
    }
}

