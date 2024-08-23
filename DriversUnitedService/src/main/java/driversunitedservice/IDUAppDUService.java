package driversunitedservice;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import driversunitedservice.JobOffers.JobOffer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
 
public class IDUAppDUService {
	private static void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
//		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "https://editor.swagger.io");
//		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); 
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBody.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream responseBodyStream = exchange.getResponseBody()) {
            responseBodyStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
//		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//    	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "https://editor.swagger.io");
//		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        String errorResponse = "{\"error\": \"" + errorMessage + "\"}";
        sendResponse(exchange, statusCode, errorResponse);
    }
 
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/authenticateAndRequestSimilarOffers", new AuthenticateUser());
        server.createContext("/submitSelectedJob", new submitJob());
        

        
        server.start();
        System.out.println("Server is running on port 8000");
    }
    
    static class submitJob implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
                return;
            }
        	
        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            System.out.println("Handling OPTIONS request for CORS preflight");
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            	
            	System.out.println("Handling OPTIONS request for CORS preflight");
            	
            	
                exchange.sendResponseHeaders(204, -1); // No Content response for preflight.
                exchange.close();
                return;
            }
            
        	String requestBody = new String(exchange.getRequestBody().readAllBytes());
            
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            
    
            for (String key : jsonObject.keySet()) {
            	if(!key.equals("offerId")) {
            		sendErrorResponse(exchange, 400, "Invalid request format");
                    return;
            	}
            }
            
            String offerId = jsonObject.get("offerId").getAsString();
            
            int index = 100;
            
            JobOffer[] s = JobOffers.getJobOffers();
            
            for(int i = 0;i < s.length;i++) {
            	if(s[i].getId().equals(offerId)){
            		index = i;
            		break;
            	}
            }
            
            String origin = "";
            String destination = "";
            String mode = "";
            if(index == 100) {
            	sendResponse(exchange, 200, "JobOfferId invalid");
            	
            }
            else {
            	origin = s[index].getStartLocation(); 
                destination = s[index].getEndLocation(); 
                mode = s[index].getTransport();
          	
            }
            
           String res = getRouteDetails(origin,destination,mode); 
           
           System.out.println(res + " "); 
           JsonObject jsonObj = new Gson().fromJson(res, JsonObject.class);
           
           jsonObj.addProperty("status", "Valid JobOffer ID");
            
           sendResponse(exchange, 200, "Valid JobOffer ID \n"+"JobOffer ID Submitted Successfully \n"+jsonObj.toString());
        }
        
//        private void handleOptionsRequest(HttpExchange exchange) throws IOException {
//        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "https://editor.swagger.io");
//            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//            // Respond with HTTP OK status
////            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
//            exchange.sendResponseHeaders(204, -1);
//            exchange.close(); 
//        }
//        	
        
    }
    
    private static String getRouteDetails(String origin, String destination, String mode) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8001/routePlanner?startLocation=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                            "&targetLocation=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                            "&transportMeans=" + URLEncoder.encode(mode, StandardCharsets.UTF_8)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            
            return responseBody;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return "Error fetching route details";
        }
    }
     
    static class AuthenticateUser implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	
        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
                return;
            }
//
//            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
//                handleOptionsRequest(exchange);
//                return;
//            }
            
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content response for preflight.
                exchange.close();
                return;
            }
//            
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            JsonObject jsonObject = new Gson().fromJson(requestBody, JsonObject.class);
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();

            boolean isValidUser = validateUser(username, password);
            
            if (isValidUser) {
                JobOffersResponse(exchange);
                JsonObject successResponse = new JsonObject();
                successResponse.addProperty("message", "User is authenticated successfully.");
                sendResponse(exchange, 200, successResponse.toString());
            } else {
                sendErrorResponse(exchange, 401, "Unauthorized: Invalid username or password");
            }
        }
        
//        private void handleOptionsRequest(HttpExchange exchange) throws IOException {
//        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "https://editor.swagger.io");
//            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//            // Respond with HTTP OK status
////            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
//            exchange.sendResponseHeaders(204, -1);
//            exchange.close(); 
//        }
//        
        private boolean validateUser(String username, String password) {
            return username.equals("admin") && password.equals("admin@123");
        }
 
        private void JobOffersResponse(HttpExchange exchange) throws IOException {
        	
        	JsonObject successResponse = new JsonObject();
            successResponse.addProperty("message", "User is authenticated successfully.");
            
            JsonArray jobOfferList = new JsonArray();
            JsonObject jobOffer1 = new JsonObject();
            jobOffer1.addProperty("jobId", "1");
            jobOffer1.addProperty("startLocation", "5 Shires Ln, Leicester LE1 4AN");
            jobOffer1.addProperty("endLocation", "University Rd, Leicester LE1 7RH");
            jobOffer1.addProperty("distance", "1.6 miles");
            jobOffer1.addProperty("deliveryPartner", "Uber");

            JsonObject jobOffer2 = new JsonObject();
            jobOffer2.addProperty("jobId", "2");
            jobOffer2.addProperty("startLocation", "115 Bath Ln, Leicester LE3 5EU");
            jobOffer2.addProperty("endLocation", "Blaby golf centre, Lutterworth Rd, Blaby, Leicester LE8 4DP");
            jobOffer2.addProperty("distance", "6.5 miles");
            jobOffer2.addProperty("deliveryPartner", "Deliveroo");

            JsonObject jobOffer3 = new JsonObject();
            jobOffer3.addProperty("jobId", "3");
            jobOffer3.addProperty("startLocation", "1 Kildare St, Leicester LE1 3YH");
            jobOffer3.addProperty("endLocation", "Exploration Dr, Leicester LE4 5NS");
            jobOffer3.addProperty("distance", "2.1 miles");
            jobOffer3.addProperty("deliveryPartner", "Just Eat");

            JsonObject jobOffer4 = new JsonObject();
            jobOffer4.addProperty("jobId", "4");
            jobOffer4.addProperty("startLocation", "7 Peacock Ln, Leicester LE1 5PZ");
            jobOffer4.addProperty("endLocation", "Leicester Rd, Oadby, Leicester LE2 4AL");
            jobOffer4.addProperty("distance", "3.7 miles");
            jobOffer4.addProperty("deliveryPartner", "Zomato");

            JsonObject jobOffer5 = new JsonObject();
            jobOffer5.addProperty("jobId", "5");
            jobOffer5.addProperty("startLocation", "Market Pl, Leicester LE1 5GG");
            jobOffer5.addProperty("endLocation", "45 High St, Lutterworth LE17 4AY");
            jobOffer5.addProperty("distance", "16.4 miles");
            jobOffer5.addProperty("deliveryPartner", "DoorDash");
            
            jobOfferList.add(successResponse);
            
            jobOfferList.add(jobOffer1);
            jobOfferList.add(jobOffer2);
            jobOfferList.add(jobOffer3);
            jobOfferList.add(jobOffer4);
            jobOfferList.add(jobOffer5);
            
            String jsonResponse = jobOfferList.toString();
            sendResponse(exchange, 200, jsonResponse);
        }
    }           
           
}