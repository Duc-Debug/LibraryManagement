package org.example.librarymanagement.port.outbound.category;

import java.util.Map;
import java.util.Set;

public interface LoadCategoryPort {
    Map<Long, String> findCategoryNamesByIds(Set<Long> categoryIds);
}