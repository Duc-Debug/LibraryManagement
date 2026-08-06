import { apiFetch } from "./httpClient";

export interface BookResponseDto {
  bookId: number;
  title: string;
  author: string;
  isbn: string;
  description?: string;
  coverImageUrl?: string;
  publisher?: string;
  publishedYear?: number;
  shelfLocation?: string;
  totalQuantity: number;
  availableQuantity: number;
  categoryId?: number;
  active: boolean;
  createdAt?: string;
}

export interface PageResult<T> {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export async function fetchBooksApi(
  page: number = 0,
  size: number = 10,
  keyword: string = ""
): Promise<PageResult<BookResponseDto>> {
  const queryParams = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
  });

  if (keyword && keyword.trim() !== "") {
    queryParams.append("keyword", keyword.trim());
  }

  return apiFetch<PageResult<BookResponseDto>>(`/api/librarians/books?${queryParams.toString()}`);
}

export async function fetchBookByIdApi(id: number): Promise<BookResponseDto> {
  return apiFetch<BookResponseDto>(`/api/librarians/books/${id}`);
}

export async function deleteBookApi(id: number): Promise<void> {
  return apiFetch<void>(`/api/librarians/books/${id}`, {
    method: "DELETE",
  });
}

export async function hideBookApi(id: number): Promise<void> {
  return apiFetch<void>(`/api/librarians/books/${id}/hide`, {
    method: "PATCH",
  });
}

export async function unhideBookApi(id: number): Promise<void> {
  return apiFetch<void>(`/api/librarians/books/${id}/unhide`, {
    method: "PATCH",
  });
}
