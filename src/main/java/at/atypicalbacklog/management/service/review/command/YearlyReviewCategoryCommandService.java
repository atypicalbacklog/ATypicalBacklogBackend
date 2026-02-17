package at.atypicalbacklog.management.service.review.command;

import at.atypicalbacklog.management.dto.review.yearly.UpsertYearlyReviewCategoryRequest;
import at.atypicalbacklog.management.repository.YearlyReviewCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import at.atypicalbacklog.management.domain.review.YearlyReviewCategory;

@Service
public class YearlyReviewCategoryCommandService {
    private final YearlyReviewCategoryRepository categoryRepo;

    public YearlyReviewCategoryCommandService(YearlyReviewCategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public void upsert(UpsertYearlyReviewCategoryRequest req) {

        if (req.uid() == null) {
            categoryRepo.save(
                new YearlyReviewCategory(
                    UUID.randomUUID().toString(),
                    req.yearlyReviewUid(),
                    req.name(),
                    req.order()
                )
            );
            return;
        }

        YearlyReviewCategory cat =
            categoryRepo.findById(req.uid()).orElseThrow();

        cat.setName(req.name());
        cat.setOrder(req.order());

        categoryRepo.save(cat);
    }

    /** Sets valid = false for the category with the given uid. Returns true if found and invalidated. */
    public boolean invalidateByUid(String uid) {
        return categoryRepo.findById(uid)
            .map(cat -> {
                cat.setValid(false);
                categoryRepo.save(cat);
                return true;
            })
            .orElse(false);
    }
}