package contracts.i18n

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class LanguageCatalogTest {

    @Test
    fun `should contain exactly 22 languages`() {
        assertEquals(22, LanguageCatalog.ALL.size)
    }

    @Test
    fun `should find English by code`() {
        val lang = LanguageCatalog.findByCode("en")
        assertNotNull(lang)
        assertEquals("English", lang!!.name)
        assertEquals("en-US", lang.localeTag)
    }

    @Test
    fun `should find French by code`() {
        val lang = LanguageCatalog.findByCode("fr")
        assertNotNull(lang)
        assertEquals("Français", lang!!.nativeName)
        assertEquals("fr-FR", lang.localeTag)
    }

    @Test
    fun `should find Arabic with RTL flag`() {
        val lang = LanguageCatalog.findByCode("ar")
        assertNotNull(lang)
        assertTrue(lang!!.rtl)
        assertEquals("ar-SA", lang.localeTag)
    }

    @Test
    fun `should find Urdu with RTL flag`() {
        val lang = LanguageCatalog.findByCode("ur")
        assertNotNull(lang)
        assertTrue(lang!!.rtl)
        assertEquals("ur-PK", lang.localeTag)
    }

    @Test
    fun `should return null for unknown code`() {
        val lang = LanguageCatalog.findByCode("xx")
        assertNull(lang)
    }

    @Test
    fun `should return all supported codes`() {
        val codes = LanguageCatalog.supportedCodes()
        assertEquals(22, codes.size)
        assertTrue(codes.containsAll(listOf("en", "zh", "hi", "es", "fr", "ar", "bn", "pt", "ru", "ur")))
        assertTrue(codes.containsAll(listOf("it", "nl", "de", "el", "tr", "vi", "th", "id", "ko", "ja", "sr", "fa")))
    }

    @Test
    fun `should have unique codes`() {
        val codes = LanguageCatalog.ALL.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `should have Chinese Mandarin with correct native name`() {
        val lang = LanguageCatalog.findByCode("zh")
        assertNotNull(lang)
        assertEquals("中文", lang!!.nativeName)
        assertEquals("zh-CN", lang.localeTag)
    }

    @Test
    fun `should have Hindi with correct native name`() {
        val lang = LanguageCatalog.findByCode("hi")
        assertNotNull(lang)
        assertEquals("हिन्दी", lang!!.nativeName)
    }

    @Test
    fun `should find Persian with RTL flag`() {
        val lang = LanguageCatalog.findByCode("fa")
        assertNotNull(lang)
        assertEquals("Persian", lang!!.name)
        assertEquals("فارسی", lang.nativeName)
        assertTrue(lang.rtl)
        assertEquals("fa-IR", lang.localeTag)
    }

    @Test
    fun `should find Italian by code`() {
        val lang = LanguageCatalog.findByCode("it")
        assertNotNull(lang)
        assertEquals("Italiano", lang!!.nativeName)
        assertEquals("it-IT", lang.localeTag)
    }

    @Test
    fun `should find Japanese by code`() {
        val lang = LanguageCatalog.findByCode("ja")
        assertNotNull(lang)
        assertEquals("日本語", lang!!.nativeName)
        assertEquals("ja-JP", lang.localeTag)
    }

    @Test
    fun `should find Korean by code`() {
        val lang = LanguageCatalog.findByCode("ko")
        assertNotNull(lang)
        assertEquals("한국어", lang!!.nativeName)
        assertEquals("ko-KR", lang.localeTag)
    }

    @Test
    fun `should have exactly 3 RTL languages`() {
        val rtl = LanguageCatalog.ALL.filter { it.rtl }.map { it.code }.toSet()
        assertEquals(setOf("ar", "ur", "fa"), rtl)
    }

    @Test
    fun `should expose talaria school target languages`() {
        val talaria = setOf(
            "fr", "en", "zh", "hi", "es", "ar", "bn", "pt", "ru", "ur",
            "it", "nl", "de", "el", "tr", "vi", "th", "id", "ko", "ja", "sr", "fa"
        )
        assertEquals(talaria, LanguageCatalog.supportedCodes())
    }
}
