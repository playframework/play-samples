package v1.post;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 *
 */
public class TimeoutPostRepository implements PostRepository {
    private final PostExecutionContext ec;

    @Inject
    public TimeoutPostRepository(PostExecutionContext ec) {
        this.ec = ec;
    }

    @Override
    public CompletionStage<Stream<PostData>> list() {
        return supplyAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Stream.empty();
        }, ec);
    }

    @Override
    public CompletionStage<PostData> create(PostData postData) {
        return supplyAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            postData.id = System.currentTimeMillis();
            return postData;
        }, ec);
    }

    @Override
    public CompletionStage<Optional<PostData>> get(Integer id) {
        return supplyAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, ec);
    }
}
