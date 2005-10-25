/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.mc;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class McQueContent implements Serializable {

    /** identifier field */
    private Long uid;

    /** persistent field */
    private Long mcQueContentId;

    /** nullable persistent field */
    private String question;

    /** nullable persistent field */
    private Integer displayOrder;
    
    /** nullable persistent field */
    private Integer weight;
    
    /** persistent field */
    private boolean disabled;
    
    /** non persistent field */
    private Long mcContentId;
    
    /** persistent field */
    private org.lamsfoundation.lams.tool.mc.McContent mcContent;
    
    /** persistent field */
    private Set mcUsrAttempts;

    /** persistent field */
    private Set mcOptionsContents;

    /** full constructor */
    public McQueContent(Long mcQueContentId, String question, Integer displayOrder,  McContent mcContent, Set mcUsrAttempts, Set mcOptionsContents) {
        this.mcQueContentId = mcQueContentId;
        this.question = question;
        this.displayOrder = displayOrder;
        this.mcContent=mcContent;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    public McQueContent(String question, Integer displayOrder,  McContent mcContent, Set mcUsrAttempts, Set mcOptionsContents) {
        this.question = question;
        this.displayOrder = displayOrder;
        this.mcContent=mcContent;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    public McQueContent(String question, Integer displayOrder, Integer weight,  boolean disabled, McContent mcContent, Set mcUsrAttempts, Set mcOptionsContents) {
        this.question = question;
        this.displayOrder = displayOrder;
        this.weight = weight;
        this.disabled = disabled;
        this.mcContent=mcContent;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    
    public McQueContent(Long mcQueContentId, String question, Integer displayOrder,  Set mcUsrAttempts, Set mcOptionsContents) {
        this.mcQueContentId = mcQueContentId;
        this.question = question;
        this.displayOrder = displayOrder;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    public McQueContent(Long mcQueContentId, String question, Integer displayOrder, Integer weight, Set mcUsrAttempts, Set mcOptionsContents) {
        this.mcQueContentId = mcQueContentId;
        this.question = question;
        this.displayOrder = displayOrder;
        this.weight=weight;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    
    
    public McQueContent(String question, Integer displayOrder,  Set mcUsrAttempts, Set mcOptionsContents) {
        this.question = question;
        this.displayOrder = displayOrder;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    

    /** default constructor */
    public McQueContent() {
    }

    /** minimal constructor */
    public McQueContent(Long mcQueContentId, org.lamsfoundation.lams.tool.mc.McContent mcContent, Set mcUsrAttempts, Set mcOptionsContents) {
        this.mcQueContentId = mcQueContentId;
        this.mcContent = mcContent;
        this.mcUsrAttempts = mcUsrAttempts;
        this.mcOptionsContents = mcOptionsContents;
    }
    
    
    /**
     *  gets called by copyToolContent
     * 
     * Copy constructor
     * @param queContent the original qa question content
     * @return the new qa question content object
     */
    public static McQueContent newInstance(McQueContent queContent,
    										McContent newMcContent,
    										McQueContent parentQuestion)
    {
    	McQueContent newQueContent = new McQueContent(queContent.getQuestion(),
													  queContent.getDisplayOrder(),
													  queContent.getWeight(),
													  queContent.isDisabled(), 
													  newMcContent,
                                                      new TreeSet(),
                                                      new TreeSet());
    	return newQueContent;
    }

    public Long getUid() {
        return this.uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getMcQueContentId() {
        return this.mcQueContentId;
    }

    public void setMcQueContentId(Long mcQueContentId) {
        this.mcQueContentId = mcQueContentId;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public org.lamsfoundation.lams.tool.mc.McContent getMcContent() {
        return this.mcContent;
    }

    public void setMcContent(org.lamsfoundation.lams.tool.mc.McContent mcContent) {
        this.mcContent = mcContent;
    }

    public Set getMcUsrAttempts() {
    	if (this.mcUsrAttempts == null)
        	setMcUsrAttempts(new HashSet());
        return this.mcUsrAttempts;
    }

    
    public void setMcUsrAttempts(Set mcUsrAttempts) {
        this.mcUsrAttempts = mcUsrAttempts;
    }

    
    public Set getMcOptionsContents() {
    	if (this.mcOptionsContents == null)
        	setMcOptionsContents(new HashSet());
        return this.mcOptionsContents;
    }

    public void setMcOptionsContents(Set mcOptionsContents) {
        this.mcOptionsContents = mcOptionsContents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("uid", getUid())
            .toString();
    }
	
	/**
	 * @return Returns the mcContentId.
	 */
	public Long getMcContentId() {
		return mcContentId;
	}
	/**
	 * @param mcContentId The mcContentId to set.
	 */
	public void setMcContentId(Long mcContentId) {
		this.mcContentId = mcContentId;
	}
	/**
	 * @return Returns the disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}
	/**
	 * @param disabled The disabled to set.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	/**
	 * @return Returns the weight.
	 */
	public Integer getWeight() {
		return weight;
	}
	/**
	 * @param weight The weight to set.
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
