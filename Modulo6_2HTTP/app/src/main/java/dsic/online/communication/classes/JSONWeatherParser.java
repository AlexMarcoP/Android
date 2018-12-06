package dsic.online.communication.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONWeatherParser {

    public static WeatherInformationParaJSON getWeather(String data) throws JSONException {

        // Este método devolverá un objeto de tipo Weather que debe crearse y
        // rellenarse con la información suministrada por el servidor
        WeatherInformationParaJSON weatherInformationParaJSON = new WeatherInformationParaJSON();

        // data contiene la información remitida por el servidor
        // Creamos un objecto JSONObject a partir de los datos recibidos
        JSONObject jObj = new JSONObject(data);

        // Extraemos la informacion suministrada de jObj y la guardamos en weather

        //**********************************************************************
        // Información relativa a la localización actual
        // sobre la que se ha solicitado el tiempo
        // Obtenemos weather.location
        Location loc = new Location();

        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        weatherInformationParaJSON.location = loc;

        //**********************************************************************
        // Condiciones climáticas actuales
        // Obtenermos: weather.currentCondition

        // Obtener la información relativa al campo WEATHER (es un array)
        JSONArray jArr = jObj.getJSONArray("weather");
        // Aunque puede tener muchos valores, sólo utilizamos el primero
        JSONObject JSONWeather = jArr.getJSONObject(0);
        weatherInformationParaJSON.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weatherInformationParaJSON.currentCondition.setDescr(getString("description", JSONWeather));
        weatherInformationParaJSON.currentCondition.setCondition(getString("main", JSONWeather));
        weatherInformationParaJSON.currentCondition.setIcon(getString("icon", JSONWeather));
        JSONObject mainObj = getObject("main", jObj);
        weatherInformationParaJSON.currentCondition.setHumidity(getInt("humidity", mainObj));
        weatherInformationParaJSON.currentCondition.setPressure(getInt("pressure", mainObj));

        //**********************************************************************
        // Situación de la temperatura
        // weather.temperature
        weatherInformationParaJSON.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weatherInformationParaJSON.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weatherInformationParaJSON.temperature.setTemp(getFloat("temp", mainObj));

        //**********************************************************************
        // Situación del VIENTO
        // weather.wind
        JSONObject wObj = getObject("wind", jObj);
        weatherInformationParaJSON.wind.setSpeed(getFloat("speed", wObj));
        weatherInformationParaJSON.wind.setDeg(getFloat("deg", wObj));

        //**********************************************************************
        // Situación de las NUBES
        // weather.clouds
        JSONObject cObj = getObject("clouds", jObj);
        weatherInformationParaJSON.clouds.setPerc(getInt("all", cObj));

        return weatherInformationParaJSON;
    }

    //**********************************************************************
    // Métodos de ayuda para facilitar la extracción de datos

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getJSONObject(tagName);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
