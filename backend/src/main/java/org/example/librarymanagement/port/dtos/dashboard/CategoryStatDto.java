package org.example.librarymanagement.port.dtos.dashboard;

/**
 * Thống kê số lượng sách theo từng thể loại — dùng cho biểu đồ phân bố
 * trên trang Dashboard.
 *
 * @param categoryName  Tên thể loại
 * @param bookCount     Số tựa sách trong thể loại đó
 */
public record CategoryStatDto(
    String categoryName,
    long bookCount
) {}
