package tasktree.aws.cleanup;

import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import tasktree.Configuration;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class FilterInternetGateways extends AWSFilter<InternetGateway> {

    private String vpcId;

    public FilterInternetGateways(Configuration config, String vpcId) {
        super(config);
        this.vpcId = vpcId;
    }

    private boolean match(InternetGateway resource) {
        var prefix = getConfig().getAwsCleanupPrefix();
        var match = resource.attachments().stream().anyMatch(vpc -> vpc.vpcId().equals(vpcId));
        match = match || resource.tags().stream()
                .anyMatch(tag -> tag.key().equals("Name")
                        && tag.value().startsWith(prefix));
        log().debug("Found Internet Gateway {} {}", mark(match), resource);
        return match;
    }

    private List<InternetGateway> filterResources() {
        var client = newEC2Client();
        var resources = client.describeInternetGateways().internetGateways();
        var matches = resources.stream().filter(this::match).toList();
        log().info("Matched {} Internet Gateways in region [{}]", matches.size(), getRegion());
        return matches;
    }


    @Override
    public void run() {
        var resources = filterResources();
        dryPush(deleteTasks(resources));
    }


    private Stream<Task> deleteTasks(List<InternetGateway> resources) {
        return resources.stream().map(this::deleteTask);
    }


    private Task deleteTask(InternetGateway resource) {
        return new DeleteInternetGateway(getConfig(), resource);
    }

}
