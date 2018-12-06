package proyecto.whowantstobeamillionaire_alex;
import java.util.List;

public class HighScoreList {

    private List<HighScore> scores;

    public HighScoreList(List<HighScore> highScores) {this.scores = highScores;}

    public List<HighScore> getScores() {
        return scores;
    }

    public void setScores(List<HighScore> scores) {
        this.scores = scores;
    }

    public int getCount(){ return this.scores.size();}
}
