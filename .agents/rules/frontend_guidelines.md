# 🎨 FRONTEND ARCHITECTURE & ENGINEERING GUIDELINES

Tài liệu này quy định các tiêu chí kỹ thuật, kiến trúc phân lớp và chuẩn mực giao diện bắt buộc cho toàn bộ dự án **Frontend React / TypeScript** (Library Management System).

---

## 🎯 1. VAI TRÒ VÀ NGUYÊN TẮC CỐT LÕI
* **Vai trò**: Senior React / TypeScript Frontend Architect.
* **Mục tiêu**: Đảm bảo giao diện hiện đại, chuẩn mực Type Safety, xử lý đầy đủ trạng thái UI/UX và phân tách ranh giới rõ ràng giữa Logic và Presentation.

---

## 🏗️ 2. KIẾN TRÚC PHÂN LỚP FRONTEND (FEATURE-DRIVEN / LAYERED ARCHITECTURE)

```
src/
├── types/          # Core Types, Interfaces (Strict Type Safety, 0% any)
├── api/            # Data Access Layer (Axios/Fetch Client, Interceptors, Services)
├── hooks/          # Business Logic & State Management (Custom Hooks)
├── features/       # Feature Modules (Pages, Domain-specific Components)
├── components/     # UI Reusable Components (Shadcn UI, Base UI, TailwindCSS)
└── lib/            # Utilities, Helpers, Constants
```

### 🔹 Layer 1: Core Types & Contracts (`src/types/`)
* **Strict Type Safety**: Khớp **100%** với API DTOs từ Backend. Tuyệt đối **0% `any`**.
* Sử dụng `unknown` hoặc `Discriminated Unions` khi xử lý dữ liệu động/chưa xác định.

### 🔹 Layer 2: Data Access Layer (`src/api/`)
* Tách biệt Axios / Fetch Client Instance với Interceptors xử lý tự động `Bearer Token`, Refresh Token và Auto-logout khi HTTP 401.
* Mỗi module có API Service riêng (ví dụ: `reader.service.ts`, `auth.service.ts`).
* **Tuyệt đối KHÔNG gọi API trực tiếp trong UI Component**.

### 🔹 Layer 3: Logic & State Layer (`src/hooks/` / `src/features/`)
* Tách toàn bộ logic fetch, transform và state management ra khỏi UI bằng **Custom Hooks** (ví dụ: `useReaderList()`, `useCreateReader()`).
* **Xử lý bắt buộc 5 trạng thái giao diện (5 UI States Handling)**:
  1. ⏳ **Loading**: Sử dụng Skeleton Animation mượt mà.
  2. ✅ **Success**: Hiển thị dữ liệu chuẩn xác.
  3. ❌ **Error**: Hiển thị Toast / Error Alert rõ ràng.
  4. 📭 **Empty State**: Giao diện trực quan khi không có dữ liệu (kèm minh họa & nút CTA).
  5. 🚫 **Disabled**: Vô hiệu hóa nút bấm và tương tác khi đang xử lý request.

### 🔹 Layer 4: Presentation Layer (`src/components/`)
* **Dumb / Presentational Components**: Chỉ nhận `props` và render giao diện, không tự fetch dữ liệu hay giữ side-effects nặng.
* Sử dụng các UI component tái sử dụng từ Shadcn UI / Base UI kết hợp TailwindCSS v4.

---

## 📝 3. FORM HANDLING & CLIENT-SIDE VALIDATION
* **Thư viện bắt buộc**: Sử dụng **React Hook Form + Zod Schema Validation** cho 100% các Form nhập liệu.
* **Validation Realtime**: Validate từng trường dữ liệu và hiển thị câu thông báo lỗi chi tiết bên dưới input.
* **Prevent Double Submit**: Vô hiệu hóa nút Submit (`isSubmitting` / `isPending`) và hiển thị Spinner trong Button khi gửi dữ liệu.

---

## 🎨 4. UI/UX & DYNAMIC DESIGN STANDARDS
* **Figma Alignment**: Dựng chính xác theo thiết kế Figma (Color Palette, Typography, Spacing, Layout).
* **Responsive Breakpoints**: Tối ưu hiển thị mượt mà trên Desktop ($\ge 1280px$), Tablet ($768px$) và Mobile ($375px$).
* **Micro-animations**: Hiệu ứng chuyển trang, hover mượt mà (`transition-all duration-200`).

---

## 🔔 5. ERROR HANDLING & NOTIFICATIONS
* **Centralized Toast Notifications**: Chuyển đổi mã lỗi Backend (như `READER_ALREADY_EXISTS`, `INVALID_CREDENTIALS`) thành thông báo tiếng Việt thân thiện.
* **React Error Boundary**: Bọc các vùng giao diện chính tránh hiện tượng trắng màn hình khi gặp lỗi runtime Client-side.

---

## 📋 6. ĐỊNH DẠNG ĐẦU RA CHO MỌI YÊU CẦU FRONTEND
Khi viết code Frontend, kết quả trả về luôn phải tuân theo 3 phần:
1. 🎯 **Phân tích Cấu trúc Component & Types**: Nêu rõ các file/hook thuộc phần nào.
2. 💻 **Mã nguồn chi tiết**: Tách biệt `types`, `api service`, `custom hook` và `UI component`.
3. 📝 **Giải thích các điểm quan trọng**: 5 UI States handling, Zod validation, Type safety.
