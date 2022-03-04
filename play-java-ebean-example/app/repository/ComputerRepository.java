package repository;

import io.ebean.DB;
import io.ebean.Model;
import io.ebean.PagedList;
import io.ebean.Transaction;
import models.Computer;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class ComputerRepository {

    private final DatabaseExecutionContext executionContext;

    @Inject
    public ComputerRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    /**
     * Return a paged list of computer
     *
     * @param page     Page to display
     * @param pageSize Number of computers per page
     * @param sortBy   Computer property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public CompletionStage<PagedList<Computer>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() ->
                DB.find(Computer.class)
                    .fetch("company").where()
                    .ilike("name", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList(), executionContext);
    }

    public CompletionStage<Optional<Computer>> lookup(Long id) {
        return supplyAsync(() -> DB.find(Computer.class).setId(id).findOneOrEmpty(), executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, Computer newComputerData) {
        return supplyAsync(() -> {
            Transaction txn = DB.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Computer savedComputer = DB.find(Computer.class).setId(id).findOne();
                if (savedComputer != null) {
                    savedComputer.update(newComputerData);
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> delete(Long id) {
        return supplyAsync(() -> {
            try {
                Optional<Computer> computerOptional = DB.find(Computer.class).setId(id).findOneOrEmpty();
                computerOptional.ifPresent(Model::delete);
                return computerOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Long> insert(Computer computer) {
        return supplyAsync(() -> {
             computer.setId(System.currentTimeMillis()); // not ideal, but it works
             DB.insert(computer);
             return computer.getId();
        }, executionContext);
    }
}
