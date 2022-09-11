package nz.ac.canterbury.seng302.portfolio.utils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum containing all possible sprint colors
 */
public enum SprintColor {
    WHITE("white", 0),
    BLUE("blue",1),
    SYYBLUE("skyblue", 2),
    PURPLE("purple", 3),
    ORANGE("orange", 4),
    GREEN("green", 5),
    PINK("pink", 6),
    NAVY("navy", 7);

    private String color;
    private int labelNum;
    SprintColor(String color, int labelNum) {
        this.color = color;
        this.labelNum = labelNum;
    }
    public String getColor() {
        return this.color;
    }
    public static SprintColor valueOf(int value) {
        Optional<SprintColor> color = Arrays.stream(values())
                .filter(SprintColor -> SprintColor.labelNum == value)
                .findFirst();

        return color.orElse(null);
    }
}
