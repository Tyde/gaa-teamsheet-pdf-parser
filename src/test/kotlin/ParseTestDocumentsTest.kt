import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseTestDocumentsTest {

    @Test
    fun compareToTestDocuments() {
        val pdfs = File("testDocuments").listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray()
        //First check if the json files exist
        val filePairs = pdfs.map { file ->
            val jsonFile = File(file.absolutePath.replace(".pdf", ".json"))
            (file to jsonFile)
        }.filter { (_, json) -> json.exists() }
        //Then compare the json files
        filePairs.forEach { (pdf, json) ->
            println("Reading ${pdf.name}")
            val players = TeamsheetReader.readFromPath(pdf.absolutePath).getOrThrow()

            val expectedPlayers = Json.decodeFromString<List<ExtractedPlayer>>(json.readText())
            players.zip(expectedPlayers).forEach { (actual, expected) ->
                assertEquals(expected,actual)
            }
        }
    }
}
