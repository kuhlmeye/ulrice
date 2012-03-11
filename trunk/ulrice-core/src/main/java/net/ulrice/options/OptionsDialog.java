package net.ulrice.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.Ulrice;
import net.ulrice.message.Translation;
import net.ulrice.message.TranslationUsage;
import net.ulrice.options.modules.IFOptionModule;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = -8292944021110980145L;
    
    private JList moduleList = new JList();
    private OptionListModel model = new OptionListModel();
    
    public OptionsDialog(List<IFOptionModule> optionModules) {
        super(Ulrice.getMainFrame().getFrame());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(moduleList), BorderLayout.WEST);
        
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(Ulrice.getMainFrame().getFrame());

        Translation titleTranslation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Title, "Options");
        setTitle(titleTranslation.isAvailable() ? titleTranslation.getText() : titleTranslation.getKey());
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Translation okTranslation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Button, "OK");
        JButton okButton = new JButton(okTranslation.isAvailable() ? okTranslation.getText() : okTranslation.getKey());
        okButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < model.getSize(); i++) {
                    model.getElementAt(i).onSave();
                }
                dispose();
            }
        });

        Translation cancelTranslation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Button, "Cancel");
        JButton cancelButton = new JButton(cancelTranslation.isAvailable() ? cancelTranslation.getText() : cancelTranslation.getKey());
        cancelButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        moduleList.setCellRenderer(new OptionsModuleRenderer());
        moduleList.setModel(model);
        if(optionModules != null) {
            model.addAllModules(optionModules);
        }
        
        moduleList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(model.getActiveModule() != null) {
                    model.getActiveModule().onHide();
                    getContentPane().remove(2);        
                }
                
                model.setActiveModule((IFOptionModule)moduleList.getSelectedValue());
                
                if(model.getActiveModule() != null) {
                    model.getActiveModule().onShow();
                    getContentPane().add(model.getActiveModule().getView(), BorderLayout.CENTER);   
                    getContentPane().validate();                 
                }
            }
        });
        
        
        if(optionModules != null) {
            for (IFOptionModule optionModule : optionModules) {
                optionModule.onInitialize();
            }
        }
    }

}
