package nz.ac.canterbury.seng302.portfolio.utils;

import java.util.Arrays;
import java.util.Optional;

public enum SprintColor {
    DEFAULT_BLUE("blue", 0),
    GREEN("green",1),
    PURPLE("purple", 2),
    DARK_SLATE_GREY("darkSlateGrey", 3),
    FIREBRICK("firebrick", 4),
    VIOLET_RED("mediumVioletRed", 5),
    SEA_GREEN("mediumSeaGreen", 6),
    ORANGE_RED("orangeRed", 7);

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
