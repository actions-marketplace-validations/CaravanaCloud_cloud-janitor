package cloudjanitor.aws.ec2;

import software.amazon.awssdk.services.ec2.model.DeleteNatGatewayRequest;
import software.amazon.awssdk.services.ec2.model.NatGateway;
import cloudjanitor.aws.AWSCleanup;

public class DeleteNATGateway extends AWSCleanup {
    /*
    public DeleteNATGateway(NatGateway resource) {
        super(resource);
    }

    @Override
    protected void cleanup(NatGateway resource) {
        log().debug("Deleting {}", resource);
        var deleteNat = DeleteNatGatewayRequest.builder().natGatewayId(resource.natGatewayId()).build();
        var ec2 = aws().newEC2Client(getRegion());
        ec2.deleteNatGateway(deleteNat);
    }

    @Override
    protected String getResourceType() {
        return "NAT Gateway";
    }

     */
}