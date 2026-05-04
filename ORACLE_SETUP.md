/* Main Background */
.root {
    -fx-font-family: "Segoe UI", "Roboto", sans-serif;
}

.main-bg {
    -fx-background-color: linear-gradient(to bottom right, #6a11cb, #2575fc, #ff7eb3);
}

/* Glass Card */
.glass-card {
    -fx-background-color: rgba(255, 255, 255, 0.15);
    -fx-background-radius: 20;
    -fx-border-color: rgba(255, 255, 255, 0.3);
    -fx-border-radius: 20;
    -fx-border-width: 1.5;
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);
}

/* Sidebar */
.sidebar {
    -fx-background-color: rgba(0, 0, 0, 0.2);
    -fx-background-radius: 0 20 20 0;
}

.nav-button {
    -fx-background-color: transparent;
    -fx-text-fill: white;
    -fx-font-size: 14px;
    -fx-alignment: center-left;
    -fx-padding: 10 20 10 20;
    -fx-cursor: hand;
}

.nav-button:hover {
    -fx-background-color: rgba(255, 255, 255, 0.1);
}

.nav-button-active {
    -fx-background-color: rgba(255, 255, 255, 0.2);
    -fx-font-weight: bold;
}

/* Inputs */
.text-input {
    -fx-background-color: rgba(255, 255, 255, 0.1);
    -fx-background-radius: 10;
    -fx-border-color: rgba(255, 255, 255, 0.2);
    -fx-border-radius: 10;
    -fx-text-fill: white;
    -fx-prompt-text-fill: rgba(255, 255, 255, 0.6);
    -fx-padding: 10;
}

.text-input:focused {
    -fx-border-color: #ff7eb3;
    -fx-background-color: rgba(255, 255, 255, 0.2);
}

/* Buttons */
.gradient-button {
    -fx-background-color: linear-gradient(to right, #2575fc, #ff7eb3);
    -fx-background-radius: 10;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-padding: 10 20 10 20;
    -fx-cursor: hand;
}

.gradient-button:hover {
    -fx-effect: dropshadow(three-pass-box, rgba(255, 126, 179, 0.5), 10, 0, 0, 0);
}

/* Labels */
.title-label {
    -fx-font-size: 24px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}

.subtitle-label {
    -fx-font-size: 14px;
    -fx-text-fill: rgba(255, 255, 255, 0.8);
}

/* Chat Bubbles */
.chat-bubble-user {
    -fx-background-color: #2575fc;
    -fx-background-radius: 15 15 0 15;
    -fx-padding: 10;
    -fx-text-fill: white;
}

.chat-bubble-bot {
    -fx-background-color: rgba(255, 255, 255, 0.2);
    -fx-background-radius: 15 15 15 0;
    -fx-padding: 10;
    -fx-text-fill: white;
}

/* Table / List styling */
.list-view {
    -fx-background-color: transparent;
}

.list-cell {
    -fx-background-color: transparent;
    -fx-text-fill: white;
}

.list-cell:filled:hover {
    -fx-background-color: rgba(255, 255, 255, 0.1);
}
