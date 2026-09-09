package com.bulkbasket.ui.buyer.category

fun getCategoryEmoji(name: String): String {
    return when (name.lowercase().trim()) {
        "grains", "grain"           -> "🌾"
        "produce", "vegetables"     -> "🌿"
        "oils", "oil"               -> "🫙"
        "spices", "spice"           -> "⭐"
        "protein", "proteins",
        "fish", "meat"              -> "🐟"
        "bakery", "bread"           -> "🥐"
        "dairy", "milk"             -> "🥛"
        "beverages", "drinks"       -> "🥤"
        "household"                 -> "🧴"
        "snacks", "snack"           -> "🍿"
        "fruits", "fruit"           -> "🍎"
        "frozen"                    -> "🧊"
        else                        -> "🛒"
    }
}
