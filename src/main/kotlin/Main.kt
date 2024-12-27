import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val dictionary = loadDictionary()
    var isExit = false

    while (!isExit) {
        println(
            """
            Меню: 
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent()
        )
        val input = readln()
        when (input) {
            "1" -> println("Выбран пункт \"Учить слова\"")
            "2" -> println("Выбран пункт \"Статистика\"")
            "0" -> isExit = true
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
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
    return dictionary
}