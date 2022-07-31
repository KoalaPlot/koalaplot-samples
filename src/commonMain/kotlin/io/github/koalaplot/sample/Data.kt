package io.github.koalaplot.sample

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal val padding = 8.dp
internal val paddingMod = Modifier.padding(padding)

internal val fibonacci = mutableStateListOf(1.0f, 1.0f, 2.0f, 3.0f, 5.0f, 8.0f, 13.0f, 21.0f)
internal val fibonacciSum = fibonacci.sumOf { it.toDouble() }.toFloat()

internal val fibonacciExtended = mutableStateListOf(
    1.0f, 1.0f, 2.0f, 3.0f, 5.0f, 8.0f, 13.0f, 21.0f, 34f, 55f, 89f, 144f, 233f, 377f, 610f, 987f
)

// Data from
// https://data.cityofnewyork.us/City-Government/New-York-City-Population-by-Borough-1950-2040/xywu-7bv9
internal object PopulationData {
    enum class Categories(val display: String) {
        Bronx("Bronx"), Brooklyn("Brooklyn"), Manhattan("Manhattan"), Queens("Queens"),
        StatenIsland("Staten Island");

        override fun toString(): String {
            return display
        }
    }

    val years = listOf(1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)

    val data = buildMap {
        put(
            Categories.Bronx,
            listOf(1451277, 1424815, 1471701, 1168972, 1203789, 1332650, 1385108, 1446788)
        )
        put(
            Categories.Brooklyn,
            listOf(2738175, 2627319, 2602012, 2230936, 2300664, 2465326, 2552911, 2648452)
        )
        put(
            Categories.Manhattan,
            listOf(1960101, 1698281, 1539233, 1428285, 1487536, 1537195, 1585873, 1638281)
        )
        put(
            Categories.Queens,
            listOf(1550849, 1809578, 1986473, 1891325, 1951598, 2229379, 2250002, 2330295)
        )
        put(
            Categories.StatenIsland,
            listOf(191555, 221991, 295443, 352121, 378977, 443728, 468730, 487155)
        )
    }

    val maxPopulation = data.maxOf { entry ->
        entry.value.maxOf { it }
    }
}

// Data from www.worldclimate.com
internal object RainData {
    val months =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    val rainfall = buildMap {
        put(
            "New York City",
            listOf(83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3)
        )

        put(
            "San Diego",
            listOf(55.6, 41.3, 49.9, 19.8, 4.8, 1.9, 0.5, 2.1, 4.7, 8.6, 29.5, 35.4)
        )

        put(
            "Tokyo",
            listOf(49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4)
        )

        put(
            "Sydney",
            listOf(103.4, 111.0, 131.3, 129.7, 123.0, 129.2, 102.8, 80.3, 69.3, 82.6, 81.4, 78.2)
        )

        put(
            "London",
            listOf(61.5, 36.2, 49.8, 42.5, 45.0, 45.8, 45.7, 44.2, 42.7, 72.6, 45.1, 59.3)
        )
    }

    val max = rainfall.maxOf { it.value.maxOf { it } }
    val min = rainfall.minOf { it.value.minOf { it } }
}
