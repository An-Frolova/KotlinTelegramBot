fun main() {

    val trainer = LearnWordsTrainer()

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

fun learnWords(trainer: LearnWordsTrainer) {
    while (true) {
        val question = trainer.getNewQuestion()
        if (question == null) {
            println("Все слова в словаре выучены или в словаре недостаточно слов\n")
            break
        } else {
            println("\n${question.correctAnswer.original}:")
            for (i in 0..<question.variants.size) {
                println("${i + 1} - ${question.variants[i].translate}")
            }
            println("----------\n0 - Меню")
            val userAnswer = readln().toInt()
            when (userAnswer) {
                0 -> {
                    println("")
                    break
                }
            }
            if (trainer.isAnswerCorrect(userAnswer)) {
                println("Правильно!")
            } else {
                println("Неправильно - слово \"${question.correctAnswer.translate}\"")
            }
        }
    }
}