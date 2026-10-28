package unciv.webemu.java.text;

import java.util.Comparator;
import java.util.Locale;

/**
 * Stand-in for java.text.Collator, which TeaVM lacks. Unciv uses it to sort names for display.
 * This one compares case-insensitively; real locale rules would need Intl.Collator.
 */
public class Collator implements Comparator<Object>, Cloneable {
    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;
    public static final int TERTIARY = 2;
    public static final int IDENTICAL = 3;

    private int strength = TERTIARY;

    protected Collator() {}

    public static Collator getInstance() { return new Collator(); }

    public static Collator getInstance(Locale locale) { return new Collator(); }

    public int compare(String source, String target) {
        int result = source.compareToIgnoreCase(target);
        return result != 0 || strength == PRIMARY ? result : source.compareTo(target);
    }

    @Override
    public int compare(Object o1, Object o2) { return compare((String) o1, (String) o2); }

    public boolean equals(String source, String target) { return compare(source, target) == 0; }

    public int getStrength() { return strength; }

    public void setStrength(int newStrength) { strength = newStrength; }

    public void setDecomposition(int decompositionMode) {}

    public int getDecomposition() { return 0; }

    @Override
    public Object clone() {
        Collator copy = new Collator();
        copy.strength = strength;
        return copy;
    }
}
