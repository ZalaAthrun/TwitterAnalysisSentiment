package entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Word {
    private Sentiment sentiment;
    private String content;

    public Word(Sentiment sentiment, String content) {
        this.sentiment = sentiment;
        this.content = content;
    }
}
