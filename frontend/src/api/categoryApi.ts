import { apiFetch } from "./httpClient";

export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
}

export interface UpdateCategoryRequest {
  name?: string;
  description?: string;
  active?: boolean;
}

export async function fetchCategoriesApi(): Promise<CategoryResponse[]> {
  return apiFetch<CategoryResponse[]>("/api/categories");
}

export async function fetchCategoryByIdApi(id: number): Promise<CategoryResponse> {
  return apiFetch<CategoryResponse>(`/api/categories/${id}`);
}

export async function createCategoryApi(data: CreateCategoryRequest): Promise<CategoryResponse> {
  return apiFetch<CategoryResponse>("/api/categories", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateCategoryApi(id: number, data: UpdateCategoryRequest): Promise<CategoryResponse> {
  return apiFetch<CategoryResponse>(`/api/categories/${id}`, {
    method: "PATCH",
    body: JSON.stringify(data),
  });
}

export async function deleteCategoryApi(id: number): Promise<void> {
  return apiFetch<void>(`/api/categories/${id}`, {
    method: "DELETE",
  });
}
