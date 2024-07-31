import kotlinx.serialization.Serializable
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

class NoTeamSheetProvidedException : Exception("The provided file does not seem to be of the expected team sheet format")
class IllegalPlayerContentException: Exception("While parsing the players, the content was not as expected")

object TeamsheetReader {
    fun readFromPath(path: String): Result<List<ExtractedPlayer>> {
        val file = File(path)
        val document = Loader.loadPDF(file)
        return readDocument(document)
    }

    fun readFromBytes(bytes: ByteArray): Result<List<ExtractedPlayer>> {
        val document = Loader.loadPDF(bytes)
        return readDocument(document)
    }

    private fun readDocument(document: PDDocument?): Result<List<ExtractedPlayer>> {
        val lines = PDFTextStripper().getText(document).split("\n")
        return selectRelevantLines(lines).fold(
            onSuccess = { mapLinesToExtractedPlayers(it) },
            onFailure = { Result.failure(it) }
        )

    }

    private fun selectRelevantLines(lines: List<String>): Result<List<List<String>>> {
        val start = lines.indexOfFirst { it.contains("= ag tosú") } + 1
        var end = lines.indexOfFirst { it.contains("(Sínithe ag an Rúnaí)") }
        val duplicateNoteStartingWith = "Duplicate lists of players, giving full names"
        val indexDuplicateNoteLine = lines.indexOfFirst { it.startsWith(duplicateNoteStartingWith) }
        if (indexDuplicateNoteLine != -1) {
            end = indexDuplicateNoteLine
        }
        // If there is a Bainisteoirí section we set the end to the start of that section
        val bainisteoiri = "Bainisteoirí"
        if (lines.contains(bainisteoiri)) {
            end = lines.indexOfFirst { it.contains(bainisteoiri) }
        }
        if (start == -1 || end == -1) {
            return Result.failure(Exception("Could not find relevant lines in PDF"))
        }
        val extracted = lines.subList(start, end).filter { it.isNotBlank() }
        //Now every player is contained in exactly 2 lines, so we can zip them together
        val chunked = extracted.chunked(2)
        chunked.firstOrNull { it.size == 1 }?.let {
            return Result.failure(IllegalPlayerContentException())
        }
        return Result.success(chunked)

    }

    private fun mapLinesToExtractedPlayers(lines: List<List<String>>): Result<List<ExtractedPlayer>> {
        val pattern = """(\d{2}\/\d{2}\/\d{4})?\s?([0-9]+)\s(.*)""".toRegex()
        try {
            return Result.success(lines.map { (firstLine, secondLine) ->
                val irishName = firstLine.trim()
                val pair = pattern.find(secondLine.trim()) ?: return@map null
                val dateOfBirth = pair.groupValues[1]
                val number = pair.groupValues[2].toInt()
                val romanName = pair.groupValues[3]
                ExtractedPlayer(number, irishName, romanName, dateOfBirth)
            }.filterNotNull())
        } catch (e: ArrayIndexOutOfBoundsException) {
            return Result.failure(IllegalPlayerContentException())
        } catch (e: NumberFormatException) {
            return Result.failure(IllegalPlayerContentException())
        }
    }
}

@Serializable
data class ExtractedPlayer(
    val number: Int,
    val irishName: String,
    val romanName: String,
    val dateOfBirth: String,
)
