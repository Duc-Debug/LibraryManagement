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
                // 'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: usernameInput.value.trim(),
                password: passwordInput.value.trim()
            })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('tokenType', data.tokenType || 'Bearer');

            localStorage.setItem('currentUser', JSON.stringify({
                userId: data.userId,
                username: data.username,
                fullName: data.fullName,
                roles: data.roles
            }));
            window.location.replace('index.html');
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

    const token = localStorage.getItem('accessToken');

    // Nếu không có token, xóa dữ liệu thừa và đưa về trang đăng nhập ngay
    if (!token) {
        clearAuthData();
        window.location.replace('dang-nhap.html');
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
        clearAuthData();
        window.location.replace('dang-nhap.html');
    }
}

/**
 * Hàm hỗ trợ: Kiểm tra quyền truy cập cho các trang cần đăng nhập (Ví dụ: dashboard.html)
 * Đặt hàm này ở đầu các trang bảo mật để chặn người dùng chưa đăng nhập.
 */
function requireAuth() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        clearAuthData();
        window.location.replace('dang-nhap.html');
    }
}
/**
 * Hàm hỗ trợ: Xóa sạch dữ liệu Auth trong LocalStorage
 */
function clearAuthData() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('currentUser');
}