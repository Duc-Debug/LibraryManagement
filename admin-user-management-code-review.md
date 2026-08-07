# Review code: Quản lý tài khoản người dùng vai trò Admin

## 1. Mục tiêu của module

Module này thực hiện các chức năng quản trị tài khoản người dùng trong hệ thống thư viện, chủ yếu tập trung vào việc:

- tạo tài khoản thủ thư (librarian)
- xem thông tin tài khoản theo ID
- liệt kê toàn bộ tài khoản theo vai trò
- cập nhật thông tin tài khoản
- vô hiệu hóa tài khoản (deactivate)

Luồng này có thể coi là một ví dụ tốt về việc tách riêng giữa lớp giao diện, use case nghiệp vụ, domain model và persistence.

---

## 2. Tổng quan kiến trúc

Module được tổ chức theo hướng Clean Architecture / Hexagonal-ish với các tầng chính:

- Controller layer: [backend/src/main/java/org/example/librarymanagement/infrastructure/web/admin/LibrarianManagementController.java](backend/src/main/java/org/example/librarymanagement/infrastructure/web/admin/LibrarianManagementController.java)
- Application/use case layer: [backend/src/main/java/org/example/librarymanagement/application/manage/UserManagementService.java](backend/src/main/java/org/example/librarymanagement/application/manage/UserManagementService.java)
- Domain layer: [backend/src/main/java/org/example/librarymanagement/domain/entity/User.java](backend/src/main/java/org/example/librarymanagement/domain/entity/User.java), [backend/src/main/java/org/example/librarymanagement/domain/entity/Role.java](backend/src/main/java/org/example/librarymanagement/domain/entity/Role.java), [backend/src/main/java/org/example/librarymanagement/domain/policies/AuthorizationAccessPolicy.java](backend/src/main/java/org/example/librarymanagement/domain/policies/AuthorizationAccessPolicy.java)
- Port/outbound layer: [backend/src/main/java/org/example/librarymanagement/port/outbound/manage/FindUserPort.java](backend/src/main/java/org/example/librarymanagement/port/outbound/manage/FindUserPort.java), [backend/src/main/java/org/example/librarymanagement/port/outbound/manage/SaveUserPort.java](backend/src/main/java/org/example/librarymanagement/port/outbound/manage/SaveUserPort.java), [backend/src/main/java/org/example/librarymanagement/port/outbound/manage/LoadRolePort.java](backend/src/main/java/org/example/librarymanagement/port/outbound/manage/LoadRolePort.java)
- Persistence layer: [backend/src/main/java/org/example/librarymanagement/infrastructure/persistence/manage/ManageUserPersistenceAdapter.java](backend/src/main/java/org/example/librarymanagement/infrastructure/persistence/manage/ManageUserPersistenceAdapter.java)
- Tests: [backend/src/test/java/org/example/librarymanagement/application/manage/UserManagementServiceTest.java](backend/src/test/java/org/example/librarymanagement/application/manage/UserManagementServiceTest.java)

### Điểm mạnh về kiến trúc

- Tách rõ trách nhiệm giữa controller, service và persistence.
- Domain model có logic nghiệp vụ riêng thay vì toàn bộ logic nằm ở controller/service.
- Có dùng policy class để kiểm soát quyền và điều kiện tài khoản.
- Có unit test cho flow chính của service.

---

## 3. Phân tích từng luồng chức năng

### 3.1. Tạo tài khoản thủ thư

Luồng này bắt đầu tại [LibrarianManagementController.java](backend/src/main/java/org/example/librarymanagement/infrastructure/web/admin/LibrarianManagementController.java).

#### Mô tả hành vi

- Controller nhận request tạo tài khoản.
- Chuyển request sang `CreateUserCommand`.
- Service kiểm tra:
  - người gọi có phải admin không
  - username đã tồn tại chưa
  - role `LIBRARIAN` có tồn tại trong hệ thống không
  - mật khẩu có thể mã hóa được không

#### Điểm tốt

- Có kiểm tra trùng username bằng `UniqueUsernamePolicy`.
- Mật khẩu được mã hóa trước khi lưu.
- Tự động gán role `LIBRARIAN` cho user mới.
- Nếu dữ liệu không hợp lệ, hệ thống ném `DomainException` rõ ràng.

#### Nhược điểm

- Role được gán cứng trong controller bằng chuỗi `"LIBRARIAN"`.
  - Điều này làm code khó mở rộng nếu sau này cần tạo `ADMIN` hoặc các role khác.
  - Nếu tên role thay đổi, cần sửa nhiều chỗ.
- Không có validation nghiệp vụ cho password strength. Chỉ có size validation ở DTO, chưa có policy về độ mạnh mật khẩu.
- Không có kiểm tra riêng cho email/phone bị trùng hoặc format nghiêm ngặt ở domain layer.

### 3.2. Cập nhật thông tin tài khoản

#### Mô tả hành vi

- Service lấy user theo `userId`.
- Gọi `updateProfile(...)` để cập nhật full name/email/phone.
- Nếu `enabled` được truyền, sẽ gọi `activate()` hoặc `deactivate()`.

#### Điểm tốt

- Tách logic update profile và enable/disable account.
- Có thể khóa/mở khóa tài khoản từ một thao tác duy nhất.

#### Nhược điểm

- `updateProfile(...)` trong [User.java](backend/src/main/java/org/example/librarymanagement/domain/entity/User.java) không kiểm tra null/blank. Nếu controller gửi `fullName` rỗng hoặc null, domain object sẽ bị ghi đè bằng giá trị không hợp lệ.
- Không có logic riêng để kiểm tra username update. Nếu sau này yêu cầu đổi username thì hiện tại không hỗ trợ.
- Không có phân biệt giữa update thông tin và update trạng thái tài khoản ở cấp độ domain policy riêng.

### 3.3. Vô hiệu hóa tài khoản

#### Mô tả hành vi

- Service tìm user theo ID.
- Gọi `deactivate()`.
- Lưu lại thay đổi.

#### Điểm tốt

- Thao tác vô hiệu hóa rất đơn giản, rõ ràng.
- Dùng `deactivate()` thay vì xóa trực tiếp khỏi DB, giúp tránh mất dữ liệu lịch sử và bảo toàn tính toàn vẹn.

#### Nhược điểm

- Logic “xóa” thực chất là disable, nhưng tên endpoint và method có thể gây hiểu nhầm (`deleteLibrarian` vẫn dùng `deactivateUser`) trong controller.
- Không có audit log hoặc ghi nhận lý do vô hiệu hóa.
- Không có policy kiểm tra “không cho phép tự vô hiệu hóa tài khoản chính mình” nếu cần.

### 3.4. Xem danh sách tài khoản theo role

#### Mô tả hành vi

- `getAllUsersByRole("LIBRARIAN")` gọi repository để lấy tất cả user có role phù hợp.

#### Điểm tốt

- Query bằng role name khá trực tiếp và dễ hiểu.
- Controller expose endpoint riêng cho librarian.

#### Nhược điểm

- Chỉ hỗ trợ lọc theo role, chưa hỗ trợ phân trang, lọc thêm theo trạng thái enabled, tìm kiếm theo tên/email, v.v.
- Nếu dữ liệu user nhiều, việc query và map toàn bộ có thể gây hiệu năng issue.

---

## 4. Đánh giá về domain model

### 4.1. User domain entity

[User.java](backend/src/main/java/org/example/librarymanagement/domain/entity/User.java) là lớp domain khá tốt vì nó encapsulates trạng thái tài khoản và logic nghiệp vụ.

#### Điểm mạnh

- Có `ensureCanLogin()` để ngăn tài khoản bị vô hiệu hóa đăng nhập.
- Có `activate()` và `deactivate()` rõ ràng.
- Có `addRole()` và `hasRole()` giúp logic role dễ đọc.

#### Điểm cần cải thiện

- `updateProfile()` không kiểm tra dữ liệu đầu vào, có thể làm domain object trở nên inconsistent.
- `touch()` dùng `LocalDateTime.now()` trực tiếp, khiến việc test khó kiểm soát thời gian.
- Không có method để cập nhật password một cách domain-driven; hiện password chỉ được set ở service bằng adapter mã hóa.

### 4.2. Role domain entity

[Role.java](backend/src/main/java/org/example/librarymanagement/domain/entity/Role.java) khá đơn giản và đúng mục đích.

#### Điểm mạnh

- Chuẩn hóa tên role thành uppercase.
- Override `equals()` và `hashCode()` hợp lý.

#### Điểm cần cải thiện

- Nếu hệ thống có nhiều role phức tạp hơn, nên dùng enum thay vì string, để tránh typo và tăng type safety.

---

## 5. Đánh giá về bảo mật và kiểm soát quyền

### 5.1. Phân quyền

[AuthorizationAccessPolicy.java](backend/src/main/java/org/example/librarymanagement/domain/policies/AuthorizationAccessPolicy.java) làm việc khá rõ ràng:

- Admin được phép quản trị
- Admin hoặc Librarian được phép thao tác nghiệp vụ thư viện

#### Điểm tốt

- Quyền được kiểm soát tập trung ở một policy class.
- Logic phân quyền dễ đọc và dễ tái dùng.

#### Rủi ro

- Kiểm tra quyền hiện tại chỉ dựa vào role, chưa có kiểm tra về scope hoặc điều kiện khác như “chỉ admin cấp cao mới tạo tài khoản admin”.
- Không có kiểm soát việc một admin tự đổi quyền cho chính mình.

### 5.2. Account active check

[AccountLockPolicy.java](backend/src/main/java/org/example/librarymanagement/domain/policies/AccountLockPolicy.java) đảm bảo tài khoản bị vô hiệu hóa không được thao tác.

#### Điểm tốt

- Ngăn việc quản lý tài khoản khi tài khoản bị khóa.

#### Rủi ro

- Chỉ kiểm tra trạng thái enabled, chưa có kiểm tra về password expiry, last login, hoặc token revocation.

---

## 6. Đánh giá về persistence và mapping

### 6.1. Adapter mapping

[ManageUserPersistenceAdapter.java](backend/src/main/java/org/example/librarymanagement/infrastructure/persistence/manage/ManageUserPersistenceAdapter.java) làm tốt việc chuyển đổi giữa domain model và JPA entity.

#### Điểm tốt

- Đúng pattern ports/adapters.
- Dùng repository abstraction để tách khỏi JPA implementation.

#### Nhược điểm

- Mapping role entities từ domain model có thể gây vấn đề khi role chưa tồn tại trong DB hoặc khi entity ID bị null.
- Không có transaction-level handling riêng cho các thao tác phức tạp hơn.

### 6.2. JPA entity

[UserJpaEntity.java](backend/src/main/java/org/example/librarymanagement/infrastructure/persistence/user/UserJpaEntity.java) có một số điểm cần chú ý:

- `@ManyToMany(fetch = FetchType.EAGER)` rất tiện nhưng có thể gây vấn đề về performance và nặng memory khi truy vấn nhiều user.
- Nếu user có nhiều role và danh sách lớn, việc eager load có thể dẫn đến vấn đề N+1 hoặc tải quá nhiều dữ liệu không cần thiết.

Khuyến nghị: dùng `LAZY` và query join fetch nơi cần thiết.

---

## 7. Đánh giá về tests

Tests ở [UserManagementServiceTest.java](backend/src/test/java/org/example/librarymanagement/application/manage/UserManagementServiceTest.java) khá phong phú, gồm các case chính:

- tạo thành công
- duplicate username
- role không tồn tại
- update thành công
- user không tồn tại
- deactivate thành công
- không được quyền tạo user nếu là librarian
- disabled admin không được thao tác
- enable/disable qua update

#### Điểm mạnh

- Có test cho happy path và các edge case quan trọng.
- Dùng Mockito để mock dependency, giúp test service logic độc lập.

#### Nhược điểm

- Chưa có integration test cho controller hoặc persistence.
- Chưa có test cho trường hợp tự vô hiệu hóa tài khoản chính mình.
- Chưa có test cho validation đầu vào bất hợp lệ (blank fullName, email không hợp lệ, password dài/thiếu).

---

## 8. Các vấn đề cần cải thiện ưu tiên cao

### 8.1. Hard-coded role

Role `LIBRARIAN` được hard-code ở controller. Đây là điểm cần sửa ngay vì:

- khó mở rộng
- dễ lỗi typo
- khó bảo trì

Khuyến nghị: dùng constant hoặc enum.

### 8.2. Thiếu validation nghiệp vụ ở domain layer

Hiện tại validation chủ yếu nằm ở DTO và service. Điều này khiến domain model có thể nhận dữ liệu không hợp lệ.

Khuyến nghị:

- validate `fullName` không rỗng ở domain layer
- validate email/phone trước khi lưu
- validate password complexity ở service hoặc domain policy

### 8.3. Cần phân trang và filter cho danh sách user

Hiện endpoint chỉ lấy toàn bộ user theo role.

Khuyến nghị:

- thêm pagination
- thêm search theo username/fullName/email
- thêm filter theo enabled

### 8.4. Nên dùng `LAZY` cho role loading

EAGER fetch có thể gây hiệu năng xấu khi số lượng user lớn.

### 8.5. Nên có audit log

Đặc biệt với thao tác disable/enable và role change.

---

## 9. Khuyến nghị thiết kế nâng cấp

### Gợi ý 1: Tách riêng command/value object cho thao tác quản trị

Có thể tạo các object riêng như:

- `CreateLibrarianCommand`
- `UpdateLibrarianCommand`
- `DeactivateLibrarianCommand`

để tránh dùng generic `CreateUserCommand`/`UpdateUserCommand` quá chung chung.

### Gợi ý 2: Dùng enum cho role

Ví dụ:

```java
public enum UserRole {
    ADMIN,
    LIBRARIAN
}
```

Điều này sẽ tăng độ an toàn kiểu dữ liệu và giảm lỗi typo.

### Gợi ý 3: Thêm policy cho business rules

Ví dụ:

- `UserAccountPolicy.validateNoSelfDeactivation`
- `PasswordPolicy.validateStrength`
- `UserProfilePolicy.validateProfile`

### Gợi ý 4: Thêm endpoint admin cho role management

Nếu hệ thống cần quản trị nâng cao, nên có chức năng:

- gán role cho user
- bỏ role khỏi user
- chuyển user từ librarian sang admin

### Gợi ý 5: Bổ sung integration test

Nên có test cho controller + repository để kiểm tra tính đúng đắn của dữ liệu thật trên DB.

---

## 10. Kết luận

Module quản lý tài khoản người dùng dành cho admin hiện tại có nền tảng khá tốt và có cấu trúc rõ ràng. Nó đã thể hiện được các nguyên tắc cơ bản của một hệ thống sạch:

- tách tầng
- có domain logic
- có authorization policy
- có test cho logic chính

Tuy nhiên, để đủ chất lượng production, cần cải thiện ở các điểm:

- hard-coded role
- validation nghiệp vụ còn thiếu
- performance của JPA loading
- missing audit và hơn nữa các policy nghiệp vụ nâng cao

Nhìn chung, đây là một implementation có nền tốt, nhưng vẫn cần được hoàn thiện để phù hợp với môi trường vận hành thực tế và mở rộng lâu dài.
