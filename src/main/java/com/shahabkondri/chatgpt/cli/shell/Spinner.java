package com.shahabkondri.chatgpt.cli.shell;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Spinner {

	private static final char[] SPINNER_FRAMES = { '⣾', '⣷', '⣯', '⣟', '⡿', '⢿', '⣻', '⣽' };

	private final AtomicBoolean isSpinnerRunning = new AtomicBoolean(false);

	private final TerminalPrinter terminalPrinter;

	private ScheduledExecutorService spinnerExecutorService;

	private ScheduledFuture<?> scheduledFuture;

	public Spinner(TerminalPrinter terminalPrinter) {
		this.terminalPrinter = terminalPrinter;
	}

	public void startSpinner(long delay) {
		if (isSpinnerRunning.compareAndSet(false, true)) {
			spinnerExecutorService = Executors.newSingleThreadScheduledExecutor();
			AtomicInteger index = new AtomicInteger(0);
			scheduledFuture = spinnerExecutorService.scheduleAtFixedRate(() -> {
				if (isSpinnerRunning.get()) {
					int i = index.getAndIncrement() % SPINNER_FRAMES.length;
					terminalPrinter.print("\r" + SPINNER_FRAMES[i]);
				}
			}, 0, delay, TimeUnit.MILLISECONDS);
		}
	}

	public void stopSpinner() {
		if (isSpinnerRunning.compareAndSet(true, false)) {
			scheduledFuture.cancel(true);
			spinnerExecutorService.shutdown();
			terminalPrinter.print("\r");
		}
	}

}
