package dsic.online.communication.classes;

public class WeatherInformation {
    private WeatherInformationParaGSON gsonInfo = null;
    private WeatherInformationParaJSON jsonInfo = null;

    public static final int GSON = 0;
    public static final int JSON = 1;
    public static final int EMPTY = -1;

    private int format = EMPTY;

    public WeatherInformation() {
        this.format = EMPTY;
    }

    public WeatherInformation(WeatherInformationParaGSON info) {
        this.setInformation(info);
    }

    public WeatherInformation(WeatherInformationParaJSON info) {
        this.setInformation(info);
    }

    public void setInformation(WeatherInformationParaGSON info) {
        this.format = GSON;
        this.gsonInfo = info;
        this.jsonInfo = null;
    }

    public void setInformation(WeatherInformationParaJSON info) {
        this.format = JSON;
        this.jsonInfo = info;
        this.gsonInfo = null;
    }

    public Object getInformation() {
        if (this.format == GSON) return this.gsonInfo;
        else if (this.format == JSON) return this.jsonInfo;
        else return null;
    }


    public int getFormat() {
        return this.format;
    }

    public String getIconName() {
        if (this.format == GSON) return gsonInfo.getWeather().get(0).getIcon();
        else if (this.format == JSON) return jsonInfo.currentCondition.getIcon();
        else return null;
    }

    public String getCity() {
        if (this.format == GSON) return gsonInfo.getName();
        else if (this.format == JSON) return jsonInfo.location.getCity();
        else return null;
    }

    public String getCountry() {
        if (this.format == GSON) return gsonInfo.getSys().getCountry();
        else if (this.format == JSON) return jsonInfo.location.getCountry();
        else return null;
    }

    public String getCondition() {
        if (this.format == GSON) return gsonInfo.getWeather().get(0).getMain();
        else if (this.format == JSON) return jsonInfo.currentCondition.getCondition();
        else return null;
    }

    public String getDescription() {
        if (this.format == GSON) return gsonInfo.getWeather().get(0).getDescription();
        else if (this.format == JSON) return jsonInfo.currentCondition.getDescr();
        else return null;
    }

    public String getTemperature() {
        if (this.format == GSON) return String.valueOf(Math.round(gsonInfo.getMain().getTemp()));
        else if (this.format == JSON)
            return String.valueOf(Math.round(jsonInfo.temperature.getTemp()));
        else return null;
    }

    public String getHumidity() {
        if (this.format == GSON) return String.valueOf(gsonInfo.getMain().getHumidity());
        else if (this.format == JSON)
            return String.valueOf(jsonInfo.currentCondition.getHumidity());
        else return null;
    }

    public double getPressure() {
        if (this.format == GSON) return gsonInfo.getMain().getPressure();
        else if (this.format == JSON) return jsonInfo.currentCondition.getPressure();
        else return 0;
    }

    public double getWindSpeed() {
        if (this.format == GSON) return gsonInfo.getWind().getSpeed();
        else if (this.format == JSON) return jsonInfo.wind.getSpeed();
        else return 0;
    }

    public double getWindDeg() {
        if (this.format == GSON) return gsonInfo.getWind().getDeg();
        else if (this.format == JSON) return jsonInfo.wind.getDeg();
        else return 0;
    }

}
