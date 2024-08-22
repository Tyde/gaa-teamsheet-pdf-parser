import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
   /*val pdfs = File("testDocuments")
       .listFiles { _, name -> name.endsWith(".pdf") }
       .filter { it.name == "Augsburg_LGFA_VG_2024.pdf" || it.name == "teamsheet_ladies_augsburg_augsburg.pdf" || it.name.contains("Munich") }

    pdfs.forEach { file ->
        println("Reading teamsheet from ${file.name}")
        val players = TeamsheetReader.readFromPath(file.absolutePath)
        //Write into json file (same name as source document but with json ending):
        val jsonFile = File(file.absolutePath.replace(".pdf", ".json"))

        players.fold(
            onSuccess = {
                println("  Successfully read teamsheet from ${file.name}: ${it.size} players")
                it.forEach {player ->  println("    ${player.romanName}, Number: ${player.number} - DOB: ${player.dateOfBirth}") }
            },
            onFailure = {
                println("  Could not read teamsheet from ${file.name}")
            }
        )
        //jsonFile.writeText(Json.encodeToString(players))
    }*/
    createJSONForTest()


}

fun createJSONForTest() {
    // Get all PDFS in the "testDocuments" directory
    val pdfs = File("testDocuments").listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray()
    // Initialize JSON
    val json = Json {
        prettyPrint = true
    }
    // For each PDF, create a TeamsheetReader and print the player data
    pdfs
        .forEach { file ->
            println("Reading teamsheet from ${file.name}")
            val players = TeamsheetReader.readFromPath(file.absolutePath)
            //Write into json file (same name as source document but with json ending):
            val jsonFile = File(file.absolutePath.replace(".pdf", ".json"))

            players.fold(
                onSuccess = {
                    println("Successfully read teamsheet from ${file.name}: ${it.size} players")
                    jsonFile.writeText(json.encodeToString(it))
                },
                onFailure = {
                    println("Could not read teamsheet from ${file.name}")
                }
            )

        }
}


