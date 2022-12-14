package cj.ocp;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigMapping
@StaticInitSafe
public interface OCPConfiguration {
    @WithName("baseDomain")
    Optional<String> baseDomain();

    @WithName("clusterName")
    Optional<String> clusterName();

    @WithName("pullSecret")
    Optional<String> pullSecret();

    @WithName("sshKey")
    Optional<String> sshKey();

    @WithName("awsRegion")
    Optional<String> awsRegion();

    @WithName("clusterProfile")
    Optional<ClusterProfile> clusterProfile();

    @WithName("infrastructureProvider")
    @WithDefault("ipi")
    Optional<OCPInfrastructureProvider> infrastructureProvider();

    @WithName("instanceType")
    @WithDefault("m5.xlarge")
    Optional<String> instanceType();
}
