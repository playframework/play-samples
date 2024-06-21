package v1.post;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface PostRepository {

    CompletionStage<Stream<PostData>> list();

    CompletionStage<PostData> create(PostData postData);

    CompletionStage<Optional<PostData>> get(Long id);

    CompletionStage<Optional<PostData>> update(Long id, PostData postData);
}

