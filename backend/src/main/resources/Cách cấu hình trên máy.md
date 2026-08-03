[Environment]::SetEnvironmentVariable("SPRING_PROFILES_ACTIVE","local","User")

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
    "123456",
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
Sau khi cấu hình enviroment thì tắt VS CODE sau đó mở lại thì mới cập nhật được các biến môi trường mới lưu.