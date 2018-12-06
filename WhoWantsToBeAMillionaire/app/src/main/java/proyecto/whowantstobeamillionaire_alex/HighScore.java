package proyecto.whowantstobeamillionaire_alex;

public class HighScore implements Comparable<HighScore>{

    String name;
    String scoring;
    String longitude;
    String latitude;

    public HighScore() {
    }

    public HighScore (String name, String scoring, String longitude, String latitude) {
        this.name = name;
        this.scoring = scoring;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    @Override
    public int compareTo(HighScore o) {

        if (Integer.parseInt(this.getScoring()) > Integer.parseInt(o.getScoring())) {
            return 1;
        }
        else if (Integer.parseInt(this.getScoring()) < Integer.parseInt(o.getScoring())) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScoring() {
        return scoring;
    }

    public void setScoring(String scoring) {
        this.scoring = scoring;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
