package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.category.CategoryManagementService;
import org.example.librarymanagement.infrastructure.transaction.category.TransactionalCategoryManagementUseCase;
import org.example.librarymanagement.port.inbound.category.CategoryManagementUseCase;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.example.librarymanagement.port.outbound.category.CategoryUsagePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    @Bean
    public CategoryManagementUseCase categoryManagementUseCase(
            CategoryRepositoryPort categoryRepositoryPort,
            CategoryUsagePort categoryUsagePort
    ) {
        CategoryManagementUseCase service =
                new CategoryManagementService(
                        categoryRepositoryPort,
                        categoryUsagePort
                );

        return new TransactionalCategoryManagementUseCase(service);
    }
}