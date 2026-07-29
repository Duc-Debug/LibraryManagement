Bạn là Senior Java Backend Engineer và Software Architect.

Hãy sửa trực tiếp project LibraryManagement hiện tại để chức năng Login có thể compile, chạy được, đúng hướng Strict Hexagonal Architecture và đủ nền tảng để tiếp tục làm JWT authentication.

Mục tiêu của lần sửa này:

1. Project phải chạy được từ source sạch.
2. `mvn clean test` phải pass.
3. `mvn verify` phải pass.
4. Endpoint `POST /api/auth/login` phải gọi được.
5. Login thành công phải trả JWT.
6. Application và Domain không được phụ thuộc Spring, JPA, Servlet hoặc JJWT.
7. Chưa cần làm refresh token, logout, rate limiting hoặc production hardening nâng cao.
8. Không được viết lại toàn bộ kiến trúc.

==================================================
NGUYÊN TẮC THỰC HIỆN
==================================================

- Trước tiên hãy đọc toàn bộ cấu trúc project và các file liên quan.
- Không đoán package hoặc tên method.
- Phải dùng đúng package, field và method đang tồn tại trong source.
- Nếu prompt và source khác nhau, source hiện tại là nguồn đúng.
- Ưu tiên sửa tối thiểu.
- Không đổi public API không cần thiết.
- Không đưa annotation Spring vào Domain hoặc Application.
- Không dùng `@Service` cho `LoginService`.
- Đăng ký `LoginService` bằng `@Bean` tại Composition Root.
- Không để Controller gọi JPA Repository trực tiếp.
- Không hard-code JWT secret trong source.
- Không commit secret thật.
- Không trả password hoặc passwordHash trong response.
- Không log raw password, password hash hoặc access token.
- Không dừng khi gặp lỗi đầu tiên; sửa lần lượt và chạy lại build.
- Sau khi hoàn thành, báo cáo chính xác các file đã sửa.

==================================================
PHA 1 — KHẢO SÁT TRƯỚC KHI SỬA
==================================================

Hãy kiểm tra:

- `pom.xml`
- `application.properties`
- `application.yml` nếu có
- class Spring Boot main
- `LoginController`
- `LoginRequest`
- `LoginCommand`
- `LoginResult`
- `LoginUseCase`
- `LoginService`
- `LoadUserPort`
- `PasswordVerifierPort`
- `TokenProviderPort`
- `UserPersistenceAdapter`
- `UserJpaRepository`
- `UserPersistenceMapper`
- `BCryptPasswordVerifierAdapter`
- `JwtTokenProviderAdapter`
- `JwtProperties`
- `JwtConfig`
- `AuthenticationBeanConfig`
- Spring Security config nếu có
- test hiện có

In ra ngắn gọn:

1. Package thực tế đang dùng là `port.inbound/outbound` hay `port.in/out`.
2. Ba file JWT nào đang rỗng.
3. Adapter security hiện đang nằm ở package nào.
4. Project đang dùng Spring Boot và JJWT version nào.
5. LoginService constructor hiện tại nhận dependency nào.

Sau đó mới bắt đầu sửa.

==================================================
PHA 2 — SỬA PROJECT COMPILE SẠCH
==================================================

1. Hoàn thiện `TokenProviderPort.java`

Contract mong muốn về ý nghĩa:

```java
String generateAccessToken(User user);
````

Nhưng phải dùng đúng package hiện tại của project.

File này phải:

* nằm ở outbound port;
* chỉ phụ thuộc domain `User`;
* không import JJWT hoặc Spring.

2. Hoàn thiện `JwtProperties.java`

Dùng:

```java
@ConfigurationProperties(prefix = "security.jwt")
```

Chứa tối thiểu:

* `String secret`
* `long accessTokenExpirationMs`

Có thể dùng record nếu project và Java version hỗ trợ.

3. Hoàn thiện `JwtConfig.java`

Đăng ký `JwtProperties` thành Spring Bean bằng một trong hai cách phù hợp:

```java
@EnableConfigurationProperties(JwtProperties.class)
```

hoặc cấu hình tương đương đang được project sử dụng.

Không đăng ký trùng Bean.

4. Hoàn thiện `JwtTokenProviderAdapter`

Adapter phải:

* implements `TokenProviderPort`;
* nằm trong Infrastructure;
* dùng JJWT;
* inject `JwtProperties`;
* decode secret Base64;
* tạo `SecretKey`;
* fail-fast nếu:

  * properties null;
  * secret null hoặc blank;
  * secret không phải Base64 hợp lệ;
  * secret không đủ mạnh;
  * expiration <= 0;
* sinh JWT có:

  * subject là username;
  * userId claim;
  * roles claim;
  * issuedAt;
  * expiration;
  * chữ ký;
* không chứa password hoặc passwordHash.

Phải dùng đúng method thực tế của `User` và `Role`.
Không tự giả định `Role::getName` nếu source dùng tên khác.

5. Kiểm tra `pom.xml`

Bảo đảm có đủ dependency JJWT phù hợp với version đang dùng:

* jjwt-api
* jjwt-impl runtime
* jjwt-jackson runtime

Không thêm duplicate dependency.

6. Kiểm tra `application.properties`

Phải có cấu hình tương đương:

```properties
security.jwt.secret=${JWT_SECRET}
security.jwt.access-token-expiration-ms=3600000
```

Không đưa secret mặc định yếu vào source.

Nếu context test hiện tại fail vì không có `JWT_SECRET`, hãy tạo cấu hình test riêng tại:

```text
src/test/resources/application.properties
```

hoặc profile test phù hợp, với một secret Base64 chỉ dùng cho test.

Không đặt secret test vào main production properties.

==================================================
PHA 3 — SỬA DI VÀ LOGIN SERVICE
===============================

1. `LoginService` phải là pure Java:

* không `@Service`;
* không import Spring;
* không import JJWT;
* implements `LoginUseCase`.

2. Constructor phải nhận:

* `LoadUserPort`
* `PasswordVerifierPort`
* `TokenProviderPort`

Dùng `Objects.requireNonNull` cho dependency.

3. Luồng login phải:

* validate command;
* normalize username nhất quán, tối thiểu trim;
* tìm user qua `LoadUserPort`;
* dùng cùng một thông báo cho user không tồn tại và sai password;
* gọi `user.ensureCanLogin()`;
* kiểm tra password qua `PasswordVerifierPort`;
* chỉ gọi `TokenProviderPort` sau khi xác thực thành công;
* map roles sang `Set<String>`;
* trả đúng cấu trúc `LoginResult` hiện tại.

Không thay đổi thứ tự field của `LoginResult` nếu không cần thiết.

4. `AuthenticationBeanConfig`

Sửa Bean để inject đủ ba outbound port:

* `LoadUserPort`
* `PasswordVerifierPort`
* `TokenProviderPort`

Không dùng `new` adapter trực tiếp trong config nếu adapter đã là Bean.

==================================================
PHA 4 — SỬA WEB INPUT VÀ EXCEPTION
==================================

1. `LoginRequest`

Nếu chưa có validation, thêm:

```java
@NotBlank
```

cho username và password.

Dùng Jakarta Validation phù hợp với Spring Boot version hiện tại.

2. `LoginController`

Phải:

* dùng `@Valid`;
* nhận `@RequestBody LoginRequest`;
* map sang `LoginCommand`;
* gọi `LoginUseCase`;
* trả `LoginResult`;
* không chứa business logic;
* không gọi repository.

3. Exception riêng

Tạo exception phù hợp cho credentials sai, ví dụ:

```java
InvalidCredentialsException
```

Đặt ở Application hoặc vị trí phù hợp với kiến trúc hiện tại.

Không dùng exception Infrastructure trong Application.

4. Global exception handler

Tạo `@RestControllerAdvice` để map tối thiểu:

* validation lỗi → 400;
* sai credentials → 401;
* account bị disable/không được phép login → 403 nếu source có exception phân biệt;
* JSON malformed → 400;
* lỗi không mong muốn → 500 với message chung.

Không trả stack trace cho client.

Giữ error response đơn giản và ổn định.

==================================================
PHA 5 — SECURITY CONFIG TỐI THIỂU
=================================

Tạo hoặc sửa `SecurityFilterChain` để:

* `/api/auth/login` được `permitAll`;
* session là `STATELESS`;
* CSRF được xử lý phù hợp với REST Bearer-token API;
* `/error` được phép truy cập;
* không bật form login;
* không bật HTTP Basic trừ khi project thực sự cần.

Trong lần sửa này:

* Nếu project CHƯA có JWT verification filter, tạm thời có thể để các endpoint khác `permitAll` để xác minh login hoạt động.
* Nhưng phải thêm comment hoặc báo cáo rõ rằng đây chưa phải cấu hình production.
* Không được kết luận JWT đã bảo vệ API.

Không tạo một JWT filter giả hoặc filter chưa đầy đủ chỉ để đánh dấu hoàn thành.

==================================================
PHA 6 — PACKAGE VÀ HEXAGONAL CLEANUP
====================================

Nếu security adapter đang nằm ở:

```text
infrastructure.persistence.security
```

hãy chuyển về:

```text
infrastructure.security
```

Chỉ thực hiện nếu refactor không gây thay đổi lớn.

Cập nhật:

* package declaration;
* imports;
* file location;
* config references.

Bảo đảm:

* Domain không import Infrastructure/Spring/JPA.
* Application không import Infrastructure/Spring/JJWT.
* Ports không import Infrastructure.
* Controller chỉ phụ thuộc inbound port.
* JWT/BCrypt/JPA chỉ nằm ở Infrastructure.

Không đổi toàn bộ cấu trúc package ngoài phạm vi authentication.

==================================================
PHA 7 — TEST
============

Viết hoặc bổ sung test tối thiểu.

1. `LoginServiceTest`

Phải có các case:

* command null;
* username blank;
* password blank;
* user không tồn tại;
* password sai;
* token provider không được gọi khi login thất bại;
* login thành công;
* token provider được gọi đúng một lần khi thành công;
* roles được map đúng;
* LoginResult đúng.

Dùng Mockito nếu project đã có hoặc thêm dependency test phù hợp.

2. `JwtTokenProviderAdapterTest`

Test:

* sinh token không null/blank;
* subject đúng;
* userId đúng;
* roles đúng;
* expiration sau issuedAt;
* secret blank làm fail;
* secret Base64 sai làm fail;
* expiration <= 0 làm fail;
* user null làm fail.

3. Controller test

Test tối thiểu:

* request hợp lệ → 200;
* username blank → 400;
* password blank → 400;
* credentials sai → 401;
* response không chứa `password` hoặc `passwordHash`.

4. Context test

Bảo đảm `contextLoads()` chạy được với test configuration độc lập, không cần secret production.

Không cần viết JWT authentication filter integration test trong pha này nếu filter chưa tồn tại.

==================================================
PHA 8 — CHẠY KIỂM TRA
=====================

Sau khi sửa, chạy theo thứ tự:

Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd verify
```

Nếu không có Maven Wrapper:

```powershell
mvn clean test
mvn verify
```

Sau đó thử startup với biến môi trường JWT_SECRET.

Không hard-code một secret production.

Nếu cần tạo secret test runtime, có thể dùng một khóa Base64 hợp lệ chỉ cho local verification.

Nếu database thật chưa cấu hình hoặc không chạy được:

* không xóa persistence code;
* không đổi kiến trúc;
* ghi rõ startup bị chặn bởi dependency môi trường;
* vẫn bảo đảm compile và unit tests pass;
* có thể dùng profile test/H2 nếu project đã có hướng đó, nhưng không tự chuyển production database sang H2.

==================================================
TIÊU CHÍ HOÀN THÀNH
===================

Chỉ coi lần sửa hoàn thành khi:

* Không còn file Java rỗng liên quan Login/JWT.
* `mvn clean test` pass.
* `mvn verify` pass.
* `LoginService` không phụ thuộc Spring/JJWT.
* `TokenProviderPort` tồn tại đúng tầng.
* `JwtTokenProviderAdapter` sinh token được.
* `LoginController` validate request.
* Sai credentials được map thành 401.
* `/api/auth/login` được permitAll.
* Không trả password/hash.
* Không hard-code JWT secret main.
* Có test cho luồng login chính.

==================================================
KHÔNG LÀM TRONG LẦN NÀY
=======================

Không triển khai:

* refresh token;
* token revocation;
* logout blacklist;
* account lockout;
* rate limiting;
* key rotation;
* OAuth2;
* Docker/Kubernetes;
* Flyway migration lớn;
* JWT filter nếu chưa đủ dữ liệu thiết kế;
* refactor toàn bộ project.

Chỉ ghi các mục này vào phần TODO sau cùng.

==================================================
BÁO CÁO SAU KHI SỬA
===================

Trả kết quả bằng tiếng Việt:

# 1. Files Changed

Bảng:

| File | Thay đổi | Lý do |
| ---- | -------- | ----- |

# 2. Build Results

| Lệnh | Kết quả |
| ---- | ------- |

# 3. Login Flow Sau Khi Sửa

Trace luồng thực tế.

# 4. Hexagonal Architecture Check

Nêu rõ:

* Domain có sạch không;
* Application có sạch không;
* Port có đúng hướng không;
* Infrastructure adapter có đúng vị trí không;
* còn vi phạm nào không.

# 5. Security Status

Phân biệt rõ:

* JWT generation: DONE/PARTIAL/FAIL
* JWT verification: DONE/PARTIAL/NOT IMPLEMENTED
* Authorization: DONE/PARTIAL/NOT IMPLEMENTED

# 6. Remaining Risks

Chỉ ra những gì vẫn phải làm trước production.

# 7. Final Verdict

Trả lời:

* Project compile sạch chưa?
* Login endpoint đã sẵn sàng test chưa?
* JWT đã thực sự bảo vệ API chưa?
* Mức deploy hiện tại là gì?

Bắt đầu bằng việc khảo sát source. Sau đó sửa trực tiếp các file, chạy test và tiếp tục sửa cho đến khi build pass hoặc gặp dependency môi trường không thể xử lý trong source.

Bạn được phép chỉnh sửa file trực tiếp và chạy command. Không chỉ đưa hướng dẫn. Sau mỗi nhóm thay đổi, hãy chạy lại test để phát hiện regression. Không xóa code đang hoạt động chỉ để làm test pass.

