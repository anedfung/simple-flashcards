
package com.example.simpleflashcards.data

data class CsvFlashcard(
    val question: String,
    val answer: String
)

object CsvUtils {

    fun export(cards: List<Flashcard>): String {
        return buildString {
            append("question,answer\r\n")

            cards.forEach { card ->
                append(escape(card.question))
                append(",")
                append(escape(card.answer))
                append("\r\n")
            }
        }
    }

    private fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")

        return if (
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n") ||
            value.contains("\r")
        ) {
            "\"$escaped\""
        } else {
            escaped
        }
    }

    fun import(csv: String): List<CsvFlashcard> {
        val rows = parseRows(csv)

        require(rows.isNotEmpty()) {
            "The CSV file is empty."
        }

        val header = rows.first().map {
            it.trim().removePrefix("\uFEFF").lowercase()
        }

        require(header == listOf("question", "answer")) {
            "Invalid CSV format. Expected question,answer as the first row."
        }

        return rows.drop(1)
            .filter { row -> row.any { it.isNotBlank() } }
            .mapIndexed { index, row ->
                require(row.size == 2) {
                    "Invalid data on CSV row ${index + 2}."
                }

                val question = row[0].trim()
                val answer = row[1].trim()

                require(question.isNotEmpty() && answer.isNotEmpty()) {
                    "Missing question or answer on CSV row ${index + 2}."
                }

                CsvFlashcard(question, answer)
            }
    }

    private fun parseRows(csv: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()

        var insideQuotes = false
        var i = 0

        while (i < csv.length) {
            val char = csv[i]

            when {
                char == '"' && insideQuotes &&
                    i + 1 < csv.length && csv[i + 1] == '"' -> {
                    field.append('"')
                    i++
                }

                char == '"' -> {
                    insideQuotes = !insideQuotes
                }

                char == ',' && !insideQuotes -> {
                    row.add(field.toString())
                    field.clear()
                }

                (char == '\n' || char == '\r') && !insideQuotes -> {
                    row.add(field.toString())
                    field.clear()

                    if (row.any { it.isNotEmpty() }) {
                        rows.add(row)
                    }

                    row = mutableListOf()

                    if (char == '\r' &&
                        i + 1 < csv.length &&
                        csv[i + 1] == '\n'
                    ) {
                        i++
                    }
                }

                else -> field.append(char)
            }

            i++
        }

        require(!insideQuotes) {
            "The CSV file contains an unclosed quotation mark."
        }

        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }

        return rows
    }
}
