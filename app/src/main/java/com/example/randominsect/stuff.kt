data class Insectt(val name: String, val habitat: Habitat?)
data class Habitat(val type: String, val averageTemperature: Double?)

fun getClimateDescription(Insectt: Insect?): String {
    return try {
        // 1. Chained safe call (Null Safety)
        // If 'Insectt' or 'habitat' is null, the chain breaks safely
        val temp = Insectt?.habitat?.averageTemperature 
            ?: throw IllegalArgumentException("Insectt or habitat data is incomplete.")

        // 2. Range validation (Business Logic)
        if (temp < -50.0 || temp > 60.0) {
            throw ArithmeticException("Temperature ($temp °C) is outside valid terrestrial limits.")
        }

        // 3. Safe processing of response
        val species = Insectt?.name ?: "Unknown species"
        "$species lives in a suitable environment averaging $temp °C."

    } catch (e: IllegalArgumentException) {
        println("Data Notice: ${e.message}")
        "Climate info unavailable"

    } catch (e: ArithmeticException) {
        println("Measurement Error: ${e.message}")
        "Anomalous climate reading"

    } catch (e: Exception) {
        println("Unexpected Error: ${e.localizedMessage}")
        "Error in Insectt record"
    }
}

fun processInsects(
    insects: List<Insect>, 
    predicate: (Insect) -> Boolean
): List<Insect> {
    val result = mutableListOf<Insect>()
    for (insect in insects) {
        if (predicate(insect)) { // Executing the lambda
            result.add(insect)
        }
    }
    return result
}

fun main() {
    // Test cases
    val ant = Insectt("Red Ant", Habitat("Soil", 24.5))
    val beetleWithoutTemp = Insectt("Beetle", Habitat("Forest", null))
    val beeWithExtremeTemp = Insectt("Bee", Habitat("Hive", 150.0))
    val nullInsectt: Insect? = null

    println(getClimateDescription(ant))          
    // Output: Red Ant lives in a suitable environment averaging 24.5 °C.

    println(getClimateDescription(beetleWithoutTemp)) 
    // Output: Data Notice... -> Climate info unavailable

    println(getClimateDescription(beeWithExtremeTemp))     
    // Output: Measurement Error... -> Anomalous climate reading

    println(getClimateDescription(nullInsectt))        
    // Output: Data Notice... -> Climate info unavailable
}
