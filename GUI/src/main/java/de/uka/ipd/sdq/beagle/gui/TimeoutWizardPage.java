package de.uka.ipd.sdq.beagle.gui;

import de.uka.ipd.sdq.beagle.core.UserConfiguration;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Page 2 of the wizard.
 * 
 * @author Christoph Michelbach
 */
public class TimeoutWizardPage extends WizardPage {

	/**
	 * The title of this page.
	 */
	private static final String TITLE = "Timeout";

	/**
	 * The description of this page.
	 */
	private static final String DESCRIPTION =
		"To avoid infinite loops or too long calculations, a timeout can be used. Please choose as you wish.";

	/**
	 * Numeric value for "adaptive timeout".
	 */
	private static final int ADAPTIVE_TIMEOUT = -2;

	/**
	 * Numeric value for "no timeout".
	 */
	private static final int NO_TIMEOUT = -1;

	/**
	 * The default setting for the timeout. [-2 → adaptive timeout] [-1 → no timeout] [≥ 0
	 * → timeout in seconds]
	 */
	private static final int DEFAULT_TIMEOUT = ADAPTIVE_TIMEOUT;

	/**
	 * The number of columns of the layout of container which contains the entire content
	 * of this page.
	 */
	private static final int MAIN_LAYOUT_NR_COLUMS = 2;

	/**
	 * The number of columns of the layout of the upper container.
	 */
	private static final int UPPER_LAYOUT_NR_COLUMS = 2;

	/**
	 * The number of columns of the layout of the lower container.
	 */
	private static final int LOWER_LAYOUT_NR_COLUMS = 3;

	/**
	 * The {@link UserConfiguration} this {@link TimeoutWizardPage} uses.
	 */
	private final UserConfiguration userConfiguration;

	/**
	 * A textbox for the timeout in seconds (if the timeout is set manually).
	 */
	private Text textboxTimeoutSeconds;

	/**
	 * The main container for this page.
	 */
	private Composite mainContainer;

	/**
	 * A sub-container.
	 */
	private Composite lowerContainer;

	/**
	 * A sub-container.
	 */
	private Composite upperContainer;

	/**
	 * Applies the default setting for the timeout. [-2 → adaptive timeout] [-1 → no
	 * timeout] [≥ 0 → timeout in seconds]
	 */
	private int timeout;

	/**
	 * Constructor setting the tile and the introduction to this page.
	 * 
	 * @param userConfiguration The {@link UserConfiguration} this
	 *            {@link TimeoutWizardPage} will use.
	 */
	public TimeoutWizardPage(final UserConfiguration userConfiguration) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setControl(this.textboxTimeoutSeconds);
		this.userConfiguration = userConfiguration;
		this.timeout = this.userConfiguration.getTimeout();
	}

	@Override
	public void createControl(final Composite parent) {
		this.mainContainer = new Composite(parent, SWT.NONE);
		final GridLayout layoutAll = new GridLayout();
		this.mainContainer.setLayout(layoutAll);
		layoutAll.numColumns = MAIN_LAYOUT_NR_COLUMS;

		this.upperContainer = new Composite(this.mainContainer, SWT.NONE);
		final GridLayout upperLayout = new GridLayout();
		this.upperContainer.setLayout(upperLayout);
		upperLayout.numColumns = UPPER_LAYOUT_NR_COLUMS;

		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		final Button radioAdaptiveTimout = new Button(this.upperContainer, SWT.RADIO);
		final Label lblRadioAdaptiveTimout = new Label(this.upperContainer, SWT.NONE);
		lblRadioAdaptiveTimout.setText("Use an adaptive timeout.");

		final Button radioSetTimout = new Button(this.upperContainer, SWT.RADIO);
		final Label lblRadioSetTimout = new Label(this.upperContainer, SWT.NONE);
		lblRadioSetTimout.setText("Use a set timout.");

		final Button radioNoTimeout = new Button(this.upperContainer, SWT.RADIO);
		final Label lblRadioNoTimeout = new Label(this.upperContainer, SWT.NONE);
		lblRadioNoTimeout.setText("Don't use a timout.");

		radioAdaptiveTimout.setSelection(true);

		final SelectionListener radioAdaptiveTimeoutSelected = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				TimeoutWizardPage.this.timeout = ADAPTIVE_TIMEOUT;
				TimeoutWizardPage.this.userConfiguration.setTimeout(TimeoutWizardPage.this.timeout);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}
		};

		final SelectionListener radioSetTimeoutSelected = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				TimeoutWizardPage.this.textboxTimeoutSeconds.setEnabled(true);

				if (TimeoutWizardPage.this.textboxTimeoutSeconds.getText().isEmpty()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
					TimeoutWizardPage.this.timeout =
						Integer.parseInt(TimeoutWizardPage.this.textboxTimeoutSeconds.getText());
					TimeoutWizardPage.this.userConfiguration.setTimeout(ADAPTIVE_TIMEOUT);
					TimeoutWizardPage.this.userConfiguration.setTimeout(TimeoutWizardPage.this.timeout);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}
		};

		final SelectionListener radioSetTimeoutDeselected = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				TimeoutWizardPage.this.textboxTimeoutSeconds.setEnabled(false);
				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}
		};

		final SelectionListener radioNoTimeoutSelected = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				TimeoutWizardPage.this.timeout = NO_TIMEOUT;
				TimeoutWizardPage.this.userConfiguration.setTimeout(TimeoutWizardPage.this.timeout);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}
		};

		radioAdaptiveTimout.addSelectionListener(radioAdaptiveTimeoutSelected);

		radioAdaptiveTimout.addSelectionListener(radioSetTimeoutDeselected);
		radioSetTimout.addSelectionListener(radioSetTimeoutSelected);
		radioNoTimeout.addSelectionListener(radioSetTimeoutDeselected);

		radioNoTimeout.addSelectionListener(radioNoTimeoutSelected);

		this.lowerContainer = new Composite(this.mainContainer, SWT.NONE);
		final GridLayout lowerLayout = new GridLayout();
		this.lowerContainer.setLayout(lowerLayout);
		lowerLayout.numColumns = LOWER_LAYOUT_NR_COLUMS;

		final Label label1 = new Label(this.lowerContainer, SWT.NONE);
		label1.setText("Custom timeout: ");

		this.textboxTimeoutSeconds = new Text(this.lowerContainer, SWT.BORDER | SWT.SINGLE);
		this.textboxTimeoutSeconds.setText("");
		this.textboxTimeoutSeconds.setEnabled(false);

		final Label label2 = new Label(this.lowerContainer, SWT.NONE);
		label2.setText("seconds");

		this.textboxTimeoutSeconds.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent keyEvent) {
			}

			@Override
			public void keyReleased(final KeyEvent keyEvent) {
				// remove everything not a number
				if (keyEvent.character < '0' || keyEvent.character > '9') {
					TimeoutWizardPage.this.textboxTimeoutSeconds.setText(
						TimeoutWizardPage.this.textboxTimeoutSeconds.getText().replace("" + keyEvent.character, ""));
				}

				if (TimeoutWizardPage.this.textboxTimeoutSeconds.getText().isEmpty()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}
		});

		this.textboxTimeoutSeconds.setLayoutData(gridData);

		// required to avoid an error in the system
		setControl(this.upperContainer);

		setPageComplete(true);
	}

	/**
	 * Returns the timeout chosen by the user.
	 * 
	 * @return the timeout chosen by the user.
	 */
	public int getTimeout() {
		return this.timeout;
	}
}
