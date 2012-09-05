package net.ulrice.sample.module.behavior;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.ActionType;
import net.ulrice.module.impl.action.UlriceAction;
import net.ulrice.sample.SingleListTableModel;
import net.ulrice.sample.SingleObjectModel;
import net.ulrice.sample.TableAMBuilder;

public class CBehavior extends AbstractController {

    private static UlriceAction SAVE_ACTION = new UlriceAction("Save", "Save", true, ActionType.ModuleAction,
        new ImageIcon(CBehavior.class.getClassLoader().getResource("net/ulrice/sample/save.gif"))) {

        private static final long serialVersionUID = -3947104621476257214L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Ulrice.getActionManager().performAction(this, e);
        }
    };

    private final VBehavior view = new VBehavior(this);
    private final SingleObjectModel<BehaviorDTO> behaviorModel =
            new SingleObjectModel<BehaviorDTO>(BehaviorDTO.class) {
                {
                    setAttributeModel("data.occupation", new SingleListTableModel<String>(String.class).build());
                    setAttributeModel("data.knowledge",
                        new TableAMBuilder(this, "data.knowledge", KnowledgeDTO.class).addColumn("knowledge")
                            .addColumn("stars").addColumn("comment").build());
                }
            };
    private final BindingGroup bindingGroup = new BindingGroup();
    private final BehaviorDTO behaviorDTO = new BehaviorDTO();

    public CBehavior() {
    }

    @Override
    public void postCreate() {
        super.postCreate();

        behaviorModel.setData(behaviorDTO);

        bindingGroup.bind(behaviorModel.getAttributeModel("firstname"), view.getFirstnameVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("lastname"), view.getLastnameVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("gender"), view.getMaleVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("gender"), view.getFemaleVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("gender"), view.getUnspecifiedVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("occupation"), view.getOccupationVA());
        bindingGroup.bind(behaviorModel.getAttributeModel("knowledge"), view.getKnowledgeVA());

        bindingGroup.read();
    }

    @Override
    public JComponent getView() {
        return view.getView();
    }

    @Override
    public List<ModuleActionState> getHandledActions() {
        return Arrays.asList(new ModuleActionState(true, SAVE_ACTION));
    }

    @Override
    public boolean performModuleAction(String actionId) {
        if ("Save".equals(actionId)) {
            bindingGroup.write();

            SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
		            BehaviorDTO data = behaviorModel.getData();

		            System.out.println("Saved data: " + data);

		            JDialog dialog = new JDialog(Ulrice.getMainFrame().getFrame(), "Saved");

		            dialog.setLayout(new BorderLayout());
		            dialog.add(new JTextArea("If I'd have a database, I'd have saved:\n\n" + data.toString(), 12, 40),
		                BorderLayout.CENTER);
		            dialog.pack();
		            dialog.setLocationRelativeTo(Ulrice.getMainFrame().getFrame());
		            dialog.setVisible(true);
				}
            	
            });
            
            return true;
        }

        return false;
    }

    @Override
    public void onClose(IFClosing closing) {
        closing.doClose();
    }

    public void addKnowledge() {
        bindingGroup.write();
        behaviorDTO.getKnowledge().add(new KnowledgeDTO("", "", ""));
        bindingGroup.read();
    }

    public void removeKnowledge() {
        bindingGroup.write();
        List< ?> selectedObjects = view.getKnowledgeVA().getSelectedObjects();
        behaviorDTO.getKnowledge().removeAll(selectedObjects);
        bindingGroup.read();
    }
}
