package cloudjanitor.aws.sts;

import static cloudjanitor.Output.*;
import cloudjanitor.aws.AWSClients;
import cloudjanitor.aws.AWSFilter;
import org.slf4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GetCallerIdentityTask extends AWSFilter {
    @Inject
    Logger log;

    @Inject
    AWSClients aws;

    @Override
    public void runSafe() {
        var sts = aws().getSTSClient();
        var account = sts.getCallerIdentity().account();
        success(AWS.Account, account);
        log.info("Found AWS Account {}", account);
    }
}
