package com.dbms.ui;

import com.dbms.MainApp;
import com.dbms.model.ProgressInfo;
import com.dbms.model.Roadmap;
import com.dbms.model.TaskItem;
import com.dbms.service.ProgressService;
import com.dbms.service.ResumeService;
import com.dbms.service.RoadmapService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardView {
    private final MainApp app;
    private final JPanel root = UIUtils.gradient();
    private final JPanel content = new JPanel(new BorderLayout());
    private final ProgressService progressService = new ProgressService();
    private final ResumeService resumeService = new ResumeService();
    private final RoadmapService roadmapService = new RoadmapService();

    private int activeRoadmapId = -1;
    private String profileGoal = "";
    private String profileSkills = "";
    private String profileLanguages = "";
    private String profileLevel = "Basic";
    private String lastAiFeedback = "No test feedback yet.";

    public DashboardView(MainApp app) {
        this.app = app;
    }

    public JPanel create() {
        root.setLayout(new BorderLayout());
        root.add(sidebar(), BorderLayout.WEST);
        content.setOpaque(false);
        root.add(content, BorderLayout.CENTER);
        show(dashboard());
        return root;
    }

    private JPanel sidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(245, 800));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 18, 28, 18));
        sidebar.add(UIUtils.title("Skill Graph", 28));
        sidebar.add(UIUtils.text(MainApp.getLoggedInUser().getName()));
        sidebar.add(Box.createVerticalStrut(24));
        nav(sidebar, "Dashboard", dashboard());
        nav(sidebar, "Resume", resume());
        nav(sidebar, "Growth Assessment", growthAssessment());
        nav(sidebar, "AI Roadmap", roadmap());
        nav(sidebar, "Skill Test", skillTest());
        nav(sidebar, "Tasks", tasks());
        nav(sidebar, "Progress", progress());
        sidebar.add(Box.createVerticalGlue());
        JButton logout = navButton("Logout");
        logout.addActionListener(e -> app.logout());
        sidebar.add(logout);
        return sidebar;
    }

    private void nav(JPanel sidebar, String name, JPanel panel) {
        JButton button = navButton(name);
        button.addActionListener(e -> show(panel));
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(8));
    }

    private JButton navButton(String text) {
        JButton button = UIUtils.primary(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(210, 44));
        return button;
    }

    private void show(JPanel panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JPanel shell(String title) {
        JPanel panel = UIUtils.panel();
        panel.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 34));
        panel.add(UIUtils.title(title, 30));
        panel.add(Box.createVerticalStrut(18));
        return panel;
    }

    private JPanel dashboard() {
        JPanel panel = shell("Dashboard");
        JPanel row = new JPanel(new GridLayout(1, 4, 18, 18));
        row.setOpaque(false);
        ProgressInfo progress = safeProgress();
        row.add(stat("User", MainApp.getLoggedInUser().getName()));
        row.add(stat("Readiness", progress.getPlacementScore() + "%"));
        row.add(stat("Branch", MainApp.getLoggedInUser().getBranch()));
        row.add(stat("Graduation", String.valueOf(MainApp.getLoggedInUser().getGraduationYear())));
        panel.add(row);
        panel.add(Box.createVerticalStrut(20));
        panel.add(UIUtils.text("Flow: Growth Assessment -> AI Roadmap -> Skill Test -> Tasks -> direct code feedback."));
        return panel;
    }

    private JPanel stat(String key, String value) {
        JPanel card = UIUtils.card();
        card.setPreferredSize(new Dimension(210, 130));
        card.add(UIUtils.text(key));
        card.add(UIUtils.title(value, 24));
        return card;
    }

    private ProgressInfo safeProgress() {
        try {
            return progressService.getProgress(MainApp.getLoggedInUserId());
        } catch (SQLException e) {
            return new ProgressInfo(0, null);
        }
    }

    private JPanel resume() {
        JPanel panel = shell("Resume Module");
        JPanel card = UIUtils.card();
        JLabel chosen = UIUtils.text("No file selected");
        JButton upload = UIUtils.primary("Choose Resume and Upload");
        upload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                int score = resumeService.calculateMockScore(file.getName(), file.length());
                try {
                    resumeService.uploadResume(MainApp.getLoggedInUserId(), file.getAbsolutePath(), score);
                    chosen.setText(file.getName() + " uploaded. Resume readiness: " + score + "%");
                    UIUtils.alert(root, "Resume Uploaded", "Resume saved. Now complete Growth Assessment.", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    UIUtils.alert(root, "Database Error", "Resume upload could not be saved: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        card.add(chosen);
        card.add(Box.createVerticalStrut(14));
        card.add(upload);
        panel.add(card);
        return panel;
    }

    private JPanel growthAssessment() {
        JPanel panel = shell("Growth Assessment");
        JPanel card = UIUtils.card();
        JTextField goal = UIUtils.field("Example: Java Backend Developer");
        JTextField skills = UIUtils.field("Example: Java, SQL, HTML");
        JTextField languages = UIUtils.field("Example: Java, SQL");
        JComboBox<String> level = new JComboBox<>(new String[]{"Basic", "Intermediate", "Advanced"});
        JComboBox<String> growth = new JComboBox<>(new String[]{"Starting", "Learning but inconsistent", "Solving but not confident", "Interview polish needed"});
        JTextArea blockers = area("Example: weak DSA, no projects, SQL joins, low confidence");

        card.add(UIUtils.text("Tell the system who you want to become and where you are now. This creates the visible AI roadmap first."));
        card.add(Box.createVerticalStrut(12));
        card.add(UIUtils.inputBlock("Goal", "Target role", goal, "Example: Java Backend Developer, Data Analyst, Full Stack Developer"));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Skills", "Current skills", skills, "Use skills from resume and skills you can explain."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Code", "Known languages", languages, "Separate with commas. Tests and tasks use these."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Level", "Self-declared level", level, "Basic gets basic questions; Intermediate/Advanced get stronger questions."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Growth", "Current growth path", growth, "Choose honestly."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Blockers", "What is stopping growth?", new JScrollPane(blockers), "These become roadmap priorities."));
        card.add(Box.createVerticalStrut(14));

        JButton createRoadmap = UIUtils.primary("Generate AI Roadmap");
        createRoadmap.addActionListener(e -> {
            profileGoal = goal.getText().trim();
            profileSkills = skills.getText().trim();
            profileLanguages = languages.getText().trim();
            profileLevel = String.valueOf(level.getSelectedItem());
            String blockerText = blockers.getText().trim();
            if (profileGoal.isEmpty() || profileSkills.isEmpty() || profileLanguages.isEmpty() || blockerText.isEmpty()) {
                UIUtils.alert(root, "Complete Assessment", "Fill goal, skills, languages, and blockers first.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                activeRoadmapId = roadmapService.createPersonalizedRoadmap(MainApp.getLoggedInUserId(), profileGoal, profileSkills, profileLevel + " | " + growth.getSelectedItem(), profileLanguages, blockerText);
                progressService.upsertProgress(MainApp.getLoggedInUserId(), 20);
                UIUtils.alert(root, "AI Roadmap Created", "Roadmap created and visible in AI Roadmap. Next, take Skill Test.", JOptionPane.INFORMATION_MESSAGE);
                show(roadmap());
            } catch (SQLException ex) {
                UIUtils.alert(root, "Database Error", "Could not create roadmap: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        });
        card.add(createRoadmap);
        panel.add(wrap(card, 980, 650));
        return panel;
    }

    private JPanel roadmap() {
        JPanel panel = shell("AI Roadmap");
        JPanel body = UIUtils.card();
        try {
            List<Roadmap> roadmaps = roadmapService.fetchRoadmaps(MainApp.getLoggedInUserId());
            if (roadmaps.isEmpty()) {
                body.add(UIUtils.text("No roadmap yet. Complete Growth Assessment first."));
            } else {
                Roadmap selected = roadmaps.get(0);
                activeRoadmapId = selected.getRoadmapId();
                body.add(UIUtils.title("Roadmap for " + selected.getTargetRole(), 22));
                body.add(UIUtils.text("Generated: " + selected.getGeneratedDate()));
                body.add(Box.createVerticalStrut(14));
                for (TaskItem task : roadmapService.fetchTasks(selected.getRoadmapId())) {
                    body.add(taskCheck(task));
                    body.add(Box.createVerticalStrut(8));
                }
                body.add(Box.createVerticalStrut(12));
                JButton test = UIUtils.primary("Start Skill Test for This Roadmap");
                test.addActionListener(e -> show(skillTest()));
                body.add(test);
            }
        } catch (SQLException ex) {
            UIUtils.alert(root, "Database Error", "Could not load roadmap: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        panel.add(wrap(body, 1000, 650));
        return panel;
    }

    private JPanel skillTest() {
        JPanel panel = shell("Skill Test");
        JPanel card = UIUtils.card();
        JTextField skills = UIUtils.field("Example: Java, SQL, HTML");
        JTextField languages = UIUtils.field("Example: Java, SQL");
        JComboBox<String> level = new JComboBox<>(new String[]{"Basic", "Intermediate", "Advanced"});
        if (!profileSkills.isEmpty()) skills.setText(profileSkills);
        if (!profileLanguages.isEmpty()) languages.setText(profileLanguages);
        level.setSelectedItem(profileLevel == null || profileLevel.isEmpty() ? "Basic" : profileLevel);
        JPanel questions = UIUtils.card();
        questions.setVisible(false);
        List<JTextArea> answers = new ArrayList<>();
        final List<String>[] generated = new List[]{new ArrayList<String>()};
        JLabel feedback = UIUtils.text("AI feedback will appear here after submission.");

        card.add(UIUtils.text("This test decides the actual level and score. Questions are real coding/problem tasks based on skills and selected level."));
        card.add(Box.createVerticalStrut(10));
        card.add(UIUtils.inputBlock("Skills", "Skills to test", skills, "Use the same skills from your resume/growth assessment."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Code", "Languages", languages, "Questions and tasks are generated from these."));
        card.add(Box.createVerticalStrut(8));
        card.add(UIUtils.inputBlock("Level", "Question difficulty", level, "Basic: simple programs. Intermediate: arrays/maps/SQL. Advanced: design/refactor/optimization."));
        card.add(Box.createVerticalStrut(12));
        JButton generate = UIUtils.primary("Generate Test Questions");
        JButton submit = UIUtils.primary("Submit Test and Create Tasks");
        submit.setVisible(false);
        generate.addActionListener(e -> {
            answers.clear();
            questions.removeAll();
            generated[0] = generateQuestions(skills.getText(), languages.getText(), String.valueOf(level.getSelectedItem()));
            int i = 1;
            for (String question : generated[0]) {
                JLabel label = UIUtils.text(i + ". " + question);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                questions.add(label);
                questions.add(Box.createVerticalStrut(5));
                JTextArea answer = area("Write code/approach here...");
                JScrollPane answerScroll = new JScrollPane(answer);
                answerScroll.setMaximumSize(new Dimension(820, 105));
                answerScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
                answers.add(answer);
                questions.add(answerScroll);
                questions.add(Box.createVerticalStrut(12));
                i++;
            }
            questions.setVisible(true);
            submit.setVisible(true);
            card.revalidate();
            card.repaint();
        });
        submit.addActionListener(e -> submitSkillTest(skills.getText(), languages.getText(), String.valueOf(level.getSelectedItem()), generated[0], answers, feedback));
        card.add(generate);
        card.add(Box.createVerticalStrut(12));
        card.add(questions);
        card.add(Box.createVerticalStrut(12));
        card.add(submit);
        card.add(Box.createVerticalStrut(12));
        card.add(feedback);
        panel.add(wrap(card, 1020, 650));
        return panel;
    }

    private void submitSkillTest(String skills, String languages, String requestedLevel, List<String> questions, List<JTextArea> answers, JLabel feedbackLabel) {
        if (questions.isEmpty()) {
            UIUtils.alert(root, "Generate Questions", "Generate test questions before submitting.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TestResult result = evaluateAnswers(answers, questions);
        lastAiFeedback = result.feedback + " Techniques: dry-run with sample inputs, handle empty/null cases, name variables clearly, split logic into functions, mention time and space complexity, and test boundary values.";
        feedbackLabel.setText("<html><body style='width:760px'>Level: " + result.level + " | Score: " + result.score + "/100<br>" + lastAiFeedback + "</body></html>");
        try {
            if (activeRoadmapId <= 0) {
                activeRoadmapId = roadmapService.createPersonalizedRoadmap(MainApp.getLoggedInUserId(), profileGoal.isEmpty() ? "Placement Goal" : profileGoal, skills, requestedLevel, languages, "Generated from skill test");
            }
            roadmapService.addTasksToRoadmap(activeRoadmapId, roadmapService.generatePracticeTasks(profileGoal.isEmpty() ? "target role" : profileGoal, result.level, languages, lastAiFeedback));
            progressService.upsertProgress(MainApp.getLoggedInUserId(), result.score);
            UIUtils.alert(root, "Test Completed", "Score: " + result.score + "/100\nLevel: " + result.level + "\n\nTasks were created. Open Tasks to solve them.", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            UIUtils.alert(root, "Database Error", "Could not save test result/tasks: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> generateQuestions(String skills, String languages, String level) {
        List<String> questions = new ArrayList<>();
        List<String> langs = splitCsv(languages);
        if (langs.isEmpty()) langs.add("Java");
        for (String lang : langs) {
            String lower = lang.toLowerCase();
            if (level.equals("Basic")) {
                questions.add(lang + ": Write code to find the largest among three numbers entered by the user.");
                questions.add(lang + ": Write code to calculate the sum of first 10 natural numbers using a loop.");
                questions.add(lang + ": Write code to count vowels in a string. Explain how it handles uppercase letters.");
            } else if (level.equals("Intermediate")) {
                questions.add(lang + ": Write code to find the second largest number in an array without sorting.");
                questions.add(lang + ": Write code to count frequency of each word in a sentence and print repeated words.");
                questions.add(lang + ": Create a small menu-driven student marks program with add, average, and highest score options.");
            } else {
                questions.add(lang + ": Design a mini task manager with add/update/complete/list operations and explain class/function structure.");
                questions.add(lang + ": Solve a log-analysis problem: count events by type, find most frequent event, and handle invalid rows.");
                questions.add(lang + ": Refactor one solution into clean functions/classes and explain time complexity plus edge tests.");
            }
            if (lower.contains("sql")) {
                questions.add("SQL: Write a query to join USERS and TEST_RESULTS, show each user email with latest score, and sort by score descending.");
            }
        }
        for (String skill : splitCsv(skills)) {
            if (skill.toLowerCase().contains("sql") && questions.toString().toLowerCase().indexOf("join users") < 0) {
                questions.add("SQL: Write a GROUP BY query to find average score per test and explain HAVING vs WHERE.");
            }
        }
        return questions;
    }

    private TestResult evaluateAnswers(List<JTextArea> answers, List<String> questions) {
        int total = 0;
        int codeSignals = 0;
        for (JTextArea area : answers) {
            String answer = area.getText() == null ? "" : area.getText().trim();
            if (answer.isEmpty() || answer.equalsIgnoreCase("Write code/approach here...")) continue;
            int points = 8;
            if (answer.length() > 80) points += 5;
            if (answer.length() > 180) points += 5;
            if (answer.matches("(?s).*(for|while|if|else|class|return|select|join|def|public|static).*")) { points += 7; codeSignals++; }
            if (answer.toLowerCase().matches("(?s).*(empty|null|edge|complexity|test|invalid|sort|array|loop|function|method).*")) points += 5;
            total += Math.min(points, 25);
        }
        int score = (int) Math.round((total * 100.0) / Math.max(1, questions.size() * 25));
        String level = score < 40 ? "Beginner" : score < 75 ? "Intermediate" : "Advanced";
        String feedback = score < 40
                ? "Focus on basic syntax, loops, conditions, simple functions, and writing complete code instead of only explanations."
                : score < 75
                ? "Your basics are forming. Improve edge cases, decomposition, time complexity, and clarity of approach."
                : "Strong attempt. Improve optimization, reusable structure, testing strategy, and interview-level explanation.";
        if (codeSignals == 0) feedback += " No clear code was detected, so write actual code in answers for better review.";
        return new TestResult(score, level, feedback);
    }

    private JPanel tasks() {
        JPanel panel = shell("Tasks");
        JPanel body = UIUtils.card();
        try {
            List<TaskItem> all = roadmapService.fetchAllUserTasks(MainApp.getLoggedInUserId());
            if (all.isEmpty()) {
                body.add(UIUtils.text("No tasks yet. Generate AI Roadmap, then complete Skill Test."));
            } else {
                body.add(UIUtils.text("Solve these tasks. Coding questions from your test level appear here too."));
                body.add(Box.createVerticalStrut(12));
                for (TaskItem task : all) {
                    body.add(taskCheck(task));
                    body.add(Box.createVerticalStrut(8));
                }
            }
        } catch (SQLException ex) {
            UIUtils.alert(root, "Database Error", "Could not load tasks: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        panel.add(wrap(body, 1020, 650));
        return panel;
    }

    private JCheckBox taskCheck(TaskItem task) {
        JCheckBox checkBox = new JCheckBox("[" + task.getStatus() + "] " + task.getTitle());
        checkBox.setOpaque(false);
        checkBox.setForeground(Color.WHITE);
        checkBox.setSelected(task.isCompleted());
        checkBox.addActionListener(e -> {
            try {
                roadmapService.updateTaskStatus(task.getTaskId(), checkBox.isSelected() ? "Completed" : "Pending");
                checkBox.setText("[" + (checkBox.isSelected() ? "Completed" : "Pending") + "] " + task.getTitle());
            } catch (SQLException ex) {
                UIUtils.alert(root, "Database Error", "Task status could not be updated: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                checkBox.setSelected(!checkBox.isSelected());
            }
        });
        return checkBox;
    }

    private JPanel progress() {
        JPanel panel = shell("Progress");
        JPanel card = UIUtils.card();
        ProgressInfo progress = safeProgress();
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(progress.getPlacementScore());
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(650, 28));
        String updated = progress.getLastUpdated() == null ? "No progress record yet" : progress.getLastUpdated().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        card.add(UIUtils.title(progress.getPlacementScore() + "% placement readiness", 28));
        card.add(Box.createVerticalStrut(14));
        card.add(bar);
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.text("Last updated: " + updated));
        panel.add(card);
        return panel;
    }

    private JTextArea area(String text) {
        JTextArea area = new JTextArea(4, 42);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText(text);
        return area;
    }

    private JScrollPane wrap(JComponent component, int width, int height) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(width, height));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private List<String> splitCsv(String value) {
        List<String> output = new ArrayList<>();
        if (value == null) return output;
        for (String part : value.split(",")) {
            String cleaned = part.trim();
            if (!cleaned.isEmpty()) output.add(cleaned);
        }
        return output;
    }

    private static class TestResult {
        private final int score;
        private final String level;
        private final String feedback;
        private TestResult(int score, String level, String feedback) {
            this.score = score;
            this.level = level;
            this.feedback = feedback;
        }
    }
}
