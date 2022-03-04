package tasktree.aws.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.Vpc;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class FilterVPCs extends AWSFilter<Vpc> {
    static final Logger log = LoggerFactory.getLogger(FilterVPCs.class);

    Ec2Client ec2;

    private boolean match(Vpc vpc) {
        var prefix = getAwsCleanupPrefix();
        var match = vpc.tags().stream()
                .anyMatch(tag -> tag.key().equals("Name") && tag.value().startsWith(prefix));
        log.debug("Found {} {} {}", "VPC", mark(match), vpc);
        return match;
    }

    private List<Vpc> filterVpcs() {
        var request = DescribeVpcsRequest.builder().build();
        var resources = ec2.describeVpcs(request).vpcs();
        var matches = resources.stream().filter(this::match).toList();
        log.info("Matched [{}] VPCs in region [{}] {}", matches.size(), getRegion(), matches);
        return matches;
    }


    @Override
    public void run() {
        ec2 = newEC2Client();
        filterVpcs().forEach(this::listAndDeleteVPC);
    }

    private void listAndDeleteVPC(Vpc vpc) {
        var vpcId = vpc.vpcId();
        addAllTasks(
                new FilterLoadBalancers(vpcId),
                new FilterVPCEndpoints(vpcId),
                new FilterNetworkInterfaces(vpcId),
                new FilterRouteTables(vpcId),
                new FilterSecurityGroups(vpcId),
                new FilterSubnets(vpcId),
                new FilterInternetGateways(vpcId),
                new DeleteVpc(vpc)
        );
    }
}
