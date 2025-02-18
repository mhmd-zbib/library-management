package dev.zbib.librarymanagement.specification;

import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BorrowingRecordSpecification {
    
    public static Specification<BorrowingRecord> withFilter(BorrowingRecordFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            
            if (filter.getBookId() != null) {
                predicates.add(cb.equal(root.get("book").get("id"), filter.getBookId()));
            }
            
            if (filter.getPatronId() != null) {
                predicates.add(cb.equal(root.get("patron").get("id"), filter.getPatronId()));
            }
            
            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("borrowDate"), filter.getStartDate()));
            }
            
            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("borrowDate"), filter.getEndDate()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 