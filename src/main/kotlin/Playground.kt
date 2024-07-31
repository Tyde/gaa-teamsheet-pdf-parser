import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    // Get all PDFS in the "testDocuments" directory
    val pdfs = File("testDocuments").listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray()
    // For each PDF, create a TeamsheetReader and print the player data
    pdfs.forEach { file ->
        println("Reading teamsheet from ${file.name}")
        val players = TeamsheetReader.readFromPath(file.absolutePath)
        //Write into json file (same name as source document but with json ending):
        val jsonFile = File(file.absolutePath.replace(".pdf", ".json"))

        jsonFile.writeText(Json.encodeToString(players))
    }
}


