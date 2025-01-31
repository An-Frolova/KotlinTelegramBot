import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService {

    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(botToken: String, chatId: Long?, messageText: String?): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$messageText"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

fun main(args: Array<String>) {

    val telegramBotService = TelegramBotService()
    val botToken = args[0]
    var updateId = 0
    val updateIdRegex: Regex = "\"update_id\":(\\d{9})".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d{10})".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)
        println(updates)

        val matchResultForUpdateId: MatchResult? = updateIdRegex.find(updates)
        val groupsForUpdateId: MatchGroupCollection? = matchResultForUpdateId?.groups
        val groupsSize = groupsForUpdateId?.size
        if (groupsSize != null) {
            updateId = groupsForUpdateId.let {
                it[groupsSize - 1]?.value?.toInt()?.plus(1)
            } ?: throw IllegalArgumentException("Invalid updateId")
        }

        val matchResultForMessageText: MatchResult? = messageTextRegex.find(updates)
        val groupsForMessageText: MatchGroupCollection? = matchResultForMessageText?.groups
        val text = groupsForMessageText?.get(1)?.value
        println(text)

        val matchResultForChatId: MatchResult? = chatIdRegex.find(updates)
        val groupsForChatId: MatchGroupCollection? = matchResultForChatId?.groups
        val chatId = groupsForChatId?.get(1)?.value?.toLong()
        println(chatId)

        if (text != null && chatId != null) {
            telegramBotService.sendMessage(botToken, chatId, text)
        }
    }
}