package com.example.bledemoapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bledemoapp.database.tables.DeviceEntity

/**
 * [DeviceDao] DAO class to perform device related DB operations
 * @author Aanal Shah
 */
@Dao
abstract class DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertScannedDevice(vararg devices: DeviceEntity)

    @Query("SELECT COUNT(*) FROM device")
    abstract suspend fun getDeviceDataCount(): Int
}
