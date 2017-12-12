package entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class WordValue {
    private int negative;

    public WordValue(){}

    public WordValue(int negative, int positive, int neutral) {
        this.negative = negative;
        this.positive = positive;
        this.neutral = neutral;
    }

    private int positive;
    private int neutral;
}
