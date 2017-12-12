package entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Sentiment {
    private String description;

    public Sentiment(String description, String label) {
        this.description = description;
        this.label = label;
    }

    private String label;
}
