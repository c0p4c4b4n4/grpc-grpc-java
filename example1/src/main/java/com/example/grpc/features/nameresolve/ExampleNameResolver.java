package com.example.grpc.features.nameresolve;

import com.google.common.collect.ImmutableMap;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExampleNameResolver extends NameResolver {

    private final URI uri;
    private final Map<String, List<InetSocketAddress>> addrStore;
    private Listener2 listener;

    public ExampleNameResolver(URI targetUri) {
        this.uri = targetUri;
        addrStore = ImmutableMap.<String, List<InetSocketAddress>>builder()
            .put(Settings.SERVICE_NAME,
                Stream.iterate(NameResolveServer.startPort, p -> p + 1)
                    .limit(NameResolveServer.serverCount)
                    .map(port -> new InetSocketAddress("localhost", port))
                    .collect(Collectors.toList())
            )
            .build();
    }

    @Override
    public String getServiceAuthority() {
        if (uri.getHost() != null) {
            return uri.getHost();
        }
        return "no host";
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void start(Listener2 listener) {
        this.listener = listener;
        this.resolve();
    }

    @Override
    public void refresh() {
        this.resolve();
    }

    private void resolve() {
        List<InetSocketAddress> addresses = addrStore.get(uri.getPath().substring(1));
        try {
            List<EquivalentAddressGroup> equivalentAddressGroup = addresses.stream()
                .map(this::toSocketAddress)
                .map(Arrays::asList)
                .map(this::addrToEquivalentAddressGroup)
                .collect(Collectors.toList());

            ResolutionResult resolutionResult = ResolutionResult.newBuilder()
                .setAddresses(equivalentAddressGroup)
                .build();

            this.listener.onResult(resolutionResult);

        } catch (Exception e) {
            this.listener.onError(Status.UNAVAILABLE.withDescription("Unable to resolve host ").withCause(e));
        }
    }

    private SocketAddress toSocketAddress(InetSocketAddress address) {
        return new InetSocketAddress(address.getHostName(), address.getPort());
    }

    private EquivalentAddressGroup addrToEquivalentAddressGroup(List<SocketAddress> addrList) {
        return new EquivalentAddressGroup(addrList);
    }
}
