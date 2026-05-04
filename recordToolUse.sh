package com.dbms.service;

import com.dbms.DBConnection;
import com.dbms.model.Roadmap;
import com.dbms.model.TaskItem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoadmapService {
    public int createPersonalizedRoadmap(int userId, String targetRole, String skills, String level, String languages, String blockers) throws SQLException {
        int roadmapId;
        try (Connection connection = DBConnection.getConnection()) {
            roadmapId = insertRoadmap(connection, userId, targetRole);
            addTasks(connection, roadmapId, generateRoadmapTasks(targetRole, skills, level, languages, blockers));
        }
        return roadmapId;
    }

    public void addTasksToRoadmap(int roadmapId, List<String> tasks) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            addTasks(connection, roadmapId, tasks);
        }
    }

    private int insertRoadmap(Connection connection, int userId, String targetRole) throws SQLException {
        String sql = "INSERT INTO AI_ROADMAP(user_id,target_role,generated_date) VALUES(?,?,SYSDATE)";
        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ROADMAP_ID"})) {
            statement.setInt(1, userId);
            statement.setString(2, targetRole.trim());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        try (PreparedStatement fallback = connection.prepareStatement("SELECT MAX(roadmap_id) FROM AI_ROADMAP WHERE user_id=?")) {
            fallback.setInt(1, userId);
            try (ResultSet resultSet = fallback.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("Roadmap was inserted but roadmap_id could not be read.");
    }

    private void addTasks(Connection connection, int roadmapId, List<String> tasks) throws SQLException {
        for (String task : tasks) insertTask(connection, roadmapId, task);
    }

    private void insertTask(Connection connection, int roadmapId, String title) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO TASKS(roadmap_id,task_title,status) VALUES(?,?,?)")) {
            statement.setInt(1, roadmapId);
            statement.setString(2, fitTaskTitle(title));
            statement.setString(3, "Pending");
            statement.executeUpdate();
        }
    }

    private String fitTaskTitle(String title) {
        if (title == null) return "Untitled task";
        String cleaned = title.trim().replaceAll("\\s+", " ");
        return cleaned.length() <= 240 ? cleaned : cleaned.substring(0, 237) + "...";
    }

    private List<String> generateRoadmapTasks(String targetRole, String skills, String level, String languages, String blockers) {
        List<String> tasks = new ArrayList<>();
        tasks.add("ROADMAP Phase 1 - Goal clarity: Become " + targetRole + ". Write why you want this role and what proof project you will build.");
        tasks.add("ROADMAP Phase 2 - Skill audit: Current skills are " + skills + ". Mark each as Basic, Intermediate, or Advanced.");
        tasks.add("ROADMAP Phase 3 - Foundation plan: Based on " + level + ", revise fundamentals for the weakest two skills.");
        tasks.add("ROADMAP Phase 4 - Growth blocker: Fix this blocker first: " + blockers + ". Convert it into a 7-day habit.");
        tasks.add("ROADMAP Phase 5 - Project proof: Build one mini project for " + targetRole + " using " + languages + ".");
        tasks.add("ROADMAP Phase 6 - Interview proof: Prepare 3 stories: project, failure/learning, and problem-solving approach.");
        return tasks;
    }

    public List<String> generatePracticeTasks(String targetRole, String level, String languages, String feedback) {
        List<String> tasks = new ArrayList<>();
        List<String> langs = parseLanguages(languages);
        if (langs.isEmpty()) langs = Arrays.asList("Java", "SQL");
        tasks.add("TEST REVIEW - Rewrite your weakest answer using edge cases, complexity notes, and cleaner structure.");
        for (String lang : langs) {
            String lower = lang.toLowerCase();
            if (level.toLowerCase().contains("beginner") || level.toLowerCase().contains("basic")) {
                tasks.add(lang + " basic task: Write code to find the largest of three numbers. Explain every condition.");
                tasks.add(lang + " basic task: Write code to print the sum of first 10 natural numbers using a loop.");
                tasks.add(lang + " basic task: Write code to count vowels in a string and test empty input.");
            } else if (level.toLowerCase().contains("advanced")) {
                tasks.add(lang + " advanced task: Build a small in-memory task tracker with add, update, complete, and list operations.");
                tasks.add(lang + " advanced task: Solve a frequency-analysis problem and discuss time/space complexity.");
                tasks.add(lang + " advanced task: Refactor one solution into smaller methods and add edge-case tests.");
            } else {
                tasks.add(lang + " intermediate task: Write code to find the second largest number in an array without sorting.");
                tasks.add(lang + " intermediate task: Write code to group repeated words by frequency.");
                tasks.add(lang + " intermediate task: Build a menu-driven console program related to " + targetRole + ".");
            }
            if (lower.contains("sql")) {
                tasks.add("SQL task: Write SELECT, JOIN, GROUP BY, and ORDER BY queries on USERS and TEST_RESULTS.");
            }
        }
        return tasks;
    }

    private List<String> parseLanguages(String languages) {
        List<String> result = new ArrayList<>();
        if (languages == null) return result;
        for (String value : languages.split(",")) {
            String cleaned = value.trim();
            if (!cleaned.isEmpty()) result.add(cleaned);
        }
        return result;
    }

    public List<Roadmap> fetchRoadmaps(int userId) throws SQLException {
        List<Roadmap> list = new ArrayList<>();
        String sql = "SELECT roadmap_id,target_role,generated_date FROM AI_ROADMAP WHERE user_id=? ORDER BY roadmap_id DESC";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Date generatedDate = resultSet.getDate("generated_date");
                    list.add(new Roadmap(resultSet.getInt("roadmap_id"), resultSet.getString("target_role"), String.valueOf(generatedDate)));
                }
            }
        }
        return list;
    }

    public List<TaskItem> fetchTasks(int roadmapId) throws SQLException {
        List<TaskItem> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT task_id,roadmap_id,task_title,status FROM TASKS WHERE roadmap_id=? ORDER BY task_id")) {
            statement.setInt(1, roadmapId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) list.add(new TaskItem(resultSet.getInt("task_id"), resultSet.getInt("roadmap_id"), resultSet.getString("task_title"), resultSet.getString("status")));
            }
        }
        return list;
    }

    public void updateTaskStatus(int taskId, String status) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE TASKS SET status=? WHERE task_id=?")) {
            statement.setString(1, status);
            statement.setInt(2, taskId);
            statement.executeUpdate();
        }
    }

    public List<TaskItem> fetchAllUserTasks(int userId) throws SQLException {
        List<TaskItem> list = new ArrayList<>();
        String sql = "SELECT t.task_id,t.roadmap_id,t.task_title,t.status FROM TASKS t JOIN AI_ROADMAP r ON t.roadmap_id=r.roadmap_id WHERE r.user_id=? ORDER BY t.task_id";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) list.add(new TaskItem(resultSet.getInt("task_id"), resultSet.getInt("roadmap_id"), resultSet.getString("task_title"), resultSet.getString("status")));
            }
        }
        return list;
    }
}
