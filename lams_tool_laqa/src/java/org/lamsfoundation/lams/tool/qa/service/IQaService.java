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
package org.lamsfoundation.lams.tool.qa.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.tool.BasicToolVO;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.qa.QaApplicationException;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.QaUsrResp;
import org.lamsfoundation.lams.usermanagement.User;


/**
 * This interface define the contract that all Survey service provider must
 * follow.
 * 
 * @author Ozgur Demirtas
 */
public interface IQaService 
{
	/**
     * Return the qa object according to the requested content id.
     * @param toolContentId the tool content id
     * @return the qa object
     */
    
	public QaContent retrieveQa(long toolContentId);
	
	
	/**
     * Return the qa object according to the requested content id.
     * @param toolContentId the tool content id
     * @return the qa object or null
     */
    
	public QaContent loadQa(long toolContentId);
	
	
	
	/**
     * Return the question content object according to the requested question content id.
     * @param qaQueContentId qa question content id
     * @return the qa question object
     */
	public QaQueContent retrieveQaQue(long qaQueContentId);
	
	public QaQueUsr loadQaQueUsr(Long userId);
		
	public void createQaQue(QaQueContent qaQueContent);
	
	public void createQaUsrResp(QaUsrResp qaUsrResp);
	
	public QaUsrResp retrieveQaUsrResp(long responseId);
	
	public void updateQaUsrResp(QaUsrResp qaUsrResp);
	
	
	/**
     * Return the qa session object according to the requested session id.
     * @param qaSessionId qa session id
     * @return the qa session object
     */
	public QaSession retrieveQaSession(long qaSessionId);
	
	public QaSession retrieveQaSessionOrNullById(long qaSessionId) throws QaApplicationException;
		
	public void createQaSession(QaSession qaSession);
	
	public void createQaQueUsr(QaQueUsr qaQueUsr);
	
	public void updateQaSession(QaSession qaSession);
	
	
	/**
     * Return the qa que user object according to the requested usr id.
     * @param qaQaUsrId qa usr id
     * @return the qa que usr object
     */
	public QaQueUsr retrieveQaQueUsr(long qaQaUsrId);
	
	public void updateQa(QaContent qa);
	
	public void createQa(QaContent qa);
	
	public void deleteQa(QaContent qa);
    
	public void deleteQaSession(QaSession QaSession) throws QaApplicationException;
	
	public void deleteUsrRespByQueId(Long qaQueId);
	
	public void deleteQaById(Long qaId);
	
	public void deleteQaQueUsr(QaQueUsr qaQueUsr);
	
	public void removeUserResponse(QaUsrResp resp);
	
    public User getCurrentUserData(String username);
    
    /**
     * 
     * copyToolContent(Long fromContentId, Long toContentId)
     * should ideally should not be part this interface as it is
     * already part of the interface ToolSessionManager. It is here for development purposes.
     * return void
     * @param fromContentId
     * @param toContentId
     */
    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException;
    
    public void setAsDefineLater(Long toolContentId) throws DataMissingException, ToolException;
    
    public void unsetAsDefineLater(Long toolContentId);
    
    public void setAsRunOffline(Long toolContentId) throws DataMissingException, ToolException;
    
    /**
     * TO BE DEFINED AS PART OF MAIN TOOL API
     * updates user's tool session status from INCOMPLETE to COMPLETED
     * @param userId
     */
    public void setAsForceComplete(Long userId);
    
    /**
     * TO BE DEFINED AS PART OF MAIN TOOL API
     * @param toolSessionId
     */
    public void setAsForceCompleteSession(Long toolSessionId);
    
    public boolean studentActivityOccurred(QaContent qa);
    
    public boolean studentActivityOccurredGlobal(QaContent qaContent);
    
    
    /**
     * removeToolContent(Long toolContentId) should ideally should not be part this interface as it is
     * already part of the interface ToolSessionManager. It is here for development purposes.
     * return void
     * @param toolContentId
     */
    public void removeToolContent(Long toolContentId);
    
    
    /**
     * createToolSession(Long toolSessionId, Long toolContentId) should ideally should not be part this interface as it is
     * already part of the interface ToolSessionManager. It is here for development purposes.
     * 
     * It is also defined here since in development we want to be able call it directly from our web-layer 
     * instead of it being called by the container.
     * @param toolSessionId
     * @param toolContentId
     */
    public void createToolSession(Long toolSessionId, Long toolContentId) throws ToolException;
    
    /**
     * leaveToolSession(Long toolSessionId, User learner) should ideally should not be part this interface as it is
     * already part of the interface ToolSessionManager. It is here for development purposes.
     * 
     * It is also defined here since in development we want to be able call it directly from our web-layer 
     * instead of it being called by the container.
     * @param toolSessionId
     * @param toolContentId
     */
    public String leaveToolSession(Long toolSessionId,User learner) throws DataMissingException, ToolException;
    
    public BasicToolVO getToolBySignature(String toolSignature);
    
    public long getToolDefaultContentIdBySignature(String toolSignature);
    
    public int countSessionUser(QaSession qaSession);
    
    public List getToolSessionsForContent(QaContent qa);
    
    public QaQueContent getToolDefaultQuestionContent(long contentId);
    
    /** repository access related methods  from here: */
    public void configureContentRepository() throws QaApplicationException;
    
    public ITicket getRepositoryLoginTicket() throws QaApplicationException;
    
    public void deleteFromRepository(Long uuid, Long versionID) throws QaApplicationException;

    public NodeKey uploadFileToRepository(InputStream stream, String fileName) throws QaApplicationException;
    
    public InputStream downloadFile(Long uuid, Long versionID)throws QaApplicationException;
	
	/** repository access related methods  till here */
    public void persistFile(String uuid, boolean isOnlineFile, String fileName, QaContent qaContent) throws QaApplicationException;
}

