package com.example.randominsect.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.randominsect.data.model.Insect
import kotlinx.coroutines.flow.Flow

@Dao
interface InsectDao {

    @Query("SELECT * FROM insects ORDER BY createdAt ASC")
    fun getAllInsects(): Flow<List<Insect>>

    @Query("SELECT * FROM insects WHERE id = :id")
    suspend fun getInsectById(id: String): Insect?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsect(insect: Insect)

    @Update
    suspend fun updateInsect(insect: Insect)

    @Delete
    suspend fun deleteInsect(insect: Insect)
}
