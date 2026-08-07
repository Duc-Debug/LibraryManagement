package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.example.librarymanagement.port.outbound.reader.ReaderSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public final class ReaderSpecification {

    private ReaderSpecification() {
    }

    public static Specification<ReaderJpaEntity> from(ReaderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            if (criteria != null) {
                if (criteria.createdByUserId() != null) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("createdByUserId"),
                            criteria.createdByUserId()
                    ));
                }

                if (criteria.status() != null) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("cardStatus"),
                            criteria.status()
                    ));
                }

                String keyword = criteria.keyword();
                if (keyword != null && !keyword.isBlank()) {
                    String likeKeyword = "%"
                            + keyword.trim().toLowerCase(Locale.ROOT)
                            + "%";

                    predicates.add(criteriaBuilder.or(
                            likeLower(criteriaBuilder, root.get("name"), likeKeyword),
                            likeLower(criteriaBuilder, root.get("email"), likeKeyword),
                            likeLower(criteriaBuilder, root.get("phoneNumber"), likeKeyword),
                            likeLower(criteriaBuilder, root.get("cardNumber"), likeKeyword)
                    ));
                }
            }

            return criteriaBuilder.and(
                    predicates.toArray(Predicate[]::new)
            );
        };
    }

    private static Predicate likeLower(
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            Expression<String> expression,
            String likeKeyword
    ) {
        return criteriaBuilder.like(
                criteriaBuilder.lower(expression),
                likeKeyword
        );
    }
}
