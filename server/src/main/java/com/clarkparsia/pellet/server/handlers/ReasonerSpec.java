package com.clarkparsia.pellet.server.handlers;

import java.util.Set;

import com.clarkparsia.pellet.server.PelletServer;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import io.undertow.Handlers;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ExceptionHandler;

/**
 * Abstract Spec with tools for wrapping and setting up HttpHandlers implementing reasoner's functionality.
 *
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ReasonerSpec implements PathHandlerSpec {

	protected final Set<ServiceDecoder> mDecoders;
	protected final Set<ServiceEncoder> mEncoders;

	public static String REASONER_PATH = PelletServer.ROOT_PATH + "reasoner";

	protected final ServerState mServerState;

	public ReasonerSpec(final ServerState theServerState,
	                    final Set<ServiceDecoder> theDecoders,
	                    final Set<ServiceEncoder> theEncoders) {
		mServerState = theServerState;
		mDecoders = theDecoders;
		mEncoders = theEncoders;
	}

	protected HttpHandler wrapHandlerToMethod(final String theMethod, final HttpHandler theHandler) {
		final ExceptionHandler aExceptionHandler = new ExceptionHandler(theHandler);
		aExceptionHandler.addExceptionHandler(ServerException.class, new ServerExceptionHandler());

		// Since we're doing IO in the Handlers, we have to wrap them in a BlockingHandler
		final BlockingHandler aFnHandler = new BlockingHandler(aExceptionHandler);

		// Enable the functionality for only the method selected
		return Handlers.predicate(Predicates.parse("method("+ theMethod +")"),
		                          aFnHandler,
		                          new MethodNotAllowedHandler(theMethod));
	}

	protected String path(final String theUrlPart) {
		return REASONER_PATH + "/" + theUrlPart;
	}
}
