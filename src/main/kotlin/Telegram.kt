import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$BASE_URL$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Long, messageText: String): String {
        val encoded = URLEncoder.encode(
            messageText,
            StandardCharsets.UTF_8
        )
        println(encoded)
        val urlSendMessage = "$BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMessage = "$BASE_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Учить слова",
                                "callback_data": "$LEARN_WORDS_CLICKED"
                            }
                        ],
                        [
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS_CLICKED"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

fun main(args: Array<String>) {

    val botToken = args[0]
    val telegramBotService = TelegramBotService(botToken)
    var updateId = 0
    val updateIdRegex: Regex = "\"update_id\":(\\d{9})".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d{10})".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        val matchResultForUpdateId: MatchResult? = updateIdRegex.find(updates)
        val groupsForUpdateId: MatchGroupCollection? = matchResultForUpdateId?.groups
        val groupsSize = groupsForUpdateId?.size
        if (groupsSize != null) {
            updateId = groupsForUpdateId.let {
                it[groupsSize - 1]?.value?.toInt()?.plus(1)
            } ?: throw IllegalArgumentException("Invalid updateId")
        }

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value ?: continue
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: continue

        if (text.lowercase() == "/start") {
            telegramBotService.sendMenu(chatId)
        }

        val data = dataRegex.find(updates)?.groups?.get(1)?.value ?: continue
        if (data == STATISTICS_CLICKED) {
            val statistics = trainer.getStatistic(trainer.dictionary)
            val statisticsString = "Выучено ${statistics.correctAnswersCount} из ${statistics.totalCount} слов " +
                    "| ${statistics.percent}%\n"
            telegramBotService.sendMessage(chatId, statisticsString)
        }
    }
}

const val BASE_URL = "https://api.telegram.org/bot"
const val STATISTICS_CLICKED = "statistics_clicked"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"