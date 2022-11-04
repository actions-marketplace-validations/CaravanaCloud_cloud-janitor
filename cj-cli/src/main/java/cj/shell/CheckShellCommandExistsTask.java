package cj.shell;

import cj.Input;
import cj.Output;
import cj.SafeTask;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.util.List;

import static cj.Input.shell.*;

@Dependent
public class CheckShellCommandExistsTask extends SafeTask {
    @Inject
    Instance<ShellTask> shellTaskInstance;

    @Override
    public void apply() {
        var cmdIn = getInputString(Input.shell.cmd);
        var shellTask = shellTaskInstance.get();
        var cmdList = List.of("command", "-v", cmdIn);
        submit(shellTask, cmds, cmdList);
        var code = shellTask.outputAs(Output.shell.exitCode, Integer.class);
        if (code.isEmpty() || code.get() != 0) {
            debug("Command {} does not exist", cmdIn);
            throw fail("Command '%s' not found".formatted(cmdIn));
        }else {
            debug("Command '{}' found", cmdIn);
            success();
        }
    }
}
