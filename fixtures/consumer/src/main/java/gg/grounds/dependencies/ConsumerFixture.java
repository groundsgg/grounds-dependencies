package gg.grounds.dependencies;

import com.google.protobuf.ByteString;
import com.velocitypowered.api.proxy.ProxyServer;
import io.grpc.Status;
import net.minestom.server.MinecraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsumerFixture {
    private ConsumerFixture() {}

    public static ByteString protobufPayload(String value) {
        return ByteString.copyFromUtf8(value);
    }

    public static Status grpcStatus() {
        return Status.OK.withDescription("consumer-fixture");
    }

    public static Class<?> minestomType() {
        return MinecraftServer.class;
    }

    public static Class<?> paperType() {
        return JavaPlugin.class;
    }

    public static Class<?> velocityType() {
        return ProxyServer.class;
    }
}
