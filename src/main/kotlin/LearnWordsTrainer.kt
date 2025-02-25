import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

data class Statistics(
    val totalCount: Int,
    val correctAnswersCount: Int,
    val percent: Int,
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
    private val correctAnswersToLearnWord: Int = 3,
    private val countOfWordsForQuestion: Int = 4,
) {

    private var _question: Question? = null
    val question: Question?
        get() = _question
    val dictionary = loadDictionary()

    private fun loadDictionary(): List<Word> {
        val wordsFile = File(fileName)
        if (!wordsFile.exists()) {
            File("words.txt").copyTo(wordsFile)
        }
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

    fun saveDictionary() {
        val dictionaryFile = File(fileName)
        dictionaryFile.writeText("")
        for (word in dictionary) {
            dictionaryFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
        }
    }

    fun getNewQuestion(): Question? {
        val notLearnedList = dictionary.filter { word: Word -> word.correctAnswersCount < correctAnswersToLearnWord }
        if (notLearnedList.isEmpty()) {
            return null
        }
        val questionWordsMList = notLearnedList.shuffled().take(countOfWordsForQuestion).toMutableList()
        val correctAnswer = questionWordsMList.random()

        if (questionWordsMList.size < countOfWordsForQuestion && dictionary.size < countOfWordsForQuestion) {
            return null
        } else {
            while (questionWordsMList.size < countOfWordsForQuestion) {
                val learnedWord = dictionary.random()
                if (learnedWord !in questionWordsMList) {
                    questionWordsMList.add(learnedWord)
                }
            }
            questionWordsMList.shuffle()
        }
        _question = Question(questionWordsMList, correctAnswer)
        return _question
    }

    fun isAnswerCorrect(userAnswerIndex: Int?): Boolean {
        return _question?.let {
            val correctAnswerIndex = it.variants.indexOf(it.correctAnswer)
            if (userAnswerIndex == correctAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else {
                false
            }
        } ?: false
    }

    fun getStatistic(dictionary: List<Word>): Statistics {
        val totalCount = dictionary.size
        val correctAnswersCount = dictionary.filter { it.correctAnswersCount >= correctAnswersToLearnWord }.size
        val percent = ((correctAnswersCount.toDouble()) / (totalCount.toDouble()) * AS_DECIMAL).toInt()
        return Statistics(totalCount, correctAnswersCount, percent)
    }
}

const val AS_DECIMAL = 100