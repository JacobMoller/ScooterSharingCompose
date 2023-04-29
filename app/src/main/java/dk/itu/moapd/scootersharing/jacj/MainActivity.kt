package dk.itu.moapd.scootersharing.jacj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import dk.itu.moapd.scootersharing.jacj.ui.theme.ScooterSharingTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                    MainPage()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScooterSharingTheme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    val navController = rememberNavController()

    var scope = rememberCoroutineScope();
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet (drawerContainerColor=MaterialTheme.colorScheme.background){
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
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
            },
        )
        {
            Box(modifier = Modifier.padding(it)) {
                NavHost(navController = navController, startDestination = "HomePage") {
                    composable("HomePage") {
                        HomePage()
                    }
                    composable("AboutPage") {
                        AboutPage()
                    }
                    composable("SettingPage") {
                        SettingPage()
                    }
                    composable("PhotoPage") {
                        PhotoPage()
                    }
                    composable("QrCodeScannerPage") {
                        QrCodeScannerScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(navController: NavHostController, drawerState: DrawerState) {
    var scope = rememberCoroutineScope()

    var currentBackStackEntryAsState = navController.currentBackStackEntryAsState()

    var destination =  currentBackStackEntryAsState.value?.destination
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f,true),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var menuItems = listOf(
                MenuItem(
                    "Home",
                    "HomePage",
                    Icons.Filled.Home,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "About",
                    "AboutPage",
                    Icons.Filled.LocationOn,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Settings",
                    "SettingPage",
                    Icons.Filled.Settings,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Photo",
                    "PhotoPage",
                    Icons.Filled.Star,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Qr Code Scanner",
                    "QrCodeScannerPage",
                    Icons.Filled.Create,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
                MenuItem(
                    "Log out",
                    "LogoutPage",
                    Icons.Filled.Star,
                    navController,
                    scope,
                    destination,
                    drawerState
                ),
            )
            item {
                Spacer(modifier = Modifier.height(40.dp))
                DrawerHeader()
                Spacer(modifier = Modifier.height(40.dp))
            }
            items(menuItems) { item ->
                CustomNavigationItem(item)
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

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerItems(navController: NavHostController, drawerState: DrawerState) {
    var scope = rememberCoroutineScope()

    var currentBackStackEntryAsState = navController.currentBackStackEntryAsState()

    var destination =  currentBackStackEntryAsState.value?.destination
    CustomNavigationItem("Home", "HomePage", Icons.Filled.Home, navController, scope, destination, drawerState)
    Spacer(modifier = Modifier.height(10.dp))
    CustomNavigationItem("About","AboutPage", Icons.Filled.LocationOn, navController, scope, destination, drawerState)
    Spacer(modifier = Modifier.height(10.dp))
    CustomNavigationItem("Settings","SettingPage", Icons.Filled.Settings, navController, scope, destination, drawerState)
}
*/
@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Image here?")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Name here?")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Email here?")
    }
}

@Composable
fun HomePage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Page Content")
    }
}

@Composable
fun AboutPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "About Page Content")
    }
}

@Composable
fun SettingPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Setting Page Content")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationItem(
    item: MenuItem
) {
    NavigationDrawerItem(
        icon = { Icon(item.icon, contentDescription = item.label)},
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

data class MenuItem @OptIn(ExperimentalMaterial3Api::class) constructor(var label: String, var destinationName: String, var icon: ImageVector, var navController: NavController, var scope: CoroutineScope, var destination: NavDestination?, var drawerState: DrawerState)