/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
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
 * ****************************************************************
 */

/* $$Id$$ */
package org.lamsfoundation.lams.learning.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.gradebook.GradebookUserActivity;
import org.lamsfoundation.lams.gradebook.service.IGradebookService;
import org.lamsfoundation.lams.learning.progress.ProgressBuilder;
import org.lamsfoundation.lams.learning.progress.ProgressEngine;
import org.lamsfoundation.lams.learning.progress.ProgressException;
import org.lamsfoundation.lams.learning.web.bean.ActivityPositionDTO;
import org.lamsfoundation.lams.learning.web.bean.GateActivityDTO;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ActivityEvaluation;
import org.lamsfoundation.lams.learningdesign.BranchActivityEntry;
import org.lamsfoundation.lams.learningdesign.BranchCondition;
import org.lamsfoundation.lams.learningdesign.BranchingActivity;
import org.lamsfoundation.lams.learningdesign.ConditionGateActivity;
import org.lamsfoundation.lams.learningdesign.DataFlowObject;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.GroupUser;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.LearnerChoiceGrouper;
import org.lamsfoundation.lams.learningdesign.LearnerChoiceGrouping;
import org.lamsfoundation.lams.learningdesign.OptionsActivity;
import org.lamsfoundation.lams.learningdesign.SequenceActivity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.learningdesign.ToolBranchingActivity;
import org.lamsfoundation.lams.learningdesign.Transition;
import org.lamsfoundation.lams.learningdesign.dao.IActivityDAO;
import org.lamsfoundation.lams.learningdesign.dao.IDataFlowDAO;
import org.lamsfoundation.lams.learningdesign.dao.IGroupUserDAO;
import org.lamsfoundation.lams.learningdesign.dao.IGroupingDAO;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonDAO;
import org.lamsfoundation.lams.lesson.dto.LearnerProgressDTO;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.lesson.service.LessonServiceException;
import org.lamsfoundation.lams.logevent.LogEvent;
import org.lamsfoundation.lams.logevent.service.ILogEventService;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputValue;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.exception.LamsToolServiceException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.service.ILamsCoreToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * This class is a facade over the Learning middle tier.
 * 
 * @author chris, Jacky Fang
 */
public class LearnerService implements ICoreLearnerService {
    // ---------------------------------------------------------------------
    // Instance variables
    // ---------------------------------------------------------------------
    private static Logger log = Logger.getLogger(LearnerService.class);

    private ILearnerProgressDAO learnerProgressDAO;
    private ILessonDAO lessonDAO;
    private IActivityDAO activityDAO;
    private IGroupingDAO groupingDAO;
    private IGroupUserDAO groupUserDAO;
    private ProgressEngine progressEngine;
    private IDataFlowDAO dataFlowDAO;
    private ILamsCoreToolService lamsCoreToolService;
    private ActivityMapping activityMapping;
    private IUserManagementService userManagementService;
    private ILessonService lessonService;
    private static HashMap<Integer, Long> syncMap = new HashMap<Integer, Long>();
    protected MessageService messageService;
    private IGradebookService gradebookService;
    private ILogEventService logEventService;

    // ---------------------------------------------------------------------
    // Inversion of Control Methods - Constructor injection
    // ---------------------------------------------------------------------

    /** Creates a new instance of LearnerService */
    public LearnerService(ProgressEngine progressEngine) {
	this.progressEngine = progressEngine;
    }

    /**
     * Creates a new instance of LearnerService. To be used by Spring, assuming the Spring will set up the progress
     * engine via method injection. If you are creating the bean manually then use the other constructor.
     */
    public LearnerService() {
    }

    // ---------------------------------------------------------------------
    // Inversion of Control Methods - Method injection
    // ---------------------------------------------------------------------
    /**
     * Set i18n MessageService
     */
    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }

    @Override
    public MessageService getMessageService() {
	return messageService;
    }

    /**
     * @param lessonDAO
     *            The lessonDAO to set.
     */
    public void setLessonDAO(ILessonDAO lessonDAO) {
	this.lessonDAO = lessonDAO;
    }

    /**
     * @param learnerProgressDAO
     *            The learnerProgressDAO to set.
     */
    public void setLearnerProgressDAO(ILearnerProgressDAO learnerProgressDAO) {
	this.learnerProgressDAO = learnerProgressDAO;
    }

    /**
     * @param lamsToolService
     *            The lamsToolService to set.
     */
    public void setLamsCoreToolService(ILamsCoreToolService lamsToolService) {
	lamsCoreToolService = lamsToolService;
    }

    public void setActivityMapping(ActivityMapping activityMapping) {
	this.activityMapping = activityMapping;
    }

    /**
     * @param activityDAO
     *            The activityDAO to set.
     */
    public void setActivityDAO(IActivityDAO activityDAO) {
	this.activityDAO = activityDAO;
    }

    /**
     * @param groupingDAO
     *            The groupingDAO to set.
     */
    public void setGroupingDAO(IGroupingDAO groupingDAO) {
	this.groupingDAO = groupingDAO;
    }

    /**
     * @return the groupUserDAO
     */
    public IGroupUserDAO getGroupUserDAO() {
	return groupUserDAO;
    }

    /**
     * @param groupUserDAO
     *            groupUserDAO
     */
    public void setGroupUserDAO(IGroupUserDAO groupUserDAO) {
	this.groupUserDAO = groupUserDAO;
    }

    /**
     * @return the User Management Service
     */
    @Override
    public IUserManagementService getUserManagementService() {
	return userManagementService;
    }

    /**
     * @param userService
     *            User Management Service
     */
    public void setUserManagementService(IUserManagementService userService) {
	userManagementService = userService;
    }

    public void setLessonService(ILessonService lessonService) {
	this.lessonService = lessonService;
    }

    public void setLogEventService(ILogEventService logEventService) {
	this.logEventService = logEventService;
    }

    // ---------------------------------------------------------------------
    // Service Methods
    // ---------------------------------------------------------------------
    /**
     * Delegate to lesson dao to load up the lessons.
     * 
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActiveLessonsFor(org.lamsfoundation.lams.usermanagement.User)
     */
    @Override
    public LessonDTO[] getActiveLessonsFor(Integer learnerId) {
	User learner = (User) userManagementService.findById(User.class, learnerId);
	List<Lesson> activeLessons = lessonDAO.getActiveLessonsForLearner(learner);
	// remove lessons which do not have preceding lessons finished
	Iterator<Lesson> lessonIter = activeLessons.iterator();
	while (lessonIter.hasNext()) {
	    if (!lessonService.checkLessonReleaseConditions(lessonIter.next().getLessonId(), learnerId)) {
		lessonIter.remove();
	    }
	}
	return getLessonDataFor(activeLessons);
    }

    @Override
    public Lesson getLesson(Long lessonId) {
	return lessonDAO.getLesson(lessonId);
    }

    /**
     * Get the lesson data for a particular lesson. In a DTO format suitable for sending to the client.
     */
    @Override
    public LessonDTO getLessonData(Long lessonId) {
	Lesson lesson = getLesson(lessonId);
	return lesson != null ? lesson.getLessonData() : null;
    }

    /**
     * <p>
     * Joins a User to a lesson as a learner. It could either be a new lesson or a lesson that has been started.
     * </p>
     * 
     * <p>
     * In terms of new lesson, a new learner progress would be initialized. Tool session for the next activity will be
     * initialized if necessary.
     * </p>
     * 
     * <p>
     * In terms of an started lesson, the learner progress will be returned without calculation. Tool session will be
     * initialized if necessary. Note that we won't initialize tool session for current activity because we assume tool
     * session will always initialize before it becomes a current activity.
     * </p
     * 
     * 
     * @param learnerId
     *            the Learner's userID
     * @param lessionID
     *            identifies the Lesson to start
     * @throws LamsToolServiceException
     * @throws LearnerServiceException
     *             in case of problems.
     */
    @Override
    public synchronized LearnerProgress joinLesson(Integer learnerId, Long lessonID) {
	User learner = (User) userManagementService.findById(User.class, learnerId);

	Lesson lesson = getLesson(lessonID);

	if ((lesson == null) || !lesson.isLessonStarted()) {
	    LearnerService.log.error("joinLesson: Learner " + learner.getLogin() + " joining lesson " + lesson
		    + " but lesson has not started");
	    throw new LearnerServiceException("Cannot join lesson as lesson has not started");
	}
	if (!lessonService.checkLessonReleaseConditions(lessonID, learnerId)) {
	    throw new LearnerServiceException("Cannot join lesson as preceding lessons have not been finished");
	}

	LearnerProgress learnerProgress = learnerProgressDAO.getLearnerProgressByLearner(learner.getUserId(), lessonID);

	if (learnerProgress == null) {
	    // create a new learner progress for new learner
	    learnerProgress = new LearnerProgress(learner, lesson);

	    try {
		progressEngine.setUpStartPoint(learnerProgress);
	    } catch (ProgressException e) {
		LearnerService.log.error("error occurred in 'setUpStartPoint':" + e.getMessage());
		throw new LearnerServiceException(e.getMessage());
	    }
	    // Use TimeStamp rather than Date directly to keep consistent with Hibnerate persiste object.
	    learnerProgress.setStartDate(new Timestamp(new Date().getTime()));
	    learnerProgressDAO.saveLearnerProgress(learnerProgress);

	    // check if lesson is set to be finished for individual users then store finish date
	    if (lesson.isScheduledToCloseForIndividuals()) {
		GroupUser groupUser = groupUserDAO.getGroupUser(lesson, learnerId);
		if (groupUser != null) {
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(learnerProgress.getStartDate());
		    calendar.add(Calendar.DATE, lesson.getScheduledNumberDaysToLessonFinish());
		    Date endDate = calendar.getTime();
		    groupUser.setScheduledLessonEndDate(endDate);
		}
	    }

	} else {

	    Activity currentActivity = learnerProgress.getCurrentActivity();
	    if (currentActivity == null) {
		// something may have gone wrong and we need to recalculate the current activity
		try {
		    progressEngine.setUpStartPoint(learnerProgress);
		} catch (ProgressException e) {
		    LearnerService.log.error("error occurred in 'setUpStartPoint':" + e.getMessage());
		    throw new LearnerServiceException(e.getMessage());
		}
	    }

	    // The restarting flag should be setup when the learner hit the exit
	    // button. But it is possible that user exit by closing the browser,
	    // In this case, we set the restarting flag again.
	    if (!learnerProgress.isRestarting()) {
		learnerProgress.setRestarting(true);
		learnerProgressDAO.updateLearnerProgress(learnerProgress);
	    }
	}

	return learnerProgress;
    }

    /**
     * This method creates the tool session (if needed) for a tool activity. It won't try it for the child activities,
     * as any Sequence activities inside this activity may have a grouping WITHIN the sequence, and then the grouped
     * activities get the won't be grouped properly (See LDEV-1774). We could get the child tool activities for a
     * parallel activity but that could create a bug in the future - if we ever put sequences inside parallel activities
     * then we are stuck again!
     * 
     * We look up the database to check up the existence of correspondent tool session. If the tool session doesn't
     * exist, we create a new tool session instance.
     * 
     * @param learnerProgress
     *            the learner progress we are processing.
     * @throws LamsToolServiceException
     */
    @Override
    public void createToolSessionsIfNecessary(Activity activity, LearnerProgress learnerProgress) {
	try {
	    if ((activity != null) && activity.isToolActivity()) {
		ToolActivity toolActivity = (ToolActivity) activity;
		createToolSessionFor(toolActivity, learnerProgress.getUser(), learnerProgress.getLesson());
	    }
	} catch (LamsToolServiceException e) {
	    LearnerService.log.error("error occurred in 'createToolSessionFor':" + e.getMessage());
	    throw new LearnerServiceException(e.getMessage());
	} catch (ToolException e) {
	    LearnerService.log.error("error occurred in 'createToolSessionFor':" + e.getMessage());
	    throw new LearnerServiceException(e.getMessage());
	}
    }

    /**
     * Returns the current progress data of the User.
     * 
     * @param learnerId
     *            the Learner's userID
     * @param lessonId
     *            the Lesson to get progress from.
     * @return LearnerProgess contains the learner's progress for the lesson.
     * @throws LearnerServiceException
     *             in case of problems.
     */
    @Override
    public LearnerProgress getProgress(Integer learnerId, Long lessonId) {
	return learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getProgressById(java.lang.Long)
     */
    @Override
    public LearnerProgress getProgressById(Long progressId) {
	return learnerProgressDAO.getLearnerProgress(progressId);
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getProgressDTOByLessonId(java.lang.Long,
     *      org.lamsfoundation.lams.usermanagement.User)
     */
    @Override
    public LearnerProgressDTO getProgressDTOByLessonId(Long lessonId, Integer learnerId) {
	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
	if (progress != null) {
	    return progress.getLearnerProgressData();
	} else {
	    return null;
	}
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getStructuredProgressDTOs(java.lang.Long,
     *      java.lang.Long)
     */
    @Override
    public Object[] getStructuredActivityURLs(Integer learnerId, Long lessonId) {

	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
	Lesson lesson = progress.getLesson();

	ProgressBuilder builder = new ProgressBuilder(progress, activityDAO, activityMapping);
	builder.parseLearningDesign();

	Object[] retValue = new Object[3];
	retValue[0] = builder.getActivityList();

	retValue[1] = progress.getCurrentActivity() != null ? progress.getCurrentActivity().getActivityId() : null;
	retValue[2] = lesson.isPreviewLesson();

	return retValue;
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#chooseActivity(org.lamsfoundation.lams.usermanagement.User,
     *      java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity)
     */
    @Override
    public LearnerProgress chooseActivity(Integer learnerId, Long lessonId, Activity activity,
	    Boolean clearCompletedFlag) {
	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);

	if (!progress.getCompletedActivities().containsKey(activity)) {
	    // if we skip a sequence in an optional sequence, or have been force completed for branching / optional
	    // sequence
	    // and we go back to the sequence later, then the LessonComplete flag must be reset so that it will step
	    // through
	    // all the activities in the sequence - otherwise it will go to the "Completed" screen after the first
	    // activity in the sequence
	    if (clearCompletedFlag && (activity.getParentActivity() != null)
		    && activity.getParentActivity().isSequenceActivity() && progress.isComplete()) {
		progress.setLessonComplete(LearnerProgress.LESSON_NOT_COMPLETE);
	    }

	    progressEngine.setActivityAttempted(progress, activity);

	    progress.setCurrentActivity(activity);
	    progress.setNextActivity(activity);

	    learnerProgressDAO.saveLearnerProgress(progress);
	}

	return progress;
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#moveToActivity(java.lang.Integer,
     *      java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity,
     *      org.lamsfoundation.lams.learningdesign.Activity)
     */
    @Override
    public LearnerProgress moveToActivity(Integer learnerId, Long lessonId, Activity fromActivity, Activity toActivity)
	    throws LearnerServiceException {
	int count = 0;
	LearnerProgress progress = null;

	// wait till lock is released
	while (LearnerService.syncMap.containsKey(learnerId)) {
	    count++;
	    try {
		Thread.sleep(1000);

		if (count > 100) {
		    throw new LearnerServiceException("Thread wait count exceeded limit.");
		}

	    } catch (InterruptedException e1) {
		throw new LearnerServiceException("While retrying to move activity, thread was interrupted.", e1);
	    }
	}

	// lock
	try {

	    LearnerService.syncMap.put(learnerId, lessonId);

	    progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);

	    if ((fromActivity != null)
		    && (fromActivity.getActivityId() != progress.getCurrentActivity().getActivityId())) {
		progress.setProgressState(fromActivity, LearnerProgress.ACTIVITY_ATTEMPTED, activityDAO);
	    }

	    if (toActivity != null) {
		progress.setProgressState(toActivity, LearnerProgress.ACTIVITY_ATTEMPTED, activityDAO);

		if (!toActivity.getReadOnly()) {
		    toActivity.setReadOnly(true);
		    activityDAO.update(toActivity);
		}

		if (!toActivity.isFloating()) {
		    progress.setCurrentActivity(toActivity);
		    progress.setNextActivity(toActivity);
		}
	    }

	    learnerProgressDAO.updateLearnerProgress(progress);
	} catch (Exception e) {
	    throw new LearnerServiceException(e.getMessage());
	} finally {
	    // remove lock
	    if (LearnerService.syncMap.containsKey(learnerId)) {
		LearnerService.syncMap.remove(learnerId);
	    }
	}

	return progress;

    }

    /**
     * Calculates learner progress and returns the data required to be displayed to the learner.
     * 
     * @param completedActivity
     *            the activity just completed
     * @param learner
     *            the Learner
     * @param learnerProgress
     *            the current progress
     * @return the bean containing the display data for the Learner
     * @throws LamsToolServiceException
     * @throws LearnerServiceException
     *             in case of problems.
     */
    @Override
    public LearnerProgress calculateProgress(Activity completedActivity, Integer learnerId,
	    LearnerProgress currentLearnerProgress) {
	try {
	    LearnerProgress learnerProgress = progressEngine.calculateProgress(currentLearnerProgress.getUser(),
		    completedActivity, currentLearnerProgress);
	    learnerProgressDAO.updateLearnerProgress(learnerProgress);
	    return learnerProgress;
	} catch (ProgressException e) {
	    throw new LearnerServiceException(e.getMessage());
	}

    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ILearnerService#completeToolSession(java.lang.Long, java.lang.Long)
     */
    @Override
    public String completeToolSession(Long toolSessionId, Long learnerId) {
	// this method is called by tools, so it mustn't do anything that relies on all the tools' Spring beans
	// being available in the context. Hence it is defined in the ILearnerService interface, not the
	// IFullLearnerService
	// interface. If it calls any other methods then it mustn't use anything on the ICoreLearnerService interface.

	String returnURL = null;

	ToolSession toolSession = lamsCoreToolService.getToolSessionById(toolSessionId);
	if (toolSession == null) {
	    // something has gone wrong - maybe due to Live Edit. The tool session supplied by the tool doesn't exist.
	    // have to go to a "can't do anything" screen and the user will have to hit resume.
	    returnURL = activityMapping.getProgressBrokenURL();

	} else {
	    Long lessonId = toolSession.getLesson().getLessonId();
	    LearnerProgress currentProgress = getProgress(new Integer(learnerId.intValue()), lessonId);
	    // TODO Cache the learner progress in the session, but mark it with the progress id. Then get the progress
	    // out of the session
	    // for ActivityAction.java.completeActivity(). Update LearningWebUtil to look under the progress id, so we
	    // don't get
	    // a conflict in Preview & Learner.
	    returnURL = activityMapping.getCompleteActivityURL(toolSession.getToolActivity().getActivityId(),
		    currentProgress.getLearnerProgressId());

	}

	if (LearnerService.log.isDebugEnabled()) {
	    LearnerService.log.debug("CompleteToolSession() for tool session id " + toolSessionId + " learnerId "
		    + learnerId + " url is " + returnURL);
	}

	return returnURL;

    }

    /**
     * Complete the activity in the progress engine and delegate to the progress engine to calculate the next activity
     * in the learning design. It is currently triggered by various progress engine related action classes, which then
     * calculate the url to go to next, based on the ActivityMapping class.
     * 
     * @param learnerId
     *            the learner who are running this activity in the design.
     * @param activity
     *            the activity is being run.
     * @param lessonId
     *            lesson id
     * @return the updated learner progress
     */
    @Override
    public synchronized LearnerProgress completeActivity(Integer learnerId, Activity activity, LearnerProgress progress) {
	LearnerProgress nextLearnerProgress = null;

	// Need to synchronise the next bit of code so that if the tool calls
	// this twice in quick succession, with the same parameters, it won't update
	// the database twice! This may happen if a tool has a double submission problem.
	// I don't want to synchronise on (this), as this could cause too much of a bottleneck,
	// but if its not synchronised, we get db errors if the same tool session is completed twice
	// (invalid index). I can'tfind another object on which to synchronise - Hibernate does not give me the
	// same object for tool session or current progress and user is cached via login, not userid.

	// bottleneck synchronized (this) {
	if (activity == null) {
	    try {
		nextLearnerProgress = progressEngine.setUpStartPoint(progress);
	    } catch (ProgressException e) {
		LearnerService.log.error("error occurred in 'setUpStartPoint':" + e.getMessage(), e);
		throw new LearnerServiceException(e);
	    }

	} else {

	    nextLearnerProgress = calculateProgress(activity, learnerId, progress);

	    ToolSession toolSession = lamsCoreToolService.getToolSessionByLearner(progress.getUser(), activity);
	    if ((activity instanceof ToolActivity) && (toolSession != null)) {
		sendDataToGradebook((ToolActivity) activity, toolSession, progress);
	    }
	}
	// }
	logEventService.logEvent(LogEvent.TYPE_LEARNER_ACTIVITY_FINISH, learnerId, activity.getLearningDesign()
		.getLearningDesignId(), progress.getLesson().getLessonId(), activity.getActivityId());

	return nextLearnerProgress;
    }

    /**
     * @throws
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#completeActivity(java.lang.Integer,
     *      org.lamsfoundation.lams.learningdesign.Activity, java.lang.Long )
     */
    @Override
    public LearnerProgress completeActivity(Integer learnerId, Activity activity, Long lessonId) {
	LearnerProgress currentProgress = getProgress(new Integer(learnerId.intValue()), lessonId);
	return completeActivity(learnerId, activity, currentProgress);
    }

    private void sendDataToGradebook(ToolActivity toolActivity, ToolSession toolSession, LearnerProgress progress) {

	User learner = progress.getUser();
	Lesson lesson = progress.getLesson();
	if ((toolActivity != null) && (toolSession != null) && (learner != null) && (lesson != null)) {
	    if ((toolActivity.getActivityEvaluations() != null) && (toolActivity.getActivityEvaluations().size() > 0)) {

		// Getting the first activity evaluation
		ActivityEvaluation eval = toolActivity.getActivityEvaluations().iterator().next();

		try {
		    ToolOutput toolOutput = lamsCoreToolService.getOutputFromTool(eval.getToolOutputDefinition(),
			    toolSession, learner.getUserId());

		    if (toolOutput != null) {
			ToolOutputValue outputVal = toolOutput.getValue();
			if (outputVal != null) {
			    Double outputDouble = outputVal.getDouble();

			    GradebookUserActivity gradebookUserActivity = gradebookService.getGradebookUserActivity(
				    toolActivity.getActivityId(), learner.getUserId());

			    // Only set the mark if it hasnt previously been set by a teacher
			    if ((gradebookUserActivity == null) || !gradebookUserActivity.getMarkedInGradebook()) {
				gradebookService.updateUserActivityGradebookMark(lesson, learner, toolActivity,
					outputDouble, false);
			    }
			}
		    }

		} catch (ToolException e) {
		    LearnerService.log.debug("Runtime exception when attempted to get outputs for activity: "
			    + toolActivity.getActivityId(), e);
		}
	    }

	}
    }

    /**
     * Exit a lesson.
     * 
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#exitLesson(org.lamsfoundation.lams.lesson.LearnerProgress)
     */
    @Override
    public void exitLesson(Integer learnerId, Long lessonId) {

	User learner = (User) userManagementService.findById(User.class, learnerId);

	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learner.getUserId(), lessonId);

	if (progress != null) {
	    progress.setRestarting(true);
	    learnerProgressDAO.updateLearnerProgress(progress);
	} else {
	    String error = "Learner Progress " + lessonId + " does not exist. Cannot exit lesson successfully.";
	    LearnerService.log.error(error);
	    throw new LearnerServiceException(error);
	}
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActivity(java.lang.Long)
     */
    @Override
    public Activity getActivity(Long activityId) {
	return activityDAO.getActivityByActivityId(activityId);
    }

    /**
     * @throws LearnerServiceException
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#performGrouping(java.lang.Long, java.lang.Long,
     *      java.lang.Integer)
     */
    @Override
    public boolean performGrouping(Long lessonId, Long groupingActivityId, Integer learnerId, boolean forceGrouping)
	    throws LearnerServiceException {
	GroupingActivity groupingActivity = (GroupingActivity) activityDAO.getActivityByActivityId(groupingActivityId,
		GroupingActivity.class);
	User learner = (User) userManagementService.findById(User.class, learnerId);

	boolean groupingDone = false;
	try {
	    if ((groupingActivity != null) && (groupingActivity.getCreateGrouping() != null) && (learner != null)) {
		Grouping grouping = groupingActivity.getCreateGrouping();

		// first check if the grouping already done for the user. If done, then skip the processing.
		groupingDone = grouping.doesLearnerExist(learner);

		if (!groupingDone) {
		    if (grouping.isRandomGrouping()) {
			// normal and preview cases for random grouping
			lessonService.performGrouping(lessonId, groupingActivity, learner);
			groupingDone = true;

		    } else if (forceGrouping) {
			// preview case for chosen grouping
			Lesson lesson = getLesson(lessonId);
			groupingDone = forceGrouping(lesson, grouping, null, learner);
		    }
		}

	    } else {
		String error = "Grouping activity " + groupingActivity + " learner " + learnerId
			+ " does not exist. Cannot perform grouping.";
		LearnerService.log.error(error);
		throw new LearnerServiceException(error);
	    }
	} catch (LessonServiceException e) {
	    throw new LearnerServiceException("performGrouping failed due to " + e.getMessage(), e);
	}
	return groupingDone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean learnerChooseGroup(Long lessonId, Long groupingActivityId, Long groupId, Integer learnerId)
	    throws LearnerServiceException {
	GroupingActivity groupingActivity = (GroupingActivity) activityDAO.getActivityByActivityId(groupingActivityId,
		GroupingActivity.class);

	if ((groupingActivity != null) && (groupId != null) && (learnerId != null)) {
	    Grouping grouping = groupingDAO.getGroupingById(groupingActivity.getCreateGrouping().getGroupingId());
	    if ((grouping != null) && grouping.isLearnerChoiceGrouping()) {

		User learner = (User) userManagementService.findById(User.class, learnerId);
		if (grouping.doesLearnerExist(learner)) {
		    return true;
		}
		if (learner != null) {
		    Integer maxNumberOfLearnersPerGroup = null;
		    Set<Group> groups = grouping.getGroups();
		    if (((LearnerChoiceGrouping) grouping).getLearnersPerGroup() == null) {
			if (((LearnerChoiceGrouping) grouping).getEqualNumberOfLearnersPerGroup()) {
			    if (((LearnerChoiceGrouping) grouping).getLearnersPerGroup() == null) {
				Lesson lesson = getLesson(lessonId);
				int learnerCount = lesson.getAllLearners().size();
				int groupCount = grouping.getGroups().size();
				maxNumberOfLearnersPerGroup = learnerCount / groupCount
					+ (learnerCount % groupCount == 0 ? 0 : 1);
			    }
			}
		    } else {
			maxNumberOfLearnersPerGroup = ((LearnerChoiceGrouping) grouping).getLearnersPerGroup();
		    }
		    if (maxNumberOfLearnersPerGroup != null) {
			for (Group group : groups) {
			    if (group.getGroupId().equals(groupId)) {
				if (group.getUsers().size() >= maxNumberOfLearnersPerGroup) {
				    return false;
				}
			    }
			}
		    }

		    lessonService.performGrouping(grouping, groupId, learner);
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean forceGrouping(Lesson lesson, Grouping grouping, Group group, User learner) {
	boolean groupingDone = false;
	if (lesson.isPreviewLesson()) {
	    ArrayList<User> learnerList = new ArrayList<User>();
	    learnerList.add(learner);
	    if (group != null) {
		if (group.getGroupId() != null) {
		    lessonService.performGrouping(grouping, group.getGroupId(), learnerList);
		} else {
		    lessonService.performGrouping(grouping, group.getGroupName(), learnerList);
		}
	    } else {
		if (grouping.getGroups().size() > 0) {
		    // if any group exists, put them in there.
		    Group aGroup = (Group) grouping.getGroups().iterator().next();
		    if (aGroup.getGroupId() != null) {
			lessonService.performGrouping(grouping, aGroup.getGroupId(), learnerList);
		    } else {
			lessonService.performGrouping(grouping, aGroup.getGroupName(), learnerList);
		    }
		} else {
		    // just create a group and stick the user in there!
		    lessonService.performGrouping(grouping, (String) null, learnerList);
		}
	    }
	    groupingDone = true;
	}
	return groupingDone;
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#knockGate(java.lang.Long,
     *      org.lamsfoundation.lams.usermanagement.User)
     */
    @Override
    public GateActivityDTO knockGate(Long gateActivityId, User knocker, boolean forceGate) {
	GateActivity gate = (GateActivity) activityDAO.getActivityByActivityId(gateActivityId, GateActivity.class);
	if (gate != null) {
	    return knockGate(gate, knocker, forceGate);
	}

	String error = "Gate activity " + gateActivityId + " does not exist. Cannot knock on gate.";
	LearnerService.log.error(error);
	throw new LearnerServiceException(error);
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#knockGate(org.lamsfoundation.lams.learningdesign.GateActivity,
     *      org.lamsfoundation.lams.usermanagement.User)
     */
    @Override
    public GateActivityDTO knockGate(GateActivity gate, User knocker, boolean forceGate) {
	Lesson lesson = getLessonByActivity(gate);
	List lessonLearners = getLearnersForGate(gate, lesson);

	boolean gateOpen = false;

	if (forceGate) {
	    if (lesson.isPreviewLesson()) {
		// special case for preview - if forceGate is true then brute force open the gate
		gateOpen = gate.forceGateOpen();
	    }
	}

	if (!gateOpen) {
	    // normal case - knock the gate.
	    gateOpen = gate.shouldOpenGateFor(knocker, lessonLearners);
	    if (!gateOpen) {
		// only for a condition gate
		gateOpen = determineConditionGateStatus(gate, knocker);
	    }
	}

	// update gate including updating the waiting list and gate status in
	// the database.
	activityDAO.update(gate);
	return new GateActivityDTO(gate, lessonLearners, gateOpen);

    }

    /**
     * Get all the learners who may come through this gate. For a Group Based branch and the Teacher Grouped branch, it
     * is the group of users in the Branch's group, but only the learners who have started the lesson. Otherwise we just
     * get all learners who have started the lesson.
     * 
     * @param gate
     * @param lesson
     * @return List of User
     */
    private List getLearnersForGate(GateActivity gate, Lesson lesson) {

	List lessonLearners = null;
	Activity branchActivity = gate.getParentBranch();
	while ((branchActivity != null)
		&& !(branchActivity.getParentActivity().isChosenBranchingActivity() || branchActivity
			.getParentActivity().isGroupBranchingActivity())) {
	    branchActivity = branchActivity.getParentBranch();
	}

	if (branchActivity != null) {
	    // set up list based on branch - all members of a group attached to the branch are destined for the gate
	    SequenceActivity branchSequence = (SequenceActivity) activityDAO.getActivityByActivityId(
		    branchActivity.getActivityId(), SequenceActivity.class);
	    Set branchEntries = branchSequence.getBranchEntries();
	    Iterator entryIterator = branchEntries.iterator();
	    while (entryIterator.hasNext()) {
		BranchActivityEntry branchActivityEntry = (BranchActivityEntry) entryIterator.next();
		Group group = branchActivityEntry.getGroup();
		if (group != null) {
		    List groupLearners = lessonService.getActiveLessonLearnersByGroup(lesson.getLessonId(),
			    group.getGroupId());
		    if (lessonLearners == null) {
			lessonLearners = groupLearners;
		    } else {
			lessonLearners.addAll(groupLearners);
		    }
		}
	    }

	} else {
	    lessonLearners = getActiveLearnersByLesson(lesson.getLessonId());
	}
	return lessonLearners;
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getWaitingGateLearners(org.lamsfoundation.lams.learningdesign.GateActivity)
     */
    @Override
    public List getLearnersForGate(GateActivity gate) {
	return getLearnersForGate(gate, getLessonByActivity(gate));
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getLearnerActivityURL(java.lang.Integer,
     *      java.lang.Long)
     */
    @Override
    public String getLearnerActivityURL(Integer learnerId, Long activityId) {
	User learner = (User) userManagementService.findById(User.class, learnerId);
	Activity requestedActivity = getActivity(activityId);
	Lesson lesson = getLessonByActivity(requestedActivity);
	return activityMapping.calculateActivityURLForProgressView(lesson, learner, requestedActivity);
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActiveLearnersByLesson(long)
     */
    @Override
    public List getActiveLearnersByLesson(long lessonId) {
	return lessonService.getActiveLessonLearners(lessonId);
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getCountActiveLessonLearners(long)
     */
    @Override
    public Integer getCountActiveLearnersByLesson(long lessonId) {
	return lessonService.getCountActiveLessonLearners(lessonId);
    }

    /**
     * Get the lesson for this activity. If the activity is not part of a lesson (ie is from an authoring design then it
     * will return null.
     */
    @Override
    public Lesson getLessonByActivity(Activity activity) {
	Lesson lesson = lessonDAO.getLessonForActivity(activity.getActivityId());
	if (lesson == null) {
	    LearnerService.log
		    .warn("Tried to get lesson id for a non-lesson based activity. An error is likely to be thrown soon. Activity was "
			    + activity);
	}
	return lesson;
    }

    // ---------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------

    /**
     * <p>
     * Create a lams tool session for learner against a tool activity. This will have concurrency issues interms of
     * grouped tool session because it might be inserting some tool session that has already been inserted by other
     * member in the group. If the unique_check is broken, we need to query the database to get the instance instead of
     * inserting it. It should be done in the Spring rollback strategy.
     * </p>
     * 
     * Once lams tool session is inserted, we need to notify the tool to its own session.
     * 
     * @param toolActivity
     * @param learner
     * @throws LamsToolServiceException
     */
    private void createToolSessionFor(ToolActivity toolActivity, User learner, Lesson lesson)
	    throws LamsToolServiceException, ToolException {
	// if the tool session already exists, createToolSession() will return null
	ToolSession toolSession = null;
	try {
	    toolSession = lamsCoreToolService.createToolSession(learner, toolActivity, lesson);
	} catch (DataIntegrityViolationException e) {
	    LearnerService.log
		    .warn("There was an attempt to create two tool sessions with the same name. Skipping further attempts as the session exists.",
			    e);
	}
	if (toolSession != null) {
	    toolActivity.getToolSessions().add(toolSession);
	    lamsCoreToolService.notifyToolsToCreateSession(toolSession, toolActivity);
	}
    }

    /**
     * Create an array of lesson dto based a list of lessons.
     * 
     * @param lessons
     *            the list of lessons.
     * @return the lesson dto array.
     */
    private LessonDTO[] getLessonDataFor(List lessons) {
	List<LessonDTO> lessonDTOList = new ArrayList<LessonDTO>();
	for (Iterator i = lessons.iterator(); i.hasNext();) {
	    Lesson currentLesson = (Lesson) i.next();
	    lessonDTOList.add(currentLesson.getLessonData());
	}
	return lessonDTOList.toArray(new LessonDTO[lessonDTOList.size()]);
    }

    /**
     * @throws LearnerServiceException
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#determineBranch(org.lamsfoundation.lams.lesson.Lesson,
     *      org.lamsfoundation.lams.learningdesign.BranchingActivity, java.lang.Integer)
     */
    @Override
    public SequenceActivity determineBranch(Lesson lesson, BranchingActivity branchingActivity, Integer learnerId)
	    throws LearnerServiceException {
	User learner = (User) userManagementService.findById(User.class, learnerId);
	if (learner == null) {
	    String error = "determineBranch: learner " + learnerId + " does not exist. Cannot determine branch.";
	    LearnerService.log.error(error);
	    throw new LearnerServiceException(error);
	}

	try {
	    if (branchingActivity.isToolBranchingActivity()) {
		return determineToolBasedBranch(lesson, (ToolBranchingActivity) branchingActivity, learner);

	    } else {
		// assume either isGroupBranchingActivity() || isChosenBranchingActivity() )
		// in both cases, the branch is based on the group the learner is in.
		return determineGroupBasedBranch(lesson, branchingActivity, learner);

	    }
	} catch (LessonServiceException e) {
	    String message = "determineBranch failed due to " + e.getMessage();
	    LearnerService.log.error(message, e);
	    throw new LearnerServiceException("determineBranch failed due to " + e.getMessage(), e);
	}
    }

    /**
     * Get all the conditions for this branching activity, ordered by order id. Go through each condition until we find
     * one that passes and that is the required branch. If no conditions match, use the branch that is the "default"
     * branch for this branching activity.
     */
    private SequenceActivity determineToolBasedBranch(Lesson lesson, ToolBranchingActivity branchingActivity,
	    User learner) {
	Activity defaultBranch = branchingActivity.getDefaultActivity();
	SequenceActivity matchedBranch = null;

	// Work out the tool session appropriate for this user and branching activity. We expect there to be only one at
	// this point.
	ToolSession toolSession = null;
	for (Activity inputActivity : (Set<Activity>) branchingActivity.getInputActivities()) {
	    toolSession = lamsCoreToolService.getToolSessionByLearner(learner, inputActivity);
	}

	if (toolSession != null) {

	    // Get all the conditions for this branching activity, ordered by order id.
	    Map<BranchCondition, SequenceActivity> conditionsMap = new TreeMap<BranchCondition, SequenceActivity>();
	    Iterator branchIterator = branchingActivity.getActivities().iterator();
	    while (branchIterator.hasNext()) {
		Activity branchActivity = (Activity) branchIterator.next();
		SequenceActivity branchSequence = (SequenceActivity) activityDAO.getActivityByActivityId(
			branchActivity.getActivityId(), SequenceActivity.class);
		Iterator<BranchActivityEntry> entryIterator = branchSequence.getBranchEntries().iterator();
		while (entryIterator.hasNext()) {
		    BranchActivityEntry entry = entryIterator.next();
		    if (entry.getCondition() != null) {
			conditionsMap.put(entry.getCondition(), branchSequence);
		    }
		}
	    }

	    // Go through each condition until we find one that passes and that is the required branch.
	    // Cache the tool output so that we aren't calling it over an over again.
	    Map<String, ToolOutput> toolOutputMap = new HashMap<String, ToolOutput>();
	    Iterator<BranchCondition> conditionIterator = conditionsMap.keySet().iterator();

	    while ((matchedBranch == null) && conditionIterator.hasNext()) {
		BranchCondition condition = conditionIterator.next();
		String conditionName = condition.getName();
		ToolOutput toolOutput = toolOutputMap.get(conditionName);
		if (toolOutput == null) {
		    toolOutput = lamsCoreToolService.getOutputFromTool(conditionName, toolSession, learner.getUserId());
		    if (toolOutput == null) {
			LearnerService.log.warn("Condition " + condition + " refers to a tool output " + conditionName
				+ " but tool doesn't return any tool output for that name. Skipping this condition.");
		    } else {
			toolOutputMap.put(conditionName, toolOutput);
		    }
		}

		if ((toolOutput != null) && condition.isMet(toolOutput)) {
		    matchedBranch = conditionsMap.get(condition);
		}
	    }
	}

	// If no conditions match, use the branch that is the "default" branch for this branching activity.
	if (matchedBranch != null) {
	    if (LearnerService.log.isDebugEnabled()) {
		LearnerService.log.debug("Found branch " + matchedBranch.getActivityId() + ":"
			+ matchedBranch.getTitle() + " for branching activity " + branchingActivity.getActivityId()
			+ ":" + branchingActivity.getTitle() + " for learner " + learner.getUserId() + ":"
			+ learner.getLogin());
	    }
	    return matchedBranch;

	} else if (defaultBranch != null) {
	    if (LearnerService.log.isDebugEnabled()) {
		LearnerService.log.debug("Using default branch " + defaultBranch.getActivityId() + ":"
			+ defaultBranch.getTitle() + " for branching activity " + branchingActivity.getActivityId()
			+ ":" + branchingActivity.getTitle() + " for learner " + learner.getUserId() + ":"
			+ learner.getLogin());
	    }
	    // have to convert it to a real activity of the correct type, as it could be a cglib value
	    return (SequenceActivity) activityDAO.getActivityByActivityId(defaultBranch.getActivityId(),
		    SequenceActivity.class);
	} else {
	    if (LearnerService.log.isDebugEnabled()) {
		LearnerService.log
			.debug("No branches match and no default branch exists. Uable to allocate learner to a branch for the branching activity"
				+ branchingActivity.getActivityId()
				+ ":"
				+ branchingActivity.getTitle()
				+ " for learner " + learner.getUserId() + ":" + learner.getLogin());
	    }
	    return null;
	}
    }

    private SequenceActivity determineGroupBasedBranch(Lesson lesson, BranchingActivity branchingActivity, User learner) {
	SequenceActivity sequenceActivity = null;

	if (branchingActivity.getGrouping() != null) {
	    Grouping grouping = branchingActivity.getGrouping();

	    // If the user is in a group, then check if the group is assigned to a sequence activity. If it
	    // is then we are done and we return the sequence
	    Group group = grouping.getGroupBy(learner);
	    if (group != null) {
		if (group.getBranchActivities() != null) {
		    Iterator branchesIterator = group.getBranchActivities().iterator();
		    while ((sequenceActivity == null) && branchesIterator.hasNext()) {
			BranchActivityEntry branchActivityEntry = (BranchActivityEntry) branchesIterator.next();
			if (branchActivityEntry.getBranchingActivity().equals(branchingActivity)) {
			    sequenceActivity = branchActivityEntry.getBranchSequenceActivity();
			}
		    }
		}
	    }

	    if (sequenceActivity != null) {
		if (LearnerService.log.isDebugEnabled()) {
		    LearnerService.log.debug("Found branch " + sequenceActivity.getActivityId() + ":"
			    + sequenceActivity.getTitle() + " for branching activity "
			    + branchingActivity.getActivityId() + ":" + branchingActivity.getTitle() + " for learner "
			    + learner.getUserId() + ":" + learner.getLogin());
		}
	    }

	}

	return sequenceActivity;
    }

    /**
     * Checks if any of the conditions that open the gate is met.
     * 
     * @param gate
     *            gate to check
     * @param learner
     *            learner who is knocking to the gate
     * @return <code>true</code> if learner satisfied any of the conditions and is allowed to pass
     */
    private boolean determineConditionGateStatus(GateActivity gate, User learner) {
	boolean shouldOpenGate = false;
	if (gate instanceof ConditionGateActivity) {
	    ConditionGateActivity conditionGate = (ConditionGateActivity) gate;

	    // Work out the tool session appropriate for this user and gate activity. We expect there to be only one at
	    // this point.
	    ToolSession toolSession = null;
	    for (Activity inputActivity : (Set<Activity>) conditionGate.getInputActivities()) {
		toolSession = lamsCoreToolService.getToolSessionByLearner(learner, inputActivity);
	    }

	    if (toolSession != null) {

		Set<BranchActivityEntry> branchEntries = conditionGate.getBranchActivityEntries();

		// Go through each condition until we find one that passes and that opens the gate.
		// Cache the tool output so that we aren't calling it over an over again.
		Map<String, ToolOutput> toolOutputMap = new HashMap<String, ToolOutput>();
		Iterator<BranchActivityEntry> entryIterator = branchEntries.iterator();

		while (entryIterator.hasNext()) {
		    BranchActivityEntry entry = entryIterator.next();
		    BranchCondition condition = entry.getCondition();
		    String conditionName = condition.getName();
		    ToolOutput toolOutput = toolOutputMap.get(conditionName);
		    if (toolOutput == null) {
			toolOutput = lamsCoreToolService.getOutputFromTool(conditionName, toolSession,
				learner.getUserId());
			if (toolOutput == null) {
			    LearnerService.log
				    .warn("Condition "
					    + condition
					    + " refers to a tool output "
					    + conditionName
					    + " but tool doesn't return any tool output for that name. Skipping this condition.");
			} else {
			    toolOutputMap.put(conditionName, toolOutput);
			}
		    }

		    if ((toolOutput != null) && condition.isMet(toolOutput)) {
			shouldOpenGate = entry.getGateOpenWhenConditionMet();
			if (shouldOpenGate) {
			    // save the learner to the "allowed to pass" list so we don't check the conditions over and
			    // over
			    // again (maybe we should??)
			    conditionGate.addLeaner(learner, true);
			}
			break;
		    }
		}
	    }
	}
	return shouldOpenGate;
    }

    /**
     * Select a particular branch - we are in preview mode and the author has selected a particular activity.
     * 
     * @throws LearnerServiceException
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#determineBranch(org.lamsfoundation.lams.lesson.Lesson,
     *      org.lamsfoundation.lams.learningdesign.BranchingActivity, java.lang.Integer)
     */
    @Override
    public SequenceActivity selectBranch(Lesson lesson, BranchingActivity branchingActivity, Integer learnerId,
	    Long branchId) throws LearnerServiceException {

	User learner = (User) userManagementService.findById(User.class, learnerId);
	if (learner == null) {
	    String error = "selectBranch: learner " + learnerId + " does not exist. Cannot determine branch.";
	    LearnerService.log.error(error);
	    throw new LearnerServiceException(error);
	}

	SequenceActivity selectedBranch = (SequenceActivity) activityDAO.getActivityByActivityId(branchId,
		SequenceActivity.class);
	if (selectedBranch != null) {

	    if ((selectedBranch.getParentActivity() == null)
		    || !selectedBranch.getParentActivity().equals(branchingActivity)) {
		String error = "selectBranch: activity " + selectedBranch
			+ " is not a branch within the branching activity " + branchingActivity + ". Unable to branch.";
		LearnerService.log.error(error);
		throw new LearnerServiceException(error);
	    }

	    Set<Group> groups = selectedBranch.getGroupsForBranch();
	    Grouping grouping = branchingActivity.getGrouping();

	    // Does this matching branch have any groups? If so, see if the learner is in
	    // the appropriate group and add them if necessary.
	    if ((groups != null) && (groups.size() > 0)) {
		boolean isInGroup = false;
		Group aGroup = null;
		Iterator<Group> groupIter = groups.iterator();
		while (!isInGroup && groupIter.hasNext()) {
		    aGroup = groupIter.next();
		    isInGroup = aGroup.hasLearner(learner);
		}

		// If the learner is not in the appropriate group, then force the learner in the
		// last group we checked. this will only work if the user is in preview.
		if (!isInGroup) {
		    if (!forceGrouping(lesson, grouping, aGroup, learner)) {
			String error = "selectBranch: learner " + learnerId + " cannot be added to the group " + aGroup
				+ " for the branch " + selectedBranch + " for the lesson " + lesson.getLessonName()
				+ " preview is " + lesson.isPreviewLesson()
				+ ". This will only work if preview is true.";
			LearnerService.log.error(error);
			throw new LearnerServiceException(error);
		    }
		}

		// if no matching groups exist (just to Define in Monitor), then create one and assign it to the branch.
		// if it is a chosen grouping, make sure we allow it to go over the normal number of groups (the real
		// groups will exist
		// but it too hard to reuse them.)
	    } else {
		if (grouping.isChosenGrouping() && (grouping.getMaxNumberOfGroups() != null)) {
		    grouping.setMaxNumberOfGroups(null);
		}

		Group group = lessonService.createGroup(grouping, selectedBranch.getTitle());
		group.allocateBranchToGroup(null, selectedBranch, branchingActivity);
		if (!forceGrouping(lesson, grouping, group, learner)) {
		    String error = "selectBranch: learner " + learnerId + " cannot be added to the group " + group
			    + " for the branch " + selectedBranch + " for the lesson " + lesson.getLessonName()
			    + " preview is " + lesson.isPreviewLesson() + ". This will only work if preview is true.";
		    LearnerService.log.error(error);
		    throw new LearnerServiceException(error);
		}
	    }
	    groupingDAO.update(grouping);

	    if (LearnerService.log.isDebugEnabled()) {
		LearnerService.log.debug("Found branch " + selectedBranch.getActivityId() + ":"
			+ selectedBranch.getTitle() + " for branching activity " + branchingActivity.getActivityId()
			+ ":" + branchingActivity.getTitle() + " for learner " + learner.getUserId() + ":"
			+ learner.getLogin());
	    }

	    return selectedBranch;

	} else {
	    String error = "selectBranch: Unable to find branch for branch id " + branchId;
	    LearnerService.log.error(error);
	    throw new LearnerServiceException(error);
	}

    }

    public ProgressEngine getProgressEngine() {
	return progressEngine;
    }

    public void setProgressEngine(ProgressEngine progressEngine) {
	this.progressEngine = progressEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer calculateMaxNumberOfLearnersPerGroup(Long lessonId, Grouping grouping) {
	Lesson lesson = getLesson(lessonId);
	LearnerChoiceGrouping learnerChoiceGrouping = (LearnerChoiceGrouping) grouping;
	Integer maxNumberOfLearnersPerGroup = null;
	int learnerCount = lesson.getAllLearners().size();
	int groupCount = grouping.getGroups().size();
	if (learnerChoiceGrouping.getLearnersPerGroup() == null) {
	    if (groupCount == 0) {
		((LearnerChoiceGrouper) grouping.getGrouper()).createGroups(learnerChoiceGrouping, 2);
		groupCount = grouping.getGroups().size();
		groupingDAO.update(grouping);
	    }
	    if (learnerChoiceGrouping.getEqualNumberOfLearnersPerGroup()) {
		maxNumberOfLearnersPerGroup = learnerCount / groupCount + (learnerCount % groupCount == 0 ? 0 : 1);
	    }
	} else {
	    maxNumberOfLearnersPerGroup = learnerChoiceGrouping.getLearnersPerGroup();
	    int desiredGroupCount = learnerCount / maxNumberOfLearnersPerGroup
		    + (learnerCount % maxNumberOfLearnersPerGroup == 0 ? 0 : 1);
	    if (desiredGroupCount > groupCount) {
		((LearnerChoiceGrouper) grouping.getGrouper()).createGroups(learnerChoiceGrouping, desiredGroupCount
			- groupCount);
		groupingDAO.update(grouping);
	    }
	}
	return maxNumberOfLearnersPerGroup;
    }

    @Override
    public Grouping getGrouping(Long groupingId) {
	return groupingDAO.getGroupingById(groupingId);
    }

    public void setGradebookService(IGradebookService gradebookService) {
	this.gradebookService = gradebookService;
    }

    public IDataFlowDAO getDataFlowDAO() {
	return dataFlowDAO;
    }

    public void setDataFlowDAO(IDataFlowDAO dataFlowDAO) {
	this.dataFlowDAO = dataFlowDAO;
    }

    /**
     * Gets the concreted tool output (not the definition) from a tool. This method is called by target tool in order to
     * get data from source tool.
     */
    @Override
    public ToolOutput getToolInput(Long requestingToolContentId, Integer assigmentId, Integer learnerId) {
	DataFlowObject dataFlowObject = getDataFlowDAO()
		.getAssignedDataFlowObject(requestingToolContentId, assigmentId);
	User learner = (User) getUserManagementService().findById(User.class, learnerId);
	Activity activity = dataFlowObject.getDataTransition().getFromActivity();
	String outputName = dataFlowObject.getName();
	ToolSession session = lamsCoreToolService.getToolSessionByLearner(learner, activity);
	ToolOutput output = lamsCoreToolService.getOutputFromTool(outputName, session, learnerId);

	return output;
    }

    /**
     * Finds activity position within Learning Design.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ActivityPositionDTO getActivityPosition(Long activityId) {
	if (activityId == null) {
	    return null;
	}
	Activity activity = getActivity(activityId);
	if (activity == null) {
	    return null;
	}

	ActivityPositionDTO result = new ActivityPositionDTO();
	// this is not really used at the moment, but can be useful in the future
	result.setActivityCount(activity.getLearningDesign().getActivities().size());
	Activity parentActivity = activity.getParentActivity();
	boolean isFirst = false;
	boolean isLast = false;

	if (parentActivity == null) {
	    // it's an activity in the main sequence
	    isFirst = activity.getTransitionTo() == null;
	    isLast = isActivityLast(activity);
	} else {
	    if (parentActivity.isSequenceActivity()) {
		// only parent's parent is the one in main sequence
		parentActivity = parentActivity.getParentActivity();
	    }

	    ActivityPositionDTO parentPosition = getActivityPosition(parentActivity.getActivityId());
	    if (parentPosition != null) {
		// looking for first-ness is easy
		isFirst = parentPosition.getFirst() && (activity.getTransitionTo() == null);

		// looking for last-ness
		if (parentActivity.isOptionsActivity()) {
		    if (parentPosition.getLast()) {
			// this is tricky: the activity is the last one only if parent is and after completing it,
			// there are no more optional activities to do
			// (for example, it's 4th out of 5 optional activities)
			OptionsActivity parentOptionsActivity = (OptionsActivity) getActivity(parentActivity
				.getActivityId());
			Integer learnerId = LearningWebUtil.getUserId();
			Lesson lesson = getLessonByActivity(activity);
			LearnerProgress learnerProgress = getProgress(learnerId, lesson.getLessonId());

			int completedSubactivities = 0;
			for (Activity subactivity : (Set<Activity>) parentOptionsActivity.getActivities()) {
			    if (LearnerProgress.ACTIVITY_COMPLETED == learnerProgress.getProgressState(subactivity)) {
				completedSubactivities++;
			    }
			}

			isLast = completedSubactivities == parentOptionsActivity.getMaxNumberOfOptionsNotNull() - 1;
		    }
		} else if (parentActivity.isBranchingActivity() || parentActivity.isParallelActivity()) {
		    isLast = parentPosition.getLast() && isActivityLast(activity);
		}
	    }
	}

	result.setFirst(isFirst);
	result.setLast(isLast);
	return result;
    }

    @Override
    public ActivityPositionDTO getActivityPositionByToolSessionId(Long toolSessionId) {
	ToolSession toolSession = lamsCoreToolService.getToolSessionById(toolSessionId);
	return toolSession == null ? null : getActivityPosition(toolSession.getToolActivity().getActivityId());
    }

    private boolean isActivityLast(Activity activity) {
	Transition transition = activity.getTransitionFrom();
	while (transition != null) {
	    Activity nextActivity = transition.getToActivity();
	    if (!nextActivity.isGateActivity()) {
		return false;
	    }
	    transition = nextActivity.getTransitionFrom();
	}
	return true;
    }
}