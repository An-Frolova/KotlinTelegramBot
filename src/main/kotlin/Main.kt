import java.io.File


fun main() {

    val wordsFile = File("words.txt")
    val wordsList = wordsFile.readLines()
    for (word in wordsList) {
        println(word)
    }
}