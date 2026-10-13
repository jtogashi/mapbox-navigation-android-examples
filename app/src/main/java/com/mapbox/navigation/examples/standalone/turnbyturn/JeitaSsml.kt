package com.mapbox.navigation.examples.standalone.turnbyturn

import java.text.Normalizer

class JeitaSsml(
    val jeitaSsml: String
) {
    private val jeitaRegex = """<phoneme alphabet="jeita" ph="([^<>]+)">([^<>]+)</phoneme>""".toRegex()

    fun stripped(): String {
        return jeitaRegex.replace(jeitaSsml) { match ->
            val phoneme = match.groups[1]!!.value
            phoneme
                .replace("[ :]".toRegex(), ",")
                .replace("""[\x21-\x7E&&[^,]]""".toRegex(), "")
        }
    }

    private val breakRegex = "[ :]".toRegex()
    private val asciiRegex = """[\x21-\x7E&&[^',]]""".toRegex()

    fun polly(): String {
        if (!jeitaSsml.startsWith("<speak><amazon:effect")) {
            return jeitaSsml
        }

        return jeitaRegex.replace(jeitaSsml) { match ->
            val phoneme = match.groups[1]!!.value
            val polly = phoneme
                .replace("&apos;", "'")
                .replace("&quot;", "'")
                .replace("*", "'")
                .replace(breakRegex, ",")
                .replace(asciiRegex, "")
                .replace("'", "&apos;")

            val fullwidth = toFullwidthKana(polly)
            val converted = "<phoneme alphabet=\"x-amazon-pron-kana\" ph=\"$fullwidth\">${match.groups[2]!!.value}</phoneme>"
            return@replace converted
        }
    }

    private fun toFullwidthKana(from: String): String {
        val sb = StringBuilder(from.length)
        for (ch in from) {
            when (ch.code) {
                // halfwidth kana -> fullwidth
                in 0xFF61..0xFF9D -> sb.append(Normalizer.normalize(ch.toString(), Normalizer.Form.NFKC))
                // halfwidth voiced accent mark -> combining voiced accent mark
                0xFF9E -> sb.append('\u3099')
                // halfwidth semi-voiced accent mark -> combining semi-voiced accent mark
                0xFF9F -> sb.append('\u309A')
                else -> sb.append(ch)
            }
        }
        return Normalizer.normalize(sb, Normalizer.Form.NFC)
    }
}
