package cj.aws.ec2.delete;

import cj.aws.AWSInput;
import cj.aws.AWSWrite;
import software.amazon.awssdk.services.ec2.model.RevokeSecurityGroupEgressRequest;
import software.amazon.awssdk.services.ec2.model.RevokeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.SecurityGroupRule;

import javax.enterprise.context.Dependent;

@Dependent
public class DeleteSecurityGroupRuleTask extends AWSWrite {
    @Override
    public void apply() {
        var sgr = getSecurityGroupRule();
        if (sgr.isEgress()){
            var req = RevokeSecurityGroupEgressRequest.builder().groupId(sgr.groupId())
                    .securityGroupRuleIds(sgr.securityGroupRuleId()).build();
            aws().ec2().revokeSecurityGroupEgress(req);
            debug("Deleted security group rule egress {}/{}", sgr.groupId(), sgr.securityGroupRuleId());
        }else {
            var req = RevokeSecurityGroupIngressRequest.builder().groupId(sgr.groupId())
                    .securityGroupRuleIds(sgr.securityGroupRuleId()).build();
            aws().ec2().revokeSecurityGroupIngress(req);
            debug("Deleted security group rule ingress {}/{}", sgr.groupId(), sgr.securityGroupRuleId());
        }
    }

    private SecurityGroupRule getSecurityGroupRule() {
        return getInput(AWSInput.securityGroupRule, SecurityGroupRule.class);
    }

    @Override
    public String toString() {
        return "DeleteSecurityGroupRule(" +
                "securityGroupRuleId=" + getSecurityGroupRule().securityGroupRuleId() +
                ')';
    }
}
