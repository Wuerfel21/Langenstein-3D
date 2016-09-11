package net.irq_interactive.langenstein3D.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class TestLoadDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6904860503530006201L;
	private final JPanel contentPanel = new JPanel();
	protected JSpinner spinner;
	protected boolean canceled = true;

	/**
	 * Create the dialog.
	 */
	public TestLoadDialog(Frame frame) {
		super(frame, "Load test level", true);
		setResizable(false);
		setBounds(100, 100, 320, 170);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{188, 188, 188, 0};
		gbl_contentPanel.rowHeights = new int[]{20, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblWhichTestLevel = new JLabel("Which test level to load?");
			GridBagConstraints gbc_lblWhichTestLevel = new GridBagConstraints();
			gbc_lblWhichTestLevel.gridwidth = 3;
			gbc_lblWhichTestLevel.insets = new Insets(0, 0, 5, 5);
			gbc_lblWhichTestLevel.gridx = 0;
			gbc_lblWhichTestLevel.gridy = 0;
			contentPanel.add(lblWhichTestLevel, gbc_lblWhichTestLevel);
		}
		{
			spinner = new JSpinner();
			spinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
			GridBagConstraints gbc_spinner = new GridBagConstraints();
			gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
			gbc_spinner.insets = new Insets(0, 0, 0, 5);
			gbc_spinner.gridx = 1;
			gbc_spinner.gridy = 2;
			contentPanel.add(spinner, gbc_spinner);
		}
		{
			ActionListener buttonAction = new ActionListener() {


				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("OK")) canceled = false;
					TestLoadDialog.this.setVisible(false);
					TestLoadDialog.this.dispose();
				}
			};
			
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(buttonAction);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(buttonAction);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public int getSpinnerVal() {
		return ((Integer)spinner.getValue()).intValue();
	}
	
	public boolean isCanceled() {
		return canceled;
	}

}
