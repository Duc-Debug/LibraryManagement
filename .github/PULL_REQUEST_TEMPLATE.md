## 📝 Mô tả PR
- **Tính năng / Bugfix:** [Mô tả ngắn gọn công việc đã làm]
- **Ticket / Issue ID:** #[Điền ID ở đây]
- **Màn hình Figma / API liên quan:** [Link Figma hoặc API Spec]

---

## 🧪 Loại thay đổi
- [ ] Tính năng mới (Feature)
- [ ] Sửa lỗi (Bugfix)
- [ ] Refactor Code / Cấu trúc
- [ ] Cập nhật Documentation / Config

---

## ✅ PR Review Checklist

### 1. Dành cho người tạo PR (Author)
- [ ] Code tuân thủ đúng kiến trúc dự án (Hexa Architecture / Structure FE).
- [ ] Đã chạy thử code ở local và không có lỗi rác console / terminal.
- [ ] Đã xử lý type-safe (Không lạm dụng `any` trong TypeScript hay raw type trong Java).
- [ ] Đã kiểm tra UI khớp với thiết kế trên Figma.
- [ ] Đã đính kèm ảnh chụp / GIF demo kết quả bên dưới (Nếu làm Frontend/UI).

### 2. Dành cho người Review (Reviewer)
- [ ] **Nghiệp vụ (Business Logic):** Logic có đúng với yêu cầu và các Policy đã quy định không?
- [ ] **Năng lực xử lý lỗi (Error Handling):** Hệ thống có crash khi gặp lỗi không? Thông báo lỗi trả về cho user/client có rõ ràng không?
- [ ] **Tối ưu (Performance & Clean Code):**
  - Có bị re-render thừa (FE) hoặc N+1 Query (BE) không?
  - Tên biến, class, hàm có đặt theo CamelCase / chuẩn convention không?
- [ ] **Security:** Có bị rò rỉ dữ liệu nhạy cảm hay thiếu kiểm tra quyền / token không?

---

## 📸 Ảnh chụp màn hình / Demo (Nếu có)
*(Kéo thả ảnh screenshot giao diện hoặc video ngắn demo luồng chạy vào đây)*
