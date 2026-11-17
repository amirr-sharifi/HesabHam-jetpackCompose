package af.amir.hesabham.presentation.core.mapper

import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.model.category.CategoryId // فرضی - بر اساس الگوی Value Class یا Value Object شما
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import af.amir.hesabham.presentation.core.model.UiText
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector

class UiCategoryMapper(private val context: Context) {

    fun mapToUiModel(category: Category): CategoryUiModel {
        return CategoryUiModel(
            id = category.categoryId.id,
            parentId = category.parentId?.id,
            type = category.categoryType,
            title = getStringResource(category.nameResKey, category.name,context),
            icon = getIconByName(category.iconName),
            color = Color(android.graphics.Color.parseColor(category.color)),
            isDefault = category.isDefault,
            isArchived = category.isArchived
        )
    }

    fun mapToDomainModel(uiModel: CategoryUiModel): Category {
        val (resKey, dynamicName) = extractNameAndResKey(uiModel.title)

        return Category(
            categoryId = CategoryId(uiModel.id),
            parentId = uiModel.parentId?.let { CategoryId(it) },
            categoryType = uiModel.type,
            nameResKey = resKey,
            name = dynamicName,
            iconName = getIconNameByVector(uiModel.icon),
            color = colorToHex(uiModel.color),
            isDefault = uiModel.isDefault,
            isArchived = uiModel.isArchived
        )
    }


    private fun extractNameAndResKey(uiText: UiText): Pair<String?, String?> {
        return when (uiText) {
            is UiText.Resource -> {
                val resourceName = runCatching {
                    context.resources.getResourceEntryName(uiText.textResId)
                }.getOrNull()
                Pair(resourceName, null)
            }
            is UiText.Dynamic -> {
                Pair(null, uiText.text)
            }
        }
    }

    private fun colorToHex(color: Color): String {
        return String.format("#%08X", color.toArgb())
    }

    private fun getIconNameByVector(icon: ImageVector): String {
        return when (icon) {
            Icons.Rounded.Restaurant -> "Restaurant"
            Icons.Rounded.RestaurantMenu -> "RestaurantMenu"
            Icons.Rounded.LocalCafe -> "LocalCafe"
            Icons.Rounded.LocalGroceryStore -> "LocalGroceryStore"
            Icons.Rounded.LunchDining -> "LunchDining"
            Icons.Rounded.DirectionsCar -> "DirectionsCar"
            Icons.Rounded.LocalGasStation -> "LocalGasStation"
            Icons.Rounded.DirectionsBus -> "DirectionsBus"
            Icons.Rounded.LocalTaxi -> "LocalTaxi"
            Icons.Rounded.Build -> "Build"
            Icons.Rounded.Home -> "Home"
            Icons.Rounded.Apartment -> "Apartment"
            Icons.Rounded.Bolt -> "Bolt"
            Icons.Rounded.WaterDrop -> "WaterDrop"
            Icons.Rounded.Wifi -> "Wifi"
            Icons.Rounded.HomeRepairService -> "HomeRepairService"
            Icons.Rounded.LocalHospital -> "LocalHospital"
            Icons.Rounded.MedicalServices -> "MedicalServices"
            Icons.Rounded.LocalPharmacy -> "LocalPharmacy"
            Icons.Rounded.HealthAndSafety -> "HealthAndSafety"
            Icons.Rounded.MedicalInformation -> "MedicalInformation"
            Icons.Rounded.AccountBalance -> "AccountBalance"
            Icons.Rounded.AccountBalanceWallet -> "AccountBalanceWallet"
            Icons.Rounded.ReceiptLong -> "ReceiptLong"
            Icons.Rounded.Payments -> "Payments"
            Icons.Rounded.BusinessCenter -> "BusinessCenter"
            Icons.Rounded.ShowChart -> "ShowChart"
            Icons.Rounded.PointOfSale -> "PointOfSale"
            Icons.Rounded.StackedLineChart -> "StackedLineChart"
            Icons.Rounded.CurrencyBitcoin -> "CurrencyBitcoin"
            Icons.Rounded.TrendingUp -> "TrendingUp"
            Icons.Rounded.ShoppingBag -> "ShoppingBag"
            Icons.Rounded.Checkroom -> "Checkroom"
            Icons.Rounded.Devices -> "Devices"
            Icons.Rounded.Weekend -> "Weekend"
            Icons.Rounded.Movie -> "Movie"
            Icons.Rounded.Theaters -> "Theaters"
            Icons.Rounded.SportsEsports -> "SportsEsports"
            Icons.Rounded.Event -> "Event"
            Icons.Rounded.School -> "School"
            Icons.Rounded.MenuBook -> "MenuBook"
            Icons.Rounded.CastForEducation -> "CastForEducation"
            Icons.Rounded.Flight -> "Flight"
            Icons.Rounded.Hotel -> "Hotel"
            Icons.Rounded.DirectionsTransit -> "DirectionsTransit"
            Icons.Rounded.People -> "People"
            Icons.Rounded.ChildCare -> "ChildCare"
            Icons.Rounded.Savings -> "Savings"
            Icons.Rounded.CardGiftcard -> "CardGiftcard"
            Icons.Rounded.Cake -> "Cake"
            Icons.Rounded.Celebration -> "Celebration"
            Icons.Rounded.Redeem -> "Redeem"
            Icons.Rounded.Subscriptions -> "Subscriptions"
            Icons.Rounded.LiveTv -> "LiveTv"
            Icons.Rounded.Code -> "Code"
            Icons.Rounded.CalendarMonth -> "CalendarMonth"
            Icons.Rounded.Schedule -> "Schedule"
            Icons.Rounded.Replay -> "Replay"
            else -> "Category"
        }
    }




}