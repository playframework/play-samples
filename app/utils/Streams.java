package utils;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
    /**
     * Convert an Iterable to a Stream.
     */
    public static <A> Stream<A> stream(Iterable<A> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
