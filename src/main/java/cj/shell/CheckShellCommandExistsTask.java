package cj.shell;

import cj.Output;
import cj.SafeTask;

import javax.enterprise.context.Dependent;
import java.util.List;

import static cj.shell.ShellInput.cmds;

@Dependent
public class CheckShellCommandExistsTask extends SafeTask {
    @Override
    public void apply() {
        var cmdIn = getInputString(ShellInput.cmd);
        var cmdList = List.of("which", cmdIn);
        var shellTask = tasks().shellTask(cmdList);
        submit(shellTask, cmds, cmdList);
        var code = shellTask.outputAs(Output.shell.exitCode, Integer.class);
        if (code.isEmpty() || code.get() != 0) {
            debug("Command {} does not exist", cmdIn);
            throw fail("Command '%s' not found".formatted(cmdIn));
        } else {
            debug("Command '{}' found", cmdIn);
            success();
        }
    }
}
