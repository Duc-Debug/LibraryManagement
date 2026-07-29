/**
 * File xử lý logic đăng nhập, đăng xuất và tương tác với Backend API
 */
document.addEventListener('DOMContentLoaded', () => {
    // 1. Lắng nghe sự kiện Form Đăng nhập (nếu đang ở trang Login)
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // 2. Lắng nghe sự kiện Nút Đăng xuất (nếu đang ở trang Dashboard/Header)
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', handleLogout);
    }
});

/**
 * Xử lý Đăng nhập
 */
async function handleLogin(event) {
    event.preventDefault();
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');
    const errorAlert = document.getElementById('errorAlert');

    usernameError.classList.add('hidden');
    passwordError.classList.add('hidden');
    errorAlert.classList.add('hidden');

    let isValid = true;
    if (!usernameInput.value.trim()) {
        usernameError.classList.remove('hidden');
        isValid = false;
    }
    if (!passwordInput.value.trim()) {
        passwordError.classList.remove('hidden');
        isValid = false;
    }
    if (!isValid) return;

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
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
            // Lưu token vào localStorage với key 'token'
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard.html';
        } else {
            errorAlert.innerText = data.message || "Tên đăng nhập hoặc mật khẩu không chính xác!";
            errorAlert.classList.remove('hidden');
            passwordInput.value = ''; // Reset lại ô mật khẩu
        }
    } catch (error) {
        errorAlert.innerText = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại Internet!";
        errorAlert.classList.remove('hidden');
        console.error('Login Error:', error);
    }
}

/**
 * Xử lý Đăng xuất
 */
async function handleLogout(event) {
    if (event) event.preventDefault();

    const token = localStorage.getItem('token');

    // Nếu không có token, xóa dữ liệu thừa và đưa về trang đăng nhập ngay
    if (!token) {
        localStorage.removeItem('token');
        window.location.href = '/login.html';
        return;
    }

    try {
        // Gửi request báo Server đưa Token vào Blacklist
        await fetch('http://localhost:8080/api/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
    } catch (error) {
        console.error('Logout Error:', error);
    } finally {
        // Dù API có lỗi hay không, Client VẪN BẮT BUỘC xóa token và thoát
        localStorage.removeItem('token');
        window.location.href = '/login.html';
    }
}

/**
 * Hàm hỗ trợ: Kiểm tra quyền truy cập cho các trang cần đăng nhập (Ví dụ: dashboard.html)
 * Đặt hàm này ở đầu các trang bảo mật để chặn người dùng chưa đăng nhập.
 */
function requireAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
    }
}