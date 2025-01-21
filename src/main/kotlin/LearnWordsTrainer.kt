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
    private val correctAnswersToLearnWord: Int = 3,
    private val countOfWordsForQuestion: Int = 4,
) {

    private var question: Question? = null
    val dictionary = loadDictionary()

    private fun loadDictionary(): List<Word> {
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

    private fun saveDictionary(learnedWord: Word) {
        val dictionaryFile = File("words.txt")
        val lines = dictionaryFile.readLines().toMutableList()
        for (i in lines.indices) {
            val parts = lines[i].split("|")
            if (parts[0] == learnedWord.original) {
                val updatedLine = "${parts[0]}|${parts[1]}|${learnedWord.correctAnswersCount}"
                lines[i] = updatedLine
                break
            }
        }
        dictionaryFile.writeText(lines.joinToString("\n"))
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
        question = Question(questionWordsMList, correctAnswer)
        return question
    }

    fun isAnswerCorrect(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerIndex = it.variants.indexOf(it.correctAnswer) + 1
            if (userAnswerIndex == correctAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(it.correctAnswer)
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