package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class DatePickerView extends JFrame {
	private DatePickerPanel datePickerPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DatePickerView frame = new DatePickerView();
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
	public DatePickerView() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 300, 356);
		setUndecorated(true);
		datePickerPanel = new DatePickerPanel();
		setContentPane(datePickerPanel);
	}

	public LocalDate getChoosenDate() {
		return datePickerPanel.getChoosenDate();
	}

	public void setDate(LocalDate date) {
		datePickerPanel.setDate(date);
	}

	private class DatePickerPanel extends AbstractCalendar {

		public DatePickerPanel() {
			super(String.class, new DatePickerTblRenderer(), new DatePickerTableModel());
		}

		@Override
		public void refreshCalendar() {
			addDatesToCalendar();
		}

		private void addDatesToCalendar() {
			((DatePickerTableModel) super.getTableModel()).setModelData(super.getCurrSetYearMonth());
		}

		public LocalDate getChoosenDate() {
			int row = super.getTblCalendar().getSelectedRow();
			int col = super.getTblCalendar().getSelectedColumn();
			LocalDate res = null;
			if (row != -1 || col != -1) {
				res = (LocalDate) super.getTblCalendar().getValueAt(row, col);
			}
			return res;
		}

		public void setDate(LocalDate date) {
			super.setCurrSetYearMonth(YearMonth.of(date.getYear(), date.getMonth()));

			int row = ((DatePickerTableModel) super.getTableModel()).getRowByDayOfMonth(date.getDayOfMonth());
			int col = ((DatePickerTableModel) super.getTableModel()).getcolumnByDayOfMonth(date.getDayOfMonth());
			if (row != -1 && col != -1) {
				super.getTblCalendar().changeSelection(row, col, false, false);

			}

		}

		private static class DatePickerTblRenderer extends JEditorPane implements TableCellRenderer {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// Insert day number.
				if (value != null) {

					setContentType("text/html");

					String res = "<b>" + ((LocalDate) value).getDayOfMonth();
					setText(res);

				} else {
					setText(null);
				}

				if (value != null) {
					// Change cell colour for weekend, week and today.

					if (column == 6 || column == 5) {
						setBackground(new Color(255, 200, 200)); // darker red
						if (((LocalDate) value).isBefore(LocalDate.now())) {
							setBackground(new Color(255, 220, 220)); // lighter red
						}
					} else {
						setBackground(new Color(255, 255, 255)); // White
						if (((LocalDate) value).isBefore(LocalDate.now())) {
							setBackground(new Color(240, 240, 240)); // Light gray
						}
					}

					// Change color for today.
					if (value.equals(LocalDate.now())) {
						setBackground(new Color(210, 205, 255)); // Purple
					}

					// cell background color when selected.
					if (isSelected) {
						setBackground(new Color(175, 205, 230)); // Blue
					}

				} else {
					// Set color for null cells.
					setBackground(new Color(247, 247, 247)); // Light gray
				}

				return this;
			}
		}

		private static class DatePickerTableModel extends DefaultTableModel {
			private static final String[] MONTHS = { "Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn" };
			private LocalDate[][] days;

			public DatePickerTableModel() {
				this.days = new LocalDate[6][7];
			}

			public void setModelData(YearMonth yearMonth) {
				// Clears the days array.
				for (int i = 0; i < 6; i++) {
					Arrays.fill(days[i], null);
				}

				// Insert new data.

				int lengthOfMonth = yearMonth.lengthOfMonth();

				int firstDayOfMonth = yearMonth.atDay(1).getDayOfWeek().getValue();

				for (int currDayOfMonth = 1; currDayOfMonth <= lengthOfMonth; currDayOfMonth++) {
					int row = (currDayOfMonth + firstDayOfMonth - 2) / 7;
					int column = (currDayOfMonth + firstDayOfMonth - 2) % 7;
					days[row][column] = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), currDayOfMonth);
				}
				super.fireTableDataChanged();
			}

			@Override
			public String getColumnName(int column) {
				return MONTHS[column];
			}

			@Override
			public LocalDate getValueAt(int row, int column) {
				LocalDate res = days[row][column];
				return res;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public int getRowCount() {
				int res = 5;
				if (days != null) {
					for (int col = 0; col < 7; col++) {
						if (days[5][col] != null) {
							res = 6;
						}
					}
				}
				return res;
			}

			@Override
			public int getColumnCount() {
				return 7;
			}

			@Override
			public Class<String> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getRowByDayOfMonth(int dayOfMonth) {
				int res = -1;

				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 7 && res == -1; col++) {

						LocalDate currday = days[row][col];
						if (currday != null) {
							int currDayOfMonth = currday.getDayOfMonth();
							if (currDayOfMonth == dayOfMonth) {
								res = row;
							}
						}
					}
				}
				return res;
			}

			public int getcolumnByDayOfMonth(int dayOfMonth) {
				int res = -1;

				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 7 && res == -1; col++) {

						LocalDate currday = days[row][col];
						if (currday != null) {
							int currDayOfMonth = currday.getDayOfMonth();
							if (currDayOfMonth == dayOfMonth) {
								res = col;
							}
						}
					}
				}
				return res;
			}

		}

	}

}
