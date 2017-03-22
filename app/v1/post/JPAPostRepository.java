package v1.post;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.CircuitBreakerOpenException;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.function.Predicate;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
@Singleton
public class JPAPostRepository implements PostRepository {

    private final JPAApi jpaApi;
    private final PostExecutionContext ec;
    private final CircuitBreaker circuitBreaker = new CircuitBreaker();

    @Inject
    public JPAPostRepository(JPAApi api, PostExecutionContext ec) {
        this.jpaApi = api;
        this.ec = ec;
    }

    @Override
    public CompletionStage<Stream<PostData>> list() {
        return supplyAsync(() -> wrap(em -> select(em)), ec);
    }

    @Override
    public CompletionStage<PostData> create(PostData postData) {
        return supplyAsync(() -> wrap(em -> insert(em, postData)), ec);
    }

    @Override
    public CompletionStage<Optional<PostData>> get(Integer id) {
        return supplyAsync(() -> wrap(em -> lookup(em, id)), ec);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        try {
            return Failsafe.with(circuitBreaker).get(() -> jpaApi.withTransaction(function));
        } catch (CircuitBreakerOpenException e) {
            throw new UnavailableRepositoryException(e.getMessage(), e);
        }
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

class UnavailableRepositoryException extends RuntimeException {
    public UnavailableRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
