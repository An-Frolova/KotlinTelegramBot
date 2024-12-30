import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val dictionary = loadDictionary()

    do {
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
            "1" -> learnWords(dictionary)
            "2" -> println(getStatistic(dictionary))
            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    } while (input != "0")
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

fun getStatistic(dictionary: List<Word>): String {
    val totalCount = dictionary.size
    val correctAnswersCount = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_TO_LEARN }.size
    val percent = ((correctAnswersCount.toDouble()) / (totalCount.toDouble()) * AS_DECIMAL).toInt()
    val statistic = "Выучено $correctAnswersCount из $totalCount слов | $percent%\n"
    return statistic
}

fun learnWords(dictionary: List<Word>) {
    do {
        val notLearnedList = dictionary.filter { word: Word -> word.correctAnswersCount < CORRECT_ANSWERS_TO_LEARN }
        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            return
        }
        val questionWords = notLearnedList.shuffled().take(COUNT_OF_WORDS_FOR_QUESTION).toMutableList()
        val word = questionWords.random()

        if (questionWords.size < COUNT_OF_WORDS_FOR_QUESTION && dictionary.size < COUNT_OF_WORDS_FOR_QUESTION) {
            println(
                "В словаре недостаточно слов! Минимальное количество слов, необходимое для изучения - " +
                        "$COUNT_OF_WORDS_FOR_QUESTION"
            )
            return
        } else if (questionWords.size < COUNT_OF_WORDS_FOR_QUESTION && dictionary.size > COUNT_OF_WORDS_FOR_QUESTION) {
            while (questionWords.size < COUNT_OF_WORDS_FOR_QUESTION) {
                val learnedWord = dictionary.random()
                if (learnedWord !in questionWords) {
                    questionWords.add(learnedWord)
                }
            }
            questionWords.shuffle()
        }

        println("\n${word.original}:")
        for (i in 0..<questionWords.size) {
            println("${i + 1} - ${questionWords[i].translate}")
        }
        val userAnswer = readln()
    } while (notLearnedList.isNotEmpty())
}

const val AS_DECIMAL = 100
const val CORRECT_ANSWERS_TO_LEARN = 3
const val COUNT_OF_WORDS_FOR_QUESTION = 4