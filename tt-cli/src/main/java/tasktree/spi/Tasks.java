package tasktree.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tasktree.Configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@ApplicationScoped
public class Tasks {
    static final Logger log = LoggerFactory.getLogger(Tasks.class);
    static final ServiceLoader<Task> loader = ServiceLoader.load(Task.class);
    static final ExecutorService executor = createExecutor();
    @Inject
    @Any
    Instance<Task> tasks;
    @Inject
    Configuration config;
    Deque<Task> queue = new LinkedList<>();

    public static Stream<Task> byProviders() {
        return byProviders(true).stream();
    }

    public static List<Task> byProviders(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        var result = new ArrayList<Task>();
        loader.iterator().forEachRemaining(result::add);
        return result;
    }

    public static final ExecutorService createExecutor() {
        //Executors.newWorkStealingPool()
        return Executors.newSingleThreadExecutor();
    }

    public static final void waitFor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Stream<Task> stream() {
        var concat = Stream.concat(byProviders(), byCDI());
        return concat;
    }

    private Stream<Task> byCDI() {
        return tasks.stream();
    }

    public void run(String[] args) {
        config.parse(args);
        log.info(config.toString());
        var tasks = stream().filter(this::filter).toList();
        log.debug("Matched [{}]", tasks.size());
        runAll(tasks);
    }

    private void runAll(List<Task> tasks) {
        queue.addAll(tasks);
        while (!queue.isEmpty()) {
            log.debug("New round with {} tasks", queue.size());
            //config.userWait();
            var task = queue.poll();
            runTask(task);
        }
        log.info("Task queue empty! Done!");
    }

    private void runTask(Task task) {
        log.info("Running {}", task);
        task.run();
    }

    private boolean filter(Task p) {
        var match = p.filter(config.getRoot());
        var mark = match ? "x" : "o";
        log.debug("Found task {} {}.{} ", mark, p.getPackage(), p.getSimpleName());
        return match;
    }

    protected ExecutorService getExecutor() {
        return executor;
    }

    public void push(Task task) {
        queue.push(task);
    }

}