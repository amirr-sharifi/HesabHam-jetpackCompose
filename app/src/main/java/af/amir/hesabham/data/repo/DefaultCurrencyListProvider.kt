package af.amir.hesabham.data.repo

import af.amir.hesabham.data.util.readRawJsonFromAssetsFile
import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.domain.provider.CurrencyListProvider
import android.content.Context
import org.json.JSONArray

class DefaultCurrencyListProvider(
    private val context: Context,
) : CurrencyListProvider {
    override suspend fun getALl(): List<AppCurrency> {
        val rawJson = readRawJsonFromAssetsFile(context,"currencies.json")
        val jsonArray = JSONArray(rawJson)
        val result = mutableListOf<AppCurrency>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result += AppCurrency(
                code = obj.getString("code"),
            )
        }
        return result
    }


}