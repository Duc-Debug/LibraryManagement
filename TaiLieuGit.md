## Tài liệu đơn giản về Git
Tài liệu này hướng dẫn đơn giản sử dụng các câu lệnh command để sử dụng git.
## Quy trình làm việc khuyến nghị cho team
1. Cập nhật nhánh develop mới nhất về local:
   ```
   git checkout develop
   git pull origin develop
   ```

2. Tạo nhánh feature mới từ develop (đặt tên theo định dạng feature/ten-tinh-nang):
```
   git checkout -b feature/login-page
```
3. Làm việc, commit code và push nhánh feature lên GitHub:
```
   git add .
   git commit -m "feat: add UI and validation for login page"
   git push -u origin feature/login-page
```
4. Mở Pull Request (PR) trên GitHub:
   - Base branch (đích): develop
   - Compare branch (nguồn): feature/login-page
   - Gửi review cho các contributor khác.

5. Sau khi PR được duyệt (Approve) và Merge vào develop:
   - Xóa nhánh feature trên GitHub và Local để giữ repo gọn gàng.
   - Nhánh sống ngắn: Khuyến khích hoàn thành và merge trong 1-2 ngày.
### 1. Clone dự án từ github về máy
- B1: Mở đường dẫn thư mực muốn clone
 ```bash 
 cd Duong/Dan/Den/Thu/Muc
 ```
- B2: Chạy lệnh clone 
```bash
git clone https://github.com/Duc-Debug/LibraryManagement.git
```
- B3: Chuyển vào thư mục repo vừa tải về: 
```bash
cd LibraryManagement
```
### 2. Kiểm tra trạng thái của các file
```bash
git status
```
### 3. Thêm tất cả các file vào Git theo dõi
```
git add .
```
Nếu muốn chỉ thêm 1 file 
```
git add tenfile
```
### 4. Tạo Commit 
```
git commit -m "Nhap message tai day"
```
### 5. Đẩy code lên github
```
git push origin tenNhanh
```
### 6. Tạo 1 branch
```bash
# 1. Chuyển sang nhánh gốc mà bạn muốn tách ra (ví dụ: develop)
git checkout develop

# 2. Cập nhật code mới nhất của nhánh gốc từ GitHub về
git pull origin develop

# 3. Tạo nhánh mới và tự động chuyển sang nhánh đó luôn
git checkout -b feature/a0.4-walking-skeleton
```
Hoặc tạo trực tiếp mà không cần checkout nó
```bash
# Cú pháp: git checkout -b <tên-nhánh-mới> <tên-nhánh-gốc>
git checkout -b feature/a0.4-walking-skeleton develop
```
---
Sau khi tạo nhánh, đẩy lên github
- Lần đầu tiên đẩy (push) nhánh mới này lên GitHub, bạn cần thêm tham số -u (hoặc --set-upstream) để liên kết nhánh local với remote. 
- Từ các lần sau, bạn chỉ cần gõ git push là xong!
```bash
git push -u origin <ten-nhanh-moi>
```
### 7. Đồng bộ các danh sách nhánh trên Github 
```bash
# Tải về thông tin tất cả các nhánh mới và xóa tham chiếu các nhánh đã bị xóa trên GitHub
git fetch --prune
```
### 8. Xóa 1 nhánh 
1. Xóa nhánh ở local(Trên máy)
```bash 
# Chuyển sang nhánh khác trước
git checkout develop

# Xóa branch (chỉ xóa được nếu đã merge code)
git branch -d tên-branch-cần-xóa

# Nếu nhánh chưa merge nhưng vẫn muốn ép xóa hoàn toàn:
git branch -D tên-branch-cần-xóa
```
2. Xóa nhánh trên Github(remote)
```bash
git push origin --delete tên-branch-cần-xóa
```
### 9. Gộp 1 nhánh 
1. Đẩy nhánh làm việc lên Github
```
git push -u origin feature/ten-nhanh-cua-ban
```
2. Tạo Pull Request(PR) trên GitHub
    - Truy cập vào Repository trên GitHub.
    - Bấm vào nút Compare & pull request (hoặc chuyển sang tab Pull requests --> chọn New pull request).
    - Thêm thông tin cho PR:
        - Base (nhánh đích): Chọn develop.
        - Compare (nhánh làm việc): Chọn feature/ten-nhanh-cua-ban.
        - Gắn thẻ Reviewers (chọn đồng đội cần review) và liên kết thẻ Task trên Jira nếu cần.
    - Bấm Create pull request.
3. Thỏa mãn các điều kiện bảo vệ(Protection Requirements)
    - Chờ Review & Approve: Thành viên được gán review sẽ vào tab Files changed, xem code và bấm Review changes $\rightarrow$ Approve.
    - Kiểm tra CI/CD (nếu có): Chờ các luồng tự động kiểm thử (Status checks) chạy xong và báo xanh.
    - Cập nhật code mới nhất (nếu bị đứng sau nhánh chính): Nếu nhánh đích có code mới hơn, hãy bấm nút Update branch ngay trên giao diện PR (hoặc gộp develop về nhánh feature ở local rồi push lại).
4. Thực hiện Merge trên GitHub
- Bấm nút Merge pull request.

- Chọn hình thức merge phù hợp (thường chọn Create a merge commit hoặc Squash and merge tùy quy chuẩn team).

- Bấm Confirm merge.

- (Tùy chọn) Bấm Delete branch để dọn dẹp nhánh feature trên GitHub sau khi gộp xong.
5. Đồng bộ lại code ở máy Local
```bash
# Chuyển về nhánh chính
git checkout develop

# Cập nhật code đã gộp từ GitHub về local
git pull origin develop

# Xóa nhánh feature ở local
git branch -d feature/ten-nhanh-cua-ban
```
 ### 10. Cập nhật file từ nhánh khác vào nhánh của mình
 ```bash
    # Cú pháp lấy 1 file:
git checkout <tên-nhánh-chứa-file> -- <đường-dẫn-file>

# Ví dụ: Lấy file pom.xml từ nhánh develop về nhánh hiện tại
git checkout develop -- backend/pom.xml
```
Nếu muốn nhiều file hoặc cả thư mục
```bash
# Lấy toàn bộ thư mục frontend từ nhánh develop
git checkout develop -- frontend/
```
Sau khi lấy được code, Hãy commit
```bash
git commit -m "chore: copy updated configuration from develop"
```