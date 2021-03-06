package ln_zap.zap.connection;

import java.util.concurrent.Executor;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Attributes;
import io.grpc.Status;

/**
 * This class is used to create macaroon CallCredentials for the gRPC calls.
 */
public class MacaroonCallCredential implements CallCredentials {
    private final String macaroon;

    /**
     * The macaroon this MacaroonCallCredential is based on.
     * @param macaroon Supply this as Hex-encoded string.
     */
    public MacaroonCallCredential(String macaroon) {
        this.macaroon = macaroon;
    }

    public void thisUsesUnstableApi() {
    }

    public void applyRequestMetadata(
            MethodDescriptor<?, ?> methodDescriptor,
            Attributes attributes,
            Executor executor,
            final MetadataApplier metadataApplier
    ) {
        String authority = attributes.get(ATTR_AUTHORITY);
        System.out.println(authority);
        executor.execute(new Runnable() {
            public void run() {
                try {
                    Metadata headers = new Metadata();
                    Metadata.Key<String> macaroonKey = Metadata.Key.of("macaroon", Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(macaroonKey, macaroon);
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            }
        });
    }
}