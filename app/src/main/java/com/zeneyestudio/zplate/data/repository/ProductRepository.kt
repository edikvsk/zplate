package com.zeneyestudio.zplate.data.repository

import com.zeneyestudio.zplate.data.db.ProductDao
import com.zeneyestudio.zplate.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    fun getDefaultProducts(): Flow<List<Product>> = productDao.getDefaultProducts()

    fun getCustomProducts(): Flow<List<Product>> = productDao.getCustomProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun insertProducts(products: List<Product>) = productDao.insertProducts(products)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun getProductCount(): Int = productDao.getProductCount()
}
