[Environment]::SetEnvironmentVariable(
    "SPRING_PROFILES_ACTIVE",
    "local",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "DB_URL",
    "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "DB_USERNAME",
    "root",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "DB_PASSWORD",
    "MAT_KHAU_MYSQL_CUA_BAN",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "JWT_ACCESS_TOKEN_EXPIRATION_MS",
    "3600000",
    "User"
)
=======================================
$bytes = New-Object byte[] 64
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()

try {
    $rng.GetBytes($bytes)
}
finally {
    $rng.Dispose()
}

$jwtSecret = [Convert]::ToBase64String($bytes)

[Environment]::SetEnvironmentVariable(
    "JWT_SECRET",
    $jwtSecret,
    "User"
)
==================================
# CẤU HÌNH CLOUDFLARE R2 STORAGE (NHẬP KEY CỦA BẠN VÀO)
[Environment]::SetEnvironmentVariable(
    "R2_ACCESS_KEY",
    "CAC_KY_TU_ACCESS_KEY_32_CHAR_CUA_BAN",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "R2_SECRET_KEY",
    "CAC_KY_TU_SECRET_KEY_CUA_BAN",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "R2_BUCKET_NAME",
    "library-management",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "R2_PUBLIC_URL",
    "https://pub-21b3e7ec426b49d29c3f815a5312ac5f.r2.dev",
    "User"
)

[Environment]::SetEnvironmentVariable(
    "R2_ENDPOINT",
    "https://8ab691aba9a8e084f6f68a2036b4a19a.r2.cloudflarestorage.com",
    "User"
)

==================================
LƯU Ý: Sau khi chạy các câu lệnh PowerShell cấu hình enviroment ở trên, bạn cần TẮT VS CODE / TERMINAL sau đó MỞ LẠI thì mới cập nhật và nhận được các biến môi trường mới lưu.