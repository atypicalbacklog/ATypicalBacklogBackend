package at.atypicalbacklog.management.yearlyreview.service;

import at.atypicalbacklog.management.yearlyreview.domain.YearlyReviewGame;
import at.atypicalbacklog.management.yearlyreview.dto.UpsertYearlyReviewGameRequest;
import at.atypicalbacklog.management.yearlyreview.repository.YearlyReviewGameRepository;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class YearlyReviewGameCommandService {

    private final YearlyReviewGameRepository gameRepo;

    public YearlyReviewGameCommandService(YearlyReviewGameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }

    public void upsert(UpsertYearlyReviewGameRequest req) {

        if (req.uid() == null) {
            YearlyReviewGame game = new YearlyReviewGame(
                UUID.randomUUID().toString(),
                req.categoryUid(),
                req.gameUid(),
                req.gameInstanceUid(),
                req.preferredImageUid(),
                req.additionalImageUid(),
                req.text(),
                req.order()
            );
            if (req.presentationType() != null) {
                game.setPresentationType(req.presentationType());
            }
            gameRepo.save(game);
            return;
        }

        YearlyReviewGame game =
            gameRepo.findById(req.uid()).orElseThrow();

        game.setGameUid(req.gameUid());
        game.setGameInstanceUid(req.gameInstanceUid());
        game.setPreferredImageUid(req.preferredImageUid());
        game.setAdditionalImageUid(req.additionalImageUid());
        game.setText(req.text());
        game.setOrder(req.order());
        if (req.presentationType() != null) {
            game.setPresentationType(req.presentationType());
        }

        gameRepo.save(game);
    }

    /** Sets valid = false for the game with the given uid. Returns true if found and invalidated. */
    public boolean invalidateByUid(String uid) {
        return gameRepo.findById(uid)
            .map(game -> {
                game.setValid(false);
                gameRepo.save(game);
                return true;
            })
            .orElse(false);
    }
}