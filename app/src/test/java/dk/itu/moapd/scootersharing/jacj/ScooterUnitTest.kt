package dk.itu.moapd.scootersharing.jacj

import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import org.junit.Assert
import org.junit.Test

class ScooterUnitTest {
    @Test
    fun scooterName_isCorrect() {
        var scooter: Scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        var actual = scooter.name

        var expected = "CPH001"

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun scooterLatitude_exists() {
        var scooter: Scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        var actual = scooter.coords?.lat

        var expected = 55.659153

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun scooterLongitude_exists() {
        var scooter: Scooter = Scooter(coords = Coords(55.659153, 12.590966),"CPH001")

        var actual = scooter.coords?.long

        var expected = 12.590966

        Assert.assertEquals(expected, actual)
    }
}