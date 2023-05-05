package dk.itu.moapd.scootersharing.jacj

import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import org.junit.Assert
import org.junit.Test

class ScooterUnitTest {
    @Test
    fun scooterName_isCorrect() {
        val scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        val actual = scooter.name

        val expected = "CPH001"

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun scooterLatitude_exists() {
        val scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        val actual = scooter.coords?.lat

        val expected = 55.659153

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun scooterLongitude_exists() {
        val scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        val actual = scooter.coords?.long

        val expected = 12.590966

        Assert.assertEquals(expected, actual)
    }
}