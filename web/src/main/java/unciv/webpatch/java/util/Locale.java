package unciv.webpatch.java.util;

/** Adds Locale.forLanguageTag to TeaVM's Locale. Handles the simple "ll", "ll-CC" and "ll-CC-variant" tags Unciv uses. */
public final class Locale {
    public static java.util.Locale forLanguageTag(String languageTag) {
        String[] parts = languageTag.replace('_', '-').split("-");
        String language = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1] : "";
        String variant = parts.length > 2 ? parts[2] : "";
        if (language.equals("und")) language = "";
        return new java.util.Locale(language, country, variant);
    }
}
