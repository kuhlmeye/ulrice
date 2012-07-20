package net.ulrice.sample.module.behavior;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.ulrice.databinding.viewadapter.impl.JListViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JRadioButtonViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.ui.components.Tube;

public class VBehavior
{

	private final Tube tube;

	private final JTextComponentViewAdapter firstnameVA = new JTextComponentViewAdapter(new JTextField(25), null);
	private final JTextComponentViewAdapter lastnameVA = new JTextComponentViewAdapter(new JTextField(25), null);
	private final JRadioButtonViewAdapter<Gender> maleVA = new JRadioButtonViewAdapter<Gender>(
	    new JRadioButton("Male"), null, Gender.MALE);
	private final JRadioButtonViewAdapter<Gender> femaleVA = new JRadioButtonViewAdapter<Gender>(new JRadioButton(
	    "Female"), null, Gender.FEMALE);
	private final JRadioButtonViewAdapter<Gender> unspecifiedVA = new JRadioButtonViewAdapter<Gender>(new JRadioButton(
	    "Unspecified"), null, Gender.UNSPECIFIED);
	private final JListViewAdapter occupationVA = new JListViewAdapter(new JList(new String[] {
	    "Customer", "Software Architect", "Software Engineer", "Software Tester", "Removal Specialist", "Other"
	}), null);
	private final UTableViewAdapter knowledgeVA = new UTableViewAdapter(new UTableComponent(1), null);

	public VBehavior(final CBehavior behavior)
	{
		super();

		// TODO why, by all means, is this necessary?
		knowledgeVA.getComponent().init(knowledgeVA);
		
		JPanel personalPanel = new JPanel(new GridBagLayout());
		personalPanel.setName("Personal");
		Constraints c = new Constraints();

		personalPanel.add(createLabel("Firstname", firstnameVA.getComponent()), c);
		personalPanel.add(firstnameVA.getComponent(), c.next().width(3).weight(1).fillHorizontal());

		personalPanel.add(createLabel("Lastname", lastnameVA.getComponent()), c.nextLine());
		personalPanel.add(lastnameVA.getComponent(), c.next().width(3).weight(1).fillHorizontal());

		ButtonGroup group = new ButtonGroup();
		group.add((AbstractButton) maleVA.getComponent());
		group.add((AbstractButton) femaleVA.getComponent());
		group.add((AbstractButton) unspecifiedVA.getComponent());
		personalPanel.add(new JLabel("Gender"), c.nextLine());
		personalPanel.add(maleVA.getComponent(), c.next());
		personalPanel.add(femaleVA.getComponent(), c.next());
		personalPanel.add(unspecifiedVA.getComponent(), c.weight(1).next());

		personalPanel.add(createLabel("Occupation", occupationVA.getComponent()), c.nextLine());
		personalPanel.add(createScrollPane(occupationVA.getComponent(), 128, 64), c.next().width(3).weight(1).fill());

		JPanel knowledgePanel = new JPanel(new GridBagLayout());
		knowledgePanel.setName("Knowledge");
		c = new Constraints();

		JButton knowledgeAdd = new JButton("Add");
		knowledgeAdd.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				behavior.addKnowledge();
			}
		});
		JButton knowledgeRemove = new JButton("Remove");
		knowledgeRemove.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				behavior.removeKnowledge();
			}
		});
		knowledgePanel.add(createLabel("Knowledge", knowledgeVA.getComponent()), c.height(3));
		knowledgeVA.getComponent().setPreferredSize(new Dimension(128, 128));
		knowledgePanel.add(knowledgeVA.getComponent(), c.next().height(3).weight(1).fill());
		knowledgePanel.add(knowledgeAdd, c.next().fillHorizontal());
		knowledgePanel.add(knowledgeRemove, c.nextLine().next().next().fillHorizontal());

		tube = new Tube();
		tube.addTab("Personal", personalPanel);
		tube.addTab("Knowledge", knowledgePanel);
	}

	public JComponent getView()
	{
		return tube;
	}

	public JTextComponentViewAdapter getFirstnameVA()
	{
		return firstnameVA;
	}

	public JTextComponentViewAdapter getLastnameVA()
	{
		return lastnameVA;
	}

	public JRadioButtonViewAdapter<Gender> getMaleVA()
	{
		return maleVA;
	}

	public JRadioButtonViewAdapter<Gender> getFemaleVA()
	{
		return femaleVA;
	}

	public JRadioButtonViewAdapter<Gender> getUnspecifiedVA()
	{
		return unspecifiedVA;
	}

	public JListViewAdapter getOccupationVA()
	{
		return occupationVA;
	}

	public UTableViewAdapter getKnowledgeVA()
	{
		return knowledgeVA;
	}

	private static JLabel createLabel(String text, Component labelFor)
	{
		JLabel label = new JLabel(text);

		label.setLabelFor(labelFor);

		return label;
	}

	private static JScrollPane createScrollPane(Component component, int preferredWidth, int preferredHeight)
	{
		JScrollPane pane = new JScrollPane(component);

		pane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

		return pane;
	}
}
