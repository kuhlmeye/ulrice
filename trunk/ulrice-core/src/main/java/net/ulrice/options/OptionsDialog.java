package net.ulrice.options;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.Ulrice;
import net.ulrice.message.Translation;
import net.ulrice.message.TranslationUsage;
import net.ulrice.options.modules.IFOptionModule;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = -8292944021110980145L;

    private final JList moduleList;
    private final OptionListModel moduleListModel = new OptionListModel();
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    
    public OptionsDialog(final List<IFOptionModule> optionModules) {
        super(Ulrice.getMainFrame().getFrame());

        setTitle(getTranslationText(TranslationUsage.Title, "Options"));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        final Dimension preferredSize = new Dimension(550, 400);
        setMinimumSize(preferredSize);
        setPreferredSize(preferredSize);
        setLocationRelativeTo(Ulrice.getMainFrame().getFrame());

        moduleListModel.addAllModules(optionModules);
        moduleList = createModuleList();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(moduleList), BorderLayout.WEST);
        getContentPane().add(cardPanel, BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

        initializeOptionModules(optionModules);
        
        if (moduleListModel.getSize() > 0) {
            moduleList.setSelectedIndex(0);
        }
    }
    
    private JPanel createButtonPanel() {
        final JButton okButton = new JButton(getTranslationText(TranslationUsage.Button, "OK"));
        okButton.setPreferredSize(new Dimension(100, 28));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < moduleListModel.getSize(); i++) {
                    moduleListModel.getElementAt(i).onSave();
                }
                dispose();
            }
        });

        final JButton cancelButton = new JButton(getTranslationText(TranslationUsage.Button, "Cancel"));
        cancelButton.setPreferredSize(new Dimension(100, 28));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        final Border outsideBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY);
        final Border insideBorder = BorderFactory.createEmptyBorder();
        final Border buttonPanelBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
        buttonPanel.setBorder(buttonPanelBorder);

        return buttonPanel;
    }
    
    private String getTranslationText(final TranslationUsage usage, final String key) {
        final Translation ranslation = Ulrice.getTranslationProvider().getUlriceTranslation(usage, key);
        return ranslation.isAvailable() ? ranslation.getText() : ranslation.getKey();
    }
    
    private JList createModuleList() {
        final JList moduleList = new JList(moduleListModel);
        moduleList.setCellRenderer(new OptionsModuleRenderer());

        moduleList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
                final IFOptionModule oldActiveModule = moduleListModel.getActiveModule();
                if (oldActiveModule != null) {
                    oldActiveModule.onHide();
                }

                moduleListModel.setActiveModule((IFOptionModule) moduleList.getSelectedValue());

                final IFOptionModule newActiveModule = moduleListModel.getActiveModule();
                if (newActiveModule != null) {
                    newActiveModule.onShow();
                    cardLayout.show(cardPanel, newActiveModule.getClass().getSimpleName());
                }
            }
        });

        return moduleList;
    }

    private void initializeOptionModules(final List<IFOptionModule> optionModules) {
        for (IFOptionModule optionModule : optionModules) {
            optionModule.onInitialize();
            cardPanel.add(optionModule.getView(), optionModule.getClass().getSimpleName());
        }
    }
    
    @Override
    public void dispose() {
        for (int i = 0; i < moduleListModel.getSize(); i++) {
            moduleListModel.getElementAt(i).onClose();
        }
        super.dispose();
    }
}
