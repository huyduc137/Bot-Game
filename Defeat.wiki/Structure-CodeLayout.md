# 🧱 Cấu trúc mã nguồn Defeat

```bash
src/
├── bot/
│   ├── core/              # Lớp chính: MyBot, BotController
│   ├── logic/             # Chiến đấu, chọn item
│   ├── memory/            # Ghi nhớ hành động (BotMemory)
│   ├── navigation/        # Điều hướng
│   ├── utils/             # Tiện ích dùng chung
│   ├── BotContext.java
│   └── BotConfig.java
├── sdk/                   # Tự mô phỏng SDK
└── Main.java              # Entry point (nếu có)
