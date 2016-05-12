package de.developgroup.mrf;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import de.developgroup.mrf.server.handler.RoverHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;

public class Main {

	public static final int SERVER_PORT= 80;
	public static final int SERVER_PORT_DEV= 8000;

	private static boolean useMocks = false;
	private static boolean developerMode = false;

	@Inject
	public static RoverHandler roverHandler;

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		boolean exit = parseArgs(args);
		if(exit){
			return;
		}

		NonServletModule nonServletModule = new NonServletModule(useMocks);
		RoverServletsModule roverServletsModule = new RoverServletsModule();

		Injector injector = Guice.createInjector(nonServletModule,
				roverServletsModule);

		// initialize rover handler
		roverHandler.initRover();

		// set up jetty default server
		int port;
		if(developerMode){
			port = SERVER_PORT_DEV;
		} else {
			// productive settings
			port = SERVER_PORT;
		}
		Server server = new Server(port);
		ServletContextHandler servletContextHandler = new ServletContextHandler(
				server, "/", ServletContextHandler.SESSIONS);
		servletContextHandler.addFilter(GuiceFilter.class, "/*",
				EnumSet.allOf(DispatcherType.class));

		servletContextHandler.addServlet(DefaultServlet.class, "/");

		// serve client files
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		URL clientDir = Main.class.getClassLoader().getResource("client");
		if (clientDir != null) {
			resourceHandler.setResourceBase(clientDir.toExternalForm());
		} else {

		}
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler,
				servletContextHandler, new DefaultHandler() });
		server.setHandler(handlers);

		try {
			server.start();
			server.join();
		} catch (Exception e) {

		}
	}

	public static boolean parseArgs(String[] args){
		int i = 0;
		String arg;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.equals("-m") || arg.equals("--use-mocks")) {
				LOGGER.info("use mocks activated");
				useMocks = true;
			}

			if (arg.equals("-d") || arg.equals("--dev")) {
				LOGGER.info("developer mode activated. Jetty server port is now "+SERVER_PORT_DEV);
				developerMode = true;
			}

			if (arg.equals("-h") || arg.equals("--help")) {
				System.out.println("Valid Arguments:\n");
				System.out.println("-m --use-mocks\t-> mockup gpio ports");
				System.out.println("-d --dev\t-> start in developer mode");
				System.out.println("-h --help\t-> this help output");

				return true;
			}

		}
		return false;
	}
}
