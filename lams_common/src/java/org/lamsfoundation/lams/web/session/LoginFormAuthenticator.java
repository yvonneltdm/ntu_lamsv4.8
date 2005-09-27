/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */
package org.lamsfoundation.lams.web.session;

import java.io.IOException;

import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
/**
 * This class is special for JBOSS/Tomcat.
 * 
 * The resson is JBOSS ignores any Filter if j_security_checks submit. It becomes impossilble to preset the current
 * session ID to UniversialLoginModule. This class will replace default <code>SystemSessionFilter</code> setting.
 * 
 * @author Steve.Ni
 * 
 * $version$
 */
public class LoginFormAuthenticator extends FormAuthenticator{

	public boolean authenticate(Request request, Response response, LoginConfig config) throws IOException {
		SessionManager.startSession(request,response);
		boolean result = super.authenticate(request, response, config);
		SessionManager.endSession();
		return result;
	}

	
}
