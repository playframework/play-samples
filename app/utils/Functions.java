package utils;

import java.util.function.Consumer;
import java.util.function.Supplier;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

public class Functions {
    /**
     * Convert Supplier to Scala (=> A).
     */
    public static <A> AbstractFunction0<A> supplier(Supplier<A> s) {
        return new AbstractFunction0<A>() {
            public A apply() { return s.get(); }
        };
    }

    /**
     * Convert Consumer to Scala (A => Unit).
     */
    public static <A> AbstractFunction1<A, BoxedUnit> consumer(Consumer<A> c) {
        return new AbstractFunction1<A, BoxedUnit>() {
            public BoxedUnit apply(A a) { c.accept(a); return BoxedUnit.UNIT; }
        };
    }
}
