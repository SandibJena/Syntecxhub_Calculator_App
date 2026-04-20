package com.noguts.calcy.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val dao: CalculationHistoryDao
) {

    fun observeHistory(): Flow<List<CalculationHistoryEntity>> = dao.observeHistory()

    suspend fun addHistory(expression: String, result: String) {
        dao.insert(
            CalculationHistoryEntity(
                expression = expression,
                result = result
            )
        )
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
