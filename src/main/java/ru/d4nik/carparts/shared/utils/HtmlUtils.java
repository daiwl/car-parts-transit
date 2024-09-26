package ru.d4nik.carparts.shared.utils;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlUtils {

    private static final Safelist COMMON_WHITELIST;

    static {
        COMMON_WHITELIST = new Safelist();
        COMMON_WHITELIST.addTags("i", "b", "br", "div", "li", "ol", "p", "ul");
    }

    private HtmlUtils() {
        // Запрет создания экземпляра
    }

    /**
     * Выделяет чистый текст из строки и удаляет всю HTML-разметку, за исключением тегов из белого списка.
     *
     * @param raw       Строка, которая может содержать HTML-разметку
     * @param whitelist Список тэгов, которые не следует удалять из текста
     * @return Чистый текст
     */
    @Nullable
    public static String clean(@Nullable String raw, @Nonnull Safelist whitelist) {
        return (null != raw) ? Jsoup.clean(raw, whitelist) : null;
    }

    /**
     * Выделяет чистый текст из строки и удаляет всю HTML-разметку, за исключением основных структурных тегов.
     *
     * @param raw Строка, которая может содержать HTML-разметку
     * @return Чистый текст
     */
    @Nullable
    public static String clean(@Nullable String raw) {
        return clean(raw, COMMON_WHITELIST);
    }

    /**
     * Выделяет чистый текст из строки и удаляет всю HTML-разметку.
     *
     * @param raw Строка, которая может содержать HTML-разметку
     * @return Чистый текст
     */
    @Nullable
    public static String cleanAll(@Nullable String raw) {
        return (null != raw) ? Jsoup.clean(raw, Safelist.none()) : null;
    }
}

