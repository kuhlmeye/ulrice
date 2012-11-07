package net.ulrice.remotecontrol.impl.keyboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RemoteKeyboardApp extends JFrame implements KeyListener
{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args)
	{
		RemoteKeyboardApp tester = new RemoteKeyboardApp();

		tester.setVisible(true);
	}

	private final JTextField inputField;
	private final JTextField codeField;
	private final JTextField instructionField;
	private final JTextArea resultArea;

	private final List<RemoteKeyboardInstruction> instructions;

	public RemoteKeyboardApp() throws HeadlessException
	{
		super("Key Mapping Application");

		inputField = new JTextField(120);
		inputField.addKeyListener(this);

		codeField = new JTextField(120);
		codeField.setEditable(false);

		instructionField = new JTextField(120);
		instructionField.setEditable(false);

		resultArea = new JTextArea(20, 120);

		instructions = new ArrayList<RemoteKeyboardInstruction>();

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}

		});

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(8, 16, 8, 16);
		c.fill = GridBagConstraints.HORIZONTAL;

		add(new JLabel("Text:"), c);

		c.gridx += 1;
		c.weightx = 1;

		add(inputField, c);

		c.gridx = 1;
		c.gridy += 1;
		c.weightx = 0;

		add(new JLabel("Code:"), c);

		c.gridx += 1;
		c.weightx = 1;

		add(codeField, c);

		c.gridx = 1;
		c.gridy += 1;
		c.weightx = 0;

		add(new JLabel("Instruction:"), c);

		c.gridx += 1;
		c.weightx = 1;

		add(instructionField, c);

		c.gridx = 1;
		c.gridy += 1;
		c.gridwidth = 2;

		add(new JLabel("Configuration:"), c);

		c.gridy += 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;

		add(new JScrollPane(resultArea), c);

		pack();
	}

	private void updateInstructionField()
	{
		StringBuilder builder = new StringBuilder();
		Iterator<RemoteKeyboardInstruction> it = instructions.iterator();

		while (it.hasNext())
		{
			builder.append(it.next());

			if (it.hasNext())
			{
				builder.append(", ");
			}
		}

		instructionField.setText(builder.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				return;

			default:
				instructions.add(RemoteKeyboardInstruction.press(e.getKeyCode()));
				updateInstructionField();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_ENTER:
				register();
				//$FALL-THROUGH$
			case KeyEvent.VK_ESCAPE:
				inputField.setText("");
				codeField.setText("");
				instructions.clear();
				updateInstructionField();
				return;

			default:
				if (e.getKeyChar() == 65535)
				{
					switch (e.getKeyCode())
					{
						case KeyEvent.VK_SHIFT:
						case KeyEvent.VK_CONTROL:
						case KeyEvent.VK_ALT:
						case KeyEvent.VK_META:
						case KeyEvent.VK_ALT_GRAPH:
							break;

						default:
							StringBuilder builder = new StringBuilder(codeField.getText());

							builder.append("{");

							if (e.getModifiers() != 0)
							{
								builder.append(KeyEvent.getKeyModifiersText(e.getModifiers())).append(" ");
							}

							builder.append(KeyEvent.getKeyText(e.getKeyCode())).append("}");

							codeField.setText(builder.toString());
							break;
					}
				}

				instructions.add(RemoteKeyboardInstruction.release(e.getKeyCode()));
				updateInstructionField();
				break;
		}
	}

	private void register()
	{
		int caretPosition = resultArea.getCaretPosition();

		StringBuilder code = new StringBuilder("register(");

		code.append("\'").append(codeField.getText()).append("\'");
		code.append(", ");
		code.append(instructionField.getText());
		code.append(");\n");

		StringBuilder result = new StringBuilder(resultArea.getText());

		result.insert(caretPosition, code);

		resultArea.setText(result.toString());
		resultArea.setCaretPosition(caretPosition + code.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		char ch = e.getKeyChar();
		StringBuilder builder = new StringBuilder(codeField.getText());

		switch (ch)
		{
			case '\n':
			case '\r':
			case 27:
				return;

			case '\t':
				builder.append("\\t");
				break;

			case '\b':
				builder.append("\\b");
				break;

			case '\f':
				builder.append("\\f");
				break;

			case '\\':
				builder.append("\\\\");
				break;

			case '\'':
				builder.append("\\\'");
				break;

			case '\"':
				builder.append("\\\"");
				break;

			default:
				if ((ch >= 32) && (ch <= 126))
				{
					builder.append(ch);
				}
				else
				{
					builder.append("\\u");

					String hex = Integer.toHexString(ch);

					for (int j = hex.length(); j < 4; j += 1)
					{
						builder.append("0");
					}

					builder.append(hex);
				}
				break;
		}

		codeField.setText(builder.toString());
	}

}
