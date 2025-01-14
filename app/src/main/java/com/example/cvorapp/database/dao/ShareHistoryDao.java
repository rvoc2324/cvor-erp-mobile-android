package com.example.cvorapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cvorapp.models.ShareHistory;

import java.util.List;

@Dao
public interface ShareHistoryDao {

    // Insert a single share history entry
    @Insert
    void insert(ShareHistory shareHistory);

    // Insert multiple share history entries
    @Insert
    void insertAll(List<ShareHistory> shareHistories);

    // Retrieve all share history entries, sorted by shared date in descending order
    @Query("SELECT * FROM share_history ORDER BY sharedDate DESC")
    List<ShareHistory> getAllShareHistory();

    // Retrieve share history entries by specific file name
    @Query("SELECT * FROM share_history WHERE fileName = :fileName ORDER BY sharedDate DESC")
    List<ShareHistory> getShareHistoryByFileName(String fileName);

    // Retrieve share history entries by share medium (e.g., WhatsApp, Gmail)
    @Query("SELECT * FROM share_history WHERE shareMedium = :shareMedium ORDER BY sharedDate DESC")
    List<ShareHistory> getShareHistoryByShareMedium(String shareMedium);

    // Retrieve share history entries within a date range
    @Query("SELECT * FROM share_history WHERE sharedDate BETWEEN :startDate AND :endDate ORDER BY sharedDate DESC")
    List<ShareHistory> getShareHistoryByDateRange(long startDate, long endDate);

    // Retrieve the most recent share history entry
    @Query("SELECT * FROM share_history ORDER BY sharedDate DESC LIMIT 1")
    ShareHistory getLatestShareHistory();

    // Delete specific share history entry
    @Delete
    void delete(ShareHistory shareHistory);

    // Delete all share history entries
    @Query("DELETE FROM share_history")
    void deleteAll();
}
