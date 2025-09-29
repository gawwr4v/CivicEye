data class Article(
    val title: String,
    val description: String,
    val content: String, // full text for detail screen
    val imageRes: Int? = null // optional thumbnail
)
