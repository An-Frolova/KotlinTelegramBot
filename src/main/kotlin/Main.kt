fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("unable to load dictionary")
        return
    }

    while (true) {
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
            "1" -> learnWords(trainer)
            "2" -> {
                val statistics = trainer.getStatistic(trainer.dictionary)
                println(
                    "Выучено ${statistics.correctAnswersCount} из ${statistics.totalCount} слов " +
                            "| ${statistics.percent}%\n"
                )
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> " ${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.original + "\n" + variants + "\n 0 - выйти в меню"
}

fun learnWords(trainer: LearnWordsTrainer) {
    while (true) {
        val question = trainer.getNewQuestion()
        if (question == null) {
            println("Все слова выучены или в словаре недостаточно слов\n")
            break
        } else {
            println(question.asConsoleString())
            val userAnswer = readln().toIntOrNull()
            when (userAnswer) {
                0 -> {
                    println("")
                    break
                }
            }
            if (trainer.isAnswerCorrect(userAnswer)) {
                println("Правильно!\n")
            } else {
                println("Неправильно - слово \"${question.correctAnswer.translate}\"\n")
            }
        }
    }
}