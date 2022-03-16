package tasktree.aws.cleanup;

import software.amazon.awssdk.services.ec2.model.NatGateway;
import tasktree.aws.AWSTask;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class FilterNATGateways extends AWSFilter<NatGateway> {

    @Override
    protected List<NatGateway> filterResources() {
        var ec2 = newEC2Client();
        return ec2.describeNatGateways()
                .natGateways();
    }

    @Override
    protected Stream<Task> mapSubtasks(NatGateway natGateway) {
        return Stream.of(new DeleteNATGateway(natGateway));
    }

    public boolean match(NatGateway nat) {
        var match = nameMatches(nat, getAwsCleanupPrefix());
        var mark = match ? "x" : "o";
        return match;
    }

    private boolean nameMatches(NatGateway nat, String prefix) {
        return nat.tags().stream()
                .anyMatch(tag -> tag.key().equals("Name") && tag.value().startsWith(prefix));
    }


    protected String getResourceType() {
        return "NAT Gateway";
    }
}
