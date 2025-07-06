# 🤖 Bot AI - CodeFest 2025

Đây là bot được phát triển để tham gia cuộc thi **CodeFest 2025** do **Trường Đại học FPT - Hà Nội** tổ chức.  
Bot được xây dựng bằng ngôn ngữ **Java**, tập trung vào chiến lược tìm kiếm, ra quyết định và ghi nhớ trạng thái để đối phó hiệu quả trong môi trường thi đấu.

---

## 🧠 Chiến lược bot

- **Ghi nhớ trạng thái:** Lưu lại hành động ở lượt trước để tránh lặp lại hoặc thích nghi với tình huống mới.
- **Tìm kiếm thông minh:** Ưu tiên vũ khí tầm xa (súng), sau đó là vũ khí cận chiến, throwable và special.
- **Tấn công có điều kiện:** Chỉ tấn công khi có trang bị phù hợp và kẻ địch nằm trong phạm vi an toàn.
- **Tự động điều hướng:** Dùng `PathPlanner` và `SafeZoneNavigator` để di chuyển hợp lý giữa bản đồ.

---

## 📂 Cấu trúc thư mục

```bash
src/
├── bot/
│   ├── core/                # Lớp bot chính và điều khiển
│   │   ├── MyBot.java
│   │   └── BotController.java
│   │
│   ├── logic/               # Logic chiến đấu và chọn mục tiêu
│   │   ├── CombatManager.java
│   │   ├── EnemySelector.java
│   │   └── ItemFinder.java
│   │
│   ├── memory/              # Hệ thống ghi nhớ hành động
│   │   └── BotMemory.java
│   │
│   ├── navigation/          # Di chuyển và tìm đường
│   │   ├── PathPlanner.java
│   │   └── SafeZoneNavigator.java
│   │
│   ├── utils/               # Các hàm hỗ trợ chung
│   │   └── GameUtils.java
│   │
│   ├── BotContext.java      # Bối cảnh chạy bot
│   └── BotConfig.java       # Cấu hình bot
│
├── sdk/                     # Các lớp SDK được tự hiện thực bởi thí sinh
│   ├── Hero.java            # Mô phỏng đối tượng nhân vật chính
│   └── HeroActionType.java  # Enum định nghĩa các hành động của Hero
│
└── Main.java                # Chạy chương trình