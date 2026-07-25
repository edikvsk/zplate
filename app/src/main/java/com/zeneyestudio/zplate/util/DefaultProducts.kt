package com.zeneyestudio.zplate.util

import com.zeneyestudio.zplate.data.model.Product

object DefaultProducts {

    fun getDefaultProducts(): List<Product> = listOf(
        // Белки
        Product(name = "Яйцо куриное", caloriesPer100g = 155, proteinPer100g = 13f, fatPer100g = 11f, carbsPer100g = 1.1f, isDefault = true, category = "Белки"),
        Product(name = "Куриная грудка", caloriesPer100g = 165, proteinPer100g = 31f, fatPer100g = 3.6f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Куриное бедро", caloriesPer100g = 209, proteinPer100g = 26f, fatPer100g = 10.9f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Говядина (нежирная)", caloriesPer100g = 250, proteinPer100g = 26f, fatPer100g = 15f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Свинина (нежирная)", caloriesPer100g = 242, proteinPer100g = 27f, fatPer100g = 14f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Баранина", caloriesPer100g = 294, proteinPer100g = 25f, fatPer100g = 21f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Индейка", caloriesPer100g = 189, proteinPer100g = 29f, fatPer100g = 7f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Лосось", caloriesPer100g = 208, proteinPer100g = 20f, fatPer100g = 13f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Тунец", caloriesPer100g = 132, proteinPer100g = 28f, fatPer100g = 1.3f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Треска", caloriesPer100g = 82, proteinPer100g = 18f, fatPer100g = 0.7f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Творог 5%", caloriesPer100g = 121, proteinPer100g = 17.2f, fatPer100g = 5f, carbsPer100g = 1.8f, isDefault = true, category = "Белки"),
        Product(name = "Творог 0%", caloriesPer100g = 71, proteinPer100g = 18f, fatPer100g = 0.1f, carbsPer100g = 1.8f, isDefault = true, category = "Белки"),
        Product(name = "Сыр твёрдый", caloriesPer100g = 350, proteinPer100g = 26f, fatPer100g = 27f, carbsPer100g = 0f, isDefault = true, category = "Белки"),
        Product(name = "Сыр моцарелла", caloriesPer100g = 280, proteinPer100g = 28f, fatPer100g = 17f, carbsPer100g = 3.1f, isDefault = true, category = "Белки"),
        Product(name = "Йогурт натуральный", caloriesPer100g = 59, proteinPer100g = 10f, fatPer100g = 0.7f, carbsPer100g = 3.6f, isDefault = true, category = "Белки"),
        Product(name = "Кефир 1%", caloriesPer100g = 40, proteinPer100g = 3f, fatPer100g = 1f, carbsPer100g = 4f, isDefault = true, category = "Белки"),
        Product(name = "Молоко 2.5%", caloriesPer100g = 52, proteinPer100g = 2.8f, fatPer100g = 2.5f, carbsPer100g = 4.7f, isDefault = true, category = "Белки"),

        // Углеводы
        Product(name = "Гречка (крупа)", caloriesPer100g = 343, proteinPer100g = 13f, fatPer100g = 3.4f, carbsPer100g = 72f, isDefault = true, category = "Углеводы"),
        Product(name = "Рис (крупа)", caloriesPer100g = 344, proteinPer100g = 6.7f, fatPer100g = 0.7f, carbsPer100g = 79f, isDefault = true, category = "Углеводы"),
        Product(name = "Овсянка (хлопья)", caloriesPer100g = 352, proteinPer100g = 13f, fatPer100g = 6.2f, carbsPer100g = 62f, isDefault = true, category = "Углеводы"),
        Product(name = "Макароны", caloriesPer100g = 350, proteinPer100g = 12f, fatPer100g = 1.5f, carbsPer100g = 72f, isDefault = true, category = "Углеводы"),
        Product(name = "Хлеб белый", caloriesPer100g = 265, proteinPer100g = 9f, fatPer100g = 3.3f, carbsPer100g = 49f, isDefault = true, category = "Углеводы"),
        Product(name = "Хлеб чёрный", caloriesPer100g = 210, proteinPer100g = 7f, fatPer100g = 1.3f, carbsPer100g = 41f, isDefault = true, category = "Углеводы"),
        Product(name = "Картофель", caloriesPer100g = 77, proteinPer100g = 2f, fatPer100g = 0.1f, carbsPer100g = 17f, isDefault = true, category = "Углеводы"),
        Product(name = "Картофель жареный", caloriesPer100g = 192, proteinPer100g = 2.8f, fatPer100g = 9.5f, carbsPer100g = 23f, isDefault = true, category = "Углеводы"),
        Product(name = "Киноа", caloriesPer100g = 368, proteinPer100g = 14f, fatPer100g = 6f, carbsPer100g = 64f, isDefault = true, category = "Углеводы"),
        Product(name = "Булгур", caloriesPer100g = 342, proteinPer100g = 12f, fatPer100g = 1.5f, carbsPer100g = 73f, isDefault = true, category = "Углеводы"),
        Product(name = "Перловка", caloriesPer100g = 352, proteinPer100g = 12f, fatPer100g = 2.3f, carbsPer100g = 73f, isDefault = true, category = "Углеводы"),
        Product(name = "Пшено", caloriesPer100g = 378, proteinPer100g = 11f, fatPer100g = 4.2f, carbsPer100g = 73f, isDefault = true, category = "Углеводы"),

        // Жиры
        Product(name = "Масло подсолнечное", caloriesPer100g = 899, proteinPer100g = 0f, fatPer100g = 99.9f, carbsPer100g = 0f, isDefault = true, category = "Жиры"),
        Product(name = "Масло сливочное", caloriesPer100g = 748, proteinPer100g = 0.5f, fatPer100g = 82.5f, carbsPer100g = 0.8f, isDefault = true, category = "Жиры"),
        Product(name = "Масло оливковое", caloriesPer100g = 884, proteinPer100g = 0f, fatPer100g = 100f, carbsPer100g = 0f, isDefault = true, category = "Жиры"),
        Product(name = "Авокадо", caloriesPer100g = 160, proteinPer100g = 2f, fatPer100g = 15f, carbsPer100g = 9f, isDefault = true, category = "Жиры"),
        Product(name = "Грецкий орех", caloriesPer100g = 654, proteinPer100g = 15f, fatPer100g = 65f, carbsPer100g = 14f, isDefault = true, category = "Жиры"),
        Product(name = "Миндаль", caloriesPer100g = 579, proteinPer100g = 21f, fatPer100g = 50f, carbsPer100g = 22f, isDefault = true, category = "Жиры"),
        Product(name = "Арахис", caloriesPer100g = 567, proteinPer100g = 26f, fatPer100g = 49f, carbsPer100g = 16f, isDefault = true, category = "Жиры"),
        Product(name = "Кешью", caloriesPer100g = 553, proteinPer100g = 18f, fatPer100g = 44f, carbsPer100g = 30f, isDefault = true, category = "Жиры"),
        Product(name = "Сливки 20%", caloriesPer100g = 205, proteinPer100g = 2.8f, fatPer100g = 20f, carbsPer100g = 3.7f, isDefault = true, category = "Жиры"),

        // Овощи
        Product(name = "Помидор", caloriesPer100g = 18, proteinPer100g = 0.9f, fatPer100g = 0.2f, carbsPer100g = 3.9f, isDefault = true, category = "Овощи"),
        Product(name = "Огурец", caloriesPer100g = 15, proteinPer100g = 0.7f, fatPer100g = 0.1f, carbsPer100g = 3.6f, isDefault = true, category = "Овощи"),
        Product(name = "Капуста белокочанная", caloriesPer100g = 25, proteinPer100g = 1.3f, fatPer100g = 0.1f, carbsPer100g = 5.8f, isDefault = true, category = "Овощи"),
        Product(name = "Капуста брокколи", caloriesPer100g = 34, proteinPer100g = 2.8f, fatPer100g = 0.4f, carbsPer100g = 7f, isDefault = true, category = "Овощи"),
        Product(name = "Морковь", caloriesPer100g = 41, proteinPer100g = 0.9f, fatPer100g = 0.2f, carbsPer100g = 9.6f, isDefault = true, category = "Овощи"),
        Product(name = "Лук репчатый", caloriesPer100g = 40, proteinPer100g = 1.1f, fatPer100g = 0.1f, carbsPer100g = 9.3f, isDefault = true, category = "Овощи"),
        Product(name = "Перец болгарский", caloriesPer100g = 27, proteinPer100g = 1.3f, fatPer100g = 0.1f, carbsPer100g = 5.3f, isDefault = true, category = "Овощи"),
        Product(name = "Свёкла", caloriesPer100g = 43, proteinPer100g = 1.6f, fatPer100g = 0.1f, carbsPer100g = 9.6f, isDefault = true, category = "Овощи"),
        Product(name = "Тыква", caloriesPer100g = 26, proteinPer100g = 1f, fatPer100g = 0.1f, carbsPer100g = 6.5f, isDefault = true, category = "Овощи"),
        Product(name = "Кабачок", caloriesPer100g = 17, proteinPer100g = 1.2f, fatPer100g = 0.3f, carbsPer100g = 3.1f, isDefault = true, category = "Овощи"),
        Product(name = "Шпинат", caloriesPer100g = 23, proteinPer100g = 2.9f, fatPer100g = 0.4f, carbsPer100g = 3.6f, isDefault = true, category = "Овощи"),
        Product(name = "Салат листовой", caloriesPer100g = 15, proteinPer100g = 1.4f, fatPer100g = 0.2f, carbsPer100g = 2.9f, isDefault = true, category = "Овощи"),

        // Фрукты
        Product(name = "Яблоко", caloriesPer100g = 52, proteinPer100g = 0.3f, fatPer100g = 0.2f, carbsPer100g = 14f, isDefault = true, category = "Фрукты"),
        Product(name = "Банан", caloriesPer100g = 89, proteinPer100g = 1.1f, fatPer100g = 0.3f, carbsPer100g = 23f, isDefault = true, category = "Фрукты"),
        Product(name = "Апельсин", caloriesPer100g = 47, proteinPer100g = 0.9f, fatPer100g = 0.1f, carbsPer100g = 12f, isDefault = true, category = "Фрукты"),
        Product(name = "Виноград", caloriesPer100g = 69, proteinPer100g = 0.7f, fatPer100g = 0.2f, carbsPer100g = 18f, isDefault = true, category = "Фрукты"),
        Product(name = "Груша", caloriesPer100g = 57, proteinPer100g = 0.4f, fatPer100g = 0.1f, carbsPer100g = 15f, isDefault = true, category = "Фрукты"),
        Product(name = "Клубника", caloriesPer100g = 32, proteinPer100g = 0.7f, fatPer100g = 0.3f, carbsPer100g = 7.7f, isDefault = true, category = "Фрукты"),
        Product(name = "Арбуз", caloriesPer100g = 30, proteinPer100g = 0.6f, fatPer100g = 0.2f, carbsPer100g = 7.6f, isDefault = true, category = "Фрукты"),
        Product(name = "Дыня", caloriesPer100g = 34, proteinPer100g = 0.8f, fatPer100g = 0.2f, carbsPer100g = 8.2f, isDefault = true, category = "Фрукты"),
        Product(name = "Киви", caloriesPer100g = 61, proteinPer100g = 1.1f, fatPer100g = 0.5f, carbsPer100g = 15f, isDefault = true, category = "Фрукты"),
        Product(name = "Манго", caloriesPer100g = 60, proteinPer100g = 0.8f, fatPer100g = 0.4f, carbsPer100g = 15f, isDefault = true, category = "Фрукты"),
        Product(name = "Ананас", caloriesPer100g = 50, proteinPer100g = 0.5f, fatPer100g = 0.1f, carbsPer100g = 13f, isDefault = true, category = "Фрукты"),
        Product(name = "Гранат", caloriesPer100g = 83, proteinPer100g = 1.7f, fatPer100g = 1.2f, carbsPer100g = 19f, isDefault = true, category = "Фрукты"),

        // Молочные продукты
        Product(name = "Сметана 15%", caloriesPer100g = 158, proteinPer100g = 2.6f, fatPer100g = 15f, carbsPer100g = 3f, isDefault = true, category = "Молочные"),
        Product(name = "Сметана 20%", caloriesPer100g = 206, proteinPer100g = 2.8f, fatPer100g = 20f, carbsPer100g = 3.2f, isDefault = true, category = "Молочные"),
        Product(name = "Масло сливочное 82.5%", caloriesPer100g = 748, proteinPer100g = 0.5f, fatPer100g = 82.5f, carbsPer100g = 0.8f, isDefault = true, category = "Молочные"),

        // Бобовые
        Product(name = "Чечевица", caloriesPer100g = 352, proteinPer100g = 25f, fatPer100g = 1.1f, carbsPer100g = 60f, isDefault = true, category = "Бобовые"),
        Product(name = "Нут", caloriesPer100g = 364, proteinPer100g = 19f, fatPer100g = 6f, carbsPer100g = 61f, isDefault = true, category = "Бобовые"),
        Product(name = "Фасоль", caloriesPer100g = 333, proteinPer100g = 21f, fatPer100g = 1.5f, carbsPer100g = 60f, isDefault = true, category = "Бобовые"),
        Product(name = "Горох", caloriesPer100g = 341, proteinPer100g = 23f, fatPer100g = 1.6f, carbsPer100g = 60f, isDefault = true, category = "Бобовые"),
        Product(name = "Соя", caloriesPer100g = 446, proteinPer100g = 36f, fatPer100g = 20f, carbsPer100g = 30f, isDefault = true, category = "Бобовые"),

        // Напитки
        Product(name = "Сок апельсиновый", caloriesPer100g = 45, proteinPer100g = 0.7f, fatPer100g = 0.2f, carbsPer100g = 10f, isDefault = true, category = "Напитки"),
        Product(name = "Сок яблочный", caloriesPer100g = 46, proteinPer100g = 0.1f, fatPer100g = 0.1f, carbsPer100g = 11f, isDefault = true, category = "Напитки"),
        Product(name = "Чай без сахара", caloriesPer100g = 1, proteinPer100g = 0f, fatPer100g = 0f, carbsPer100g = 0.3f, isDefault = true, category = "Напитки"),
        Product(name = "Кофе без сахара", caloriesPer100g = 2, proteinPer100g = 0.1f, fatPer100g = 0f, carbsPer100g = 0.3f, isDefault = true, category = "Напитки"),
        Product(name = "Кофе с молоком", caloriesPer100g = 28, proteinPer100g = 1.5f, fatPer100g = 1f, carbsPer100g = 3f, isDefault = true, category = "Напитки"),

        // Мясо и колбасы
        Product(name = "Ветчина", caloriesPer100g = 239, proteinPer100g = 27f, fatPer100g = 14f, carbsPer100g = 1f, isDefault = true, category = "Мясо"),
        Product(name = "Колбаса варёная", caloriesPer100g = 260, proteinPer100g = 12f, fatPer100g = 22f, carbsPer100g = 1.5f, isDefault = true, category = "Мясо"),
        Product(name = "Бекон", caloriesPer100g = 541, proteinPer100g = 37f, fatPer100g = 42f, carbsPer100g = 1.4f, isDefault = true, category = "Мясо"),

        // Сладости
        Product(name = "Шоколад молочный", caloriesPer100g = 535, proteinPer100g = 7f, fatPer100g = 30f, carbsPer100g = 59f, isDefault = true, category = "Сладости"),
        Product(name = "Шоколад тёмный 70%", caloriesPer100g = 598, proteinPer100g = 7.8f, fatPer100g = 43f, carbsPer100g = 46f, isDefault = true, category = "Сладости"),
        Product(name = "Мёд", caloriesPer100g = 304, proteinPer100g = 0.3f, fatPer100g = 0f, carbsPer100g = 82f, isDefault = true, category = "Сладости"),
        Product(name = "Сахар", caloriesPer100g = 387, proteinPer100g = 0f, fatPer100g = 0f, carbsPer100g = 100f, isDefault = true, category = "Сладости"),
        Product(name = "Печенье", caloriesPer100g = 466, proteinPer100g = 7.5f, fatPer100g = 20f, carbsPer100g = 65f, isDefault = true, category = "Сладости"),
        Product(name = "Зефир", caloriesPer100g = 326, proteinPer100g = 0.8f, fatPer100g = 0.1f, carbsPer100g = 80f, isDefault = true, category = "Сладости"),
        Product(name = "Пастила", caloriesPer100g = 338, proteinPer100g = 0.5f, fatPer100g = 0.1f, carbsPer100g = 80f, isDefault = true, category = "Сладости"),
        Product(name = "Мороженое", caloriesPer100g = 207, proteinPer100g = 3.5f, fatPer100g = 11f, carbsPer100g = 24f, isDefault = true, category = "Сладости"),

        // Соусы
        Product(name = "Майонез", caloriesPer100g = 629, proteinPer100g = 0.3f, fatPer100g = 67f, carbsPer100g = 3.9f, isDefault = true, category = "Соусы"),
        Product(name = "Кетчуп", caloriesPer100g = 112, proteinPer100g = 1.8f, fatPer100g = 1f, carbsPer100g = 22f, isDefault = true, category = "Соусы"),
        Product(name = "Соевый соус", caloriesPer100g = 53, proteinPer100g = 8f, fatPer100g = 0f, carbsPer100g = 4.9f, isDefault = true, category = "Соусы"),
        Product(name = "Уксус", caloriesPer100g = 18, proteinPer100g = 0f, fatPer100g = 0f, carbsPer100g = 0.6f, isDefault = true, category = "Соусы"),

        // Готовые блюда
        Product(name = "Омлет из 2 яиц", caloriesPer100g = 154, proteinPer100g = 11f, fatPer100g = 11f, carbsPer100g = 1.6f, isDefault = true, category = "Готовые"),
        Product(name = "Яичница из 2 яиц", caloriesPer100g = 227, proteinPer100g = 14f, fatPer100g = 18f, carbsPer100g = 1.8f, isDefault = true, category = "Готовые"),
        Product(name = "Блины", caloriesPer100g = 233, proteinPer100g = 6.1f, fatPer100g = 12f, carbsPer100g = 26f, isDefault = true, category = "Готовые"),
        Product(name = "Плов", caloriesPer100g = 218, proteinPer100g = 8f, fatPer100g = 10f, carbsPer100g = 24f, isDefault = true, category = "Готовые"),
        Product(name = "Щи", caloriesPer100g = 31, proteinPer100g = 1f, fatPer100g = 1.5f, carbsPer100g = 3.5f, isDefault = true, category = "Готовые"),
        Product(name = "Борщ", caloriesPer100g = 49, proteinPer100g = 1.1f, fatPer100g = 2.2f, carbsPer100g = 6.5f, isDefault = true, category = "Готовые"),
        Product(name = "Суп куриный", caloriesPer100g = 56, proteinPer100g = 5f, fatPer100g = 2.5f, carbsPer100g = 3.5f, isDefault = true, category = "Готовые"),
        Product(name = "Гречка варёная", caloriesPer100g = 132, proteinPer100g = 5f, fatPer100g = 1.3f, carbsPer100g = 25f, isDefault = true, category = "Готовые"),
        Product(name = "Рис варёный", caloriesPer100g = 130, proteinPer100g = 2.7f, fatPer100g = 0.3f, carbsPer100g = 28f, isDefault = true, category = "Готовые"),
        Product(name = "Овсянка на молоке", caloriesPer100g = 102, proteinPer100g = 3.2f, fatPer100g = 2.5f, carbsPer100g = 17f, isDefault = true, category = "Готовые"),
        Product(name = "Тост белый", caloriesPer100g = 290, proteinPer100g = 9f, fatPer100g = 5.5f, carbsPer100g = 52f, isDefault = true, category = "Готовые"),
        Product(name = "Сэндвич с курицей", caloriesPer100g = 210, proteinPer100g = 15f, fatPer100g = 8f, carbsPer100g = 18f, isDefault = true, category = "Готовые"),

        // Орехи и семечки
        Product(name = "Семечки подсолнуха", caloriesPer100g = 584, proteinPer100g = 21f, fatPer100g = 51f, carbsPer100g = 20f, isDefault = true, category = "Орехи"),
        Product(name = "Семечки тыквы", caloriesPer100g = 559, proteinPer100g = 30f, fatPer100g = 49f, carbsPer100g = 11f, isDefault = true, category = "Орехи"),
        Product(name = "Кунжут", caloriesPer100g = 573, proteinPer100g = 18f, fatPer100g = 50f, carbsPer100g = 23f, isDefault = true, category = "Орехи"),
        Product(name = "Лён", caloriesPer100g = 534, proteinPer100g = 18f, fatPer100g = 42f, carbsPer100g = 29f, isDefault = true, category = "Орехи"),
        Product(name = "Фисташки", caloriesPer100g = 560, proteinPer100g = 20f, fatPer100g = 45f, carbsPer100g = 27f, isDefault = true, category = "Орехи"),
        Product(name = "Фундук", caloriesPer100g = 628, proteinPer100g = 15f, fatPer100g = 61f, carbsPer100g = 17f, isDefault = true, category = "Орехи"),
        Product(name = "Кешью", caloriesPer100g = 553, proteinPer100g = 18f, fatPer100g = 44f, carbsPer100g = 30f, isDefault = true, category = "Орехи"),
        Product(name = "Арахисовая паста", caloriesPer100g = 588, proteinPer100g = 25f, fatPer100g = 50f, carbsPer100g = 20f, isDefault = true, category = "Орехи"),

        // Напитки алкогольные
        Product(name = "Пиво", caloriesPer100g = 43, proteinPer100g = 0.5f, fatPer100g = 0f, carbsPer100g = 3.6f, isDefault = true, category = "Напитки"),
        Product(name = "Вино белое", caloriesPer100g = 82, proteinPer100g = 0.1f, fatPer100g = 0f, carbsPer100g = 2.6f, isDefault = true, category = "Напитки"),
        Product(name = "Вино красное", caloriesPer100g = 85, proteinPer100g = 0.1f, fatPer100g = 0f, carbsPer100g = 2.6f, isDefault = true, category = "Напитки"),
        Product(name = "Водка", caloriesPer100g = 235, proteinPer100g = 0f, fatPer100g = 0f, carbsPer100g = 0f, isDefault = true, category = "Напитки"),
    )
}
