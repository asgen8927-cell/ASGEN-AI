package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class ScrapedMetadata(
    val title: String,
    val description: String,
    val imageUrl: String,
    val domain: String
)

class LinkScraperService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
) {
    suspend fun scrapeUrl(rawUrl: String): Result<ScrapedMetadata> = withContext(Dispatchers.IO) {
        try {
            var formattedUrl = rawUrl.trim()
            if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                formattedUrl = "https://$formattedUrl"
            }

            val uri = URI(formattedUrl)
            val host = uri.host?.removePrefix("www.") ?: "link"

            val request = Request.Builder()
                .url(formattedUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: Could not fetch webpage"))
            }

            val html = response.body?.string() ?: ""

            val title = extractMetaTag(html, "og:title")
                ?: extractMetaTag(html, "twitter:title")
                ?: extractHtmlTag(html, "title")
                ?: host

            val description = extractMetaTag(html, "og:description")
                ?: extractMetaTag(html, "twitter:description")
                ?: extractMetaTag(html, "description")
                ?: "Link preview for $formattedUrl"

            var imageUrl = extractMetaTag(html, "og:image")
                ?: extractMetaTag(html, "twitter:image")
                ?: extractMetaTag(html, "image")
                ?: ""

            // Resolve relative image URLs if present
            if (imageUrl.isNotBlank() && !imageUrl.startsWith("http")) {
                imageUrl = try {
                    uri.resolve(imageUrl).toString()
                } catch (e: Exception) {
                    imageUrl
                }
            }

            Result.success(
                ScrapedMetadata(
                    title = cleanHtmlEntities(title),
                    description = cleanHtmlEntities(description),
                    imageUrl = imageUrl,
                    domain = host
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractMetaTag(html: String, propertyName: String): String? {
        // Match <meta property="og:title" content="..." /> or <meta name="description" content="..." />
        val pattern1 = Pattern.compile(
            """<meta\s+[^>]*(?:property|name)=["']${Pattern.quote(propertyName)}["']\s+[^>]*content=["']([^"']*)["']""",
            Pattern.CASE_INSENSITIVE
        )
        val matcher1 = pattern1.matcher(html)
        if (matcher1.find()) {
            return matcher1.group(1)?.trim()
        }

        // Match flipped attributes: content="..." property="og:title"
        val pattern2 = Pattern.compile(
            """<meta\s+[^>]*content=["']([^"']*)["']\s+[^>]*(?:property|name)=["']${Pattern.quote(propertyName)}["']""",
            Pattern.CASE_INSENSITIVE
        )
        val matcher2 = pattern2.matcher(html)
        if (matcher2.find()) {
            return matcher2.group(1)?.trim()
        }

        return null
    }

    private fun extractHtmlTag(html: String, tagName: String): String? {
        val pattern = Pattern.compile("<$tagName[^>]*>(.*?)</$tagName>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
        val matcher = pattern.matcher(html)
        if (matcher.find()) {
            return matcher.group(1)?.trim()
        }
        return null
    }

    private fun cleanHtmlEntities(text: String): String {
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .trim()
    }
}
