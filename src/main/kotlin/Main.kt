import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()

    wordsFile.forEachLine { it ->
        val line = it.split("|")
        dictionary.add(
            Word(
                original = line[0],
                translate = line[1],
                correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
            )
        )
    }
    println(dictionary.joinToString("\n"))
}