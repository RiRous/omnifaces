/*
 * Copyright 2014 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.exceptionhandler;

import static org.omnifaces.util.Messages.addGlobal;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

/**
 * This exception handler will add every exception as a global FATAL faces message. Refer to the documentation of
 * {@link FacesMessageExceptionHandlerFactory} to see how to set this up.
 * <p>
 * <strong>Note:</strong> it's your own responsibility to make sure that the faces messages are being shown. Make sure
 * that there's a <code>&lt;h:messages&gt;</code> or any equivalent component (OmniFaces, PrimeFaces, etc) is present
 * in the view and that it can handle global messages and that it's explicitly or automatically updated in case of ajax
 * requests. Also make sure that you don't have bugs in rendering of your views. This exception handler is not capable
 * of handling exceptions during render response. It will fail silently.
 * <p>
 * If more fine grained control of creating the FATAL faces message is desired, then the developer can opt to extend
 * this {@link FacesMessageExceptionHandler} and override the following method:
 * <ul>
 * <li>{@link #createFatalMessage(Throwable)}
 * </ul>
 *
 * @author Bauke Scholtz
 * @see FacesMessageExceptionHandlerFactory
 * @since 1.8
 */
public class FacesMessageExceptionHandler extends ExceptionHandlerWrapper {

	// Variables ------------------------------------------------------------------------------------------------------

	private ExceptionHandler wrapped;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Construct a new faces message exception handler around the given wrapped exception handler.
	 * @param wrapped The wrapped exception handler.
	 */
	public FacesMessageExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Set every exception as a global FATAL faces message.
	 */
	@Override
	public void handle() throws FacesException {
		for (Iterator<ExceptionQueuedEvent> iter = getUnhandledExceptionQueuedEvents().iterator(); iter.hasNext();) {
			String message = createFatalMessage(iter.next().getContext().getException());
			addGlobal(new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
			iter.remove();
		}

		wrapped.handle();
	}

	/**
	 * Create fatal message based on given exception which will in turn be passed to
	 * {@link FacesContext#addMessage(String, javax.faces.application.FacesMessage)}.
	 * The default implementation returns {@link Throwable#toString()}.
	 * @param exception The exception to create fatal message for.
	 * @return The fatal message created based on the given exception.
	 */
	protected String createFatalMessage(Throwable exception) {
		return exception.toString();
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

}