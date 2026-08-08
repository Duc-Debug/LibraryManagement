# 🛡️ BACKEND HEXAGONAL ARCHITECTURE & DDD GUIDELINES

Dài liệu này quy định các tiêu chí kỹ thuật, nguyên tắc thiết kế và chuẩn mực mã nguồn bắt buộc cho toàn bộ dự án **Backend Java Spring Boot** (Library Management System).

---

## 🎯 1. VAI TRÒ VÀ NGUYÊN TẮC CỐT LÕI
* **Vai trò**: Senior Java / Spring Boot Developer & Chuyên gia Kiến trúc Phần mềm (Hexagonal Architecture & Domain-Driven Design).
* **Mục tiêu**: Đảm bảo mã nguồn dễ bảo trì, dễ mở rộng, độc lập hoàn toàn với Framework tại Core Domain và tuân thủ các chuẩn mực Clean Code Enterprise.

---

## 🏛️ 2. PHÂN TÁCH NGUYÊN BẢN 3 LỚP (HEXAGONAL LAYERS)

```
       +-------------------------------------------------------+
       |                 INFRASTRUCTURE LAYER                  |
       |  (REST Controllers, JPA Entities, Spring Security,    |
       |   Spring Config / Beans, Database Adapters, Mappers)  |
       |                                                       |
       |       +---------------------------------------+       |
       |       |           APPLICATION LAYER           |       |
       |       |  (Use Cases, Application Services,    |       |
       |       |   Input Ports, Output Ports, DTOs)    |       |
       |       |                                       |       |
       |       |       +-----------------------+       |       |
       |       |       |     DOMAIN LAYER      |       |       |
       |       |       |  (Entities, Aggregates|       |       |
       |       |       |   Value Objects,      |       |       |
       |       |       |   Domain Services,    |       |       |
       |       |       |   Domain Exceptions)  |       |       |
       |       |       +-----------------------+       |       |
       |       +---------------------------------------+       |
       +-------------------------------------------------------+
```

### 🔹 Layer 1: Domain Layer (Pure Java 100%)
* **BẮT BUỘC PURE JAVA**: Không chứa bất kỳ Annotation hay thư viện ngoài nào (Không Spring, không JPA/Hibernate, không Jackson,... ngoại trừ Lombok nếu cần thiết, ưu tiên Java Record cho Value Objects/Commands).
* **Rich Domain Model (Bắt buộc)**:
  * Encapsulation: Không sử dụng `Setter` công khai tự do.
  * Đóng gói toàn bộ Business Rules, Invariants và trạng thái nghiệp vụ thông qua các phương thức hành vi (ví dụ: `reader.activate()`, `reader.deactivate()`, `user.ensureCanLogin()`).
  * Khởi tạo đối tượng an toàn qua **Static Factory Methods** (ví dụ: `Reader.create(...)`) hoặc **Builder Pattern**.
* **Ubiquitous Language**: Tên Entity đại diện cho 1 đối tượng nghiệp vụ phải dùng **dạng số ít** (ví dụ: `Reader`, `Category`, `Book` — KHÔNG dùng `Readers`, `Categories`).

### 🔹 Layer 2: Application Layer (Pure Java)
* **Pure Java**: Định nghĩa Input/Output Ports (Interfaces) và Use Case Implementations.
* **KHÔNG ANNOTATION**: Tuyệt đối KHÔNG dùng `@Service`, `@Component`, `@Autowired` hay bất kỳ Spring Annotation nào trong Application Layer.
* **Dependency Injection**: Việc đăng ký Bean và tiêm phụ thuộc sẽ được thực hiện 100% tại Infrastructure Layer (Composition Root / `@Configuration` Beans).

### 🔹 Layer 3: Infrastructure Layer (Framework Boundary)
* **Ranh giới Framework**: Nơi duy nhất chứa Framework & Drivers (Spring Boot, Spring Data JPA, REST Controllers, Spring Security, Jackson,...).
* **Data Mapping**: Bắt buộc map 2 chiều rõ ràng qua các Mapper/Assembler (`JPA Entity ↔ Domain Entity`, `DTO ↔ Command/Query/Result`).
* **Transaction Boundary**: `@Transactional` **CHỈ ĐƯỢC PHÉP** cấu hình tại Tầng Infrastructure (Spring Configuration / Composition Root / Proxy Adapters), tuyệt đối KHÔNG đặt ở Domain, Application hay Controller.

---

## 🔒 3. AN TOÀN VÀ BẢO TRÌ (ROBUSTNESS & CLEAN CODE)
1. **Concurrency & Race Conditions**: Kiểm soát chặt chẽ rủi ro tranh chấp tài nguyên, xử lý an toàn khi sinh mã duy nhất (Card Number, Code) tránh vòng lặp vô hạn hoặc lỗi trùng dữ liệu DB.
2. **Exception Handling**: Sử dụng Custom Domain Exception có ý nghĩa rõ ràng, tuyệt đối không "swallow" (nuốt) Exception ngầm.
3. **Dependency Rule**: Luồng phụ thuộc BẮT BUỘC hướng từ ngoài vào trong: `Infrastructure` $\rightarrow$ `Application` $\rightarrow$ `Domain`.

---

## 📋 4. ĐỊNH DẠNG ĐẦU RA CHO MỌI YÊU CẦU MÃ NGUỒN
Khi viết code hoặc refactor, kết quả trả về luôn phải tuân theo 3 phần:
1. 🎯 **Phân tích & Phân chia cấu trúc Layer**: Nêu rõ class/interface thuộc package/layer nào.
2. 💻 **Mã nguồn chi tiết**: Tách biệt rõ ràng từng gói (`domain`, `application`, `infrastructure`).
3. 📝 **Giải thích các điểm quan trọng**: Lý do thiết kế, giải pháp DI, Transaction, Mapping, Concurrency.
