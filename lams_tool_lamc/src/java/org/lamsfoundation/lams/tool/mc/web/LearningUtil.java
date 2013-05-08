/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.mc.web;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McGeneralLearnerFlowDTO;
import org.lamsfoundation.lams.tool.mc.McLearnerAnswersDTO;
import org.lamsfoundation.lams.tool.mc.McRandomizedListsDTO;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueUsr;
import org.lamsfoundation.lams.tool.mc.pojos.McSession;
import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;
import org.lamsfoundation.lams.tool.mc.service.IMcService;

/**
 * 
 * Keeps all operations needed for Authoring mode.
 * 
 * @author Ozgur Demirtas
 * 
 */
public class LearningUtil implements McAppConstants {
    static Logger logger = Logger.getLogger(LearningUtil.class.getName());

    /**
     * void saveFormRequestData(HttpServletRequest request, McLearningForm mcLearningForm, boolean
     * prepareViewAnswersDataMode)
     * 
     * @param request
     * @param mcLearningForm
     * @param prepareViewAnswersDataMode
     */
    public static void saveFormRequestData(HttpServletRequest request, McLearningForm mcLearningForm,
	    boolean prepareViewAnswersDataMode) {

	String httpSessionID = request.getParameter("httpSessionID");
	mcLearningForm.setHttpSessionID(httpSessionID);

	String userID = request.getParameter("userID");
	mcLearningForm.setUserID(userID);

	String passMarkApplicable = request.getParameter("passMarkApplicable");
	mcLearningForm.setPassMarkApplicable(passMarkApplicable);

	String userOverPassMark = request.getParameter("userOverPassMark");
	mcLearningForm.setUserOverPassMark(userOverPassMark);

	if (prepareViewAnswersDataMode == false) {
	    String learnerProgress = request.getParameter("learnerProgress");
	    mcLearningForm.setLearnerProgress(learnerProgress);
	    String learnerProgressUserId = request.getParameter("learnerProgressUserId");
	    mcLearningForm.setLearnerProgressUserId(learnerProgressUserId);

	}

	String questionListingMode = request.getParameter("questionListingMode");
	mcLearningForm.setQuestionListingMode(questionListingMode);
    }

    /**
     * A question is correct if the number of correct options and the number of checked options is the same, plus all
     * the checked options appears in the correct options list.
     * 
     * @param mapGeneralCorrectOptions
     * @param checkedOptionsUIDs
     * @return
     */
    public static boolean isQuestionCorrect(Collection<McOptsContent> correctOptions, List<String> checkedOptionIds) {

	for (McOptsContent mcOptsContent : correctOptions) {
	    String optionId = mcOptsContent.getUid().toString();
	    if (!optionId.equals(checkedOptionIds.get(checkedOptionIds.size() - 1))) {
		return false;
	    }
	}
	return true;
    }

    /**
     * McQueUsr getUser(HttpServletRequest request, IMcService mcService, String toolSessionId)
     * 
     * @param request
     * @param mcService
     * @param toolSessionId
     * @return
     */
    public static McQueUsr getUser(HttpServletRequest request, IMcService mcService, String toolSessionId) {
	Long queUsrId = McUtils.getUserId();

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionId));
	McQueUsr mcQueUsr = mcService.getMcUserBySession(queUsrId, mcSession.getUid());
	return mcQueUsr;
    }

    /**
     * creates the user in the db createUser(HttpServletRequest request)
     * 
     * @param request
     */
    public static McQueUsr createUser(HttpServletRequest request, IMcService mcService, Long toolSessionId) {
	Long queUsrId = McUtils.getUserId();
	String username = McUtils.getUserName();
	String fullname = McUtils.getUserFullName();

	McSession mcSession = mcService.retrieveMcSession(toolSessionId);
	McQueUsr mcQueUsr = new McQueUsr(queUsrId, username, fullname, mcSession, new TreeSet());
	mcService.createMcQueUsr(mcQueUsr);
	return mcQueUsr;
    }

    /**
     * createLearnerAttempt(HttpServletRequest request, McQueUsr mcQueUsr, List selectedQuestionAndCandidateAnswersDTO,
     * Integer totalMark, boolean passed, int highestAttemptOrder, Map mapLeanerAssessmentResults, IMcService mcService)
     * 
     * @param request
     * @param mcQueUsr
     * @param selectedQuestionAndCandidateAnswersDTO
     * @param mark
     * @param passed
     * @param highestAttemptOrder
     * @param mapLeanerAssessmentResults
     * @param mcService
     */
    public static void createLearnerAttempt(HttpServletRequest request, McQueUsr mcQueUsr,
	    List selectedQuestionAndCandidateAnswersDTO, boolean passed, Integer highestAttemptOrder,
	    Map mapLeanerAssessmentResults, IMcService mcService) {
	Date attemptTime = McUtils.getGMTDateTime();

	Iterator itSelectedMap = selectedQuestionAndCandidateAnswersDTO.iterator();
	while (itSelectedMap.hasNext()) {
	    McLearnerAnswersDTO mcLearnerAnswersDTO = (McLearnerAnswersDTO) itSelectedMap.next();

	    McQueContent mcQueContent = mcService.findMcQuestionContentByUid(mcLearnerAnswersDTO.getQuestionUid());

	    createIndividualOptions(request, mcLearnerAnswersDTO.getCandidateAnswers(), mcQueContent, mcQueUsr,
		    attemptTime, mcLearnerAnswersDTO.getMark(), passed, highestAttemptOrder, mcLearnerAnswersDTO
			    .getAttemptCorrect(), mcService);
	}

    }

    /**
     * 
     * createIndividualOptions(HttpServletRequest request, Map candidateAnswers, McQueContent mcQueContent, McQueUsr
     * mcQueUsr, Date attempTime, String timeZone, int mark, boolean passed, Integer highestAttemptOrder, String
     * isAttemptCorrect, IMcService mcService)
     * 
     * @param request
     * @param candidateAnswers
     * @param mcQueContent
     * @param mcQueUsr
     * @param attemptTime
     * @param mark
     * @param passed
     * @param highestAttemptOrder
     * @param isAttemptCorrect
     * @param mcService
     */
    public static void createIndividualOptions(HttpServletRequest request, Map candidateAnswers,
	    McQueContent mcQueContent, McQueUsr mcQueUsr, Date attemptTime, int mark, boolean passed,
	    Integer highestAttemptOrder, String isAttemptCorrect, IMcService mcService) {
	Integer IntegerMark = new Integer(mark);

	if (mcQueContent != null) {
	    if (candidateAnswers != null) {
		Iterator itCheckedMap = candidateAnswers.entrySet().iterator();
		while (itCheckedMap.hasNext()) {
		    Map.Entry checkedPairs = (Map.Entry) itCheckedMap.next();
		    McOptsContent mcOptsContent = mcService.getOptionContentByOptionText(checkedPairs.getValue()
			    .toString(), mcQueContent.getUid());
		    if (mcOptsContent != null) {
			McUsrAttempt mcUsrAttempt = new McUsrAttempt(attemptTime, mcQueContent, mcQueUsr,
				mcOptsContent, IntegerMark, passed, highestAttemptOrder, new Boolean(isAttemptCorrect)
					.booleanValue());
			mcService.createMcUsrAttempt(mcUsrAttempt);
			//created mcUsrAttempt in the db
		    }
		}
	    }
	}
    }

    /**
     * Map buildMarksMap(HttpServletRequest request, Long toolContentId, IMcService mcService)
     * 
     * @param request
     * @param toolContentId
     * @param mcService
     * @return
     */
    public static Map buildMarksMap(HttpServletRequest request, Long toolContentId, IMcService mcService) {
	Map mapMarks = new TreeMap(new McComparator());
	McContent mcContent = mcService.retrieveMc(toolContentId);

	List questionsContent = mcService.refreshQuestionContent(mcContent.getUid());

	Iterator listIterator = questionsContent.iterator();
	Long mapIndex = new Long(1);
	while (listIterator.hasNext()) {
	    McQueContent mcQueContent = (McQueContent) listIterator.next();
	    mapMarks.put(mapIndex.toString(), mcQueContent.getMark().toString());
	    mapIndex = new Long(mapIndex.longValue() + 1);
	}
	return mapMarks;
    }

    /**
     * McGeneralLearnerFlowDTO buildMcGeneralLearnerFlowDTO(McContent mcContent)
     * 
     * @param mcContent
     * @return
     */
    public static McGeneralLearnerFlowDTO buildMcGeneralLearnerFlowDTO(McContent mcContent) {
	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = new McGeneralLearnerFlowDTO();
	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());
	mcGeneralLearnerFlowDTO.setActivityTitle(mcContent.getTitle());
	mcGeneralLearnerFlowDTO.setActivityInstructions(mcContent.getInstructions());
	mcGeneralLearnerFlowDTO.setPassMark(mcContent.getPassMark());
	mcGeneralLearnerFlowDTO.setReportTitleLearner("Report");
	mcGeneralLearnerFlowDTO.setLearnerProgress(new Boolean(false).toString());

	if (mcContent.isQuestionsSequenced()) {
	    mcGeneralLearnerFlowDTO.setQuestionListingMode(McAppConstants.QUESTION_LISTING_MODE_SEQUENTIAL);
	} else {
	    mcGeneralLearnerFlowDTO.setQuestionListingMode(McAppConstants.QUESTION_LISTING_MODE_COMBINED);
	}

	mcGeneralLearnerFlowDTO.setTotalQuestionCount(new Integer(mcContent.getMcQueContents().size()));
	return mcGeneralLearnerFlowDTO;
    }

    public static McRandomizedListsDTO randomizeList(List listCandidateAnswers, List listCandidateAnswerUids) {

	McRandomizedListsDTO mcRandomizedListsDTO = new McRandomizedListsDTO();

	int caCount = listCandidateAnswers.size();

	Random generator = new Random();

	boolean listNotComplete = true;
	int randomInt = 0;

	List randomList = new LinkedList();
	List randomUidList = new LinkedList();
	while (listNotComplete) {
	    randomInt = generator.nextInt(caCount);

	    String ca = (String) listCandidateAnswers.get(randomInt);

	    String caUid = (String) listCandidateAnswerUids.get(randomInt);

	    if (!isEntryStored(ca, randomList)) {
		//adding ca, since it is a new candidate
		randomList.add(ca);
		randomUidList.add(caUid);

		LearningUtil.logger.debug("randomList size: " + randomList.size());
		if (randomList.size() == listCandidateAnswers.size()) {
		    //the list is populated completely
		    listNotComplete = false;
		}
	    }
	}

	listCandidateAnswerUids = randomUidList;

	mcRandomizedListsDTO.setListCandidateAnswers(randomList);
	mcRandomizedListsDTO.setListCandidateAnswerUids(listCandidateAnswerUids);

	return mcRandomizedListsDTO;
    }

    public static boolean isEntryStored(String ca, List randomList) {

	Iterator randomListIterator = randomList.iterator();

	while (randomListIterator.hasNext()) {
	    String caStored = (String) randomListIterator.next();

	    if (caStored.equals(ca)) {
		//this ca already is stored
		return true;
	    }
	}

	return false;
    }

    /**
     * List buildQuestionAndCandidateAnswersDTO(HttpServletRequest request, McContent mcContent, IMcService mcService)
     * 
     * @param request
     * @param mcContent
     * @param mcService
     * @return
     */
    public static List<McLearnerAnswersDTO> buildQuestionAndCandidateAnswersDTO(HttpServletRequest request,
	    McContent mcContent, boolean randomize, IMcService mcService) {
	List<McLearnerAnswersDTO> questionAndCandidateAnswersList = new LinkedList<McLearnerAnswersDTO>();
	List<McQueContent> listQuestionEntries = mcService.getAllQuestionEntries(mcContent.getUid());

	Iterator listQuestionEntriesIterator = listQuestionEntries.iterator();
	while (listQuestionEntriesIterator.hasNext()) {
	    McQueContent mcQueContent = (McQueContent) listQuestionEntriesIterator.next();
	    McLearnerAnswersDTO mcLearnerAnswersDTO = new McLearnerAnswersDTO();
	    List listCandidateAnswers = mcService.findMcOptionNamesByQueId(mcQueContent.getUid());
	    List listCandidateAnswerUids = mcService.findMcOptionUidsByQueId(mcQueContent.getUid());
	    if (randomize) {
		// listCandidateAnswers=randomizeList(listCandidateAnswers, listCandidateAnswerUids);
		McRandomizedListsDTO mcRandomizedListsDTO = randomizeList(listCandidateAnswers, listCandidateAnswerUids);
		listCandidateAnswers = mcRandomizedListsDTO.getListCandidateAnswers();
		listCandidateAnswerUids = mcRandomizedListsDTO.getListCandidateAnswerUids();
	    }
	    Map mapCandidateAnswers = convertToStringMap(listCandidateAnswers);
	    Map mapCandidateAnswerUids = convertToStringMap(listCandidateAnswerUids);

	    String question = mcQueContent.getQuestion();

	    mcLearnerAnswersDTO.setQuestion(question);
	    mcLearnerAnswersDTO.setDisplayOrder(mcQueContent.getDisplayOrder().toString());
	    mcLearnerAnswersDTO.setQuestionUid(mcQueContent.getUid());

	    mcLearnerAnswersDTO.setMark(mcQueContent.getMark());
	    mcLearnerAnswersDTO.setCandidateAnswerUids(mapCandidateAnswerUids);
	    mcLearnerAnswersDTO.setCandidateAnswers(mapCandidateAnswers);

	    questionAndCandidateAnswersList.add(mcLearnerAnswersDTO);
	}

	return questionAndCandidateAnswersList;
    }

    /**
     * Map convertToStringMap(List list)
     * 
     * @param list
     * @return
     */
    public static Map convertToStringMap(List list) {
	Map map = new TreeMap(new McComparator());

	Iterator listIterator = list.iterator();
	Long mapIndex = new Long(1);

	while (listIterator.hasNext()) {
	    String data = (String) listIterator.next();
	    map.put(mapIndex.toString(), data);
	    mapIndex = new Long(mapIndex.longValue() + 1);
	}
	return map;
    }

    /**
     * Gets the various maps used by jsps to display a learner's attempts.
     * 
     * @return Map[mapFinalAnswersIsContent, mapFinalAnswersContent, mapQueAttempts, mapQueCorrectAttempts,
     *         mapQueIncorrectAttempts, mapMarks]
     */
    public static Map[] getAttemptMapsForUser(int intTotalQuestionCount, Long toolContentUID, boolean allowRetries,
	    IMcService mcService, McQueUsr mcQueUsr) {

	Map mapFinalAnswersIsContent = new TreeMap(new McComparator());
	Map mapFinalAnswersContent = new TreeMap(new McComparator());

	// mapQueAttempts: key is the question display order, the value is the mapAttempOrderAttempts map.
	// mapAttemptOrderAttempts: key is the attempt order, the value is the mapAttempt map.
	// mapAttemptMap: key is an artificial ordering, the value is the actual value for the question
	// at the moment, there will only be one attempt for each question of each learner in a tool session
	// so mapAttemptMap will only have one value.
	// The mapQueCorrectAttempts and mapQueIncorrectAttempts work in a similar way
	Map mapQueAttempts = new TreeMap(new McComparator());
	Map mapQueCorrectAttempts = new TreeMap(new McComparator());
	Map mapQueIncorrectAttempts = new TreeMap(new McComparator());

	for (int i = 1; i <= intTotalQuestionCount; i++) {
	    McQueContent mcQueContent = mcService.getQuestionContentByDisplayOrder(new Long(i), toolContentUID);

	    McUsrAttempt mcUsrAttemptFinal = null;

	    List userAttempts = mcService.getAllAttemptsForAUserForOneQuestionContentOrderByAttempt(mcQueUsr.getUid(),
		    mcQueContent.getUid());
	    Iterator userAttemptsIter = userAttempts.iterator();

	    Map mapAttemptOrderAttempts = new TreeMap(new McComparator());
	    Map mapAttemptOrderCorrectAttempts = new TreeMap(new McComparator());
	    Map mapAttemptOrderIncorrectAttempts = new TreeMap(new McComparator());

	    while (userAttemptsIter.hasNext()) {
		McUsrAttempt mcUsrAttempt = (McUsrAttempt) userAttemptsIter.next();

		if (mcUsrAttemptFinal == null
			|| mcUsrAttempt.getAttemptOrder().compareTo(mcUsrAttemptFinal.getAttemptOrder()) > 0) {
		    mcUsrAttemptFinal = mcUsrAttempt;
		}

		addToAttemptMaps(mapAttemptOrderAttempts, mapAttemptOrderCorrectAttempts,
			mapAttemptOrderIncorrectAttempts, mcUsrAttempt);
	    }

	    String questionDisplayOrderString = new Integer(i).toString();

	    Integer mark = null;
	    if (mcUsrAttemptFinal != null) {
		mapFinalAnswersIsContent.put(questionDisplayOrderString, new Boolean(mcUsrAttemptFinal
			.isAttemptCorrect()).toString());
		mapFinalAnswersContent.put(questionDisplayOrderString, mcUsrAttemptFinal.getMcOptionsContent()
			.getMcQueOptionText().toString());
	    }
	    if (mapAttemptOrderAttempts.size() > 0) {
		mapQueAttempts.put(questionDisplayOrderString, mapAttemptOrderAttempts);
	    }
	    if (mapAttemptOrderCorrectAttempts.size() > 0) {
		mapQueCorrectAttempts.put(questionDisplayOrderString, mapAttemptOrderCorrectAttempts);
	    }
	    if (mapAttemptOrderIncorrectAttempts.size() > 0) {
		mapQueIncorrectAttempts.put(questionDisplayOrderString, mapAttemptOrderIncorrectAttempts);
	    }
	}

	return new Map[] { mapFinalAnswersIsContent, mapFinalAnswersContent, mapQueAttempts, mapQueCorrectAttempts,
		mapQueIncorrectAttempts };
    }

    private static void addToAttemptMaps(Map mapAttemptOrderAttempts, Map mapAttemptOrderCorrectAttempts,
	    Map mapAttemptOrderIncorrectAttempts, McUsrAttempt mcUsrAttempt) {
	String attemptOrderString = mcUsrAttempt.getAttemptOrder().toString();

	Map attemptMap = (Map) mapAttemptOrderAttempts.get(attemptOrderString);
	Map correctAttemptMap = (Map) mapAttemptOrderCorrectAttempts.get(attemptOrderString);
	Map incorrectAttemptMap = (Map) mapAttemptOrderIncorrectAttempts.get(attemptOrderString);

	if (attemptMap == null) {
	    attemptMap = new TreeMap(new McComparator());
	    mapAttemptOrderAttempts.put(attemptOrderString, attemptMap);

	    correctAttemptMap = new TreeMap(new McComparator());
	    mapAttemptOrderCorrectAttempts.put(attemptOrderString, correctAttemptMap);

	    incorrectAttemptMap = new TreeMap(new McComparator());
	    mapAttemptOrderIncorrectAttempts.put(attemptOrderString, incorrectAttemptMap);
	}

	int mapSize = attemptMap.size();
	String mapIndex = (new Integer(mapSize + 1)).toString();
	attemptMap.put(mapIndex, mcUsrAttempt.getMcOptionsContent().getMcQueOptionText());
	if (mcUsrAttempt.isAttemptCorrect()) {
	    correctAttemptMap.put(mapIndex, mcUsrAttempt.getMcOptionsContent().getMcQueOptionText());
	} else {
	    incorrectAttemptMap.put(mapIndex, mcUsrAttempt.getMcOptionsContent().getMcQueOptionText());
	}

    }

    /**
     * Should we show the marks for each question - we show the marks if any of the questions have a mark > 1.
     */
    public static Boolean isShowMarksOnQuestion(List<McLearnerAnswersDTO> listQuestionAndCandidateAnswersDTO) {
	Iterator iter = listQuestionAndCandidateAnswersDTO.iterator();
	while (iter.hasNext()) {
	    McLearnerAnswersDTO elem = (McLearnerAnswersDTO) iter.next();
	    if (elem.getMark().intValue() > 1) {
		return Boolean.TRUE;
	    }
	}
	return Boolean.FALSE;
    }
}
