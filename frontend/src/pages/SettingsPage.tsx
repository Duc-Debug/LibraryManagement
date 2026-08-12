import { useState, useEffect } from "react";
import { parseErrorMessage } from "@/lib/errorDictionary";
import type { UserAccount } from "@/types";
import { getProfileApi, updateProfileApi, changePasswordApi } from "@/api/authApi";
import { Eye, EyeOff, User, Lock, ShieldCheck, CheckCircle2, AlertCircle, RefreshCw } from "lucide-react";

interface SettingsPageProps {
  currentUser?: UserAccount;
  onProfileUpdated?: (updated: UserAccount) => void;
}

const defaultCurrentUser: UserAccount = {
  id: "",
  username: "",
  password: "",
  fullName: "",
  role: "thu_thu",
  active: true,
};

export default function SettingsPage({
  currentUser = defaultCurrentUser,
  onProfileUpdated = () => {},
}: SettingsPageProps) {
  const [activeTab, setActiveTab] = useState<"profile" | "password">("profile");

  // Profile Form State
  const [fullName, setFullName] = useState(currentUser?.fullName || "");
  const [email, setEmail] = useState(currentUser?.email || "");
  const [phone, setPhone] = useState(currentUser?.phone || "");

  // Password Form State
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // Status & Feedback State
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [submittingProfile, setSubmittingProfile] = useState(false);
  const [submittingPassword, setSubmittingPassword] = useState(false);

  const [profileSuccess, setProfileSuccess] = useState("");
  const [profileError, setProfileError] = useState("");

  const [passwordSuccess, setPasswordSuccess] = useState("");
  const [passwordError, setPasswordError] = useState("");

  useEffect(() => {
    let isMounted = true;
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    setLoadingProfile(true);
    getProfileApi(token)
      .then((data) => {
        if (!isMounted) return;
        setFullName(data.fullName || "");
        setEmail(data.email || "");
        setPhone(data.phone || "");
      })
      .catch((err) => {
        if (isMounted) console.error("Failed to load profile:", err);
      })
      .finally(() => {
        if (isMounted) setLoadingProfile(false);
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setProfileSuccess("");
    setProfileError("");

    if (!fullName.trim()) {
      setProfileError("Họ và tên không được để trống.");
      return;
    }

    const token = localStorage.getItem("accessToken");
    if (!token) {
      setProfileError("Phiên làm việc hết hạn. Vui lòng đăng nhập lại.");
      return;
    }

    setSubmittingProfile(true);
    try {
      const updated = await updateProfileApi(token, {
        fullName: fullName.trim(),
        email: email.trim() || undefined,
        phone: phone.trim() || undefined,
      });

      setProfileSuccess("Cập nhật thông tin cá nhân thành công!");
      onProfileUpdated({
        ...currentUser,
        fullName: updated.fullName,
        email: updated.email || undefined,
        phone: updated.phone || undefined,
      });
    } catch (err: any) {
      setProfileError(parseErrorMessage(err, "Không thể cập nhật thông tin cá nhân."));
    } finally {
      setSubmittingProfile(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordSuccess("");
    setPasswordError("");

    if (!oldPassword) {
      setPasswordError("Vui lòng nhập mật khẩu hiện tại.");
      return;
    }

    if (!newPassword || newPassword.length < 6) {
      setPasswordError("Mật khẩu mới phải có ít nhất 6 ký tự.");
      return;
    }

    if (newPassword !== confirmPassword) {
      setPasswordError("Mật khẩu xác nhận không khớp với mật khẩu mới.");
      return;
    }

    const token = localStorage.getItem("accessToken");
    if (!token) {
      setPasswordError("Phiên làm việc hết hạn. Vui lòng đăng nhập lại.");
      return;
    }

    setSubmittingPassword(true);
    try {
      const res = await changePasswordApi(token, {
        oldPassword,
        newPassword,
        confirmPassword,
      });

      setPasswordSuccess(res.message || "Đổi mật khẩu thành công!");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err: any) {
      setPasswordError(parseErrorMessage(err, "Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ."));
    } finally {
      setSubmittingPassword(false);
    }
  };

  return (
    <div className="p-6 md:p-8 max-w-4xl mx-auto space-y-6 text-foreground animate-in fade-in duration-200">
      {/* Header */}
      <div className="pb-2 border-b border-border/50">
        <h1 className="text-2xl font-bold text-foreground flex items-center gap-2">
          <User className="w-6 h-6 text-primary" />
          <span>Cài Đặt Tài Khoản Cá Nhân</span>
        </h1>
        <p className="text-sm text-muted-foreground mt-1">
          Quản lý thông tin hồ sơ của bạn và thực hiện thay đổi mật khẩu bảo mật.
        </p>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-border gap-4">
        <button
          onClick={() => setActiveTab("profile")}
          className={`pb-3 text-sm font-semibold transition-colors border-b-2 cursor-pointer flex items-center gap-2 ${
            activeTab === "profile"
              ? "border-primary text-primary"
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          <User className="w-4 h-4" /> Thông tin cá nhân
        </button>
        <button
          onClick={() => setActiveTab("password")}
          className={`pb-3 text-sm font-semibold transition-colors border-b-2 cursor-pointer flex items-center gap-2 ${
            activeTab === "password"
              ? "border-primary text-primary"
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          <Lock className="w-4 h-4" /> Đổi mật khẩu
        </button>
      </div>

      {/* Tab 1: Profile Information */}
      {activeTab === "profile" && (
        <div className="bg-card p-6 rounded-2xl shadow-sm border border-border space-y-4">
          <h2 className="text-lg font-bold text-foreground flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-indigo-500" />
            <span>Hồ Sơ Cá Nhân</span>
          </h2>

          {loadingProfile ? (
            <div className="py-8 text-center text-muted-foreground flex items-center justify-center gap-2 text-sm">
              <RefreshCw className="animate-spin h-5 w-5 text-primary" />
              Đang tải dữ liệu tài khoản từ máy chủ...
            </div>
          ) : (
            <form onSubmit={handleUpdateProfile} className="space-y-4">
              {profileSuccess && (
                <div className="p-3.5 text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" />
                  <span>{profileSuccess}</span>
                </div>
              )}
              {profileError && (
                <div className="p-3.5 text-xs font-semibold bg-destructive/10 text-destructive rounded-xl border border-destructive/20 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-destructive shrink-0" />
                  <span>{profileError}</span>
                </div>
              )}

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Username (Disabled) */}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Tên đăng nhập</label>
                  <input
                    type="text"
                    value={currentUser?.username || ""}
                    disabled
                    className="w-full px-3 py-2 text-sm bg-muted/60 border border-border rounded-xl text-muted-foreground font-mono cursor-not-allowed"
                  />
                </div>

                {/* Role (Disabled) */}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Vai trò hệ thống</label>
                  <input
                    type="text"
                    value={currentUser?.role === "admin" ? "Quản trị viên (ADMIN)" : "Thủ thư (LIBRARIAN)"}
                    disabled
                    className="w-full px-3 py-2 text-sm bg-muted/60 border border-border rounded-xl text-muted-foreground font-semibold cursor-not-allowed"
                  />
                </div>

                {/* Full Name */}
                <div className="md:col-span-2">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                    Họ và tên <span className="text-destructive">*</span>
                  </label>
                  <input
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                    placeholder="Nhập họ và tên đầy đủ"
                    className="w-full px-3.5 py-2 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                  />
                </div>

                {/* Email */}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Địa chỉ Email</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="example@library.com"
                    className="w-full px-3.5 py-2 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                  />
                </div>

                {/* Phone */}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">Số điện thoại</label>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="0912345678"
                    className="w-full px-3.5 py-2 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                  />
                </div>
              </div>

              <div className="pt-2 flex justify-end">
                <button
                  type="submit"
                  disabled={submittingProfile}
                  className="px-5 py-2.5 bg-primary text-primary-foreground hover:opacity-90 font-semibold text-sm rounded-xl transition-all shadow-md cursor-pointer disabled:opacity-50 flex items-center gap-2"
                >
                  {submittingProfile && <RefreshCw className="animate-spin h-4 w-4 text-primary-foreground" />}
                  <span>Lưu thay đổi</span>
                </button>
              </div>
            </form>
          )}
        </div>
      )}

      {/* Tab 2: Change Password */}
      {activeTab === "password" && (
        <div className="bg-card p-6 rounded-2xl shadow-sm border border-border max-w-xl space-y-4">
          <h2 className="text-lg font-bold text-foreground flex items-center gap-2">
            <Lock className="w-5 h-5 text-indigo-500" />
            <span>Thay Đổi Mật Khẩu</span>
          </h2>

          <form onSubmit={handleChangePassword} className="space-y-4">
            {passwordSuccess && (
              <div className="p-3.5 text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-500/20 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" />
                <span>{passwordSuccess}</span>
              </div>
            )}
            {passwordError && (
              <div className="p-3.5 text-xs font-semibold bg-destructive/10 text-destructive rounded-xl border border-destructive/20 flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-destructive shrink-0" />
                <span>{passwordError}</span>
              </div>
            )}

            {/* Old Password */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                Mật khẩu hiện tại <span className="text-destructive">*</span>
              </label>
              <div className="relative">
                <input
                  type={showOldPassword ? "text" : "password"}
                  value={oldPassword}
                  onChange={(e) => setOldPassword(e.target.value)}
                  required
                  placeholder="Mật khẩu đang sử dụng"
                  className="w-full px-3.5 py-2 pr-10 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowOldPassword(!showOldPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                >
                  {showOldPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* New Password */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                Mật khẩu mới <span className="text-destructive">*</span>
              </label>
              <div className="relative">
                <input
                  type={showNewPassword ? "text" : "password"}
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  placeholder="Tối thiểu 6 ký tự"
                  className="w-full px-3.5 py-2 pr-10 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowNewPassword(!showNewPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                >
                  {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Confirm Password */}
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1">
                Xác nhận mật khẩu mới <span className="text-destructive">*</span>
              </label>
              <div className="relative">
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  placeholder="Nhập lại mật khẩu mới"
                  className="w-full px-3.5 py-2 pr-10 text-sm bg-background border border-border rounded-xl text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                >
                  {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <div className="pt-2 flex justify-end">
              <button
                type="submit"
                disabled={submittingPassword}
                className="px-5 py-2.5 bg-primary text-primary-foreground hover:opacity-90 font-semibold text-sm rounded-xl transition-all shadow-md cursor-pointer disabled:opacity-50 flex items-center gap-2"
              >
                {submittingPassword && <RefreshCw className="animate-spin h-4 w-4 text-primary-foreground" />}
                <span>Cập nhật mật khẩu</span>
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
