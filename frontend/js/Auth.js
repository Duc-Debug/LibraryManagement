
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});
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
            localStorage.setItem('token', data.token);
            window.location.href = '/index.html';
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