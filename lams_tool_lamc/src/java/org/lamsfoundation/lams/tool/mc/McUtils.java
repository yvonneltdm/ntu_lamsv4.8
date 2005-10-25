/*
 * Created on 21/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lamsfoundation.lams.tool.mc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.web.McAuthoringForm;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * @author Ozgur Demirtas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * Common utility functions live here.  
 */
public abstract class McUtils implements McAppConstants {

	static Logger logger = Logger.getLogger(McUtils.class.getName());
	
	public static IMcService getToolService(HttpServletRequest request)
	{
		IMcService mcService=(IMcService)request.getSession().getAttribute(TOOL_SERVICE);
	    return mcService;
	}
	
	
	/**
	 * generateId()
	 * return long
	 * IMPORTANT: The way we obtain either content id or tool session id must be modified
	 * so that we only use lams common to get these ids. This functionality is not 
	 * available yet in the lams common as of 21/04/2005.
	 */
	public static long generateId()
	{
		Random generator = new Random();
		long longId=generator.nextLong();
		if (longId < 0) longId=longId * (-1) ;
		return longId;
	}
	
	/**
	 * helps create a mock user object in development time.
	 * static long generateIntegerId()
	 * @return long
	 */
	public static int generateIntegerId()
	{
		Random generator = new Random();
		int intId=generator.nextInt();
		if (intId < 0) intId=intId * (-1) ;
		return intId;
	}
	
	
	public static int getCurrentUserId(HttpServletRequest request) throws McApplicationException
    {
	    HttpSession ss = SessionManager.getSession();
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		logger.debug(logger + " " + "McUtils" +  " Current user is: " + user + " with id: " + user.getUserID());
		return user.getUserID().intValue();
    }
	
	
	/**
	 * This method exists temporarily until we have the user data is passed properly from teh container to the tool
	 * createMockUser()
	 * @return User 
	 */
	public static User createMockUser()
	{
		logger.debug(logger + " " + "McUtils" +  " request for new new mock user");
		int randomUserId=generateIntegerId();
		User mockUser=new User();
		mockUser.setUserId(new Integer(randomUserId));
		mockUser.setFirstName(MOCK_USER_NAME + randomUserId);
		mockUser.setLastName(MOCK_USER_LASTNAME + randomUserId);
		mockUser.setLogin(MOCK_LOGIN_NAME + randomUserId); //we assume login and username refers to the same property
		logger.debug(logger + " " + "McUtils" +  " created mockuser: " + mockUser);
		return mockUser;
	}
	
	public static User createSimpleUser(Integer userId)
	{
		User user=new User();
		user.setUserId(userId);
		return user;
	}
	
	public static User createUser(Integer userId)
	{
		User user=new User();
		user.setUserId(userId);
		
		int randomUserId=generateIntegerId();
		user.setFirstName(MOCK_USER_NAME + randomUserId);
		user.setLastName(MOCK_USER_LASTNAME + randomUserId);
		user.setLogin(MOCK_LOGIN_NAME + randomUserId); 
		return user;
	}
	
	public static boolean getDefineLaterStatus()
	{
		return false;
	}
	
	
	/**
	 * existsContent(long toolContentId)
	 * @param long toolContentId
	 * @return boolean
	 * determine whether a specific toolContentId exists in the db
	 */
	public static boolean existsContent(Long toolContentId, HttpServletRequest request)
	{
		/**
		 * retrive the service
		 */
		IMcService mcService =McUtils.getToolService(request);
	    
    	McContent mcContent=mcService.retrieveMc(toolContentId);
	    logger.debug(logger + " " + "McUtils " +  "retrieving mcContent: " + mcContent);
	    if (mcContent == null) 
	    	return false;
	    
		return true;	
	}

	/**
	 * it is expected that the tool session id already exists in the tool sessions table
	 * existsSession(long toolSessionId)
	 * @param toolSessionId
	 * @return boolean
	 */
	public static boolean existsSession(Long toolSessionId, HttpServletRequest request)
	{
		/**
		 * get the service
		 */
		logger.debug("existsSession");
    	IMcService mcService =McUtils.getToolService(request);
	    McSession mcSession=mcService.retrieveMcSession(toolSessionId);
	    logger.debug("mcSession:" + mcSession);
    	
	    if (mcSession == null) 
	    	return false;
	    
		return true;	
	}
	
	public static void setDefaultSessionAttributes(HttpServletRequest request, McContent defaultMcContent, McAuthoringForm mcAuthoringForm)
	{
		/**should never be null anyway as default content MUST exist in the db*/
		if (defaultMcContent != null)
		{		
			mcAuthoringForm.setTitle(defaultMcContent.getTitle());
			mcAuthoringForm.setInstructions(defaultMcContent.getInstructions());
			mcAuthoringForm.setOfflineInstructions (defaultMcContent.getOfflineInstructions());
			mcAuthoringForm.setOnlineInstructions (defaultMcContent.getOnlineInstructions());
		}
	}
	
	
	public static String getFormattedDateString(Date date)
	{
		logger.debug(logger + " " + " McUtils getFormattedDateString: " +  
				DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(date));
		return (DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(date));
	}
	
	public static void persistTimeZone(HttpServletRequest request)
	{
		TimeZone timeZone=TimeZone.getDefault();
	    logger.debug("current timezone: " + timeZone.getDisplayName());
	    request.getSession().setAttribute(TIMEZONE, timeZone.getDisplayName());
	    logger.debug("current timezone id: " + timeZone.getID());
	    request.getSession().setAttribute(TIMEZONE_ID, timeZone.getID());
	}
	
	
	public static void persistRichText(HttpServletRequest request)
	{
		String richTextOfflineInstructions=request.getParameter(RICHTEXT_OFFLINEINSTRUCTIONS);
		logger.debug("read parameter richTextOfflineInstructions: " + richTextOfflineInstructions);
		String richTextOnlineInstructions=request.getParameter(RICHTEXT_ONLINEINSTRUCTIONS);
		logger.debug("read parameter richTextOnlineInstructions: " + richTextOnlineInstructions);
		
		if ((richTextOfflineInstructions != null) && (richTextOfflineInstructions.length() > 0))
		{
			request.getSession().setAttribute(RICHTEXT_OFFLINEINSTRUCTIONS,richTextOfflineInstructions);	
		}
		
		if ((richTextOnlineInstructions != null) && (richTextOnlineInstructions.length() > 0))
		{
			request.getSession().setAttribute(RICHTEXT_ONLINEINSTRUCTIONS,richTextOnlineInstructions);	
		}
		
	
		String richTextTitle=request.getParameter(RICHTEXT_TITLE);
		logger.debug("read parameter richTextTitle: " + richTextTitle);
		String richTextInstructions=request.getParameter(RICHTEXT_INSTRUCTIONS);
		logger.debug("read parameter richTextInstructions: " + richTextInstructions);
		
		if ((richTextTitle != null) && (richTextTitle.length() > 0))
		{
			request.getSession().setAttribute(RICHTEXT_TITLE,richTextTitle);
		}
		
		if ((richTextInstructions != null) && (richTextInstructions.length() > 0))
		{
			request.getSession().setAttribute(RICHTEXT_INSTRUCTIONS,richTextInstructions);
		}
	}
	
	public static void addFileToContentRepository(HttpServletRequest request, McAuthoringForm mcAuthoringForm, boolean isOfflineFile)
	{
		logger.debug("attempt addFileToContentRepository");
    	IMcService mcService =McUtils.getToolService(request);
    	logger.debug("mcService: " + mcService);
    	
    	Boolean populateUploadedFilesData=(Boolean)request.getSession().getAttribute(POPULATED_UPLOADED_FILESDATA);
    	logger.debug("boolean populateUploadedFilesData: " + populateUploadedFilesData);
    	
    	if ((populateUploadedFilesData !=null) && (populateUploadedFilesData.booleanValue()))
    	{
    		Long toolContentId=(Long)request.getSession().getAttribute(TOOL_CONTENT_ID);
            logger.debug("toolContentId: " + toolContentId);
            McContent  defaultMcContent=mcService.retrieveMc(toolContentId);
            logger.debug("defaultMcContent: " + defaultMcContent);
            
            //populateUploadedFilesMetaDataFromDb(request, defaultMcContent);
            logger.debug("done populateUploadedFilesMetaDataFromDb");	
    	}
    	
        
        List listUploadedOfflineFilesUuid = (List) request.getSession().getAttribute(LIST_UPLOADED_OFFLINE_FILES_UUID);
    	logger.debug("listUploadedOfflineFilesUuid: " + listUploadedOfflineFilesUuid);
    	 
    	List listUploadedOfflineFilesName = (List) request.getSession().getAttribute(LIST_UPLOADED_OFFLINE_FILES_NAME);
    	logger.debug("listUploadedOfflineFilesName: " + listUploadedOfflineFilesName);
    	
    	List listUploadedOnlineFilesUuid = (List) request.getSession().getAttribute(LIST_UPLOADED_ONLINE_FILES_UUID);
    	logger.debug("listUploadedOnlineFilesUuid: " + listUploadedOnlineFilesUuid);
    	
    	List listUploadedOnlineFilesName = (List) request.getSession().getAttribute(LIST_UPLOADED_ONLINE_FILES_NAME);
    	logger.debug("listUploadedOnlineFilesName: " + listUploadedOnlineFilesName);
    	
    	List listUploadedOfflineFiles= (List) request.getSession().getAttribute(LIST_UPLOADED_OFFLINE_FILES);
    	logger.debug("listUploadedOfflineFiles: " + listUploadedOfflineFiles);
    	
    	List listUploadedOfflineFileNames=(List)request.getSession().getAttribute(LIST_UPLOADED_OFFLINE_FILENAMES);
    	logger.debug("listUploadedOfflineFiles: " + listUploadedOfflineFiles);
    	
    	List listUploadedOnlineFileNames=(List)request.getSession().getAttribute(LIST_UPLOADED_ONLINE_FILENAMES);
    	logger.debug("listUploadedOnlineFileNames: " + listUploadedOnlineFileNames);
    	
    	
    	if (listUploadedOfflineFileNames == null) 
    		listUploadedOfflineFileNames = new LinkedList();
    	logger.debug("listUploadedOfflineFileNames: " + listUploadedOfflineFileNames);
    	
    	if (listUploadedOnlineFileNames == null) 
    		listUploadedOnlineFileNames = new LinkedList();
    	logger.debug("listUploadedOnlineFileNames: " + listUploadedOnlineFileNames);
	    
    	Map allOfflineUuids= new TreeMap(new McComparator());
    	if ((listUploadedOfflineFilesUuid != null) && (listUploadedOfflineFilesName != null))
    	{
    		logger.debug("listUploadedOfflineFilesUuid and listUploadedOfflineFilesName are not null");
    		Iterator listUploadedOfflineFilesUuidIterator=listUploadedOfflineFilesUuid.iterator();
    		int counter=1;
    		logger.debug("allOfflineUuids: " + allOfflineUuids);
    		while (listUploadedOfflineFilesUuidIterator.hasNext())
			{
    			String uuid = (String)listUploadedOfflineFilesUuidIterator.next();
    			allOfflineUuids.put(new Integer(counter).toString(), uuid);
    			counter++;
    		}
    		logger.debug("allOfflineUuids: " + allOfflineUuids);
    		Iterator listUploadedOfflineFilesNameIterator=listUploadedOfflineFilesName.iterator();
    		
    		counter=1;
    		while (listUploadedOfflineFilesNameIterator.hasNext())
    		{
    				String fileName = (String)listUploadedOfflineFilesNameIterator.next();
    				/*
    				if (!offLineFileNameExists(request,fileName))
    				{
    					logger.debug("reading with counter: " + new Integer(counter).toString());
                		String uuid=(String)allOfflineUuids.get(new Integer(counter).toString());
            			logger.debug("parsed uuid: " + uuid);
            			listUploadedOfflineFiles.add(uuid + '~'+ fileName);
            			counter++;	
    				}
    				else
    				{
    					logger.debug("offline fileName exists: " +fileName);
    				}
    				*/
    		}
    		logger.debug("final listUploadedOfflineFiles: " + listUploadedOfflineFiles);
    		request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILES,listUploadedOfflineFiles);
    	}
    	/*
    	List listUploadedOfflineFileNames = (List) request.getSession().getAttribute(LIST_UPLOADED_OFFLINE_FILENAMES);
    	logger.debug("listUploadedOfflineFileNames: " + listUploadedOfflineFileNames);
    	
    	
    	List listUploadedOnlineFileNames = (List) request.getSession().getAttribute(LIST_UPLOADED_ONLINE_FILENAMES);
    	logger.debug("listUploadedOnlineFileNames: " + listUploadedOnlineFileNames);
    	*/
    	
    	
    	/**holds final online files list */
    	List listUploadedOnlineFiles= (List) request.getSession().getAttribute(LIST_UPLOADED_ONLINE_FILES);
    	logger.debug("listUploadedOnlineFiles: " + listUploadedOnlineFiles);
    	
    	Map allOnlineUuids= new TreeMap(new McComparator());
    	if ((listUploadedOnlineFilesUuid != null) && (listUploadedOnlineFilesName != null))
    	{
    		logger.debug("listUploadedOnlineFilesUuid and listUploadedOnlineFilesName are not null");
    		Iterator listUploadedOnlineFilesUuidIterator=listUploadedOnlineFilesUuid.iterator();
    		int counter=1;
    		logger.debug("allOnlineUuids: " + allOnlineUuids);
    		while (listUploadedOnlineFilesUuidIterator.hasNext())
			{
    			String uuid = (String)listUploadedOnlineFilesUuidIterator.next();
    			allOnlineUuids.put(new Integer(counter).toString(), uuid);
    			counter++;
    			
			}
    		logger.debug("allOnlineUuids: " + allOnlineUuids);
    		Iterator listUploadedOnlineFilesNameIterator=listUploadedOnlineFilesName.iterator();
    		
    		counter=1;
    		while (listUploadedOnlineFilesNameIterator.hasNext())
    		{
    			String fileName = (String)listUploadedOnlineFilesNameIterator.next();
    			/*
    			if (!onLineFileNameExists(request,fileName))
    			{
    				logger.debug("reading with counter: " + new Integer(counter).toString());
        			String uuid=(String)allOnlineUuids.get(new Integer(counter).toString());
        			logger.debug("parsed uuid: " + uuid);
        			listUploadedOnlineFiles.add(uuid + '~'+ fileName);
        			counter++;	
    			}
    			else
    			{
    				logger.debug("online fileName exists: " +fileName);
    			}
    			*/
    		}
    		logger.debug("final listUploadedOnlineFiles: " + listUploadedOnlineFiles);
    		request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILES,listUploadedOnlineFiles);
    	}
    	
    	
    	if (isOfflineFile)
    	{
    		/** read uploaded file informtion  - offline file*/
    		logger.debug("retrieve theOfflineFile.");
    		FormFile theOfflineFile = mcAuthoringForm.getTheOfflineFile();
    		logger.debug("retrieved theOfflineFile: " + theOfflineFile);
    		
    		String offlineFileName="";
    		NodeKey nodeKey=null;
    		String offlineFileUuid="";
    		try
    		{
    			InputStream offlineFileInputStream = theOfflineFile.getInputStream();
    			logger.debug("retrieved offlineFileInputStream: " + offlineFileInputStream);
    			offlineFileName=theOfflineFile.getFileName();
    			logger.debug("retrieved offlineFileName: " + offlineFileName);
    	    	nodeKey=mcService.uploadFileToRepository(offlineFileInputStream, offlineFileName);
    	    	logger.debug("repository returned nodeKey: " + nodeKey);
    	    	logger.debug("repository returned offlineFileUuid nodeKey uuid: " + nodeKey.getUuid());
    	    	offlineFileUuid=nodeKey.getUuid().toString();
    	    	logger.debug("offline file added to contentRepository");
    	    	logger.debug("using listUploadedOfflineFiles: " + listUploadedOfflineFiles);
    	    	listUploadedOfflineFiles.add(offlineFileUuid + "~" + offlineFileName);
    	    	logger.debug("listUploadedOfflineFiles updated: " + listUploadedOfflineFiles);
    	    	request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILES,listUploadedOfflineFiles);
    	 
    	    	listUploadedOfflineFileNames.add(offlineFileName);
    	    	request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILENAMES,listUploadedOfflineFileNames);
    	    }
    		catch(FileNotFoundException e)
    		{
    			logger.debug("exception occured, offline file not found : " + e.getMessage());
    			//possibly give warning to user in request scope
    		}
    		catch(IOException e)
    		{
    			logger.debug("exception occured in offline file transfer: " + e.getMessage());
    			//possibly give warning to user in request scope
    		}
    		catch(McApplicationException e)
    		{
    			logger.debug("exception occured in accessing the repository server: " + e.getMessage());
    			//possibly give warning to user in request scope
    		}
    	}
    	else
    	{
    		/** read uploaded file information  - online file*/
    		logger.debug("retrieve theOnlineFile");
    		FormFile theOnlineFile = mcAuthoringForm.getTheOnlineFile();
    		logger.debug("retrieved theOnlineFile: " + theOnlineFile);
    		
    		String onlineFileName="";
    		NodeKey nodeKey=null;
    		String onlineFileUuid="";
    		try
    		{
    			InputStream onlineFileInputStream = theOnlineFile.getInputStream();
    			logger.debug("retrieved onlineFileInputStream: " + onlineFileInputStream);
    			onlineFileName=theOnlineFile.getFileName();
    			logger.debug("retrieved onlineFileName: " + onlineFileName);
    	    	nodeKey=mcService.uploadFileToRepository(onlineFileInputStream, onlineFileName);
    	    	logger.debug("repository returned nodeKey: " + nodeKey);
    	    	logger.debug("repository returned onlineFileUuid nodeKey uuid: " + nodeKey.getUuid());
    	    	onlineFileUuid=nodeKey.getUuid().toString();
    	    	logger.debug("online file added to contentRepository");
    	    	listUploadedOnlineFiles.add(onlineFileUuid + "~" + onlineFileName);
    	    	logger.debug("listUploadedOnlineFiles updated: " + listUploadedOnlineFiles);
    	    	request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILES,listUploadedOnlineFiles);
    	    	
    	    	listUploadedOnlineFileNames.add(onlineFileName);
    	    	request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILENAMES,listUploadedOnlineFileNames);
    	    }
    		catch(FileNotFoundException e)
    		{
    			logger.debug("exception occured, online file not found : " + e.getMessage());
    			//possibly give warning to user in request scope
    		}
    		catch(IOException e)
    		{
    			logger.debug("exception occured in online file transfer: " + e.getMessage());
    			//possibly give warning to user in request scope
    		}
    		catch(McApplicationException e)
    		{
    			logger.debug("exception occured in accessing the repository server: " + e.getMessage());
    			//possibly give warning to user in request scope
    		}	
    	}
		
    
    	if ((populateUploadedFilesData != null) && (populateUploadedFilesData.booleanValue()))
    	{
    		logger.debug("removing ofline + online file list attributes");
    		request.getSession().removeAttribute(LIST_UPLOADED_OFFLINE_FILES_UUID);
    		request.getSession().removeAttribute(LIST_UPLOADED_OFFLINE_FILES_NAME);
    		request.getSession().removeAttribute(LIST_UPLOADED_ONLINE_FILES_UUID);
    		request.getSession().removeAttribute(LIST_UPLOADED_ONLINE_FILES_NAME);
    	}
	}
	
	
	public static void configureContentRepository(HttpServletRequest request)
	{
		logger.debug("attempt configureContentRepository");
    	IMcService mcService =McUtils.getToolService(request);
    	logger.debug("retrieving mcService from session: " + mcService);
    	logger.debug("calling configureContentRepository()");
	    mcService.configureContentRepository();
	    logger.debug("configureContentRepository ran successfully");
	}
	
	
	public static void populateUploadedFilesMetaDataFromDb(HttpServletRequest request, McContent defaultMcContent)
	{
		logger.debug("attempt populateUploadedFilesData");
		IMcService mcService =McUtils.getToolService(request);
    	logger.debug("mcService: " + mcService);

		/** just for jsp purposes **
	    /** read the uploaded offline uuid + file name pair */
	    List listOfflineFilesUuid=new LinkedList();
	    listOfflineFilesUuid=mcService.retrieveMcUploadedOfflineFilesUuid(defaultMcContent);
	    logger.debug("initial listOfflineFilesUuid: " + listOfflineFilesUuid);
	    request.getSession().removeAttribute(LIST_UPLOADED_OFFLINE_FILES_UUID);
	    request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILES_UUID, listOfflineFilesUuid);
	    
	    /** read the uploaded online uuid + file name pair */
	    List listOnlineFilesUuid=new LinkedList();
	    listOnlineFilesUuid=mcService.retrieveMcUploadedOnlineFilesUuid(defaultMcContent);
	    logger.debug("initial listOnlineFilesUuid: " + listOnlineFilesUuid);
	    request.getSession().removeAttribute(LIST_UPLOADED_ONLINE_FILES_UUID);
	    request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILES_UUID, listOnlineFilesUuid);
	    
	    
	    /** read the uploaded offline uuid + file name pair */
	    List listOfflineFilesName=new LinkedList();
	    listOfflineFilesName=mcService.retrieveMcUploadedOfflineFilesName(defaultMcContent);
	    logger.debug("initial listOfflineFilesName: " + listOfflineFilesName);
	    request.getSession().removeAttribute(LIST_UPLOADED_OFFLINE_FILES_NAME);
	    request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILES_NAME, listOfflineFilesName);
	    
	    
	    /** read the uploaded online uuid + file name pair */
	    List listOnlineFilesName=new LinkedList();
	    listOnlineFilesName=mcService.retrieveMcUploadedOnlineFilesName(defaultMcContent);
	    logger.debug("initial listOnlineFilesName: " + listOnlineFilesName);
	    request.getSession().removeAttribute(LIST_UPLOADED_ONLINE_FILES_NAME);
	    request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILES_NAME, listOnlineFilesName);
	    
	    	    
	    request.getSession().removeAttribute(LIST_UPLOADED_OFFLINE_FILENAMES);
	    request.getSession().removeAttribute(LIST_UPLOADED_ONLINE_FILENAMES);
	    request.getSession().setAttribute(LIST_UPLOADED_OFFLINE_FILENAMES, listOfflineFilesName);
	    request.getSession().setAttribute(LIST_UPLOADED_ONLINE_FILENAMES, listOnlineFilesName);
	}

		
}
