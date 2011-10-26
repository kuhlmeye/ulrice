package net.ulrice.sample.module.behavior;

import static net.ulrice.remotecontrol.ComponentInteraction.*;
import static net.ulrice.remotecontrol.ComponentMatcher.*;
import static net.ulrice.remotecontrol.RemoteControlCenter.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.remotecontrol.ActionMatcher;
import net.ulrice.remotecontrol.ComponentState;
import net.ulrice.remotecontrol.ComponentTableData;
import net.ulrice.remotecontrol.ControllerMatcher;
import net.ulrice.remotecontrol.ModuleMatcher;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.ComponentUtils;
import net.ulrice.remotecontrol.util.RemoteControlUtils;
import net.ulrice.sample.UlriceSampleApplication;
import cucumber.annotation.After;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.Table;

public class BehaviorSteps {

	public static class Knowledge {
		public String knowledge;
		public String stars;
		public String comment;
	}
	
    @Given("^the sample application is running$")
    public void ensureApplicationIsRunning() throws InterruptedException, RemoteControlException {
        if (isClientConnected()) {
            return;
        }

        try {
            connectClient("localhost", 2103, 1);
            return;
        }
        catch (RemoteControlException e) {
            // ignore;
        }

        launchApplication(null, UlriceSampleApplication.class, null);
        connectClient("localhost", 2103, 60);
        RemoteControlUtils.pause(0.25);
    }

	@After
    public void shutdown() {
        killApplication();
    }

    @Given("^the module \"([^\"]*)\" is open$")
    public void openModule(String module) throws RemoteControlException {
        controllerRC().close(ControllerMatcher.all());
        assertTrue(moduleRC().open(ModuleMatcher.like(module)));
    }

    @When("^I enter \"([^\"]*)\" into \"([^\"]*)\"$")
    public void enterData(String value, String into) throws RemoteControlException {
        assertTrue(componentRC().interact(sequence(click(), selectAll(), type(value)), labeled(into)));
    }

    @When("^I click the radio button \"([^\"]*)\"$")
    public void clickRadioButton(String name) throws RemoteControlException {
        assertTrue(componentRC().interact(click(), like(name), ofType(JRadioButton.class)));
    }

    @When("^I select \"([^\"]*)\" in \"([^\"]*)\"$")
    public void selectData(String value, String into) throws RemoteControlException {
        assertTrue(componentRC().interact(click(value), labeled(into)));
    }

    @When("^I click the button \"([^\"]*)\"$")
    public void clickButton(String name) throws RemoteControlException {
        assertTrue(componentRC().interact(click(), like(name), ofType(JButton.class)));
    }

    @When("^I enter following knowledge into the table:$")
    public void enterTableData(List<Knowledge> list) throws RemoteControlException {
        for (Knowledge input : list) {
            clickButton("Add");

            ComponentState state = componentRC().stateOf(ofType(UTableComponent.class));

            assertNotNull(state);

            ComponentTableData data = state.getData(ComponentTableData.class);

            int row = data.findEmptyRow();

            assertTrue(row >= 0);

            assertTrue(componentRC().interact(enter(input.knowledge, row, 0), withId(state.getUniqueId())));
            assertTrue(componentRC().interact(enter(input.stars, row, 1), withId(state.getUniqueId())));
            assertTrue(componentRC().interact(enter(input.comment, row, 2), withId(state.getUniqueId())));
        }
    }

    @When("^I execute the action \"([^\"]*)\"$")
    public void executeAction(String id) throws RemoteControlException {
        assertTrue(actionRC().action(ActionMatcher.withId(id)));
    }

    @Then("^a dialog should appear containing following data:$")
    public void checkDialog(Table table) throws RemoteControlException {
        ComponentState dialog = componentRC().stateOf(ofType(JDialog.class));
        ComponentState state = componentRC().stateOf(ofType(JTextArea.class), within(withId(dialog.getUniqueId())));

        assertNotNull(state);

        state.getText();

        for (List<String> content : table.raw()) {
            Pattern pattern = Pattern.compile(content.get(0));

            assertTrue("Missing data: " + content.get(0), pattern.matcher(state.getText()).find());
        }

        componentRC().interact(invoke("setVisible(false)"), withId(dialog.getUniqueId()));
    }

}
