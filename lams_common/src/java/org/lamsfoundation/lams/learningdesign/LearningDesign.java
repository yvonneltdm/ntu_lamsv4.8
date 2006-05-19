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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.learningdesign.dto.DesignDetailDTO;
import org.lamsfoundation.lams.learningdesign.dto.LearningDesignDTO;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.WorkspaceFolder;
import org.lamsfoundation.lams.workspace.dto.FolderContentDTO;


/**
 * @author Manpreet Minhas
 * @hibernate.class table="lams_learning_design"
 *  
 */
public class LearningDesign implements Serializable {

	/** Represents a single LearningDesign object in the WDDXPacket */
	public static final String DESIGN_OBJECT = "LearningDesign";

	/** Represents a list of LearningDesign objects in the WDDXPacket */
	public static final String DESIGN_LIST_OBJECT = "LearningDesignList";
	
	/** Represents a copy of LearningDesign for authoring enviornment */
	public static final int COPY_TYPE_NONE =1;
	
	/** Represents a copy of LearningDesign for monitoring enviornment */
	public static final int COPY_TYPE_LESSON=2;
	
	/** Represents a copy of LearningDesign for preview purposes */
	public static final int COPY_TYPE_PREVIEW=3;

	/** identifier field */
	private Long learningDesignId;

	/** nullable persistent field */
	private Integer learningDesignUIID;

	/** nullable persistent field */
	private String description;

	/** nullable persistent field */
	private String title; 

	/** nullable persistent field */
	private Activity firstActivity;

	/** nullable persistent field */
	private Integer maxID;

	/** persistent field */
	private Boolean validDesign;

	/** persistent field */
	private Boolean readOnly;

	/** nullable persistent field */
	private Date dateReadOnly;

	/** nullable persistent field */
	private String helpText;

	/** persistent field */
	private Integer copyTypeID;

	/** persistent field */
	private Date createDateTime;

	/** persistent field */
	private String version;

	/** persistent field */
	private User user;

	/** persistent field */
	private LearningDesign originalLearningDesign;

	/** persistent field */
	private Set childLearningDesigns;

	/** persistent field */
	private Set lessons;

	/** persistent field */
	private Set transitions;

	/** persistent field */
	private Set activities;

	/** persistent field */
	private WorkspaceFolder workspaceFolder;
	
	/** persistent field */
	private Long duration;
	
	/** nullable persistent field */
	private String licenseText;
	
	/** nullable persistent field */
	private License license;
	
	/** nullable persistent field*/
	private Long lessonOrgID;
	
	/** nullable persistent field*/
	private String lessonOrgName;
	
	/** nullable persistent field*/
	private Long lessonID;
	
	/** nullable persistent field*/
	private String lessonName;
	
	/** nullable persistent field*/
	private Date lessonStartDateTime;
	
	/** persistent field*/
	private Date lastModifiedDateTime;
	
	/** Offline Instruction for this activity*/
	private String offlineInstructions;
	
	 /** Online Instructions for this activity*/
    private String onlineInstructions;
	

    /* If the values for createDateTime and/or lastModifiedDateTime
     * are null, then it will default to the current datetime.
     */
    
	/** full constructor */
	public LearningDesign(
			Long learningDesignId,
			Integer ui_id,
			String description,
			String title,
			Activity firstActivity,
			Integer maxID,
			Boolean validDesign,
			Boolean readOnly,
			Date dateReadOnly,
			String helpText,
			Integer copyTypeID,
			Date createDateTime,
			String version,
			User user,
			LearningDesign originalLearningDesign,
			Set childLearningDesigns, Set lessons, Set transitions,
			SortedSet activities,
			Long duration,
			String licenseText,
			License license,
			String offlineInstructions,
			String onlineInstructions) {
		this.learningDesignId = learningDesignId;
		this.learningDesignUIID = ui_id;
		this.description = description;
		this.title = title;
		this.firstActivity = firstActivity;
		this.maxID = maxID;
		this.validDesign = validDesign;
		this.readOnly = readOnly;
		this.dateReadOnly = dateReadOnly;
		this.helpText = helpText;
		this.copyTypeID = copyTypeID;
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
		this.version = version;
		this.user = user;
		this.originalLearningDesign = originalLearningDesign;
		this.childLearningDesigns = childLearningDesigns;
		this.lessons = lessons;
		this.transitions = transitions;
		this.activities = activities;
		this.duration = duration;
		this.licenseText = licenseText;
		this.license = license;
		this.offlineInstructions = offlineInstructions;
		this.onlineInstructions = onlineInstructions;
		this.lastModifiedDateTime = new Date();
	}

	/** default constructor */
	public LearningDesign() {
	    //set the default values to the current datetime
	    this.createDateTime = new Date();
	    this.lastModifiedDateTime = new Date();
	}

	/** minimal constructor */
	public LearningDesign(
			Long learningDesignId,
			Boolean validDesign,
			Boolean readOnly,
			Integer copyTypeID,
			Date createDateTime,
			String version,
			User user,
			org.lamsfoundation.lams.learningdesign.LearningDesign originalLearningDesign,
			Set childLearningDesigns, Set lessons, Set transitions,
			SortedSet activities) {
		this.learningDesignId = learningDesignId;
		this.validDesign = validDesign;
		this.readOnly = readOnly;
		this.copyTypeID = copyTypeID;
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
		this.version = version;
		this.user = user;
		this.originalLearningDesign = originalLearningDesign;
		this.childLearningDesigns = childLearningDesigns;
		this.lessons = lessons;
		this.transitions = transitions;
		this.activities = activities;
		this.lastModifiedDateTime = new Date();
	}
	/** Create a new learning design based on an existing learning design. If setOriginalDesign is true, then set the input design
	 * as the original design in the copied design - this is used when runtime copies of a design are created. It is not used
	 * for user based copying of a design. 
	 * @param design Design to be copied
	 * @param designCopyType COPY_TYPE_NONE, COPY_TYPE_LESSON, COPY_TYPE_PREVIEW
	 * @param setOriginalDesign should we set the originalLearningDesign field.
	 * @return
	 */ 
	public static LearningDesign createLearningDesignCopy(LearningDesign design, Integer designCopyType, boolean setOriginalDesign){
		LearningDesign newDesign = new LearningDesign();		
		newDesign.setDescription(design.getDescription());
		newDesign.setTitle(design.getTitle());		
		newDesign.setMaxID(design.getMaxID());
		newDesign.setValidDesign(design.getValidDesign());		
		newDesign.setDateReadOnly(design.getDateReadOnly());
		newDesign.setHelpText(design.getHelpText());
		newDesign.setVersion(design.getVersion());
		newDesign.setCreateDateTime(new Date());
		newDesign.setDuration(design.getDuration());
		newDesign.setLicense(design.getLicense());
		newDesign.setLicenseText(design.getLicenseText());
		newDesign.setLastModifiedDateTime(new Date());
				
		// is this really right? why are preview set to read only when we will delete them? do we set them to not read only later?
		if(designCopyType.intValue()!=COPY_TYPE_NONE)
			newDesign.setReadOnly(new Boolean(true));
		else
			newDesign.setReadOnly(new Boolean(false));
		
		if ( setOriginalDesign ) 
			newDesign.setOriginalLearningDesign(design);
		
		newDesign.setCopyTypeID(designCopyType);		
		return newDesign;
	}	

	public Long getLearningDesignId() {
		return this.learningDesignId;
	}

	public void setLearningDesignId(Long learningDesignId) {
		this.learningDesignId = learningDesignId;
	}
	public Integer getLearningDesignUIID() {
		return this.learningDesignUIID;		
	}
	public void setLearningDesignUIID(Integer id) {
		this.learningDesignUIID = id;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Activity getFirstActivity() {
		return this.firstActivity;
	}
	public void setFirstActivity(Activity firstActivity) {
		this.firstActivity = firstActivity;
	}
	public Integer getMaxID() {
		return maxID;
	}
	public void setMaxID(Integer maxID) {
		this.maxID = maxID;
	}
	public Boolean getValidDesign() {
		return validDesign;
	}
	public void setValidDesign(Boolean validDesign) {
		this.validDesign = validDesign;
	}
	public Boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	public Date getDateReadOnly() {
		return dateReadOnly;
	}
	public void setDateReadOnly(Date dateReadOnly) {
		this.dateReadOnly = dateReadOnly;
	}
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	public Date getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public User getUser() {
		return this.user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	/** If this is a lesson type of learning design, then the original learning design was the authoring
	 * learning design which was copied to make this learning design. The original design may or may not still exist 
	 * in the database.  */
	public org.lamsfoundation.lams.learningdesign.LearningDesign getOriginalLearningDesign() {
		return this.originalLearningDesign;
	}
	public void setOriginalLearningDesign(
			org.lamsfoundation.lams.learningdesign.LearningDesign originalLearningDesign) {
		this.originalLearningDesign = originalLearningDesign;
	}
	public Set getChildLearningDesigns() {
		return this.childLearningDesigns;
	}
	public void setChildLearningDesigns(Set childLearningDesigns) {
		this.childLearningDesigns = childLearningDesigns;
	}
	public Set getLessons() {
		return this.lessons;
	}
	public void setLessons(Set lessons) {
		this.lessons = lessons;
	}
	public Set getTransitions() {
		if ( this.transitions==null) {
	        setTransitions(new HashSet());
		}
		return this.transitions;
	}
	public void setTransitions(Set transitions) {
		this.transitions = transitions;
	}
	public Set getActivities() {
		if(this.activities==null){
	        setActivities(new TreeSet(new ActivityOrderComparator()));
	    }	    
		return this.activities;
	}
	public void setActivities(Set activities) {
		this.activities = activities;
	}
	public String toString() {
		return new ToStringBuilder(this).append("learningDesignId",
				getLearningDesignId()).toString();
	}
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof LearningDesign))
			return false;
		LearningDesign castOther = (LearningDesign) other;
		return new EqualsBuilder().append(this.getReadOnly(),
				castOther.getReadOnly()).isEquals();
	}
	public int hashCode() {
		return new HashCodeBuilder().append(getReadOnly()).toHashCode();
	}	
	public HashMap getActivityTree(){		
		HashMap parentActivities = new HashMap();
		Iterator iterator = this.getActivities().iterator();
		while(iterator.hasNext()){
			Activity act = (Activity) iterator.next();
			if(act.isComplexActivity()){
				ComplexActivity complexActivity =(ComplexActivity)act;
				parentActivities.put(complexActivity.getActivityId(),complexActivity.getActivities());
			}else{
				if(act.getParentActivity()==null)
					parentActivities.put(act.getActivityId(),new HashSet());
			}
		}
		return parentActivities;
	}
	public HashSet getParentActivities(){
		HashSet parentActivities = new HashSet();
		Iterator iterator = this.getActivities().iterator();
		while(iterator.hasNext()){
			Activity activity = (Activity)iterator.next();
			if(activity.getParentActivity()==null)				
				parentActivities.add(activity);
		}
		return parentActivities;
	}
	public Activity calculateFirstActivity(){
		Activity newFirstActivity = null;
		HashSet parentActivities = this.getParentActivities();
		Iterator parentIterator = parentActivities.iterator();
		while(parentIterator.hasNext()){
			Activity activity = (Activity)parentIterator.next();
			if(activity.getTransitionTo()==null){
				newFirstActivity = activity;
				break;
			}
		}
		return newFirstActivity;
	}
	public WorkspaceFolder getWorkspaceFolder() {
		return workspaceFolder;
	}
	public void setWorkspaceFolder(WorkspaceFolder workspaceFolder) {
		this.workspaceFolder = workspaceFolder;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {		
		this.duration = duration;
	}
	public String getLicenseText() {
		return licenseText;
	}
	public void setLicenseText(String licenseText) {
		this.licenseText = licenseText;
	}
	public Integer getCopyTypeID() {
		return copyTypeID;
	}
	public void setCopyTypeID(Integer copyTypeID) {
		this.copyTypeID = copyTypeID;
	}
	public License getLicense() {
		return license;
	}
	public void setLicense(License license) {
		this.license = license;
	}
	public Long getLessonID() {
		return lessonID;
	}
	public void setLessonID(Long lessonID) {
		this.lessonID = lessonID;
	}
	public String getLessonName() {
		return lessonName;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public Long getLessonOrgID() {
		return lessonOrgID;
	}
	public void setLessonOrgID(Long lessonOrgID) {
		this.lessonOrgID = lessonOrgID;
	}
	public String getLessonOrgName() {
		return lessonOrgName;
	}
	public void setLessonOrgName(String lessonOrgName) {
		this.lessonOrgName = lessonOrgName;
	}
	public Date getLessonStartDateTime() {
		return lessonStartDateTime;
	}
	public void setLessonStartDateTime(Date lessonStartDateTime) {
		this.lessonStartDateTime = lessonStartDateTime;
	}
	public Date getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}
	public void setLastModifiedDateTime(Date lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime != null ? lastModifiedDateTime : new Date();
	}
	
	public String getOfflineInstructions() {
		return this.offlineInstructions;
	}
	public void setOfflineInstructions(String offlineInstructions) {
		this.offlineInstructions = offlineInstructions;
	}
	public String getOnlineInstructions() {
		return onlineInstructions;
	}
	public void setOnlineInstructions(String onlineInstructions) {
		this.onlineInstructions = onlineInstructions;
	}

	public DesignDetailDTO getDesignDetailDTO(){
		return new DesignDetailDTO(this);
	}
	public FolderContentDTO getFolderContentDTO(){
		return new FolderContentDTO();
	}
		

}