import kotlinx.serialization.Serializable
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.io.File

object TeamsheetReader {


    fun readFromPath(path: String):List<ExtractedPlayer> {
        val file = File(path)
        val document = Loader.loadPDF(file)
        return readDocument(document)
    }

    fun readFromBytes(bytes: ByteArray):List<ExtractedPlayer> {
        val document = Loader.loadPDF(bytes)
        return readDocument(document)
    }

    private fun readDocument(document: PDDocument?): List<ExtractedPlayer> {
        val lines = PDFTextStripper().getText(document).split("\n")

        return mapLinesToExtractedPlayers(selectRelevantLines(lines))
    }

    private fun selectRelevantLines(lines: List<String>): List<List<String>> {
        val start = lines.indexOfFirst { it.contains("= ag tosú") } + 1
        var end = lines.indexOfFirst { it.contains("(Sínithe ag an Rúnaí)") }
        val duplicateNoteStartingWith = "Duplicate lists of players, giving full names"
        val indexDuplicateNoteLine = lines.indexOfFirst { it.startsWith(duplicateNoteStartingWith)}
        if (indexDuplicateNoteLine != -1) {
            end = indexDuplicateNoteLine
        }
        // If there is a Bainisteoirí section we set the end to the start of that section
        val bainisteoiri = "Bainisteoirí"
        if (lines.contains(bainisteoiri)) {
            end = lines.indexOfFirst { it.contains(bainisteoiri) }
        }

        val extracted = lines.subList(start, end).filter { it.isNotBlank() }
        //Now every player is contained in exactly 2 lines, so we can zip them together
        return extracted.chunked(2)
    }
    private fun mapLinesToExtractedPlayers(lines: List<List<String>>): List<ExtractedPlayer> {
        val pattern = """(\d{2}\/\d{2}\/\d{4})?\s?([0-9]+)\s(.*)""".toRegex()
       return  lines.map { (firstLine, secondLine) ->
            val irishName = firstLine.trim()
            val pair = pattern.find(secondLine.trim()) ?: return@map null
            val dateOfBirth = pair.groupValues[1]
            val number = pair.groupValues[2].toInt()
            val romanName = pair.groupValues[3]
            ExtractedPlayer(number, irishName, romanName, dateOfBirth)
        }.filterNotNull()
    }
}
@Serializable
data class ExtractedPlayer(
    val number: Int,
    val irishName: String,
    val romanName: String,
    val dateOfBirth: String,
)
