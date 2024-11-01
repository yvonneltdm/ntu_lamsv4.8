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

package org.lamsfoundation.lams.tool.dokumaran.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netscape.javascript.JSObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.etherpad.EtherpadException;
import org.lamsfoundation.lams.flux.FluxRegistry;
import org.lamsfoundation.lams.gradebook.GradebookUserActivity;
import org.lamsfoundation.lams.gradebook.service.IGradebookService;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.security.ISecurityService;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.dokumaran.DokumaranConstants;
import org.lamsfoundation.lams.tool.dokumaran.dto.SessionDTO;
import org.lamsfoundation.lams.tool.dokumaran.model.Dokumaran;
import org.lamsfoundation.lams.tool.dokumaran.model.DokumaranSession;
import org.lamsfoundation.lams.tool.dokumaran.model.DokumaranUser;
import org.lamsfoundation.lams.tool.dokumaran.service.IDokumaranService;
import org.lamsfoundation.lams.tool.service.ILamsCoreToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.CommonConstants;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    public static Logger log = Logger.getLogger(MonitoringController.class);

    public static final int LEARNER_MARKS_SORTING_FULL_NAME_ASC = 0;
    public static final int LEARNER_MARKS_SORTING_FULL_NAME_DESC = 1;

    private static final String AI_REVIEW_TEMPLATE_INSTRUCTIONS = "The instructions are:";
    private static final String AI_REVIEW_TEMPLATE_DESCRIPTION = "The answer they are writing should address the following:";

    @Autowired
    private IDokumaranService dokumaranService;

    @Autowired
    private IUserManagementService userManagementService;

    @Autowired
    private IGradebookService gradebookService;

    @Autowired
    private ILessonService lessonService;

    @Autowired
    @Qualifier("lamsCoreToolService")
    private ILamsCoreToolService toolService;

    @Autowired
    private ISecurityService securityService;

    @Autowired
    @Qualifier("dokumaranMessageService")
    private MessageService messageService;

    @RequestMapping("/summary")
    private String summary(HttpServletRequest request, HttpServletResponse response) throws EtherpadException {
	// initial Session Map
	SessionMap<String, Object> sessionMap = new SessionMap<>();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
	request.setAttribute(DokumaranConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	// save contentFolderID into session
	sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID,
		WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID, true));

	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	List<SessionDTO> groupList = dokumaranService.getSummary(contentId, null);
	boolean hasFaultySession = false;
	int attemptedLearnersNumber = 0;
	for (SessionDTO group : groupList) {
	    hasFaultySession |= group.isSessionFaulty();
	    attemptedLearnersNumber += group.getNumberOfLearners();
	}

	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(contentId);

	//set SubmissionDeadline, if any
	if (dokumaran.getSubmissionDeadline() != null) {
	    Date submissionDeadline = dokumaran.getSubmissionDeadline();
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    Date tzSubmissionDeadline = DateUtil.convertToTimeZoneFromDefault(teacherTimeZone, submissionDeadline);
	    request.setAttribute(DokumaranConstants.ATTR_SUBMISSION_DEADLINE, tzSubmissionDeadline.getTime());
	    // use the unconverted time, as convertToStringForJSON() does the timezone conversion if needed
	    request.setAttribute(DokumaranConstants.ATTR_SUBMISSION_DEADLINE_DATESTRING,
		    DateUtil.convertToStringForJSON(submissionDeadline, request.getLocale()));
	}

	// cache into sessionMap
	sessionMap.put(DokumaranConstants.ATTR_SUMMARY_LIST, groupList);
	sessionMap.put(DokumaranConstants.ATTR_HAS_FAULTY_SESSION, hasFaultySession);
	sessionMap.put(DokumaranConstants.PAGE_EDITABLE, dokumaran.isContentInUse());
	sessionMap.put(DokumaranConstants.ATTR_DOKUMARAN, dokumaran);
	sessionMap.put(DokumaranConstants.ATTR_TOOL_CONTENT_ID, contentId);
	sessionMap.put(DokumaranConstants.ATTR_IS_GROUPED_ACTIVITY, dokumaranService.isGroupedActivity(contentId));
	request.setAttribute("attemptedLearnersNumber", attemptedLearnersNumber);

	// get the API key from the config table and add it to the session
	String etherpadServerUrl = Configuration.get(ConfigurationKeys.ETHERPAD_SERVER_URL);
	String etherpadApiKey = Configuration.get(ConfigurationKeys.ETHERPAD_API_KEY);
	if (StringUtils.isBlank(etherpadServerUrl) || StringUtils.isBlank(etherpadApiKey)) {
	    return "pages/learning/notconfigured";
	}
	request.setAttribute(DokumaranConstants.KEY_ETHERPAD_SERVER_URL, etherpadServerUrl);

	HttpSession ss = SessionManager.getSession();
	// get back login user DTO
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);

	//no need to store cookie if there are no sessions created yet
	if (!groupList.isEmpty()) {
	    // add new sessionID cookie in order to access pad
	    dokumaranService.createEtherpadCookieForMonitor(user, contentId, response);
	}

	boolean isAiEnabled = Configuration.isLamsModuleAvailable(Configuration.AI_MODULE_CLASS);
	request.setAttribute("isAiEnabled", isAiEnabled);

	return "pages/monitoring/monitoring";
    }

    @RequestMapping("/getLearnerMarks")
    @ResponseBody
    private String getLearnerMarks(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	Long toolSessionId = WebUtil.readLongParam(request, "toolSessionId");

	// paging parameters of tablesorter
	int size = WebUtil.readIntParam(request, "size");
	int page = WebUtil.readIntParam(request, "page");
	Integer isSortFullName = WebUtil.readIntParam(request, "column[0]", true);

	// identify sorting type
	int sorting = LEARNER_MARKS_SORTING_FULL_NAME_ASC;
	if (isSortFullName != null && isSortFullName.equals(1)) {
	    sorting = LEARNER_MARKS_SORTING_FULL_NAME_DESC;
	}

	// get all session users and sort them according to the parameter from tablesorter
	List<DokumaranUser> users = dokumaranService.getUsersBySession(toolSessionId).stream()
		.sorted(Comparator.comparing(sorting <= 1 ? DokumaranUser::getFirstName : DokumaranUser::getLastName))
		.collect(Collectors.toList());

	ObjectNode responsedata = JsonNodeFactory.instance.objectNode();
	if (!users.isEmpty()) {
	    // reverse if sorting is descending
	    if (sorting == LEARNER_MARKS_SORTING_FULL_NAME_DESC) {
		Collections.reverse(users);
	    }

	    // paging
	    int endIndex = (page + 1) * size;
	    users = users.subList(page * size, users.size() > endIndex ? endIndex : users.size());

	    ArrayNode rows = JsonNodeFactory.instance.arrayNode();

	    responsedata.put("total_rows", users.size());

	    ToolSession toolSession = toolService.getToolSessionById(toolSessionId);
	    Map<Integer, Double> gradebookUserActivities = gradebookService.getGradebookUserActivities(
			    toolSession.getToolActivity().getActivityId()).stream().filter(g -> g.getMark() != null)
		    .collect(Collectors.toMap(g -> g.getLearner().getUserId(), GradebookUserActivity::getMark));

	    DokumaranUser leader = users.get(0).getSession().getGroupLeader();
	    for (DokumaranUser user : users) {
		ObjectNode responseRow = JsonNodeFactory.instance.objectNode();

		responseRow.put("userId", user.getUserId());
		responseRow.put("firstName", user.getFirstName());
		responseRow.put("lastName", user.getLastName());
		Double mark = gradebookUserActivities.get(user.getUserId().intValue());
		responseRow.put("mark", mark == null ? "" : String.valueOf(mark));
		responseRow.put("isLeader", leader != null && leader.getUid().equals(user.getUid()));

		rows.add(responseRow);
	    }
	    responsedata.set("rows", rows);
	}

	response.setContentType("application/json;charset=utf-8");
	return responsedata.toString();
    }

    @RequestMapping(path = "/updateLearnerMark", method = RequestMethod.POST)
    @ResponseBody
    private void updateLearnerMark(@RequestParam long toolSessionId, @RequestParam int userId,
	    @RequestParam Double mark) {
	ToolSession toolSession = toolService.getToolSessionById(toolSessionId);
	long lessonId = toolSession.getLesson().getLessonId();
	securityService.ensureLessonMonitor(lessonId, getUserId(), "update Doku learner mark");

	gradebookService.updateGradebookUserActivityMark(mark, null, userId, toolSessionId, true);

    }

    @RequestMapping("/fixFaultySession")
    private void fixFaultySession(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	Long toolSessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
	DokumaranSession session = dokumaranService.getDokumaranSessionBySessionId(toolSessionId);

	try {
	    log.debug("Fixing faulty session (sessionId=" + toolSessionId + ").");
	    dokumaranService.createPad(session.getDokumaran(), session);

	} catch (Exception e) {
	    // printing out error cause
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    out.write("Failed! " + e.getMessage());
	    out.flush();
	    out.close();
	    log.error("Failed! " + e.getMessage());
	}

    }

    @RequestMapping(path = "/displayChangeLeaderForGroupDialogFromActivity")
    public String displayChangeLeaderForGroupDialogFromActivity(
	    @RequestParam(name = AttributeNames.PARAM_TOOL_SESSION_ID) long toolSessionId) {
	// tell Change Leader dialog in Leader Selection tool which learner has already reached this activity
	String availableLearners = dokumaranService.getUsersBySession(toolSessionId).stream()
		.collect(Collectors.mapping(user -> Long.toString(user.getUserId()), Collectors.joining(",")));

	return new StringBuilder("redirect:").append(Configuration.get(ConfigurationKeys.SERVER_URL))
		.append("tool/lalead11/monitoring/displayChangeLeaderForGroupDialogFromActivity.do?toolSessionId=")
		.append(toolSessionId).append("&availableLearners=").append(availableLearners).toString();
    }

    @RequestMapping(path = "/changeLeaderForGroup", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void changeLeaderForGroup(@RequestParam(name = AttributeNames.PARAM_TOOL_SESSION_ID) long toolSessionId,
	    @RequestParam long leaderUserId) {
	dokumaranService.changeLeaderForGroup(toolSessionId, leaderUserId);
    }

    @RequestMapping("/startGalleryWalk")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    private void startGalleryWalk(HttpServletRequest request) throws IOException {
	Long toolContentId = WebUtil.readLongParam(request, DokumaranConstants.ATTR_TOOL_CONTENT_ID, false);

	dokumaranService.startGalleryWalk(toolContentId);

	updateTimeLimit(toolContentId, 0, 0, null);
    }

    @RequestMapping("/skipGalleryWalk")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    private void skipGalleryWalk(HttpServletRequest request) throws IOException {
	Long toolContentId = WebUtil.readLongParam(request, DokumaranConstants.ATTR_TOOL_CONTENT_ID, false);

	dokumaranService.skipGalleryWalk(toolContentId);
    }

    @RequestMapping("/finishGalleryWalk")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    private void finishGalleryWalk(HttpServletRequest request) throws IOException {
	Long toolContentId = WebUtil.readLongParam(request, DokumaranConstants.ATTR_TOOL_CONTENT_ID, false);

	dokumaranService.finishGalleryWalk(toolContentId);
    }

    @RequestMapping("/enableGalleryWalkLearnerEdit")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    private void enableGalleryWalkLearnerEdit(HttpServletRequest request) throws IOException {
	Long toolContentId = WebUtil.readLongParam(request, DokumaranConstants.ATTR_TOOL_CONTENT_ID, false);

	dokumaranService.enableGalleryWalkLearnerEdit(toolContentId);
    }

    @RequestMapping("/showGalleryWalkClusters")
    private String showGalleryWalkClusters(
	    @RequestParam(name = AttributeNames.PARAM_TOOL_CONTENT_ID) long toolContentId, Model model) {
	Map<String, Set<String>> groups = dokumaranService.getDokumaranSessionsByToolContentId(toolContentId).stream()
		.collect(Collectors.toMap(DokumaranSession::getSessionName,
			session -> session.getGalleryWalkCluster().stream().collect(
				Collectors.mapping(DokumaranSession::getSessionName, Collectors.toCollection(
					() -> new TreeSet<>(DokumaranSession.SESSION_NAME_COMPARATOR)))),
			(session1Cluster, session2Cluster) -> session1Cluster,
			() -> new TreeMap<>(DokumaranSession.SESSION_NAME_COMPARATOR)));
	model.addAttribute("groups", groups);
	return "pages/monitoring/viewGalleryWalkClusters";
    }

    @RequestMapping("/ae")
    private String tblApplicationExcercise(HttpServletRequest request, HttpServletResponse response)
	    throws EtherpadException {
	summary(request, response);
	request.setAttribute("isTbl", true);
	return "pages/monitoring/summary5";
    }

    @RequestMapping(path = "/getTimeLimitPanelUpdateFlux", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> getTimeLimitPanelUpdateFlux(@RequestParam long toolContentId, HttpServletResponse response) {
	response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
	return FluxRegistry.get(DokumaranConstants.TIME_LIMIT_PANEL_UPDATE_FLUX_NAME, toolContentId);
    }

    @RequestMapping(path = "/updateTimeLimit", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateTimeLimit(@RequestParam(name = AttributeNames.PARAM_TOOL_CONTENT_ID) long toolContentId,
	    @RequestParam int relativeTimeLimit, @RequestParam int absoluteTimeLimit,
	    @RequestParam(required = false) Long absoluteTimeLimitFinish) {
	if (relativeTimeLimit < 0) {
	    throw new InvalidParameterException(
		    "Relative time limit must not be negative and it is " + relativeTimeLimit);
	}
	if (absoluteTimeLimit < 0) {
	    throw new InvalidParameterException(
		    "Absolute time limit must not be negative and it is " + relativeTimeLimit);
	}
	if (absoluteTimeLimitFinish != null && relativeTimeLimit != 0) {
	    throw new InvalidParameterException(
		    "Relative time limit must not be provided when absolute time limit is set");
	}

	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	dokumaran.setRelativeTimeLimit(relativeTimeLimit);
	dokumaran.setAbsoluteTimeLimit(absoluteTimeLimit);
	// set time limit as seconds from start of epoch, using current server time zone
	dokumaran.setAbsoluteTimeLimitFinish(absoluteTimeLimitFinish == null
		? null
		: LocalDateTime.ofEpochSecond(absoluteTimeLimitFinish, 0, OffsetDateTime.now().getOffset()));

	// update monitoring UI where time limits are reflected on dashboard
	FluxRegistry.emit(CommonConstants.ACTIVITY_TIME_LIMIT_CHANGED_SINK_NAME, Set.of(toolContentId));
	FluxRegistry.emit(DokumaranConstants.TIME_LIMIT_PANEL_UPDATE_SINK_NAME, toolContentId);
	dokumaranService.saveOrUpdate(dokumaran);
    }

    @RequestMapping(path = "/getPossibleIndividualTimeLimits", method = RequestMethod.GET)
    @ResponseBody
    public String getPossibleIndividualTimeLimits(
	    @RequestParam(name = AttributeNames.PARAM_TOOL_CONTENT_ID) long toolContentId,
	    @RequestParam(name = "term") String searchString) {
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	Map<Integer, Integer> timeLimitAdjustments = dokumaran.getTimeLimitAdjustments();

	List<User> users = dokumaranService.getPossibleIndividualTimeLimitUsers(toolContentId, searchString);
	Grouping grouping = dokumaranService.getGrouping(toolContentId);

	ArrayNode responseJSON = JsonNodeFactory.instance.arrayNode();
	String groupLabel = messageService.getMessage("monitoring.label.group") + " \"";
	if (grouping != null) {
	    Set<Group> groups = grouping.getGroups();
	    for (Group group : groups) {
		if (!group.getUsers().isEmpty() && group.getGroupName().toLowerCase()
			.contains(searchString.toLowerCase())) {
		    ObjectNode groupJSON = JsonNodeFactory.instance.objectNode();
		    groupJSON.put("label", groupLabel + group.getGroupName() + "\"");
		    groupJSON.put("value", "group-" + group.getGroupId());
		    responseJSON.add(groupJSON);
		}
	    }
	}

	for (User user : users) {
	    if (!timeLimitAdjustments.containsKey(user.getUserId())) {
		// this format is required by jQuery UI autocomplete
		ObjectNode userJSON = JsonNodeFactory.instance.objectNode();
		userJSON.put("value", "user-" + user.getUserId());

		String name = user.getFullName() + " (" + user.getLogin() + ")";
		if (grouping != null) {
		    Group group = grouping.getGroupBy(user);
		    if (group != null && !group.isNull()) {
			name += " - " + group.getGroupName();
		    }
		}

		userJSON.put("label", name);
		responseJSON.add(userJSON);
	    }
	}
	return responseJSON.toString();
    }

    @RequestMapping(path = "/getExistingIndividualTimeLimits", method = RequestMethod.GET)
    @ResponseBody
    public String getExistingIndividualTimeLimits(
	    @RequestParam(name = AttributeNames.PARAM_TOOL_CONTENT_ID) long toolContentId) {
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	Map<Integer, Integer> timeLimitAdjustments = dokumaran.getTimeLimitAdjustments();
	Grouping grouping = dokumaranService.getGrouping(toolContentId);
	// find User objects based on their userIDs and sort by name
	List<User> users = timeLimitAdjustments.keySet().stream()
		.map(userId -> userManagementService.getUserById(userId)).sorted().collect(Collectors.toList());

	if (grouping != null) {
	    // Make a map group -> its users who have a time limit set
	    // key are sorted by group name, users in each group are sorted by name
	    List<User> groupedUsers = grouping.getGroups().stream()
		    .collect(Collectors.toMap(Group::getGroupName, group -> {
			return group.getUsers().stream()
				.filter(user -> timeLimitAdjustments.containsKey(user.getUserId()))
				.collect(Collectors.toCollection(() -> new TreeSet<>()));
		    }, (s1, s2) -> {
			s1.addAll(s2);
			return s1;
		    }, TreeMap::new)).values().stream().flatMap(Set::stream).collect(Collectors.toList());

	    // from general user list remove grouped users
	    users.removeAll(groupedUsers);
	    // at the end of list, add remaining, not yet grouped users
	    groupedUsers.addAll(users);
	    users = groupedUsers;
	}

	ArrayNode responseJSON = JsonNodeFactory.instance.arrayNode();
	for (User user : users) {
	    ObjectNode userJSON = JsonNodeFactory.instance.objectNode();
	    userJSON.put("userId", user.getUserId());
	    userJSON.put("adjustment", timeLimitAdjustments.get(user.getUserId().intValue()));

	    String name = user.getFullName() + " (" + user.getLogin() + ")";
	    if (grouping != null) {
		Group group = grouping.getGroupBy(user);
		if (group != null && !group.isNull()) {
		    name += " - " + group.getGroupName();
		}
	    }
	    userJSON.put("name", name);

	    DokumaranUser dokumaranUser = dokumaranService.getLearnerByIDAndContent(user.getUserId().longValue(),
		    toolContentId);
	    userJSON.put("finished", dokumaranUser != null && dokumaranUser.isSessionFinished());

	    responseJSON.add(userJSON);
	}
	return responseJSON.toString();
    }

    @RequestMapping(path = "/updateIndividualTimeLimit", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateIndividualTimeLimit(@RequestParam(name = AttributeNames.PARAM_TOOL_CONTENT_ID) long toolContentId,
	    @RequestParam String itemId, @RequestParam(required = false) Integer adjustment) {
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	Map<Integer, Integer> timeLimitAdjustments = dokumaran.getTimeLimitAdjustments();
	Set<Integer> userIds = null;

	// itemId can user-<userId> or group-<groupId>
	String[] itemIdParts = itemId.split("-");
	if (itemIdParts[0].equalsIgnoreCase("group")) {
	    // add all users from a group, except for ones who are already added
	    Group group = (Group) userManagementService.findById(Group.class, Long.valueOf(itemIdParts[1]));
	    userIds = group.getUsers().stream().map(User::getUserId)
		    .filter(userId -> !timeLimitAdjustments.containsKey(userId)).collect(Collectors.toSet());
	} else {
	    // adjust for a single user
	    userIds = new HashSet<>();
	    userIds.add(Integer.valueOf(itemIdParts[1]));
	}

	for (Integer userId : userIds) {
	    if (adjustment == null) {
		timeLimitAdjustments.remove(userId);
	    } else {
		timeLimitAdjustments.put(userId, adjustment);
	    }
	}
	dokumaranService.saveOrUpdate(dokumaran);

	FluxRegistry.emit(DokumaranConstants.TIME_LIMIT_PANEL_UPDATE_SINK_NAME, toolContentId);
    }

    /**
     * Set Submission Deadline
     */
    @RequestMapping(path = "/setSubmissionDeadline", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setSubmissionDeadline(HttpServletRequest request) {
	Long contentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(contentID);

	Long dateParameter = WebUtil.readLongParam(request, DokumaranConstants.ATTR_SUBMISSION_DEADLINE, true);
	Date tzSubmissionDeadline = null;
	String formattedDate = "";
	if (dateParameter != null) {
	    Date submissionDeadline = new Date(dateParameter);
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    tzSubmissionDeadline = DateUtil.convertFromTimeZoneToDefault(teacherTimeZone, submissionDeadline);
	    formattedDate = DateUtil.convertToStringForJSON(tzSubmissionDeadline, request.getLocale());

	}
	dokumaran.setSubmissionDeadline(tzSubmissionDeadline);
	dokumaranService.saveOrUpdate(dokumaran);

	return formattedDate;
    }

    @RequestMapping(path = "/getAiReviewPromptData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAiReviewPromptData(@RequestParam Long toolSessionId) {
	boolean isAiEnabled = Configuration.isLamsModuleAvailable(Configuration.AI_MODULE_CLASS);
	if (!isAiEnabled) {
	    throw new UnsupportedOperationException("AI module is not enabled");
	}
	DokumaranSession session = dokumaranService.getDokumaranSessionBySessionId(toolSessionId);
	Dokumaran dokumaran = session.getDokumaran();
	ObjectNode responseJSON = getAiReviewPromptData(dokumaran);
	try {
	    String padContent = dokumaranService.getPadText(toolSessionId);
	    if (StringUtils.isNotBlank(padContent)) {
		responseJSON.put("content", "\n" + padContent);
	    }
	} catch (EtherpadException e) {
	    log.error("Failed to get pad content for session " + toolSessionId, e);
	    return null;
	}
	return responseJSON.toString();
    }

    @RequestMapping(path = "/getAiLearningOutcomesPromptData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAiLearningOutcomesPromptData(@RequestParam Long toolContentId) {
	boolean isAiEnabled = Configuration.isLamsModuleAvailable(Configuration.AI_MODULE_CLASS);
	if (!isAiEnabled) {
	    throw new UnsupportedOperationException("AI module is not enabled");
	}
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	ObjectNode responseJSON = getAiReviewPromptData(dokumaran);

	Lesson lesson = lessonService.getLessonByToolContentId(toolContentId);
	LearningDesign learningDesign = lesson == null ? null : lesson.getLearningDesign();
	String lessonDescription = learningDesign != null && StringUtils.isNotBlank(learningDesign.getDescription())
		? learningDesign.getDescription().strip()
		: null;
	if (lessonDescription != null) {
	    responseJSON.put("lessonDescription", lessonDescription);
	}

	return responseJSON.toString();
    }

    @RequestMapping(path = "/saveAiReview", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void saveAiReview(@RequestParam Long toolSessionId, @RequestParam(required = false) String review) {
	boolean isAiEnabled = Configuration.isLamsModuleAvailable(Configuration.AI_MODULE_CLASS);
	if (!isAiEnabled) {
	    throw new UnsupportedOperationException("AI module is not enabled");
	}
	DokumaranSession session = dokumaranService.getDokumaranSessionBySessionId(toolSessionId);
	if (StringUtils.isBlank(review)) {
	    review = null;
	}
	session.setAiReview(review);
	dokumaranService.saveOrUpdate(session);
    }

    @RequestMapping(path = "/saveAiLearningOutcomes", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void saveAiLearningOutcomes(@RequestParam Long toolContentId,
	    @RequestParam(required = false) String learningOutcomes) {
	boolean isAiEnabled = Configuration.isLamsModuleAvailable(Configuration.AI_MODULE_CLASS);
	if (!isAiEnabled) {
	    throw new UnsupportedOperationException("AI module is not enabled");
	}
	Dokumaran dokumaran = dokumaranService.getDokumaranByContentId(toolContentId);
	if (StringUtils.isBlank(learningOutcomes)) {
	    learningOutcomes = null;
	}
	dokumaran.setAiLearningOutcomes(learningOutcomes);
	dokumaranService.saveOrUpdate(dokumaran);
    }

    private ObjectNode getAiReviewPromptData(Dokumaran dokumaran) {
	ObjectNode responseJSON = JsonNodeFactory.instance.objectNode();
	if (StringUtils.isNotBlank(dokumaran.getInstructions())) {
	    responseJSON.put("instructions",
		    new StringBuilder("\n").append(AI_REVIEW_TEMPLATE_INSTRUCTIONS).append("\n")
			    .append(dokumaran.getInstructions()).append("\n\n").toString());
	}
	if (StringUtils.isNotBlank(dokumaran.getDescription())) {
	    responseJSON.put("description", new StringBuilder("\n").append(AI_REVIEW_TEMPLATE_DESCRIPTION).append("\n")
		    .append(dokumaran.getDescription()).append("\n\n").toString());
	}
	return responseJSON;
    }

    private Integer getUserId() {
	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	return user != null ? user.getUserID() : null;
    }
}