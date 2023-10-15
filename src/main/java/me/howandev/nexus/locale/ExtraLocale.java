package me.howandev.nexus.locale;

import java.util.Locale;

/**
 * The {@link ExtraLocale} class contains additional locales.
 */

@SuppressWarnings("unused")
public class ExtraLocale {
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale FRENCH = Locale.FRENCH;
    public static final Locale GERMAN = Locale.GERMAN;
    public static final Locale ITALIAN = Locale.ITALIAN;
    public static final Locale JAPANESE = Locale.JAPANESE;
    public static final Locale KOREAN = Locale.KOREAN;
    public static final Locale CHINESE = Locale.CHINESE;
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;
    public static final Locale TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE;
    public static final Locale ROOT = Locale.ROOT;
    public static final Locale SPANISH, DUTCH, DANISH, CZECH, GREEK, LATIN, BULGARIAN, AFRIKAANS, HINDI, HEBREW, POLISH, PORTUGUESE, FINNISH,
            SWEDISH, RUSSIAN, ROMANIAN, VIETNAMESE, THAI, TURKISH, UKRAINIAN, ARABIC, WELSH, NORWEGIAN_BOKMAL, NORWEGIAN_PANORSKA, HUNGARIAN;
    private static final Locale[] VALUES;

    static {
        SPANISH = new Locale("es");
        DUTCH = new Locale("nl");
        DANISH = new Locale("da");
        CZECH = new Locale("cs");
        GREEK = new Locale("el");
        LATIN = new Locale("la");
        BULGARIAN = new Locale("bg");
        AFRIKAANS = new Locale("af");
        HINDI = new Locale("hi");
        HEBREW = new Locale("he");
        POLISH = new Locale("pl");
        PORTUGUESE = new Locale("pt");
        FINNISH = new Locale("fi");
        SWEDISH = new Locale("sv");
        RUSSIAN = new Locale("ru");
        ROMANIAN = new Locale("ro");
        VIETNAMESE = new Locale("vi");
        THAI = new Locale("th");
        TURKISH = new Locale("tr");
        UKRAINIAN = new Locale("uk");
        ARABIC = new Locale("ar");
        WELSH = new Locale("cy");
        NORWEGIAN_BOKMAL = new Locale("nb");
        NORWEGIAN_PANORSKA = new Locale("nn");
        HUNGARIAN = new Locale("hu");

        VALUES = new Locale[]{
                ENGLISH,
                FRENCH,
                GERMAN,
                ITALIAN,
                JAPANESE,
                KOREAN,
                CHINESE,
                SIMPLIFIED_CHINESE,
                TRADITIONAL_CHINESE,
                ROOT,
                SPANISH,
                DUTCH,
                DANISH,
                CZECH,
                GREEK,
                LATIN,
                BULGARIAN,
                AFRIKAANS,
                HINDI,
                HEBREW,
                POLISH,
                PORTUGUESE,
                FINNISH,
                SWEDISH,
                RUSSIAN,
                ROMANIAN,
                VIETNAMESE,
                THAI,
                TURKISH,
                UKRAINIAN,
                ARABIC,
                WELSH,
                NORWEGIAN_BOKMAL,
                NORWEGIAN_PANORSKA,
                HUNGARIAN
        };
    }

    private ExtraLocale() {
        throw new IllegalStateException("ExtraLocale class should not be instantiated!");
    }

    /**
     * Quality-Of-Life method for getting all locale constants contained in {@link ExtraLocale}.
     *
     * @return Returns an array containing the locale constants, in the order they're declared.
     */
    public static Locale[] values() {
        return VALUES;
    }
}
