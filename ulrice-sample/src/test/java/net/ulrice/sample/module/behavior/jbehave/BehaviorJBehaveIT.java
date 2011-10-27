package net.ulrice.sample.module.behavior.jbehave;

import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.UsingPaths;
import org.jbehave.core.annotations.UsingSteps;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.junit.AnnotatedPathRunner;
import org.junit.After;
import org.junit.runner.RunWith;

@RunWith(AnnotatedPathRunner.class)
// @Configure(pendingStepStrategy = FailingUponPendingStep.class)
@Configure
@UsingPaths(searchIn = "target/test-classes", includes = "net/ulrice/sample/module/behavior/jbehave/behavior.story")
@UsingSteps(instances = BehaviorJBehaveSteps.class)
@UsingEmbedder(embedder = Embedder.class, metaFilters = "-skip")
public class BehaviorJBehaveIT {

    @After
    public void shutdown() {
        // killApplication();
    }

}
