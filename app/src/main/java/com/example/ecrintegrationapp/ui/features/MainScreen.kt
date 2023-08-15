import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.MainActivity

@Composable
fun MainScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            navController.navigate(MainActivity.ERC_ROUTE)
        }) {
            Text(text = "ERC")
        }

        Button(onClick = {
            navController.navigate(MainActivity.BLUETOOTH_ROUTE)
        }) {
            Text(text = "Bluetooth Scanner")
        }
    }
}