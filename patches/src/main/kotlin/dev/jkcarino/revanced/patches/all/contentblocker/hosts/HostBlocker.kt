package dev.jkcarino.revanced.patches.all.contentblocker.hosts

import java.io.File
import java.net.URI

class HostBlocker {
    private val blockedHosts = hashSetOf<String>()

    private fun processLine(line: String) {
        val trimmed = line
            .substringBefore(COMMENT_CHAR)
            .trim()

        if (trimmed.isEmpty()) return

        val host = trimmed
            .substringAfter(SPACE_CHAR)
            .trim()

        if (host in RESERVED_HOSTNAMES) return

        val normalizedHost = normalizeHost(host)
        if (normalizedHost != null) {
            blockedHosts.add(normalizedHost)
        }
    }

    private fun normalizeHost(input: String): String? {
        if (input.isEmpty() || input.length > MAX_DOMAIN_LENGTH) return null

        val host = runCatching { extractHost(input) }
            .getOrNull()
            ?: return null

        if (host.length > MAX_DOMAIN_LENGTH
            || host.startsWith(DOT_CHAR)
            || host.endsWith(DOT_CHAR)
        ) return null

        val parts = host.split(DOT_CHAR)
        if (parts.size < MIN_PARTS || parts.size > MAX_PARTS) return null

        for (part in parts) {
            if (part.length > MAX_PARTS_LENGTH) return null
        }

        return host.lowercase()
    }

    fun parse(file: File) {
        file.forEachLine { line ->
            processLine(line)
        }
    }

    fun extractHost(input: String): String {
        val urlWithScheme = if (input.contains("://")) input else "http://$input"
        val host = URI.create(urlWithScheme).host
        return host
    }

    fun isBlocked(input: String): Boolean {
        val normalizedHost = normalizeHost(input)
        return normalizedHost != null && blockedHosts.contains(normalizedHost)
    }

    fun clear() {
        blockedHosts.clear()
    }

    private companion object {
        private const val COMMENT_CHAR = '#'
        private const val SPACE_CHAR = ' '
        private const val DOT_CHAR = '.'
        private const val MAX_DOMAIN_LENGTH = 253
        private const val MAX_PARTS_LENGTH = 63
        private const val MIN_PARTS = 2
        private const val MAX_PARTS = 127
        private val RESERVED_HOSTNAMES = setOf(
            "localhost",
            "localhost.localdomain",
            "local",
            "broadcasthost",

            // IPv4
            "127.0.0.1",
            "0.0.0.0",

            // IPv6
            "::1",
        )
    }
}
