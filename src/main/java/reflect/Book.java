package reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import static java.lang.System.out;

enum Tweedle { DEE, DUM }

public class Book {
    public long chapters = 0;
    private String[] characters = {"Alice", "White Rabbit"};
    public Tweedle twin = Tweedle.DEE;

    public String[] getCharacters(){
        return this.characters;
    }
}