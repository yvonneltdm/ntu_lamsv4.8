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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */
package org.lamsfoundation.lams.web;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.tomcat.util.json.JSONArray;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.GroupComparator;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.monitoring.web.GroupingAJAXAction;
import org.lamsfoundation.lams.usermanagement.OrganisationGroup;
import org.lamsfoundation.lams.usermanagement.OrganisationGrouping;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.exception.UserAccessDeniedException;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.AlphanumComparator;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @struts.action path = "/OrganisationGroup" parameter = "method" validate = "false"
 * @struts.action-forward name = "viewGroupings" path = "/orgGrouping.jsp"
 * @struts.action-forward name = "viewGroups" path = "/orgGroup.jsp"
 */
public class OrganisationGroupAction extends DispatchAction {
    /**
     * Class for displaying data on groupings page.
     */
    public class OrganisationGroupingDTO implements Comparable<OrganisationGroupingDTO> {
	private Long groupingId;
	private String name;
	private Integer groupCount;

	public OrganisationGroupingDTO(OrganisationGrouping grouping) {
	    this.groupingId = grouping.getGroupingId();
	    this.name = grouping.getName();
	    this.groupCount = grouping.getGroups().size();
	}

	public Long getGroupingId() {
	    return groupingId;
	}

	public void setGroupingId(Long groupingId) {
	    this.groupingId = groupingId;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public Integer getGroupCount() {
	    return groupCount;
	}

	public void setGroupCount(Integer groupCount) {
	    this.groupCount = groupCount;
	}

	@Override
	public int compareTo(OrganisationGroupingDTO o) {
	    if (o == null) {
		return 1;
	    }
	    if (this.name == null) {
		return o.name == null ? 0 : 1;
	    }
	    return this.name.compareTo(o.name);
	}
    }

    private static final Comparator<OrganisationGroup> ORG_GROUP_COMPARATOR = new Comparator<OrganisationGroup>() {
	@Override
	public int compare(OrganisationGroup o1, OrganisationGroup o2) {
	    String grp1Name = o1 != null ? o1.getName() : "";
	    String grp2Name = o2 != null ? o2.getName() : "";

	    AlphanumComparator comparator = new AlphanumComparator();
	    return comparator.compare(grp1Name, grp2Name);
	}
    };

    private static Logger log = Logger.getLogger(OrganisationGroupAction.class);

    private static IUserManagementService userManagementService;
    private static ILessonService lessonService;

    private static final String MAPPING_VIEW_GROUPINGS = "viewGroupings";
    private static final String MAPPING_VIEW_GROUPS = "viewGroups";

    /**
     * Shows course grouping list or redirects to groups if a grouping was already chosen.
     */
    @SuppressWarnings("unchecked")
    public ActionForward viewGroupings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException {
	Long activityID = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID, true);
	boolean lessonGroupsExist = getLessonGrouping(request, activityID, false) != null;
	if (lessonGroupsExist) {
	    // this is lesson mode and user have already chosen a grouping before, so show it
	    return viewGroups(mapping, form, request, response);
	}

	Integer userId = getUserDTO().getUserID();
	Integer organisationId = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID, true);
	if (organisationId == null) {
	    // read organisation ID from lesson
	    Long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	    organisationId = ((Lesson) getUserManagementService().findById(Lesson.class, lessonId)).getOrganisation()
		    .getOrganisationId();
	}
	// check if user is allowed to view and edit groups
	boolean isGroupSuperuser = getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_ADMIN)
		|| getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER);
	if (!isGroupSuperuser && !getUserManagementService().isUserInRole(userId, organisationId, Role.AUTHOR)
		&& !getUserManagementService().isUserInRole(userId, organisationId, Role.MONITOR)) {
	    throw new UserAccessDeniedException("User " + userId + " may not view groupings for course "
		    + organisationId);
	}

	if (OrganisationGroupAction.log.isDebugEnabled()) {
	    OrganisationGroupAction.log.debug("Displaying course groupings for user " + userId + " and organisation "
		    + organisationId);
	}
	request.setAttribute(AttributeNames.PARAM_ORGANISATION_ID, organisationId);
	request.setAttribute("canEdit", isGroupSuperuser || (activityID != null));

	Set<OrganisationGroupingDTO> orgGroupingDTOs = new TreeSet<OrganisationGroupingDTO>();
	List<OrganisationGrouping> orgGroupings = getUserManagementService().findByProperty(OrganisationGrouping.class,
		"organisationId", organisationId);
	for (OrganisationGrouping orgGrouping : orgGroupings) {
	    orgGroupingDTOs.add(new OrganisationGroupingDTO(orgGrouping));
	}
	request.setAttribute("groupings", orgGroupingDTOs);

	return mapping.findForward(OrganisationGroupAction.MAPPING_VIEW_GROUPINGS);
    }

    /**
     * View groups of the given grouping.
     */
    @SuppressWarnings("unchecked")
    public ActionForward viewGroups(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException {
	Integer userId = getUserDTO().getUserID();
	Integer organisationId = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID, true);
	Long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID, true);
	Lesson lesson = null;
	if (organisationId == null) {
	    // read organisation ID from lesson
	    lesson = (Lesson) getUserManagementService().findById(Lesson.class, lessonId);
	    organisationId = lesson.getOrganisation().getOrganisationId();
	}

	// check if user is allowed to view and edit groups
	boolean isGroupSuperuser = getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_ADMIN)
		|| getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER);
	if (!isGroupSuperuser && !getUserManagementService().isUserInRole(userId, organisationId, Role.AUTHOR)
		&& !getUserManagementService().isUserInRole(userId, organisationId, Role.MONITOR)) {
	    throw new UserAccessDeniedException("User " + userId + " may not view groups for course " + organisationId);
	}

	if (OrganisationGroupAction.log.isDebugEnabled()) {
	    OrganisationGroupAction.log.debug("Displaying course groups for user " + userId + " and organisation "
		    + organisationId);
	}
	Long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID, true);
	request.setAttribute("canEdit", isGroupSuperuser || (activityId != null));

	JSONObject orgGroupingJSON = new JSONObject();
	orgGroupingJSON.put("organisationId", organisationId);

	Long orgGroupingId = WebUtil.readLongParam(request, "groupingId", true);
	OrganisationGrouping orgGrouping = null;
	// check if course grouping already exists or it is a new one
	if (orgGroupingId != null) {
	    orgGrouping = (OrganisationGrouping) getUserManagementService().findById(OrganisationGrouping.class,
		    orgGroupingId);
	    if (orgGrouping != null) {
		orgGroupingJSON.put("groupingId", orgGroupingId);
		orgGroupingJSON.put("name", orgGrouping.getName());
	    }
	}

	// check if any groups already exist in this grouping
	Grouping lessonGrouping = getLessonGrouping(request, activityId, true);
	Set<Group> lessonGroups = lessonGrouping == null ? null : lessonGrouping.getGroups();
	if ((activityId != null) && (orgGroupingId != null) && isDefaultChosenGrouping(lessonGrouping)) {
	    if (OrganisationGroupAction.log.isDebugEnabled()) {
		OrganisationGroupAction.log.debug("Removing default groups for grouping " + orgGroupingId);
	    }

	    Set<Long> groupIDs = new HashSet<Long>(lessonGroups.size());
	    for (Group group : lessonGroups) {
		groupIDs.add(group.getGroupId());
	    }
	    for (Long groupId : groupIDs) {
		getLessonService().removeGroup(lessonGrouping, groupId);
	    }

	    lessonGroups = null;
	}

	JSONArray orgGroupsJSON = null;
	Collection<User> learners = null;

	// select source for groups (course or lesson)
	if ((lessonGroups == null) || lessonGroups.isEmpty()) {
	    learners = getUserManagementService().getUsersFromOrganisationByRole(organisationId, Role.LEARNER, false,
		    true);
	    Set<OrganisationGroup> orgGroups = orgGrouping == null ? null : orgGrouping.getGroups();
	    orgGroupsJSON = getOrgGroupsDetails(orgGroups, learners);
	} else {
	    if (lesson == null) {
		lesson = (Lesson) getUserManagementService().findById(Lesson.class, lessonId);
	    }
	    learners = lesson.getLessonClass().getLearners();
	    orgGroupsJSON = getLessonGroupsDetails(lessonGroups, learners);
	    request.setAttribute("skipInitialAssigning", true);
	}
	orgGroupingJSON.put("groups", orgGroupsJSON);
	request.setAttribute("grouping", orgGroupingJSON);

	// all the remaining users are unassigned to any group
	JSONArray unassignedUsersJSON = new JSONArray();
	for (User unassignedUser : learners) {
	    JSONObject unassignedUserJSON = WebUtil.userToJSON(unassignedUser);
	    unassignedUsersJSON.put(unassignedUserJSON);
	}
	request.setAttribute("unassignedUsers", unassignedUsersJSON);

	return mapping.findForward(OrganisationGroupAction.MAPPING_VIEW_GROUPS);
    }

    /**
     * Saves a course grouping.
     * 
     * @throws InvalidParameterException
     */
    @SuppressWarnings("unchecked")
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException, InvalidParameterException {
	// check if user is allowed to edit groups
	Integer userId = getUserDTO().getUserID();
	int organisationId = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID);
	boolean isGroupSuperuser = getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_ADMIN)
		|| getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER);
	if (!isGroupSuperuser) {
	    throw new UserAccessDeniedException("User " + userId + " may not edit groups for course " + organisationId);
	}

	if (OrganisationGroupAction.log.isDebugEnabled()) {
	    OrganisationGroupAction.log.debug("Saving course groups for user " + userId + " and organisation "
		    + organisationId);
	}

	// deserialize grouping
	JSONObject orgGroupingJSON = new JSONObject(request.getParameter("grouping"));
	// check if already exists
	Long orgGroupingId = orgGroupingJSON.optLong("groupingId");
	if (orgGroupingId == 0L) {
	    orgGroupingId = null;
	}

	// iterate over groups
	List<OrganisationGroup> orgGroups = new LinkedList<OrganisationGroup>();
	JSONArray orgGroupsJSON = orgGroupingJSON.optJSONArray("groups");
	if (orgGroupsJSON != null) {
	    for (int i = 0; i < orgGroupsJSON.length(); i++) {
		// just overwrite existing groups; they will be updated if already exist
		Set<User> users = new HashSet<User>();
		JSONObject orgGroupJSON = orgGroupsJSON.getJSONObject(i);
		JSONArray usersJSON = orgGroupJSON.optJSONArray("users");
		if (usersJSON != null) {
		    // find user objects based on delivered IDs
		    for (int j = 0; j < usersJSON.length(); j++) {
			Integer learnerId = usersJSON.getInt(j);
			User user = (User) getUserManagementService().findById(User.class, learnerId);
			users.add(user);
		    }
		}

		OrganisationGroup orgGroup = new OrganisationGroup();
		Long orgGroupId = orgGroupJSON.optLong("groupId");
		if (orgGroupId > 0) {
		    orgGroup.setGroupId(orgGroupId);
		    orgGroup.setGroupingId(orgGroupingId);
		}
		orgGroup.setName(orgGroupJSON.optString("name", null));
		orgGroup.setUsers(users);

		orgGroups.add(orgGroup);
	    }
	}

	OrganisationGrouping orgGrouping = null;
	if (orgGroupingId != null) {
	    orgGrouping = (OrganisationGrouping) getUserManagementService().findById(OrganisationGrouping.class,
		    orgGroupingId);
	}
	if (orgGrouping == null) {
	    orgGrouping = new OrganisationGrouping();
	    orgGrouping.setOrganisationId(organisationId);
	}

	// check if there is no grouping with the same name
	String orgGroupingName = orgGroupingJSON.getString("name");
	if (!orgGroupingName.equals(orgGrouping.getName())) {
	    Map<String, Object> duplicateCheckProperties = new TreeMap<String, Object>();
	    duplicateCheckProperties.put("organisationId", organisationId);
	    duplicateCheckProperties.put("name", orgGroupingName);

	    List<OrganisationGrouping> duplicateOrgGrouping = getUserManagementService().findByProperties(
		    OrganisationGrouping.class, duplicateCheckProperties);
	    if (duplicateOrgGrouping.isEmpty()) {
		orgGrouping.setName(orgGroupingName);
	    } else {
		throw new InvalidParameterException("Grouping with name \"" + orgGroupingName + "\" already exists");
	    }

	}

	getUserManagementService().saveOrganisationGrouping(orgGrouping, orgGroups);
	return null;
    }

    /**
     * Deletes course grouping with the given ID.
     */
    public ActionForward removeGrouping(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException {
	// check if user is allowed to edit groups
	Integer userId = getUserDTO().getUserID();
	int organisationId = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID);
	boolean isGroupSuperuser = getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_ADMIN)
		|| getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER);
	if (!isGroupSuperuser) {
	    throw new UserAccessDeniedException("User " + userId + " may not remove groupings for course "
		    + organisationId);
	}

	Long groupingId = WebUtil.readLongParam(request, "groupingId");
	if (OrganisationGroupAction.log.isDebugEnabled()) {
	    OrganisationGroupAction.log.debug("Removing grouping " + groupingId + " for user " + userId
		    + " and organisation " + organisationId);
	}

	getUserManagementService().deleteById(OrganisationGrouping.class, groupingId);

	return viewGroupings(mapping, form, request, response);
    }

    /**
     * Build JSON objects based on existing lesson-level groups.
     */
    @SuppressWarnings("unchecked")
    private JSONArray getLessonGroupsDetails(Set<Group> groups, Collection<User> learners) throws JSONException {
	// serialize database group objects into JSON
	JSONArray groupsJSON = new JSONArray();
	if (groups != null) {
	    // sort groups by their name
	    List<Group> groupList = new LinkedList<Group>(groups);
	    Collections.sort(groupList, new GroupComparator());
	    for (Group group : groupList) {
		JSONObject groupJSON = new JSONObject();
		groupJSON.put("name", group.getGroupName());
		groupJSON.put("groupId", group.getGroupId());
		groupJSON.put("locked", !group.mayBeDeleted());
		if (group.getUsers() != null) {
		    for (User groupUser : (Set<User>) group.getUsers()) {
			JSONObject groupUserJSON = WebUtil.userToJSON(groupUser);
			groupJSON.append("users", groupUserJSON);

			// remove the user who is already assigned to a group
			learners.remove(groupUser);
		    }
		}
		groupsJSON.put(groupJSON);
	    }
	}

	return groupsJSON;
    }

    /**
     * Build JSON objects based on existing course-level groups.
     */
    private JSONArray getOrgGroupsDetails(Set<OrganisationGroup> groups, Collection<User> learners)
	    throws JSONException {
	// serialize database group objects into JSON
	JSONArray groupsJSON = new JSONArray();
	if (groups != null) {
	    // sort groups by their name
	    List<OrganisationGroup> groupList = new LinkedList<OrganisationGroup>(groups);
	    Collections.sort(groupList, OrganisationGroupAction.ORG_GROUP_COMPARATOR);

	    for (OrganisationGroup group : groupList) {
		JSONObject groupJSON = new JSONObject();
		groupJSON.put("name", group.getName());
		groupJSON.put("groupId", group.getGroupId());
		for (User groupUser : group.getUsers()) {
		    JSONObject groupUserJSON = WebUtil.userToJSON(groupUser);
		    groupJSON.append("users", groupUserJSON);

		    // remove the user who is already assigned to a group
		    learners.remove(groupUser);
		}

		groupsJSON.put(groupJSON);
	    }
	}

	return groupsJSON;
    }

    /**
     * Checks if lesson-level groups exist for the given activity.
     */
    @SuppressWarnings("unchecked")
    private Grouping getLessonGrouping(HttpServletRequest request, Long activityID, boolean allowDefault) {
	if (activityID != null) {
	    GroupingActivity groupingActivity = (GroupingActivity) getUserManagementService().findById(
		    GroupingActivity.class, activityID);
	    Grouping grouping = groupingActivity.getCreateGrouping();

	    if ((grouping != null) && (grouping.getGroups() != null)) {
		Set<Group> groups = grouping.getGroups();
		// not very obvious place to use it, but it made most sense
		boolean isUsedForBranching = grouping.isUsedForBranching();
		request.setAttribute(GroupingAJAXAction.PARAM_USED_FOR_BRANCHING, isUsedForBranching);

		// check if it is immutable (for branching) or default groups are allowed
		return !groups.isEmpty() && (isUsedForBranching || allowDefault || !isDefaultChosenGrouping(grouping)) ? grouping
			: null;
	    }
	}

	return null;
    }

    /**
     * Check if the give groups are default for chosen grouping. There is actually no good way to detect this, but even
     * if a custom grouping is mistaken for the default one, it should bring little harm.
     */
    @SuppressWarnings("unchecked")
    private boolean isDefaultChosenGrouping(Grouping grouping) {
	Set<Group> groups = grouping.getGroups();
	if ((groups == null) || (grouping.getMaxNumberOfGroups() == null)
		|| !grouping.getMaxNumberOfGroups().equals(groups.size())) {
	    return false;
	}
	for (Group group : groups) {
	    if (!group.getUsers().isEmpty()) {
		return false;
	    }
	}
	return true;
    }

    private UserDTO getUserDTO() {
	HttpSession ss = SessionManager.getSession();
	return (UserDTO) ss.getAttribute(AttributeNames.USER);
    }

    private IUserManagementService getUserManagementService() {
	if (OrganisationGroupAction.userManagementService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    OrganisationGroupAction.userManagementService = (IUserManagementService) ctx
		    .getBean("userManagementService");
	}
	return OrganisationGroupAction.userManagementService;
    }

    private ILessonService getLessonService() {
	if (OrganisationGroupAction.lessonService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    OrganisationGroupAction.lessonService = (ILessonService) ctx.getBean("lessonService");
	}
	return OrganisationGroupAction.lessonService;
    }
}