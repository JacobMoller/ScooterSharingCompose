package dk.itu.moapd.scootersharing.jacj

import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import dk.itu.moapd.scootersharing.jacj.feature_authentication.domain.util.GoogleAuthUIClient
import dk.itu.moapd.scootersharing.jacj.feature_authentication.presentation.sign_in_screen.SignInScreen
import dk.itu.moapd.scootersharing.jacj.feature_authentication.presentation.sign_in_screen.SignInViewModel
import dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.map_screen.CheckPermissions
import dk.itu.moapd.scootersharing.jacj.feature_past_rides.presentation.past_rides.PastRidesScreen
import dk.itu.moapd.scootersharing.jacj.feature_photo.presentation_photo.PhotoScreen
import dk.itu.moapd.scootersharing.jacj.feature_qr_reader.presentation.qr_reader.QrReaderScreen
import dk.itu.moapd.scootersharing.jacj.feature_ride_summary.presentation.ride_summary.RideSummary
import dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.scooter_list.ScooterListScreen
import dk.itu.moapd.scootersharing.jacj.presentation.sign_in.UserData
import dk.itu.moapd.scootersharing.jacj.ui.theme.ScooterSharingTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Firebase Realtime Database URL.
 */
const val DATABASE_URL =
    "https://scooter-sharing-5c9ca-default-rtdb.europe-west1.firebasedatabase.app/"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScooterSharingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage(LocalContext.current, lifecycleScope)
                }
            }
        }
    }
}

fun <A> A.toJson(): String? {
    return Gson().toJson(this)
}

fun <A> String.fromJson(type: Class<A>): A {
    return Gson().fromJson(this, type)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(applicationContext: Context, lifecycleScope: LifecycleCoroutineScope) {
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    googleAuthUIClient = googleAuthUIClient,
                )
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                val route = navBackStackEntry?.destination?.route
                if (route != "RideSummary" && route != "SignInPage") {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.app_name)) },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (drawerState.isClosed) {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                } else {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Drawer Menu")
                            }
                        },
                    )
                }
            },
        )
        {
            Box(modifier = Modifier.padding(it)) {
                NavHost(
                    navController = navController,
                    startDestination = "HomePage/{lat}/{long}/?QRScanned={QRScanned}"
                ) {
                    composable(
                        "HomePage/{lat}/{long}/?QRScanned={QRScanned}",
                        arguments = listOf(navArgument("QRScanned") { defaultValue = "" })
                    ) { backStackEntry ->
                        CheckPermissions(
                            backStackEntry.arguments?.getString("lat"),
                            backStackEntry.arguments?.getString("long"),
                            navController,
                            backStackEntry.arguments?.getString("QRScanned")
                                ?.fromJson(Scooter::class.java)
                        )
                    }
                    composable(
                        "PhotoPage/{Scooter}/",
                        arguments = listOf(navArgument("Scooter") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        backStackEntry.arguments?.getString("Scooter")
                            ?.let { jsonString ->
                                val scooterConverted = jsonString.fromJson(Scooter::class.java)
                                PhotoScreen(navController, scooterConverted)
                            }
                    }
                    composable(
                        "QrCodeScannerPage/{Scooter}/",
                        arguments = listOf(navArgument("Scooter") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->

                        backStackEntry.arguments?.getString("Scooter")
                            ?.let { jsonString ->
                                val scooterConverted = jsonString.fromJson(Scooter::class.java)
                                QrReaderScreen(
                                    navController,
                                    scooterConverted
                                )
                            }
                    }
                    composable("ScooterListScreen") {
                        ScooterListScreen(navController)
                    }
                    composable("RentalHistory") {
                        PastRidesScreen()
                    }
                    composable("RideSummary") {
                        RideSummary(navController)
                    }
                    composable("SignInPage") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUIClient.getSignedInUser() != null) {
                                navController.navigate("HomePage/0/0/")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUIClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate("HomePage/0/0/")
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUIClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                }
                if (FirebaseAuth.getInstance().currentUser == null) {
                    navController.navigate("SignInPage")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    googleAuthUIClient: GoogleAuthUIClient
) {
    val scope = rememberCoroutineScope()

    val currentBackStackEntryAsState = navController.currentBackStackEntryAsState()

    val destination = currentBackStackEntryAsState.value?.destination

    val userData = googleAuthUIClient.getSignedInUser()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f, true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val menuItems = listOf(
                MenuItem(
                    "Home",
                    "HomePage/0/0/",
                    Icons.Filled.Home,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Scooter-List",
                    "ScooterListScreen",
                    Icons.Filled.List,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Rental History",
                    "RentalHistory",
                    Icons.Filled.Refresh,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
            )
            item {
                Spacer(modifier = Modifier.height(40.dp))
                DrawerHeader(userData)
                Spacer(modifier = Modifier.height(40.dp))
            }
            items(menuItems) { item ->
                CustomNavigationItem(item)
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = {
                    scope.launch {
                        googleAuthUIClient.signOut()
                        Toast.makeText(
                            context,
                            "Signed out",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.popBackStack()
                        drawerState.close()
                    }
                }) {
                    Text(text = "Log out")
                }
            }
        }
        Text(
            text = "ScooterSharing v. 1.0.0",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DrawerHeader(userData: UserData?) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationItem(
    item: MenuItem
) {
    NavigationDrawerItem(
        icon = { Icon(item.icon, contentDescription = item.label) },
        label = { Text(text = item.label) },
        selected = item.destination?.route == item.destinationName,
        onClick = {
            item.navController.navigate(item.destinationName, navOptions {
                this.launchSingleTop = true
                this.restoreState = true
            })

            item.scope.launch {
                item.drawerState.close()
            }
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.surface,
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            unselectedContainerColor = MaterialTheme.colorScheme.background,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

data class MenuItem @OptIn(ExperimentalMaterial3Api::class) constructor(
    var label: String,
    var destinationName: String,
    var icon: ImageVector,
    var navController: NavController,
    var scope: CoroutineScope,
    var destination: NavDestination?,
    var drawerState: DrawerState
)