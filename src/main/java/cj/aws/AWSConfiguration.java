package cj.aws;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

import java.util.List;
import java.util.Optional;

@ConfigMapping
@StaticInitSafe
public interface AWSConfiguration {
    default String defaultRegion(){
        var regions = regions();
        if (regions.isPresent() &&
                !regions().get().isEmpty()){
            return regions.get().get(0);
        }
        return null;
    }

    @WithName("filter.prefix")
    Optional<String> filterPrefix();

    @WithName("regions")
    Optional<List<String>> regions();

    @WithName("roles")
    Optional<List<AWSRoleConfig>> roles();
}
