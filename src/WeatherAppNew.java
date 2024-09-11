import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import org.json.simple.JSONObject;
import java.awt.event.ActionEvent;


public class WeatherAppNew extends JFrame {
    private JSONObject weatherData;
    public WeatherAppNew() {
        //setup our GUI and add a title
        super("Weather App");

        //configure GUI to end the programs proccess once its been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set the size of our gui (in pixels)
        setSize(450, 640);

        //load out gui at the center of the screen
        setLocationRelativeTo(null);

        //make our layout manager null to manually position our compoenents to our GUI
        setLayout(null);

        //prevent any resize of our GUI
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        //search field
        JTextField searchTextField = new JTextField();

        //set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        //change the font Style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);



        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionsDesc = new JLabel("Cloudy");
        weatherConditionsDesc.setBounds(0,405,450, 36);
        weatherConditionsDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionsDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionsDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        //windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        //change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
       searchButton.addActionListener(new ActionListener(){
           @Override
                   public void actionPerformed(ActionEvent e){
               //get location from user
            String userInput = searchTextField.getText();

            //validate input - remove whitespace to ensure non-empty text
            if(userInput.replaceAll("\\s", "").length() <= 0){
                return;
            }
            //retrieve weather data
            weatherData = WeatherApp.getWeatherData(userInput);

            //update gui

            //update weather image
            String weatherCondition = (String) weatherData.get("weather_condition");

            //depending on the weather condition, we will update the weather image that corresponds with the condition
            switch(weatherCondition){
                case "Clear":
                    weatherConditionImage.setIcon(loadImage("src/assets/sun.png"));
                    break;
                case "Cloud":
                    weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                    break;
                case "Rainy":
                    weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                    break;
                case "Snowy":
                    weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                    break;

            }
            //update temperature text
            double temperature = (double) weatherData.get("temperature");
            temperatureText.setText(temperature + " C");

            //update weather condition text
            weatherConditionsDesc.setText(weatherCondition);

            //update humidity text
            long humidity = (long) weatherData.get("humidity");
            humidityText.setText("<html><b>Humidity</b> "+ humidity + "%</html>");

            //update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> "+ windspeed + "km/h</html>");


            }
       });


        add(searchButton);


    }

    //used to create images in our GUI components
    private ImageIcon loadImage(String resourcePath) {
        try {//read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //returns an image icon so our component can render it
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}