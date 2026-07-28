# Frontend & Design (Figma Alignment)

- [ ] **Khớp UI/UX:** Giao diện dựng chính xác theo Figma (Color Palette, Spacing, Typography, Component Layout).
- [ ] **States Handling:** Đã xử lý đầy đủ các trạng thái UI: Loading, Success, Error, Empty State và Disabled.
- [ ] **Responsive:** Hiển thị mượt mà trên các breakpoint thiết kế (Desktop, Tablet, Mobile nếu có).
- [ ] **Form & Validation:** Mọi Form nhập liệu đều qua kiểm tra client-side (Zod / React Hook Form) trước khi gửi request.

---

# ⚙️ Backend & Architecture (Clean Architecture / DDD)

- [ ] **Domain Rules:** Các quy tắc nghiệp vụ (Policies) được tuân thủ nghiêm ngặt tại Domain Layer.
- [ ] **Data Mapping:** Đã chuyển đổi chính xác giữa Domain Entities, DTOs và Persistence Entities.
- [ ] **Identity Standard:** Sử dụng UUID chuẩn cho các định danh chính.
- [ ] **API Spec:** Endpoint, Request/Response Body khớp 100% với file thiết kế API (OpenAPI/Swagger).

---

# 🧪 Testing & Quality Assurance

- [ ] **Unit Tests:** Viết Unit Test cho các logic quan trọng (Backend Policies/Domain, Frontend Custom Hooks/Utils).
- [ ] **Integration Test / API Test:** Đã test các API endpoint chính bằng Postman hoặc Swagger UI.
- [ ] **Manual Testing:** Người làm tính năng đã tự thực hiện test luồng chính (Happy Path) và các trường hợp lỗi (Edge Cases).

---

# 🛠️ Code Quality & Git Workflow

- [ ] **No Dead Code:** Đã dọn dẹp `console.log`, code thừa, comment nháp và các import không sử dụng.
- [ ] **Type Safety:** Không có lỗi TypeScript (`any` phải được hạn chế tối đa).
- [ ] **PR & Code Review:** Pull Request nhận tối thiểu **1–2 approvals** từ thành viên khác trong nhóm và vượt qua toàn bộ **Review Checklist**.