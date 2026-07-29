/**
 * File xử lý logic đăng nhập và tương tác với Backend API
 */

// Đăng ký sự kiện submit form khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

async function handleLogin(event) {
    event.preventDefault(); // Chặn hành vi reload trang mặc định[cite: 2]

    // Lấy các phần tử HTML cần thiết
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');
    const errorAlert = document.getElementById('errorAlert');

    // 1. Reset trạng thái lỗi cũ[cite: 2]
    usernameError.classList.add('hidden');
    passwordError.classList.add('hidden');
    errorAlert.classList.add('hidden');

    let isValid = true;

    // 2. Validate phía Client[cite: 2]
    if (!usernameInput.value.trim()) {
        usernameError.classList.remove('hidden');
        isValid = false;
    }

    if (!passwordInput.value.trim()) {
        passwordError.classList.remove('hidden');
        isValid = false;
    }

    if (!isValid) return;

    // 3. Gọi API Đăng nhập sang Spring Boot
    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: usernameInput.value.trim(),
                password: passwordInput.value.trim()
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Đăng nhập thành công -> Lưu JWT Token & Chuyển hướng trang
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard.html';
        } else {
            // Lỗi từ phía Server (Ví dụ: 401 Unauthorized, 400 Bad Request)
            errorAlert.innerText = data.message || "Tên đăng nhập hoặc mật khẩu không chính xác!";
            errorAlert.classList.remove('hidden');
            passwordInput.value = ''; // Reset lại ô mật khẩu
        }

    } catch (error) {
        // Lỗi không kết nối được tới Backend (CORS, Server sập, sai IP/Port...)
        errorAlert.innerText = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại Internet!";
        errorAlert.classList.remove('hidden');
        console.error('Login Error:', error);
    }
}