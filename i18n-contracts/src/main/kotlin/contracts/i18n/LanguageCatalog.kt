package contracts.i18n

object LanguageCatalog {

    val ALL: List<SupportedLanguage> = listOf(
        SupportedLanguage(code = "en", name = "English", nativeName = "English", localeTag = "en-US"),
        SupportedLanguage(code = "zh", name = "Chinese Mandarin", nativeName = "中文", localeTag = "zh-CN"),
        SupportedLanguage(code = "hi", name = "Hindi", nativeName = "हिन्दी", localeTag = "hi-IN"),
        SupportedLanguage(code = "es", name = "Spanish", nativeName = "Español", localeTag = "es-ES"),
        SupportedLanguage(code = "fr", name = "French", nativeName = "Français", localeTag = "fr-FR"),
        SupportedLanguage(code = "ar", name = "Arabic Standard", nativeName = "العربية", rtl = true, localeTag = "ar-SA"),
        SupportedLanguage(code = "bn", name = "Bengali", nativeName = "বাংলা", localeTag = "bn-BD"),
        SupportedLanguage(code = "pt", name = "Portuguese", nativeName = "Português", localeTag = "pt-BR"),
        SupportedLanguage(code = "ru", name = "Russian", nativeName = "Русский", localeTag = "ru-RU"),
        SupportedLanguage(code = "ur", name = "Urdu", nativeName = "اردو", rtl = true, localeTag = "ur-PK"),
        SupportedLanguage(code = "it", name = "Italian", nativeName = "Italiano", localeTag = "it-IT"),
        SupportedLanguage(code = "nl", name = "Dutch", nativeName = "Nederlands", localeTag = "nl-NL"),
        SupportedLanguage(code = "de", name = "German", nativeName = "Deutsch", localeTag = "de-DE"),
        SupportedLanguage(code = "el", name = "Greek", nativeName = "Ελληνικά", localeTag = "el-GR"),
        SupportedLanguage(code = "tr", name = "Turkish", nativeName = "Türkçe", localeTag = "tr-TR"),
        SupportedLanguage(code = "vi", name = "Vietnamese", nativeName = "Tiếng Việt", localeTag = "vi-VN"),
        SupportedLanguage(code = "th", name = "Thai", nativeName = "ไทย", localeTag = "th-TH"),
        SupportedLanguage(code = "id", name = "Indonesian", nativeName = "Bahasa Indonesia", localeTag = "id-ID"),
        SupportedLanguage(code = "ko", name = "Korean", nativeName = "한국어", localeTag = "ko-KR"),
        SupportedLanguage(code = "ja", name = "Japanese", nativeName = "日本語", localeTag = "ja-JP"),
        SupportedLanguage(code = "sr", name = "Serbian", nativeName = "Srpski", localeTag = "sr-RS"),
        SupportedLanguage(code = "fa", name = "Persian", nativeName = "فارسی", rtl = true, localeTag = "fa-IR")
    )

    fun findByCode(code: String): SupportedLanguage? = ALL.find { it.code == code }

    fun supportedCodes(): Set<String> = ALL.map { it.code }.toSet()
}
