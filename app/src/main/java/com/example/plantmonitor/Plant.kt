package com.example.plantmonitor




data class Plant(
    var name: String? = "",
    var earliestPlantMonth: String? = "",
    var latestPlantMonth: String? = "",
    var daysToSprout: Int? = 0,
    var daysToMaturity: Int? = 0,
    var seedDepthCm:Double? = 0.0,
    var seedSpacingCm:Double? = 0.0,
    var plantSpacingCm:Double? = 0.0,
    var rowSpacingCm:Double? = 0.0,
    var idealMoisture: Int? = 0,
    var idealLightHours: Int? = 0,
    var pH:Double? = 0.0,
    var nitrogen: Double? = 0.0,
    var phosphorous: Double? = 0.0,
    var potassium: Double? = 0.0,
    var idealAirTemp: Double? = 0.0,
    var idealSoilTemp:Double? = 0.0,
    var humidity: Int? = 0,
    var companions: List<String>? = listOf<String>(),
    var rivals: List<String>? = listOf<String>()
) {
    override fun toString(): String {
        return "\ncom.example.plantmonitor.Plant name: $name\n" +
                "Sow no earlier than: $earliestPlantMonth\n"+
        "Sow no earlier than: $latestPlantMonth\n"+
        "Days before seed sprouts: $daysToSprout\n"+
        "Days before plant matures: $daysToMaturity\n"+
        "PH: $pH\n"+
        "Nitrogen: $nitrogen\n"+
        "Phosphorous: $phosphorous\n"+
        "Potassium: $potassium\n"+
        "Ideal air temperature: $idealAirTemp\n"+
        "Ideal soil temperature: $idealSoilTemp\n"+
        "Humidity: $humidity\n"+
        "Grows well with: $companions\n"+
        "Grows poorly with: $rivals\n"
    }

}