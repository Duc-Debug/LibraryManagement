package org.example.librarymanagement.port.outbound.book;

import java.util.List;
import org.example.librarymanagement.domain.entity.Book;

public interface FindBookPort {

    /**
     * Kiem tra xem ISBN da ton tai trong he thong chua.
     * <p>
     * LUU Y (CONTRACT): Tham so {@code isbn} phai duoc chuan hoa (normalized: 
     * loai bo khoang trang, gach ngang va chuyen thanh in hoa) tu tang Domain 
     * hoac UseCase TRUOC KHI goi port nay. 
     * Persistence Adapter khong chiu trach nhiem xu ly logic chuan hoa nghiep vu.
     *
     * @param isbn Chuoi ISBN da chuan hoa
     * @return true neu da ton tai, nguoc lai la false
     */
    boolean existsByIsbn(String isbn);

    /**
     * Lay danh sach sach co ho tro phan trang, ngan chan rui ro tran bo nho (OOM) 
     * khi bang books co luong du lieu lon.
     * <p>
     * Luu y: Khong su dung cac class cua Spring (nhu Page, Pageable) o day 
     * de dam bao tang Port/Domain hoan toan doc lap voi Framework.
     *
     * @param page So thu tu trang (bat dau tu 0)
     * @param size So luong ban ghi tren moi trang
     * @return Danh sach sach thuoc trang duoc yeu cau
     */
    List<Book> findAll(int page, int size);

}