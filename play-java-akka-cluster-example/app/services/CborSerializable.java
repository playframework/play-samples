package services;

/**
 * Marker trait for serialization with Jackson CBOR.
 * Enabled in serialization.conf `pekko.actor.serialization-bindings` (via application.conf).
 *
 * See also https://pekko.apache.org/docs/pekko/current/serialization-jackson.html#introduction
 */
public interface CborSerializable {
}
