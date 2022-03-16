package tasktree.aws.cleanup;

import software.amazon.awssdk.services.ec2.model.DeleteVpcEndpointsRequest;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import tasktree.Configuration;

public class DeleteVPCEndpoint extends AWSDelete {
    private final VpcEndpoint resource;

    public DeleteVPCEndpoint(VpcEndpoint resource) {
        this.resource = resource;
    }

    @Override
    public void runSafe() {
        log().info("Deleting vpc endpoint {}", resource.vpcEndpointId());
        var request = DeleteVpcEndpointsRequest.builder()
                .vpcEndpointIds(resource.vpcEndpointId())
                .build();
        newEC2Client().deleteVpcEndpoints(request);
    }

    @Override
    public String getResourceDescription() {
        return resource.vpcEndpointId();
    }

    @Override
    protected String getResourceType() {
        return "VPC Endpoint";
    }

}
