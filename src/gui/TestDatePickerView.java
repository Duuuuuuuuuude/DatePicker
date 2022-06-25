package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import javax.swing.JButton;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;

public class TestDatePickerView extends JFrame {

	private JPanel contentPane;
	private JTextField txtDate;
	private DateTimeFormatter dateFormatter;
	private JButton btnDatePicker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestDatePickerView frame = new TestDatePickerView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private TestDatePickerView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		txtDate = new JTextField();
		GridBagConstraints gbc_txtDate = new GridBagConstraints();
		gbc_txtDate.insets = new Insets(0, 0, 0, 5);
		gbc_txtDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDate.gridx = 6;
		gbc_txtDate.gridy = 1;
		contentPane.add(txtDate, gbc_txtDate);
		txtDate.setColumns(10);
		txtDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

		btnDatePicker = new JButton("pick a date");
		btnDatePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDatePickerClicked();
			}
		});
		GridBagConstraints gbc_btnDatePicker = new GridBagConstraints();
		gbc_btnDatePicker.gridx = 7;
		gbc_btnDatePicker.gridy = 1;
		contentPane.add(btnDatePicker, gbc_btnDatePicker);

		dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

	}

	private void btnDatePickerClicked() {
		DatePickerView dp = new DatePickerView();

		// Auto close window.
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addVetoableChangeListener("focusedWindow",
				new VetoableChangeListener() {
					private boolean gained = false;

					@Override
					public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
						if (pce.getNewValue() == dp) {
							gained = true;
							
						}
						if (gained && pce.getNewValue() != dp) {

							// Update date in text field using date picker.
							LocalDate choosenDate = dp.getChoosenDate();
							if (choosenDate != null) {
								txtDate.setText(choosenDate.format(dateFormatter).toString());
							}
							dp.setVisible(false);

						}
					}
				});

		// Update date in date picker using text field.
		try {
			LocalDate typedInDate = LocalDate.parse(txtDate.getText(), dateFormatter);
			dp.setDate(typedInDate);
		} catch (DateTimeException dte) {
			System.out.println("Not a valid date");
		}

		dp.setLocation(btnDatePicker.getLocationOnScreen().x + 20, (btnDatePicker.getLocationOnScreen().y + 30));
		dp.setVisible(true);
	}

}
