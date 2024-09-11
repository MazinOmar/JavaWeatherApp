import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.net.URL;
import org.json.simple.JSONArray;

//retrieve weather data from APi - this backend logic will fetch the latest
//weather data from the external API and return it. The GUI will display this data
// to the user
public class WeatherApp {
    //fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using the geoocation API
        JSONArray locationData = getLocationData(locationName);

        //extrct long and lat data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude+ "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FChicago";

        try{

            //call api and get a reposnse
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 means that the conneciton was succesful
            if(conn.getResponseCode()!=200) {

                System.out.println("Error: Could not connect to API");
                return null;
            }

            //store resulting JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()) {
                //read and store into builder
                resultJson.append(scanner.nextLine());
            }
            //close scanner
            scanner.close();

            //close url connection
            conn.disconnect();

            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrive hourly weather data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //we want to get the current hour's data
            //so we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long)weathercode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather JSON data we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;







        }catch(Exception e){
            e.printStackTrace();


                return null;
            }




    }
   //retrieves geograpghic coordinates for given loaction name
    public static JSONArray getLocationData(String loactionName){
        //replace any whitespace in location name to + to adhere to APIs request format
        loactionName = loactionName.replaceAll(" ","+");

        //build API url withlocation parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                loactionName + "&count=10&language=en&format=json";

        try{
//call API and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means succesfull connection
            if(conn.getResponseCode() !=200){
                System.out.println("Error: Could not Connect to API");
                return null;
            }else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our string builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //pass the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj= (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generated from the loaction name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;

            }



        }catch(Exception e){
            e.printStackTrace();
        }
        //couldnt find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;
        }catch (IOException e){
            e.printStackTrace();
        }

        //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
    String currentTime = getCurrentTime();

    //iterate through the timelist and see which one matches our current time
        for(int i=0; i< timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return the Index
                return i;
            }
        }


    return 0;
    }

    public static String getCurrentTime() {
    //get current data and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date to be 2023-09-02T00:00 (this is how its read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH':00'");

        //format and print the current date and time
        String formmatedDateTime = currentDateTime.format(formatter);
        return formmatedDateTime;
    }

   //convert the weather code to something more readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode ==0L){
            //clear
            weatherCondition="Clear";
        }else if(weathercode >=0L && weathercode <=3L){
            //cloudy
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67) ||(weathercode >= 80L && weathercode<=99L)) {
            //rain
            weatherCondition="Rainy";

        }else if(weathercode >=71L && weathercode <=77L){
            //snow
            weatherCondition="Snowy";
        }
        return weatherCondition;

    }
}
