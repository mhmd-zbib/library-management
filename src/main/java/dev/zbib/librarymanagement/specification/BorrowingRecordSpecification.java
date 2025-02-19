package dev.zbib.librarymanagement.specification;

import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class BorrowingRecordSpecification {

    public static Specification<BorrowingRecord> withFilter(BorrowingRecordFilter filter) {
        Specification<BorrowingRecord> spec = Specification.where(null);

        if (filter == null) {
            return spec;
        }

        if (filter.getBookId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("book").get("id"), filter.getBookId()));
        }

        if (filter.getPatronId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("patron").get("id"), filter.getPatronId()));
        }

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }

        if (filter.getFromDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("borrowDate"), filter.getFromDate()));
        }

        if (filter.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("borrowDate"), filter.getToDate()));
        }

        if (filter.getIsOverdue() != null && filter.getIsOverdue()) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.lessThan(root.get("dueDate"), LocalDateTime.now()),
                            cb.equal(root.get("status"), "BORROWED")
                    ));
        }

        return spec;
    }
} 