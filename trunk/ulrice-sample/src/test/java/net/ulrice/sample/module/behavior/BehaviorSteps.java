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
import net.ulrice.sample.UlriceSampleApplication;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.Table;

public class BehaviorSteps {

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
        pause(1);
    }

    @Given("^Shutdown$")
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
        assertTrue(componentRC().interact(and(click(), selectAll(), type(value)), labeled(into)));
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

    @When("^I enter following data into the table:$")
    public void enterTableData(Table table) throws RemoteControlException {
        for (List<String> input : table.raw()) {
            clickButton("Add");

            ComponentState state = componentRC().stateOf(ofType(UTableComponent.class));

            assertNotNull(state);

            ComponentTableData data = state.getData(ComponentTableData.class);

            int row = data.findEmptyRow();

            assertTrue(row >= 0);

            assertTrue(componentRC().interact(enter(input.get(0), row, 0), withId(state.getId())));
            assertTrue(componentRC().interact(enter(input.get(1), row, 1), withId(state.getId())));
            assertTrue(componentRC().interact(enter(input.get(2), row, 2), withId(state.getId())));
        }
    }

    @When("^I execute the action \"([^\"]*)\"$")
    public void executeAction(String id) throws RemoteControlException {
        assertTrue(actionRC().action(ActionMatcher.withId(id)));
    }

    @Then("^a dialog should appear containing following data:$")
    public void checkDialog(Table table) throws RemoteControlException {
        ComponentState dialog = componentRC().stateOf(ofType(JDialog.class));
        ComponentState state = componentRC().stateOf(ofType(JTextArea.class), within(withId(dialog.getId())));

        assertNotNull(state);

        state.getText();

        for (List<String> content : table.raw()) {
            Pattern pattern = Pattern.compile(content.get(0));

            assertTrue("Missing data: " + content.get(0), pattern.matcher(state.getText()).find());
        }

        componentRC().interact(invoke("setVisible(false)"), withId(dialog.getId()));
    }

    private static void pause(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

}
