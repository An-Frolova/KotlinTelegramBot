import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val updateIdRegex: Regex = "\"update_id\":(\\d{9})".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
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
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}