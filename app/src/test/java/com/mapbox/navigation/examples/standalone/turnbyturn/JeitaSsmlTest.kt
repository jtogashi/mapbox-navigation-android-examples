package com.mapbox.navigation.examples.standalone.turnbyturn

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class JeitaSsmlTest {
    @Parameterized.Parameter(0)
    lateinit var data: Case

    @Test
    fun polly() {
        assertEquals(
            data.expectedPolly,
            JeitaSsml(data.input).polly()
        )
    }

    @Test
    fun stripped() {
        assertEquals(
            data.expectedStripped,
            JeitaSsml(data.input).stripped()
        )
    }

    class Case(
        val name: String,
        val input: String,
        val expectedPolly: String,
        val expectedStripped: String
    ) {
        override fun toString() = name
    }

    companion object {
        private const val JEITA_TAG_1_WITH_APOS = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="jeita" ph="ﾌ%ｸ&apos;ｼﾏ ﾛｸ%">福島6</phoneme>を左方向です。</prosody></amazon:effect></speak>"""
        private const val JEITA_TAG_1_WITH_QUOT = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="jeita" ph="ﾅｷﾞ&ｻﾉｴ&quot;ｷ ﾀﾃﾔﾏﾏｴ">“渚の駅”たてやま前</phoneme>を右方向です。</prosody></amazon:effect></speak>"""
        private const val JEITA_TAG_1_WITH_ASTERISK = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="jeita" ph="ﾅ*;ｺﾞ&amp;ﾔ ｺｸ%ｻｲｶｲｷﾞ&amp;ｼﾞｮｰ:ｷ%ﾀ">名古屋国際会議場北</phoneme>を右方向です。</prosody></amazon:effect></speak>"""
        private const val JEITA_TAG_3 = """<speak><amazon:effect name="drc"><prosody rate="1.08"><phoneme alphabet="jeita" ph="ﾏｷﾉﾊﾗｲﾝﾀｰﾁｪﾝｼﾞ">牧之原IC</phoneme>を左方向、<phoneme alphabet="jeita" ph="ｼﾏﾀﾞ">島田</phoneme>, <phoneme alphabet="jeita" ph="ｸｰｺｰ">空港</phoneme>方面です。。その先400メートルで左方向です。</prosody></amazon:effect></speak>"""
        private const val JEITA_TAG_0 = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル直進です。</prosody></amazon:effect></speak>"""
        private const val SAY_AS_TAG_0 = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<say-as interpret-as="address">名古屋国際会議場北</say-as>を右方向です。</prosody></amazon:effect></speak>"""

        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun getData() = arrayOf(
            Case(
                name = "jeita tag x1 with &apos; accent -> converted",
                input = JEITA_TAG_1_WITH_APOS,
                expectedPolly = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="x-amazon-pron-kana" ph="フク&apos;シマ,ロク">福島6</phoneme>を左方向です。</prosody></amazon:effect></speak>""",
                expectedStripped = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、ﾌｸｼﾏ,ﾛｸを左方向です。</prosody></amazon:effect></speak>"""
            ),
            Case(
                name = "jeita tag x1 with &quot; accent -> converted",
                input = JEITA_TAG_1_WITH_QUOT,
                expectedPolly = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="x-amazon-pron-kana" ph="ナギサノエ&apos;キ,タテヤママエ">“渚の駅”たてやま前</phoneme>を右方向です。</prosody></amazon:effect></speak>""",
                expectedStripped = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、ﾅｷﾞｻﾉｴｷ,ﾀﾃﾔﾏﾏｴを右方向です。</prosody></amazon:effect></speak>"""
            ),
            Case(
                name = "jeita tag x1 with * accent -> converted",
                input = JEITA_TAG_1_WITH_ASTERISK,
                expectedPolly = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、<phoneme alphabet="x-amazon-pron-kana" ph="ナ&apos;ゴヤ,コクサイカイギジョー,キタ">名古屋国際会議場北</phoneme>を右方向です。</prosody></amazon:effect></speak>""",
                expectedStripped = """<speak><amazon:effect name="drc"><prosody rate="1.08">100メートル先、ﾅｺﾞﾔ,ｺｸｻｲｶｲｷﾞｼﾞｮｰ,ｷﾀを右方向です。</prosody></amazon:effect></speak>"""
            ),
            Case(
                name = "jeita tag x3 -> every tag converted",
                input = JEITA_TAG_3,
                expectedPolly = """<speak><amazon:effect name="drc"><prosody rate="1.08"><phoneme alphabet="x-amazon-pron-kana" ph="マキノハラインターチェンジ">牧之原IC</phoneme>を左方向、<phoneme alphabet="x-amazon-pron-kana" ph="シマダ">島田</phoneme>, <phoneme alphabet="x-amazon-pron-kana" ph="クーコー">空港</phoneme>方面です。。その先400メートルで左方向です。</prosody></amazon:effect></speak>""",
                expectedStripped = """<speak><amazon:effect name="drc"><prosody rate="1.08">ﾏｷﾉﾊﾗｲﾝﾀｰﾁｪﾝｼﾞを左方向、ｼﾏﾀﾞ, ｸｰｺｰ方面です。。その先400メートルで左方向です。</prosody></amazon:effect></speak>"""
            ),
            Case(
                name = "no jeita tag -> no change",
                input = JEITA_TAG_0,
                expectedPolly = JEITA_TAG_0,
                expectedStripped = JEITA_TAG_0
            ),
            Case(
                name = "say-as tag -> no change",
                input = SAY_AS_TAG_0,
                expectedPolly = SAY_AS_TAG_0,
                expectedStripped = SAY_AS_TAG_0
            ),
        )
    }
}
