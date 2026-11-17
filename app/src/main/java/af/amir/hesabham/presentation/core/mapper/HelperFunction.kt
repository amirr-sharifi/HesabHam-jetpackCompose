package af.amir.hesabham.presentation.core.mapper

import af.amir.hesabham.presentation.core.model.UiText
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CastForEducation
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DirectionsTransit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.HomeRepairService
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalPharmacy
import androidx.compose.material.icons.rounded.LocalTaxi
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.MedicalInformation
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.StackedLineChart
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Weekend
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconByName(iconName: String): ImageVector {
    return when (iconName) {
        "Restaurant" -> Icons.Rounded.Restaurant
        "RestaurantMenu" -> Icons.Rounded.RestaurantMenu
        "LocalCafe" -> Icons.Rounded.LocalCafe
        "LocalGroceryStore" -> Icons.Rounded.LocalGroceryStore
        "LunchDining" -> Icons.Rounded.LunchDining
        "DirectionsCar" -> Icons.Rounded.DirectionsCar
        "LocalGasStation" -> Icons.Rounded.LocalGasStation
        "DirectionsBus" -> Icons.Rounded.DirectionsBus
        "LocalTaxi" -> Icons.Rounded.LocalTaxi
        "Build" -> Icons.Rounded.Build
        "Home" -> Icons.Rounded.Home
        "Apartment" -> Icons.Rounded.Apartment
        "Bolt" -> Icons.Rounded.Bolt
        "WaterDrop" -> Icons.Rounded.WaterDrop
        "Wifi" -> Icons.Rounded.Wifi
        "HomeRepairService" -> Icons.Rounded.HomeRepairService
        "LocalHospital" -> Icons.Rounded.LocalHospital
        "MedicalServices" -> Icons.Rounded.MedicalServices
        "LocalPharmacy" -> Icons.Rounded.LocalPharmacy
        "HealthAndSafety" -> Icons.Rounded.HealthAndSafety
        "MedicalInformation" -> Icons.Rounded.MedicalInformation
        "AccountBalance" -> Icons.Rounded.AccountBalance
        "AccountBalanceWallet" -> Icons.Rounded.AccountBalanceWallet
        "ReceiptLong" -> Icons.Rounded.ReceiptLong
        "Payments" -> Icons.Rounded.Payments
        "BusinessCenter" -> Icons.Rounded.BusinessCenter
        "ShowChart" -> Icons.Rounded.ShowChart
        "PointOfSale" -> Icons.Rounded.PointOfSale
        "StackedLineChart" -> Icons.Rounded.StackedLineChart
        "CurrencyBitcoin" -> Icons.Rounded.CurrencyBitcoin
        "TrendingUp" -> Icons.Rounded.TrendingUp
        "ShoppingBag" -> Icons.Rounded.ShoppingBag
        "Checkroom" -> Icons.Rounded.Checkroom
        "Devices" -> Icons.Rounded.Devices
        "Weekend" -> Icons.Rounded.Weekend
        "Movie" -> Icons.Rounded.Movie
        "Theaters" -> Icons.Rounded.Theaters
        "SportsEsports" -> Icons.Rounded.SportsEsports
        "Event" -> Icons.Rounded.Event
        "School" -> Icons.Rounded.School
        "MenuBook" -> Icons.Rounded.MenuBook
        "CastForEducation" -> Icons.Rounded.CastForEducation
        "Flight" -> Icons.Rounded.Flight
        "Hotel" -> Icons.Rounded.Hotel
        "DirectionsTransit" -> Icons.Rounded.DirectionsTransit
        "People" -> Icons.Rounded.People
        "ChildCare" -> Icons.Rounded.ChildCare
        "Savings" -> Icons.Rounded.Savings
        "CardGiftcard" -> Icons.Rounded.CardGiftcard
        "Cake" -> Icons.Rounded.Cake
        "Celebration" -> Icons.Rounded.Celebration
        "Redeem" -> Icons.Rounded.Redeem
        "Subscriptions" -> Icons.Rounded.Subscriptions
        "LiveTv" -> Icons.Rounded.LiveTv
        "Code" -> Icons.Rounded.Code
        "CalendarMonth" -> Icons.Rounded.CalendarMonth
        "Schedule" -> Icons.Rounded.Schedule
        "Replay" -> Icons.Rounded.Replay
        else -> Icons.Rounded.Category
    }
}


fun getStringResource(nameResKey: String?, name: String?,context: Context): UiText {
    if (nameResKey == null) return UiText.Dynamic(name ?: "")
    val packageName = context.packageName
    val resId = context.resources.getIdentifier(nameResKey, "string", packageName)
    return if (resId != 0) UiText.Resource(resId) else UiText.Dynamic("")
}