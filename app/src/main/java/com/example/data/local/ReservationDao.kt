package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations ORDER BY reservationDate ASC, reservationTime ASC")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE status = 'CONFIRMED' OR status = 'PENDING' ORDER BY reservationDate ASC, reservationTime ASC")
    fun getUpcomingReservations(): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

    @Query("UPDATE reservations SET status = :status WHERE id = :id")
    suspend fun updateReservationStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM reservations WHERE reservationDate = :date AND reservationTime = :time AND status != 'CANCELLED'")
    suspend fun getBookingsCountForSlot(date: String, time: String): Int
}
