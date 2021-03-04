package com.diefesson.filesync.cli;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.diefesson.filesync.App;
import com.diefesson.filesync.io.AuthException;

/**
 * 
 * @author Diefesson de Sousa Silva
 *
 */
public class Cli implements Runnable {

	private final App app;
	private final Scanner scanner;

	public Cli(App app) {
		this.app = app;
		scanner = new Scanner(System.in);
	}

	private void handleListen(Command c) {
		try {
			if (c.argCount() != 1)
				throw new IllegalArgumentException();
			var port = Integer.parseInt(c.getArg(0));
			app.listen(port);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: listen <port>");
		}
	}

	private void handleUnlisten(Command c) {
		try {
			if (c.argCount() != 1)
				throw new IllegalArgumentException();
			var port = Integer.parseInt(c.getArg(0));
			app.unlisten(port);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: unlisten <port>");
		}
	}

	private void handlePorts(Command c) {
		try {
			if (c.argCount() != 0)
				throw new IllegalArgumentException();
			for (var p : app.getPorts())
				System.out.println(p);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: ports");
		}
	}

	private void handleDownload(Command c) {
		try {
			if (c.argCount() != 3)
				throw new IllegalArgumentException();
			var address = c.getArg(0);
			var port = Integer.parseInt(c.getArg(1));
			var path = c.getArg(2);
			app.download(address, port, path).get();
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: download <address> <port> <path>");
		} catch (AuthException e) {
			System.out.println("Authentication error");
		} catch (IOException | ExecutionException | InterruptedException e) {
			System.out.println("Download error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleUpload(Command c) {
		try {
			if (c.argCount() != 3)
				throw new IllegalArgumentException();
			var address = c.getArg(0);
			var port = Integer.parseInt(c.getArg(1));
			var path = c.getArg(2);
			app.upload(address, port, path).get();
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: upload <address> <port> <path>");
		} catch (AuthException e) {
			System.out.println("Authentication error");
		} catch (IOException | ExecutionException | InterruptedException e) {
			System.out.println("Upload error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleAddUser(Command c) {
		try {
			if (c.argCount() != 2)
				throw new IllegalArgumentException();
			var username = c.getArg(0);
			var password = c.getArg(1);
			app.getUserManager().addUser(username, password);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: add_user <username> <password>");
		}
	}

	private void handleListUsers(Command c) {
		try {
			if (c.argCount() != 0)
				throw new IllegalArgumentException();
			for (var username : app.getUserManager().getUsernames())
				System.out.println(username);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: list_users");
		}
	}

	private void handleRemoveUser(Command c) {
		try {
			if (c.argCount() != 1)
				throw new IllegalArgumentException();
			app.getUserManager().removeUser(c.getArg(0));
		} catch (Exception e) {
			System.out.println("Usage: remove_user <username>");
		}
	}

	private void handleLogin(Command c) {
		try {
			if (c.argCount() != 2)
				throw new IllegalArgumentException();
			app.getUserManager().setUsername(c.getArg(0));
			app.getUserManager().setPassword(c.getArg(1));
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: login <username> <password>");
		}
	}

	private void handleGoAnonymous(Command c) {
		try {
			if (c.argCount() != 0)
				throw new IllegalArgumentException();
			app.getUserManager().goAnonymous();
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: go_anonymous");
		}
	}

	private void handleSaveCredentials(Command c) {
		try {
			if (c.argCount() != 0)
				throw new IllegalArgumentException();
			app.getUserManager().saveToFile(null);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: save_credentials");
		} catch (IOException e) {
			System.out.println("Save error: " + e.getMessage());
		}
	}

	private void handleLoadCredentials(Command c) {
		try {
			if (c.argCount() != 0)
				throw new IllegalArgumentException();
			app.getUserManager().loadFromFile(null);
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: load_credentials");
		} catch (IOException e) {
			System.out.println("Load error: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		while (true) {
			System.out.print("> ");
			var c = nextCommand();
			switch (c.getName()) {
			case CliConstants.LISTEN -> handleListen(c);
			case CliConstants.UNLISTEN -> handleUnlisten(c);
			case CliConstants.PORTS -> handlePorts(c);
			case CliConstants.DOWNLOAD -> handleDownload(c);
			case CliConstants.UPLOAD -> handleUpload(c);
			case CliConstants.ADD_USER -> handleAddUser(c);
			case CliConstants.LIST_USERS -> handleListUsers(c);
			case CliConstants.REMOVE_USER -> handleRemoveUser(c);
			case CliConstants.LOGIN -> handleLogin(c);
			case CliConstants.GO_ANONYMOUS -> handleGoAnonymous(c);
			case CliConstants.SAVE_CREDENTIALS -> handleSaveCredentials(c);
			case CliConstants.LOAD_CREDENTIALS -> handleLoadCredentials(c);
			case CliConstants.EXIT -> System.exit(0);
			default -> System.out.println("Unknow comand: " + c.getName());
			}
		}
	}

	private Command nextCommand() {
		return Command.parse(scanner.nextLine());
	}

}
