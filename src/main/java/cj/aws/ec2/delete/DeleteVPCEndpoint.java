package cj.aws.ec2.delete;

import cj.aws.AWSInput;
import cj.aws.AWSWrite;
import software.amazon.awssdk.services.ec2.model.DeleteVpcEndpointsRequest;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

import javax.enterprise.context.Dependent;

@Dependent
public class DeleteVPCEndpoint extends AWSWrite {

    @Override
    public void apply() {
        var resource = getInput(AWSInput.targetVPCEndpoint, VpcEndpoint.class);
        info("Deleting vpc endpoint {}", resource.vpcEndpointId());
        var request = DeleteVpcEndpointsRequest.builder()
                .vpcEndpointIds(resource.vpcEndpointId())
                .build();
        aws().ec2().deleteVpcEndpoints(request);
    }
}
