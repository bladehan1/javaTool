package reflect;

import java.util.Locale;
import static java.lang.System.out;

public class Deet<T> {
    private boolean testDeet(Locale l) {
        // getISO3Language() may throw a MissingResourceException
        out.format("Locale = %s, ISO Language Code = %s%n", l.getDisplayName(), l.getISO3Language());
        return true;
    }
    private int testFoo(Locale l) { return 0; }
    private boolean testBar() { return true; }
}