/**
 * Custom Error Class lưu giữ thông tin mã lỗi (code) và message trả về từ Backend
 */
export class ApiError extends Error {
  code: string;
  originalMessage?: string;

  constructor(code: string, message: string) {
    super(message);
    this.name = "ApiError";
    this.code = code;
    this.originalMessage = message;
  }
}

/**
 * Bảng ánh xạ tập trung tất cả Mã Lỗi (Error Code) từ Backend sang Tiếng Việt
 */
export const VIETNAMESE_ERROR_MAP: Record<string, string> = {
  // --- Xác thực & Phân quyền ---
  INVALID_CREDENTIALS: "Tên đăng nhập hoặc mật khẩu không chính xác.",
  INVALID_AUTHORIZATION_HEADER: "Header xác thực không đúng định dạng.",
  INVALID_TOKEN: "Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.",
  UNAUTHENTICATED: "Bạn chưa đăng nhập hoặc không có quyền truy cập.",
  ACCESS_DENIED: "Thao tác bị từ chối. Bạn không có quyền thực hiện chức năng này.",
  READER_ACCESS_DENIED: "Bạn không có quyền truy cập vào thông tin độc giả này.",
  ACCOUNT_DISABLED: "Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ Quản trị viên để được hỗ trợ.",
  USER_LOCKED: "Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ Quản trị viên để được hỗ trợ.",

  // --- Quản lý Độc giả ---
  READER_NOT_FOUND: "Không tìm thấy thông tin độc giả trong hệ thống.",
  READER_ALREADY_EXISTS: "Thông tin độc giả đã tồn tại (Email, SĐT hoặc Mã thẻ đã được đăng ký).",
  READER_HAS_ACTIVE_BORROW: "Không thể xóa độc giả này vì họ đang có sách mượn chưa trả.",

  // --- Quản lý Sách ---
  BOOK_NOT_FOUND: "Không tìm thấy thông tin cuốn sách này trong thư viện.",
  BOOK_HAS_ACTIVE_BORROW: "Không thể xóa hoặc chỉnh sửa sách này do đang có người mượn.",
  INVALID_BOOK_DATA: "Thông tin sách hoặc số lượng sách nhập vào không hợp lệ.",

  // --- Quản lý Thể loại ---
  CATEGORY_NOT_FOUND: "Không tìm thấy thể loại sách này.",
  DUPLICATE_CATEGORY_NAME: "Tên thể loại sách này đã tồn tại trong hệ thống.",
  CATEGORY_IN_USE: "Không thể xóa thể loại này vì đang có sách thuộc danh mục.",

  // --- Quản lý Mượn / Trả Sách ---
  BORROW_LIMIT_EXCEEDED: "Số lượng sách mượn vượt quá giới hạn cho phép của hệ thống.",
  READER_HAS_OVERDUE_BORROW: "Độc giả đang có sách quá hạn chưa hoàn trả. Vui lòng hoàn trả sách cũ trước khi tạo phiếu mượn mới.",
  BORROW_SLIP_NOT_FOUND: "Không tìm thấy thông tin phiếu mượn sách này trong hệ thống.",

  // --- Quản lý Tài khoản & Cấu hình Hệ thống ---
  USER_NOT_FOUND: "Không tìm thấy thông tin tài khoản người dùng.",
  ROLE_NOT_FOUND: "Vai trò người dùng không tồn tại.",
  SYSTEM_SETTING_NOT_FOUND: "Không tìm thấy cấu hình hệ thống yêu cầu.",
  RESOURCE_NOT_FOUND: "Tài nguyên yêu cầu không tồn tại trong hệ thống.",
  DUPLICATE_RESOURCE: "Tài nguyên này đã tồn tại trong hệ thống.",
  VALIDATION_ERROR: "Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại.",
  MALFORMED_JSON: "Cấu trúc dữ liệu yêu cầu không đúng định dạng.",
  DOMAIN_ERROR: "Vi phạm quy định nghiệp vụ của hệ thống.",
  BAD_REQUEST: "Yêu cầu không hợp lệ. Vui lòng kiểm tra lại.",
  INTERNAL_ERROR: "Đã xảy ra lỗi hệ thống bất ngờ. Vui lòng thử lại sau.",
};

/**
 * Phân tích và chuyển đổi các câu thông báo tiếng Anh đặc thù từ Backend thành tiếng Việt thân thiện
 */
function parseSpecificEnglishMessage(msg?: string): string | null {
  if (!msg) return null;
  const lower = msg.toLowerCase();

  // Lỗi tài khoản bị khóa / vô hiệu hóa
  if (lower.includes("user account is disabled") || lower.includes("account is disabled") || lower.includes("account is locked")) {
    return "Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ Quản trị viên để được hỗ trợ.";
  }

  // Lỗi xóa độc giả khi còn sách mượn
  if (lower.includes("cannot delete reader") && lower.includes("active borrow")) {
    return "Không thể xóa độc giả này vì họ đang có sách mượn chưa trả.";
  }

  // Lỗi định dạng & trùng lặp ISBN
  if (lower.includes("isbn") && (lower.includes("10-digit") || lower.includes("13-digit") || lower.includes("format"))) {
    return "Mã ISBN không hợp lệ (phải gồm 10 hoặc 13 chữ số, VD: 9780134494166).";
  }
  if (lower.includes("isbn") && (lower.includes("already have") || lower.includes("already in use") || lower.includes("already exist"))) {
    return "Mã ISBN này đã được sử dụng bởi cuốn sách khác trong hệ thống.";
  }

  // Lỗi Số điện thoại
  if ((lower.includes("phone number") || lower.includes("phone")) && (lower.includes("format is invalid") || lower.includes("invalid format") || lower.includes("định dạng"))) {
    return "Số điện thoại không đúng định dạng (VD: 0912345678).";
  }
  if ((lower.includes("phone number") || lower.includes("phone")) && lower.includes("already registered")) {
    return "Số điện thoại này đã được đăng ký bởi người dùng/độc giả khác.";
  }

  // Lỗi Email
  if (lower.includes("email") && (lower.includes("format is invalid") || lower.includes("valid domain"))) {
    return "Email không đúng định dạng hoặc thiếu tên miền hợp lệ (VD: user@example.com).";
  }
  if (lower.includes("email") && lower.includes("already registered")) {
    return "Địa chỉ Email này đã được đăng ký trong hệ thống.";
  }

  return null;
}

/**
 * Hàm hỗ trợ chuyển đổi bất kỳ lỗi nào ở Frontend thành thông báo Tiếng Việt thân thiện
 * @param error Lỗi được catch
 * @param fallbackMessage Thông báo mặc định nếu không khớp mã lỗi
 */
export function parseErrorMessage(error: unknown, fallbackMessage: string = "Đã xảy ra lỗi. Vui lòng thử lại."): string {
  if (!error) return fallbackMessage;

  let originalMsg: string | undefined;

  // 1. Kiểm tra nếu là ApiError
  if (error instanceof ApiError) {
    originalMsg = error.originalMessage || error.message;

    // 1.1 Thử dịch cụ thể từ câu thông báo tiếng Anh trả về
    const specificTranslated = parseSpecificEnglishMessage(originalMsg);
    if (specificTranslated) return specificTranslated;

    // 1.2 Nếu mã lỗi có trong bản đồ tiếng Việt (và không phải lỗi chung chung như VALIDATION_ERROR khi có câu cụ thể)
    if (error.code && VIETNAMESE_ERROR_MAP[error.code]) {
      if (error.code !== "VALIDATION_ERROR" && error.code !== "DOMAIN_ERROR" && error.code !== "BAD_REQUEST") {
        return VIETNAMESE_ERROR_MAP[error.code];
      }
    }

    // 1.3 Nếu message đã chứa tiếng Việt
    if (originalMsg && /[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(originalMsg)) {
      return originalMsg;
    }

    // 1.4 Nếu có code trong bản đồ ánh xạ
    if (error.code && VIETNAMESE_ERROR_MAP[error.code]) {
      return VIETNAMESE_ERROR_MAP[error.code];
    }
  }

  // 2. Kiểm tra đối tượng bất kỳ có thuộc tính `code`
  if (typeof error === "object" && error !== null) {
    const errObj = error as any;
    originalMsg = errObj.message || errObj.originalMessage;

    const specificTranslated = parseSpecificEnglishMessage(originalMsg);
    if (specificTranslated) return specificTranslated;

    if ("code" in errObj) {
      const codeStr = String(errObj.code);
      if (VIETNAMESE_ERROR_MAP[codeStr]) {
        return VIETNAMESE_ERROR_MAP[codeStr];
      }
    }
  }

  // 3. Nếu error là Error standard
  if (error instanceof Error) {
    originalMsg = error.message;
    const specificTranslated = parseSpecificEnglishMessage(originalMsg);
    if (specificTranslated) return specificTranslated;

    if (originalMsg && /[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(originalMsg)) {
      return originalMsg;
    }
  }

  // 4. Nếu message dạng string đơn thuần
  if (typeof error === "string") {
    const specificTranslated = parseSpecificEnglishMessage(error);
    if (specificTranslated) return specificTranslated;

    if (/[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(error)) {
      return error;
    }
  }

  return fallbackMessage;
}
