/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.impl.ga.TableGA;
import net.ulrice.databinding.impl.ga.TextFieldGA;
import net.ulrice.module.IFView;

/**
 * @author christof
 *
 */
public class VDataBinding extends JPanel implements IFView {

	/** generated serialVersionUID */
    private static final long serialVersionUID = -8885808334849700407L;
    private TextFieldGA<String> textFieldGA1;
	private TextFieldGA<String> textFieldGA2;
    private TableGA listGA;

	/**
	 * @see net.ulrice.module.IFView#initialize()
	 */
	@Override
	public void initialize() {
		textFieldGA1 = new TextFieldGA<String>("name", new DoNothingConverter());
		textFieldGA2 = new TextFieldGA<String>("name", new DoNothingConverter());
		
		listGA = new TableGA("list");
		
		setLayout(new BorderLayout());
		add(textFieldGA1.getComponent(), BorderLayout.NORTH);
        add(new JScrollPane(listGA.getComponent()), BorderLayout.CENTER);
		add(textFieldGA2.getComponent(), BorderLayout.SOUTH);
	}
	
	/**
	 * @see net.ulrice.module.IFView#getView()
	 */
	@Override
	public JComponent getView() {
		return this;
	}

	/**
	 * @return the textFieldGA1
	 */
	public TextFieldGA<String> getTextFieldGA1() {
		return textFieldGA1;
	}

	/**
	 * @return the textFieldGA2
	 */
	public TextFieldGA<String> getTextFieldGA2() {
		return textFieldGA2;
	}

    /**
     * @return the listGA
     */
    public TableGA getListGA() {
        return listGA;
    }

}
