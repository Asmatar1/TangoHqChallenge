import com.google.gson.Gson
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random
import java.io.OutputStream
import java.nio.charset.Charset
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun welcomeToChallenge() {
    val url = URL("https://interview.tangohq.com/welcome")
    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
            }
        }
    }
}

fun sendGet(token: String) {
    val url = URL("https://interview.tangohq.com/start")

    with(url.openConnection() as HttpURLConnection) {
        setRequestProperty("Authorization", "Bearer $token")
        requestMethod = "GET"
        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
            }
        }
    }
}

fun sendUpload(token: String) {
    val url = URL("https://interview.tangohq.com/upload")

    with(url.openConnection() as HttpURLConnection) {
        setRequestProperty("Authorization", "Bearer $token")
        requestMethod = "GET"
        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
            }
        }
    }
}

fun guessTheNumber(token: String) {
    var lowerBound = 0
    var upperBound = 100_000_000
    var guess: Int
    var isCorrectGuess = false

    while (lowerBound <= upperBound && !isCorrectGuess) {
        guess = lowerBound + (upperBound - lowerBound) / 2
        val url = URL("https://interview.tangohq.com/guess")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $token")

            val jsonInputString = "{\"myGuess\": $guess}"

            DataOutputStream(outputStream).use { os ->
                os.writeBytes(jsonInputString)
                os.flush()
            }
            println("\nSent 'POST' request to URL : $url; Response Code : $responseCode")
            val serverResponse = inputStream.bufferedReader().use(BufferedReader::readText)
            println("Server response: $serverResponse")
            val status = serverResponse.substringAfter("\"status\":\"").substringBefore("\"")

            when (status) {
                "lower" -> upperBound = guess - 1
                "higher" -> lowerBound = guess + 1
                "correct" -> {
                    println("Correct guess: $guess")
                    isCorrectGuess = true
                }
                else -> {
                    println("Unexpected server response: $serverResponse")
                    isCorrectGuess = true
                }
            }
        }
    }
}

fun getWordle(token: String) {
    val url = URL("https://interview.tangohq.com/wordle")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        setRequestProperty("Authorization", "Bearer $token")

        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
        val serverResponse = inputStream.bufferedReader().use(BufferedReader::readText)
        println("Server response: $serverResponse")
    }
}

fun guessWord(token: String, guess: String): Pair<String, Boolean> {
    val url = URL("https://interview.tangohq.com/guess-word")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        doOutput = true
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Authorization", "Bearer $token")
        val jsonInputString = "{\"myGuess\": \"$guess\"}"

        OutputStreamWriter(outputStream).use { writer ->
            writer.write(jsonInputString)
            writer.flush()
        }
        println("\nSent 'POST' request to URL: $url; Response Code: $responseCode")

        val serverResponse = inputStream.bufferedReader().readText()
        println("Server response: $serverResponse")

        val isCorrect = serverResponse.contains("\"status\":\"correct\"")
        return serverResponse to isCorrect
    }
}

fun parseHint(serverResponse: String): Pair<List<Boolean>, List<Boolean>> {
    val positionAndCharacter = serverResponse.substringAfter("\"positionAndCharacter\":[")
        .substringBefore("]")
        .split(',')
        .map { it.trim().toBoolean() }
    val character = serverResponse.substringAfter("\"character\":[")
        .substringBefore("]")
        .split(',')
        .map { it.trim().toBoolean() }

    return Pair(positionAndCharacter, character)
}

fun generateRandomLetter(exclude: Set<Char>): Char {
    val alphabet = ('a'..'z').toList().filterNot { it in exclude }
    return if (alphabet.isNotEmpty()) alphabet[Random.nextInt(alphabet.size)] else 'a'
}
fun getEscpos(token: String) {
    val url = URL("https://interview.tangohq.com/escpos")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        setRequestProperty("Authorization", "Bearer $token")

        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
        val serverResponse = inputStream.bufferedReader().use(BufferedReader::readText)
        println("Server response: $serverResponse")
    }
}

fun postMyGuess(json: String, token: String) {
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = json.toRequestBody(mediaType)
    val request = Request.Builder()
        .url("https://interview.tangohq.com/guess-escpos")
        .post(body)
        .addHeader("Authorization", "Bearer $token")
        .build()


    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Response code: ${response.code}")
                println("Response message: ${response.message}")
                response.body?.string()?.let {
                    println("Response body: $it")
                }
                throw IOException("Unexpected code $response")
            }

            // Handle the successful response from the server
            println("Response from the server: ${response.body?.string()}")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


fun postGitHubRepoURL(token: String, repoUrl: String): String {
    val url = "https://interview.tangohq.com/github"
    val client = OkHttpClient()

    // Create JSON payload
    val json = """
        {
          "url": "$repoUrl"
        }
    """.trimIndent()

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = json.toRequestBody(mediaType)

    val request = Request.Builder()
        .url(url)
        .post(body)
        .addHeader("Authorization", "Bearer $token")
        .build()

    val response = client.newCall(request).execute()

    return if (response.isSuccessful) {
        "GitHub repository URL successfully posted."
    } else {
        "Failed to post GitHub repository URL. Response code: ${response.code}, Response message: ${response.message}"
    }
}

fun main(args: Array<String>) {
//    welcomeToChallenge()
    val finalToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXNzaW9uSWQiOiJkNTY4NTBjMy1hNDM2LTRkZTQtYWUwOC04NjUzNjllYjU1ZjYiLCJtZXNzYWdlIjoiRGVjb2RpbmcgdGhlIHRva2VuLCBuaWNlLCBoYXZlIGEgcHJpemUiLCJ1cmwiOiIvYm9udXMtam9uYXMiLCJpYXQiOjE3MDA4Nzc2MDJ9.HAMFV8NYAJF_FozcrSiuAZZUh5y0cj-o_CQxvr3KeQs"
    val repoUrl = "https://github.com/Asmatar1/TangoHqChallenge.git"
//    sendGet(finalToken)
//    guessTheNumber(finalToken)
//    getWordle(finalToken)
//    var wordLength = 0
//    var guess = ""
//    val seenLetters = mutableSetOf<Char>()
//    var positionAndCharacter = listOf<Boolean>()
//    var character = listOf<Boolean>()
//
//    while (true) {
//        if (guess.isEmpty() || guess.length != wordLength) {
//            guess = (1..wordLength).map { generateRandomLetter(seenLetters) }.joinToString("")
//        }
//
//        val (response, correct) = guessWord(finalToken, guess)
//        if (correct) {
//            println("Guessed the correct word!")
//            break
//        } else {
//            val hints = parseHint(response)
//            positionAndCharacter = hints.first
//            character = hints.second
//            wordLength = positionAndCharacter.size
//            guess = (0 until wordLength).map { index ->
//                when {
//                    index < positionAndCharacter.size && positionAndCharacter[index] -> guess[index]
//                    index < character.size && character[index] -> generateRandomLetter(seenLetters)
//                    else -> generateRandomLetter(seenLetters)
//                }
//            }.joinToString("")
//        }
//    }

//    getEscpos(finalToken)

//    val text1 = "Hello Tango"
//    val text2 = "I'm excited to join"
//
//    val asciiText1 = text1.toCharArray().map { it.code }
//    val asciiText2 = text2.toCharArray().map { it.code }
//
//    val printAndFeedLine = listOf(27, 74, 1) // ESC J n for one line
//    val printAndFeedLine2 = listOf(27, 74, 2)
//    val feedAndPartialCut = listOf(29, 86, 65, 0) // Replace 'm' with the appropriate value for partial cut
//
//    val commandChain = asciiText1 + printAndFeedLine + asciiText2 +  printAndFeedLine2 + feedAndPartialCut
//
//    val jsonObject = mapOf("myGuess" to commandChain)
//    val gson = Gson()
//    val jsonOutput = gson.toJson(jsonObject)
//    postMyGuess(jsonOutput, finalToken)

//    sendUpload(finalToken)
    postGitHubRepoURL(finalToken, repoUrl)
}
