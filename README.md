# 🤖 Bot AI - CodeFest 2025

Đây là bot được phát triển để tham gia cuộc thi **CodeFest 2025** do **JSClub – Trường Đại học Khoa học Tự nhiên, ĐHQG TP.HCM** tổ chức.  
Bot được viết bằng ngôn ngữ **Java**, sử dụng hệ thống bộ nhớ để lưu lại hành động trước đó, từ đó đưa ra quyết định chiến đấu thông minh và linh hoạt trong môi trường đối kháng.

---

## 🧠 Chiến lược bot

- **Bộ nhớ hành động:** Bot lưu lại thông tin hành động của lượt trước để tránh lặp lại hoặc điều chỉnh chiến lược phù hợp.
- **Ưu tiên trang bị:** Bot sẽ ưu tiên nhặt vũ khí tầm xa (súng) trước khi tiếp cận kẻ địch.
- **Chiến đấu thông minh:** Tấn công nếu có đủ trang bị, né tránh nếu máu thấp hoặc chưa có vũ khí.
- **Thay đổi linh hoạt:** Tự động chọn vũ khí cận chiến hoặc tầm xa tùy khoảng cách với mục tiêu.

---

## 📂 Cấu trúc thư mục

```bash
src/
├── bot/
│   ├── core/            # Lớp bot chính (MyBot.java)
│   ├── logic/           # Logic chọn mục tiêu, tìm item
│   ├── memory/          # Ghi nhớ hành động và trạng thái
│   └── actions/         # Hành động di chuyển, tấn công
└── sdk/                 # SDK CodeFest do BTC cung cấp
