package com.shahabkondri.chatgpt.cli.shell;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides a simple text-based spinner for the terminal. The spinner is used to indicate
 * that a background task is in progress, such as loading or processing data.
 *
 * @author Shahab Kondri
 */
@Component
public class Spinner {

	private static final char[] SPINNER_FRAMES = { '⣾', '⣷', '⣯', '⣟', '⡿', '⢿', '⣻', '⣽' };

	private static final long SPINNER_SCHEDULER_PERIOD = 100;

	private final AtomicBoolean isSpinnerRunning = new AtomicBoolean(false);

	private final TerminalPrinter terminalPrinter;

	private ScheduledExecutorService spinnerExecutorService;

	private ScheduledFuture<?> scheduledFuture;

	/**
	 * Constructs a new Spinner instance with the specified terminal printer.
	 * @param terminalPrinter The terminal printer for displaying the spinner in the
	 * terminal.
	 */
	public Spinner(TerminalPrinter terminalPrinter) {
		this.terminalPrinter = terminalPrinter;
	}

	/**
	 * Starts the spinner animation in the terminal, indicating that a background task is
	 * in progress. If the spinner is already running, this method has no effect.
	 */
	public void startSpinner() {
		if (isSpinnerRunning.compareAndSet(false, true)) {
			spinnerExecutorService = Executors.newSingleThreadScheduledExecutor();
			AtomicInteger index = new AtomicInteger(0);
			scheduledFuture = spinnerExecutorService.scheduleAtFixedRate(() -> {
				if (isSpinnerRunning.get()) {
					int i = index.getAndIncrement() % SPINNER_FRAMES.length;
					terminalPrinter.print("\r" + SPINNER_FRAMES[i]);
				}
			}, 0, SPINNER_SCHEDULER_PERIOD, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Stops the spinner animation in the terminal, indicating that the background task
	 * has completed or been cancelled. If the spinner is not running, this method has no
	 * effect.
	 */
	public void stopSpinner() {
		if (isSpinnerRunning.compareAndSet(true, false)) {
			scheduledFuture.cancel(true);
			spinnerExecutorService.shutdown();
			terminalPrinter.print("\r");
		}
	}

}
