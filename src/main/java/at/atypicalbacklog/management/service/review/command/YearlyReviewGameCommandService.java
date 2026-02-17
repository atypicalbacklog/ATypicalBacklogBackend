package at.atypicalbacklog.management.service.review.command;

import at.atypicalbacklog.management.dto.review.yearly.UpsertYearlyReviewGameRequest;
import at.atypicalbacklog.management.repository.YearlyReviewGameRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import at.atypicalbacklog.management.domain.review.YearlyReviewGame;

@Service
public class YearlyReviewGameCommandService {

    private final YearlyReviewGameRepository gameRepo;

    public YearlyReviewGameCommandService(YearlyReviewGameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }

    public void upsert(UpsertYearlyReviewGameRequest req) {

        if (req.uid() == null) {
            gameRepo.save(
                new YearlyReviewGame(
                    UUID.randomUUID().toString(),
                    req.categoryUid(),
                    req.gameUid(),
                    req.gameInstanceUid(),
                    req.preferredImageUid(),
                    req.text(),
                    req.order()
                )
            );
            return;
        }

        YearlyReviewGame game =
            gameRepo.findById(req.uid()).orElseThrow();

        game.setGameUid(req.gameUid());
        game.setGameInstanceUid(req.gameInstanceUid());
        game.setPreferredImageUid(req.preferredImageUid());
        game.setText(req.text());
        game.setOrder(req.order());

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