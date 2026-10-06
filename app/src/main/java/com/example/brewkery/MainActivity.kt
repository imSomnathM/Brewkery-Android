package com.example.brewkery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.brewkery.ui.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { BrewkeryTheme { BrewkeryApp() } }
    }
}

@Composable
fun BrewkeryApp(vm: BrewViewModel = viewModel()) {
    val nav = rememberNavController()
    val menu by vm.menu.collectAsState()
    val cart by vm.cart.collectAsState()
    val order by vm.order.collectAsState()
    val toast by vm.toast.collectAsState()

    val data = (menu as? MenuUiState.Success)?.data
    val deliveryFee = data?.meta?.deliveryFee ?: 2.5
    val taxRate = data?.meta?.taxRatePercent ?: 8.0
    val eta = data?.meta?.estimatedDeliveryTime ?: "20 - 30 mins"

    // Auto-hide the toast after ~2 seconds
    LaunchedEffect(toast?.id) {
        val current = toast
        if (current != null) {
            delay(2200)
            vm.dismissToast(current.id)
        }
    }

    Box {
        NavHost(navController = nav, startDestination = "home") {

            composable("home") {
                HomeScreen(
                    menu = menu,
                    cart = cart,
                    order = order,
                    onRetry = vm::load,
                    onItemClick = { id -> nav.navigate("detail/$id") },
                    onCartClick = { nav.navigate("cart") },
                    onTrack = { nav.navigate("status") }
                )
            }

            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id")
                val item = data?.items?.find { it.id == id }
                if (item != null) {
                    DetailScreen(
                        item = item,
                        onBack = { nav.popBackStack() },
                        onAdd = { size, milk, sugar, qty ->
                            vm.addToCart(item, size, milk, sugar, qty)
                            nav.popBackStack()
                        }
                    )
                }
            }

            composable("cart") {
                CartScreen(
                    cart = cart,
                    deliveryFee = deliveryFee,
                    taxRatePercent = taxRate,
                    onBack = { nav.popBackStack() },
                    onQtyChange = vm::changeQty,
                    onClear = vm::clearCart,
                    onPlaceOrder = {
                        vm.placeOrder()
                        nav.navigate("status") { popUpTo("home") }
                    }
                )
            }

            composable("status") {
                order?.let {
                    OrderStatusScreen(it, eta) { nav.popBackStack("home", inclusive = false) }
                }
            }
        }

        // Drawn last so it floats above every screen
        ToastHost(toast, Modifier.align(Alignment.TopCenter))
    }
}
