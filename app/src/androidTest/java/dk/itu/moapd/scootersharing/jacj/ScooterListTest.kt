package dk.itu.moapd.scootersharing.jacj

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.scooter_list.ScooterListScreen
import org.junit.Rule
import org.junit.Test

class ScooterListTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun scooterListScreen_containsMapButtons() {
        rule.setContent { ScooterListScreen(navController = NavHostController(LocalContext.current)) }

        // Do something
        rule.onNodeWithText("Show on map").performClick()

        // Check something
        rule.onNodeWithTag("MarkerTag").assertExists()
    }
}