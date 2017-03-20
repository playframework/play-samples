package v1.post;

import play.Logger;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static java.util.concurrent.CompletableFuture.*;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * A repository that provides a non-blocking API with a custom execution context.
 */
public class JPAPostRepository implements PostRepository {

    private static final Logger.ALogger logger = Logger.of(JPAPostRepository.class);

    private final JPAApi jpaApi;
    private final PostExecutionContext ec;

    @Inject
    public JPAPostRepository(JPAApi api, PostExecutionContext ec) {
        this.jpaApi = api;
        this.ec = ec;
    }

    @Override
    public CompletionStage<Stream<PostData>> list() {
        // Run the list inside this execution context.
        return supplyAsync(() -> jpaApi.withTransaction(em -> select(em)), ec);
    }

    @Override
    public CompletionStage<PostData> create(PostData postData) {
        return supplyAsync(() -> jpaApi.withTransaction(em -> insert(em, postData)), ec);
    }

    @Override
    public CompletionStage<Optional<PostData>> get(Integer id) {
        return supplyAsync(() -> jpaApi.withTransaction(em -> lookup(em, id)), ec);
    }

    private Optional<PostData> lookup(EntityManager em, Integer id) {
        return Optional.ofNullable(em.find(PostData.class, id));
    }

    private Stream<PostData> select(EntityManager em) {
        TypedQuery<PostData> query = em.createQuery("SELECT p FROM PostData p", PostData.class);
        return query.getResultList().stream();
    }

    private PostData insert(EntityManager em, PostData postData) {
        return em.merge(postData);
    }
}
