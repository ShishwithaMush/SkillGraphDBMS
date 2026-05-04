package com.dbms.service;

import com.dbms.DBConnection;
import com.dbms.model.ProgressInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProgressService {
    public ProgressInfo getProgress(int userId) throws SQLException {
        String sql = "SELECT placement_score,last_updated FROM (SELECT placement_score,last_updated FROM PROGRESS WHERE user_id=? ORDER BY last_updated DESC) WHERE ROWNUM <= 1";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProgressInfo(resultSet.getInt("placement_score"), resultSet.getTimestamp("last_updated") == null ? null : resultSet.getTimestamp("last_updated").toLocalDateTime());
                }
            }
        }
        return new ProgressInfo(0, null);
    }

    public void upsertProgress(int userId, int score) throws SQLException {
        int safeScore = Math.max(0, Math.min(100, score));
        String update = "UPDATE PROGRESS SET placement_score=?, last_updated=SYSTIMESTAMP WHERE user_id=?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, safeScore);
            statement.setInt(2, userId);
            if (statement.executeUpdate() == 0) {
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO PROGRESS(user_id,placement_score,last_updated) VALUES(?,?,SYSTIMESTAMP)")) {
                    insert.setInt(1, userId);
                    insert.setInt(2, safeScore);
                    insert.executeUpdate();
                }
            }
        }
    }
}
