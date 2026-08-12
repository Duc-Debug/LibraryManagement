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

  // --- Quản lý Tài khoản & Hệ thống ---
  USER_NOT_FOUND: "Không tìm thấy thông tin tài khoản người dùng.",
  ROLE_NOT_FOUND: "Vai trò người dùng không tồn tại.",
  VALIDATION_ERROR: "Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại.",
  MALFORMED_JSON: "Cấu trúc dữ liệu yêu cầu không đúng định dạng.",
  DOMAIN_ERROR: "Vi phạm quy định nghiệp vụ của hệ thống.",
  BAD_REQUEST: "Yêu cầu không hợp lệ. Vui lòng kiểm tra lại.",
  INTERNAL_ERROR: "Đã xảy ra lỗi hệ thống bất ngờ. Vui lòng thử lại sau.",
};

/**
 * Hàm hỗ trợ chuyển đổi bất kỳ lỗi nào ở Frontend thành thông báo Tiếng Việt thân thiện
 * @param error Lỗi được catch
 * @param fallbackMessage Thông báo mặc định nếu không khớp mã lỗi
 */
export function parseErrorMessage(error: unknown, fallbackMessage: string = "Đã xảy ra lỗi. Vui lòng thử lại."): string {
  if (!error) return fallbackMessage;

  // 1. Kiểm tra nếu là ApiError có chứa code từ Backend
  if (error instanceof ApiError && error.code) {
    if (VIETNAMESE_ERROR_MAP[error.code]) {
      return VIETNAMESE_ERROR_MAP[error.code];
    }
    // Nếu có originalMessage là tiếng Việt sẵn (chứa ký tự tiếng Việt)
    if (error.originalMessage && /[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(error.originalMessage)) {
      return error.originalMessage;
    }
  }

  // 2. Kiểm tra đối tượng bất kỳ có thuộc tính `code`
  if (typeof error === "object" && error !== null && "code" in error) {
    const codeStr = String((error as any).code);
    if (VIETNAMESE_ERROR_MAP[codeStr]) {
      return VIETNAMESE_ERROR_MAP[codeStr];
    }
  }

  // 3. Nếu error.message có chứa tiếng Việt
  if (error instanceof Error && error.message) {
    if (/[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(error.message)) {
      return error.message;
    }
  }

  // 4. Nếu message dạng string đơn thuần
  if (typeof error === "string" && /[àáảãạănắằẳẵặânấầnẩẫậnèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ]/i.test(error)) {
    return error;
  }

  return fallbackMessage;
}
