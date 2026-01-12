import React from "react";
import "./pagination.scss";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  loading?: boolean;
}

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  loading = false,
}) => {
  if (totalPages <= 1) return null;

  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  const handlePrevious = () => {
    if (currentPage > 0 && !loading) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1 && !loading) {
      onPageChange(currentPage + 1);
    }
  };

  const getVisiblePages = (): (number | string)[] => {
    const delta = 2;
    const range: number[] = [];
    const rangeWithDots: (number | string)[] = [];

    for (let i = Math.max(2, currentPage - delta); i <= Math.min(totalPages - 3, currentPage + delta); i++) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, '...');
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 3) {
      rangeWithDots.push('...', totalPages);
    } else if (totalPages > 1) {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  return (
    <div className="pagination">
      <div className="pagination__info">
        <span className="pagination__text">
          Showing {startItem}-{endItem} of {totalElements} results
        </span>
      </div>

      <div className="pagination__controls">
        <button
          className="pagination__btn pagination__btn--prev"
          onClick={handlePrevious}
          disabled={currentPage === 0 || loading}
          aria-label="Previous page"
        >
          <span className="pagination__arrow">‹</span>
          Previous
        </button>

        <div className="pagination__pages">
          {getVisiblePages().map((page, index) => (
            <React.Fragment key={index}>
              {page === '...' ? (
                <span className="pagination__dots">...</span>
              ) : (
                <button
                  className={`pagination__page ${
                    page - 1 === currentPage ? 'pagination__page--active' : ''
                  }`}
                  onClick={() => onPageChange(page - 1)}
                  disabled={loading}
                  aria-label={`Page ${page}`}
                  aria-current={page - 1 === currentPage ? 'page' : undefined}
                >
                  {page}
                </button>
              )}
            </React.Fragment>
          ))}
        </div>

        <button
          className="pagination__btn pagination__btn--next"
          onClick={handleNext}
          disabled={currentPage === totalPages - 1 || loading}
          aria-label="Next page"
        >
          Next
          <span className="pagination__arrow">›</span>
        </button>
      </div>
    </div>
  );
};
